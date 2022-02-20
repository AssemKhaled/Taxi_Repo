package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.MongoDriverLocation;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoDriverLocationRepository;
import com.example.examplequerydslspringdatajpamaven.responses.DashboardDriversStatistics;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.examplequerydslspringdatajpamaven.service.AppServiceImpl.getDateDiff;

@Service
public class DashboardServiceImpl  extends RestServiceController implements DashboardService{

    private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
    private GetObjectResponse getObjectResponse;
    private final UserServiceImpl userService;
    private final DeviceRepository deviceRepository;
    private final DriverRepository driverRepository;
    private final MongoDriverLocationRepository mongoDriverLocationRepository;
    private final UserServiceImpl userServiceImpl;

    public DashboardServiceImpl(UserServiceImpl userService, DeviceRepository deviceRepository, DriverRepository driverRepository,
                                MongoDriverLocationRepository mongoDriverLocationRepository, UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.deviceRepository = deviceRepository;
        this.driverRepository = driverRepository;
        this.mongoDriverLocationRepository = mongoDriverLocationRepository;
        this.userServiceImpl = userServiceImpl;
    }

    public ResponseEntity<?> getDashboardStatisticsDriverStatus(String TOKEN, Long userId){

        logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
        if(TOKEN.equals("")) {
            List<Device> devices = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        ResponseEntity<?> tokenCheckerResponse = super.checkActive(TOKEN);

        if(tokenCheckerResponse!= null)
        {
            return tokenCheckerResponse;
        }

        if(userId.equals(0)){
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",statistics);
            logger.info("************************ getDriversStatus ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",statistics);
            logger.info("************************ getDriversStatus ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }

        List<Long> userIds = new ArrayList<>();
        if(loggedUser.getAccountType().equals(4)) {
            userIds.add(userId);
        }
        else {
            List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
            if(childernUsers.isEmpty()) {
                userIds.add(userId);
            }
            else {
                userIds.add(loggedUser.getId());
                for(User object : childernUsers) {
                    userIds.add(object.getId());
                }
            }
        }

        List<String> driversLastLocationId = deviceRepository.getDriversLastLocationIdByUserIds(userIds);

        List<MongoDriverLocation> mongoDriverLocationsIdle = new ArrayList<>();
        List<MongoDriverLocation> mongoDriverLocationsOnTrip = new ArrayList<>();

        int addMinuteTime = -3;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        Date afterSubtracting3Minutes = DateUtils.addMinutes(now, addMinuteTime);

        now = DateUtils.addHours(now, 2);
        afterSubtracting3Minutes = DateUtils.addHours(afterSubtracting3Minutes, 2);

        String nowTimeInString = formatter.format(now);
        String TimeAfterSubtracting3minutes = formatter.format(afterSubtracting3Minutes);

        MongoDriverLocation mongoDriverLocation = mongoDriverLocationRepository.findOne("5f427ed320e9f20751eea3ad");

        try {
            Date dateLast = formatter.parse(TimeAfterSubtracting3minutes);
            Date dateNow = formatter.parse(nowTimeInString);

            mongoDriverLocationsIdle = mongoDriverLocationRepository.findBy_idInAndServerTimeBetweenAndTripIdIsNull(driversLastLocationId, dateLast, dateNow);
            mongoDriverLocationsOnTrip = mongoDriverLocationRepository.findBy_idInAndServerTimeBetweenAndTripIdIsNotNull(driversLastLocationId, dateLast, dateNow);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        DashboardDriversStatistics dashboardDriversStatistics = DashboardDriversStatistics.builder()
                .mongoDriverLocationsIdle(mongoDriverLocationsIdle)
                .mongoDriverLocationsOnTrip(mongoDriverLocationsOnTrip)
                .build();

        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", Collections.singletonList(dashboardDriversStatistics));
        logger.info("************************ getDriversStatus ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);

    }
}
