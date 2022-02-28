package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.entity.*;
import com.example.examplequerydslspringdatajpamaven.repository.*;
import com.example.examplequerydslspringdatajpamaven.responses.*;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DashboardServiceImpl  extends RestServiceController implements DashboardService{

    private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
    private GetObjectResponse getObjectResponse;
    private final UserServiceImpl userService;
    private final DeviceRepository deviceRepository;
    private final DriverRepository driverRepository;
    private final MongoDriverLocationRepository mongoDriverLocationRepository;
    private final UserServiceImpl userServiceImpl;
    private final TripDetailsRepository tripDetailsRepository;
    private final MongoActivitiesRepository mongoActivitiesRepository;

    public DashboardServiceImpl(UserServiceImpl userService, DeviceRepository deviceRepository, DriverRepository driverRepository,
                                MongoDriverLocationRepository mongoDriverLocationRepository, UserServiceImpl userServiceImpl,
                                TripDetailsRepository tripDetailsRepository, MongoActivitiesRepository mongoActivitiesRepository) {
        this.userService = userService;
        this.deviceRepository = deviceRepository;
        this.driverRepository = driverRepository;
        this.mongoDriverLocationRepository = mongoDriverLocationRepository;
        this.userServiceImpl = userServiceImpl;
        this.tripDetailsRepository = tripDetailsRepository;
        this.mongoActivitiesRepository = mongoActivitiesRepository;
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
        Integer numberOfIdleDrivers = 0 ;
        Integer numberOfDriversOnTrip = 0 ;

        int addMinuteTime = -3;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        Date afterSubtracting3Minutes = DateUtils.addMinutes(now, addMinuteTime);

        now = DateUtils.addHours(now, 2);
        afterSubtracting3Minutes = DateUtils.addHours(afterSubtracting3Minutes, 2);

        String nowTimeInString = formatter.format(now);
        String TimeAfterSubtracting3minutes = formatter.format(afterSubtracting3Minutes);


        try {
            Date dateLast = formatter.parse(TimeAfterSubtracting3minutes);
            Date dateNow = formatter.parse(nowTimeInString);

            mongoDriverLocationsIdle = mongoDriverLocationRepository.findBy_idInAndServerTimeBetweenAndTripIdIsNull(driversLastLocationId, dateLast, dateNow);
            mongoDriverLocationsOnTrip = mongoDriverLocationRepository.findBy_idInAndServerTimeBetweenAndTripIdIsNotNull(driversLastLocationId, dateLast, dateNow);

            numberOfIdleDrivers = mongoDriverLocationRepository.countAllBy_idInAndServerTimeBetweenAndTripIdIsNull(driversLastLocationId, dateLast, dateNow);
            numberOfDriversOnTrip = mongoDriverLocationRepository.countAllBy_idInAndServerTimeBetweenAndTripIdIsNotNull(driversLastLocationId, dateLast, dateNow);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        DashboardDriversStatistics dashboardDriversStatistics = DashboardDriversStatistics.builder()
                .mongoDriverLocationsIdle(mongoDriverLocationsIdle)
                .mongoDriverLocationsOnTrip(mongoDriverLocationsOnTrip)
                .numberOfIdleDriver(numberOfIdleDrivers)
                .numberOfDriverOnTrip(numberOfDriversOnTrip)
                .build();

        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", Collections.singletonList(dashboardDriversStatistics));
        logger.info("************************ getDriversStatus ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);

    }

    public ResponseEntity<?> getDashboardStatisticsTrips(String TOKEN, Long userId){
        logger.info("************************ getDashboardStatisticsTrips STARTED ***************************");
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
            logger.info("************************ getDashboardStatisticsTrips ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",statistics);
            logger.info("************************ getDashboardStatisticsTrips ENDED ***************************");
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

//        List<TripDetails> completedTrips = new ArrayList<>();
//        List<TripDetails> currentTrips = new ArrayList<>();

        int numberOfTodayCompletedTrips = 0 ;
        Integer numberOfTodayCurrentTrips = 0 ;
        Double numberOfTodayTotalIncome = 0.0 ;

        List<Integer> driversIds = driverRepository.getDriversByUsersIds(userIds);
        List<Long> driverIdsLong = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfTheDay = now.with(LocalTime.MIN);
        Date startOfTheDayAsDate = Date.from(startOfTheDay.toInstant(ZoneOffset.UTC));
        LocalDateTime endOfTheDay = now.with(LocalTime.MAX);
        Date endOfTheDayAsDate = Date.from(endOfTheDay.toInstant(ZoneOffset.UTC));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        for(Integer i: driversIds){
            driverIdsLong.add(i.longValue());
        }

        numberOfTodayCompletedTrips = tripDetailsRepository.countAllByDriverIdInAndPickupDateAndDropDateTimeIsNotNull(driverIdsLong, today);
        numberOfTodayCurrentTrips = tripDetailsRepository.countAllByDriverIdInAndPickupDateAndDropDateTimeIsNull(driverIdsLong, today);
        numberOfTodayTotalIncome = tripDetailsRepository.totalTodayIncome(driverIdsLong, today);

        List<DashboardTripsStatistics> listOfDashboardTripsStatistics = new ArrayList<>();
        DashboardTripsStatistics dashboardTripStatistics = DashboardTripsStatistics.builder()
                .numberOfTodayCompletedTrips(numberOfTodayCompletedTrips)
                .numberOfTodayCurrentTrips(numberOfTodayCurrentTrips)
                .numberOfTodayTotalIncome(numberOfTodayTotalIncome != null? numberOfTodayTotalIncome: 0.0)
                .build();

        listOfDashboardTripsStatistics.add(dashboardTripStatistics);
        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", Collections.singletonList(listOfDashboardTripsStatistics));
        logger.info("************************ getDashboardStatisticsTrips ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);
    }

    public ResponseEntity<?> getDashboardDriversLiveDataTable(String TOKEN, Long userId, int offset , int limit, String search){
        logger.info("************************ getDashboardDriversLiveDataTable STARTED ***************************");
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
            logger.info("************************ getDashboardDriversLiveDataTable ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",statistics);
            logger.info("************************ getDashboardDriversLiveDataTable ENDED ***************************");
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

        List<ObjectId> driversLastLocationIds = new ArrayList<>();
        List<Long> driversLastLocationIdsLongValues = new ArrayList<>();
        List<MongoDriverLocation> driversLocationList;
        List<DashboardDriversLiveDataResponse> dashboardDriversLiveDataResponseList = new ArrayList<>();
        List<TotalIncomeForDrivers> totalIncomeForDriversList = new ArrayList<>();
        Integer size = 0;


        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        List<DriversDataInDeviceTable> driversDataInDeviceTableList = deviceRepository.getDriversDataByUserIds(userIds, offset, search);
        size = deviceRepository.getTotalNumberOfDriversForUsers(userIds);
//        List<DriversDataInDeviceTable> driversDataInDeviceTableList = deviceRepository.getDriversDataByUserIds(userIds, today);
        driversDataInDeviceTableList.forEach(data -> {
            if(data.getDriverLastLocationId() != null){
                driversLastLocationIds.add(new ObjectId(data.getDriverLastLocationId()));
            }
            driversLastLocationIdsLongValues.add(data.getDriverId());
        });

        totalIncomeForDriversList = tripDetailsRepository.totalTodayIncomeByDriversIds(driversLastLocationIdsLongValues, today);

        driversLocationList = mongoDriverLocationRepository.findBy_idInOrderByServerTimeDesc(driversLastLocationIds);


        for(MongoDriverLocation driverLocation: driversLocationList){
            int addMinuteTime = -3;
            long minutes = 0;
            String status = "";
            String vehicleName = "";
            Double totalCostPerDriver = 0.0;
            Integer numberOfCompletedTrips = 0;

            for(DriversDataInDeviceTable driverData: driversDataInDeviceTableList){
                if(Objects.equals(driverData.getDriverId(), driverLocation.getDriverId())){
                    vehicleName = driverData.getName();
                }
            }

            for(TotalIncomeForDrivers totalIncomeForDriver: totalIncomeForDriversList){
                if(Objects.equals(totalIncomeForDriver.getId(), driverLocation.getDriverId())){
                    totalCostPerDriver = totalIncomeForDriver.getTotalCost();
                    numberOfCompletedTrips = totalIncomeForDriver.getNumberOfCompletedTrips();
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = Calendar.getInstance().getTime();
            Date afterSubtracting3Minutes = DateUtils.addMinutes(now, addMinuteTime);

            minutes = getDateDiff (driverLocation.getServerTime(), now, TimeUnit.MINUTES);

            if(minutes <= 3) {
                if(driverLocation.getTripId() != null){
                    status =  "online";
                } else {
                    status = "idle";
                }
            }
            else {
                status = "offline";
            }

            DashboardDriversLiveDataResponse dashboardDriversLiveDataResponse = DashboardDriversLiveDataResponse.builder()
                    .driverId(driverLocation.getDriverId())
                    .driverName(driverLocation.getDriverName())
                    .lastUpdate(driverLocation.getLocationTime())
                    .numberOfPassengers(driverLocation.getNumberOfPassengers())
                    .driverStatus(status)
                    .vehicleName(vehicleName)
                    .TodayIncome(totalCostPerDriver)
                    .numberOfCompletedTrips(numberOfCompletedTrips)
                    .build();

            dashboardDriversLiveDataResponseList.add(dashboardDriversLiveDataResponse);
        }

        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", dashboardDriversLiveDataResponseList, size);
        logger.info("************************ getDashboardDriversLiveDataTable ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);
    }

    public ResponseEntity<?> getDashboardActivitiesList(String TOKEN, Long userId, int offset , int limit){
        logger.info("************************ getDashboardActivitiesList STARTED ***************************");
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
            logger.info("************************ getDashboardActivitiesList ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",statistics);
            logger.info("************************ getDashboardActivitiesList ENDED ***************************");
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

        List<Integer> driversIds = driverRepository.getDriversByUsersIds(userIds);
        //List<Long> driverIdsLong = new ArrayList<>();
        List<MongoActivities> mongoActivitiesList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfTheDay = now.with(LocalTime.MIN);
        Date startOfTheDayAsDate = Date.from(startOfTheDay.toInstant(ZoneOffset.UTC));
        LocalDateTime endOfTheDay = now.with(LocalTime.MAX);
        Date endOfTheDayAsDate = Date.from(endOfTheDay.toInstant(ZoneOffset.UTC));


//        for(Integer i: driversIds){
//            driverIdsLong.add(i.longValue());
//        }

        Pageable pageable = new PageRequest(offset,limit);
        mongoActivitiesList = mongoActivitiesRepository.findAllByDriverIdInAndActivityTimeBetweenOrderByActivityTimeDesc(driversIds,
                startOfTheDayAsDate,endOfTheDayAsDate,pageable);


        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success", mongoActivitiesList);
        logger.info("************************ getDashboardDriversLiveDataTable ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);
    }

    public ResponseEntity<?> checkUserValidation(String TOKEN, Long userId, String apiTitle){
        logger.info("************************" + apiTitle + "  STARTED ***************************");
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
            logger.info("************************ " + apiTitle + "  ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            List<Device> statistics = null;
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",statistics);
            logger.info("************************ " + apiTitle + "  ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }
        return ResponseEntity.ok(200);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
    {
        long diffInMillies = date2.getTime() - date1.getTime();

        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
