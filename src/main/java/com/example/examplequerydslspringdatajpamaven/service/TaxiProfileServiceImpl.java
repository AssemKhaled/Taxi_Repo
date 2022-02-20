package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.data.dtos.TaxiProfileDto;
import com.example.examplequerydslspringdatajpamaven.data.mapper.TaxiProfileMapper;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.helper.ReusableMethods;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.TaxiProfileRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.responses.TaxiProfileListSelect;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaxiProfileServiceImpl extends RestServiceController implements TaxiProfileService{



    private final UserRepository userRepository;
    private final TaxiProfileRepository taxiProfileRepository;
    private final ReusableMethods reusableMethods ;
    private final TaxiProfileMapper taxiProfileMapper;
    private final UserServiceImpl userService;
    private final UserRoleService userRoleService;
    private final DeviceRepository deviceRepository;

    GetObjectResponse getObjectResponse;
    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    public TaxiProfileServiceImpl(UserRepository userRepository, TaxiProfileRepository taxiProfileRepository, UserServiceImpl userService, UserRoleService userRoleService, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.taxiProfileRepository = taxiProfileRepository;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.deviceRepository = deviceRepository;
        this.taxiProfileMapper = new TaxiProfileMapper();
        this.reusableMethods = new ReusableMethods(userRepository);
    }

    @Override
    public ResponseEntity<?> createTaxiProfile(String TOKEN, Long loggerId,TaxiProfileDto taxiProfileDto) {
        logger.info("************************ Create Taxi Profile STARTED ***************************");
        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        User creater =  userRepository.findOne(loggerId);
        if(creater == null){
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logger Id Not found", null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        TaxiProfile taxiProfile = taxiProfileMapper.DtoToEntity(taxiProfileDto);
        List<TaxiProfile> taxiProfiles =
                taxiProfileRepository.findAllByCompanyIdIn(reusableMethods.GetUserChildrenId(loggerId));

        if(creater.getAccountType() == 1 || creater.getAccountType() == 2){
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName(), taxiProfile.getId())){
                getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                        ,"Name Of Taxi Profile Already exist", Collections.singletonList(taxiProfile));
                return ResponseEntity.ok().body(getObjectResponse);
            }
            taxiProfile.setCompanyId(creater.getId());
        }else if(creater.getAccountType() == 3){
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName(), taxiProfile.getId())){
                getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                        ,"Name Of Taxi Profile Already exist", Collections.singletonList(taxiProfile));
                return ResponseEntity.ok().body(getObjectResponse);
            }
            taxiProfile.setCompanyId(creater.getId());
        }else if(creater.getAccountType() == 4){

            Set<User> parentClients = creater.getUsersOfUser();
            if(parentClients.isEmpty()) {
                getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user account cannot create user as it has no parent",null);
                logger.info("************************createUser ENDED ***************************");
                return ResponseEntity.badRequest().body(getObjectResponse);
            }
            User parentClient = null;

            for(User user : parentClients) {
                parentClient = user;
            }

            taxiProfiles =
                    taxiProfileRepository.findAllByCompanyIdIn(reusableMethods.GetUserChildrenId(parentClient.getId()));
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName(), taxiProfile.getId())){
                getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                        ,"Name Of Taxi Profile Already exist", Collections.singletonList(taxiProfile));
                return ResponseEntity.ok().body(getObjectResponse);
            }

            taxiProfile.setCompanyId(parentClient.getId());
        }

        taxiProfileRepository.save(taxiProfile);
        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                ,"Success"
                ,Collections.singletonList(taxiProfile));
        return ResponseEntity.ok().body(getObjectResponse);
    }

    @Override
    public ResponseEntity<?> getAllTaxiProfile(String TOKEN, Long loggerId,String name, int pageSize, int pageNumber) {
        logger.info("************************ List Taxi Profile STARTED ***************************");
        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        User loggedId =  userRepository.findOne(loggerId);
        if(loggedId == null){
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logger Id Not found", null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize);

        if(name.equals("")){
            List<TaxiProfile> allTaxiProfile = taxiProfileRepository.findAll(pageable).getContent();
            List<TaxiProfileDto> allTaxiProfileDto = new ArrayList<>();
            for(TaxiProfile taxiProfile: allTaxiProfile){
                if(taxiProfile.getDeleteDate() == null){
                    allTaxiProfileDto.add(taxiProfileMapper.entityToDto(taxiProfile));
                }
            }
            getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                    , "Success"
                    ,allTaxiProfileDto
                    , Math.toIntExact(taxiProfileRepository.count()));
            return ResponseEntity.ok().body(getObjectResponse);
        }else{
            List<TaxiProfile> TaxiProfileByName = taxiProfileRepository.findAllByName(name, pageable);
            List<TaxiProfileDto> taxiProfileDto = new ArrayList<>();
            for(TaxiProfile taxiProfile: TaxiProfileByName){
                if(taxiProfile.getDeleteDate() == null){
                    taxiProfileDto.add(taxiProfileMapper.entityToDto(taxiProfile));
                }
            }
            getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                    , "Success"
                    ,taxiProfileDto
                    ,Math.toIntExact(taxiProfileRepository.countAllByName(name)));
            return ResponseEntity.ok().body(getObjectResponse);
        }
    }

    @Override
    public ResponseEntity<?> GetUserChildren(String TOKEN, Long loggerId, Long UserId, int pageSize, int pageNumber){
        logger.info("************************ Get All Children Taxi Profiles STARTED ***************************");
        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        User loggedId =  userRepository.findOne(loggerId);
        if(loggedId == null){
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logger Id Not found", null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize);
        List<TaxiProfile> UserChildrenTaxiProfile = taxiProfileRepository.findAllByCompanyIdInAndDeleteDate(reusableMethods.GetUserChildrenId(UserId), null, pageable);
        List<TaxiProfileDto> UserChildrenTaxiProfileDto = new ArrayList<>();

        for(TaxiProfile taxiProfile: UserChildrenTaxiProfile){
            UserChildrenTaxiProfileDto.add(taxiProfileMapper.entityToDto(taxiProfile));
        }

        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                ,"Success"
                ,UserChildrenTaxiProfileDto);

        return ResponseEntity.ok().body(getObjectResponse);
    }

    @Override
    public ResponseEntity<?> editTaxiProfile(String TOKEN, Long loggerId,TaxiProfileDto taxiProfileDto){
        logger.info("************************ Edit Taxi Profile STARTED ***************************");
        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        User user =  userRepository.findOne(loggerId);
        if(user == null){
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logger Id Not found", null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }
        TaxiProfile taxiProfile = taxiProfileMapper.DtoToEntity(taxiProfileDto);
        TaxiProfile taxiProfile1 = taxiProfileRepository.findOne(taxiProfile.getId());
        if(taxiProfile1 == null){
            getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                    ,"Taxi Profile Not Found", Collections.singletonList(taxiProfile));
            return ResponseEntity.ok().body(getObjectResponse);
        }

        List<TaxiProfile> taxiProfiles =
                taxiProfileRepository.findAllByCompanyIdIn(reusableMethods.GetUserChildrenId(loggerId));

        if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName(), taxiProfile.getId())){
            getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                    ,"Name Of Taxi Profile Already exist", Collections.singletonList(taxiProfile));
            return ResponseEntity.ok().body(getObjectResponse);
        }

        taxiProfileRepository.save(taxiProfile);

        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                ,"Success", Collections.singletonList(taxiProfile));
        return ResponseEntity.ok().body(getObjectResponse);
    }

    @Override
    public ResponseEntity<?> deleteTaxiProfile(String TOKEN, Long loggerId, Long taxiProfileId){
        logger.info("************************ DeleteTaxiProfile ENDED STARTED ***************************");
        if(TOKEN.equals("")) {

            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }
        User loggedUser = userService.findById(loggerId);
        if(loggedUser == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
            logger.info("************************ DeleteTaxiProfile ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }
        if(!loggedUser.getAccountType().equals(1)) {
            if(!userRoleService.checkUserHasPermission(loggerId, "DEVICE", "delete")) {
                getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete device",null);
                logger.info("************************ deleteDevice ENDED ***************************");
                return  ResponseEntity.badRequest().body(getObjectResponse);
            }
        }

        TaxiProfile taxiProfile = taxiProfileRepository.findOne(taxiProfileId);
        if (taxiProfile == null){
            getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                    ,"Taxi Profile Not Found"
                    ,null);
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        List<Device> deviceLinkedToThisTaxiProfile = deviceRepository.findByTaxiprofileId(taxiProfileId.intValue());

        deviceLinkedToThisTaxiProfile.forEach(device -> {
            device.setTaxiprofileId(null);
        });

        deviceRepository.save(deviceLinkedToThisTaxiProfile);

        Date date = new Date();
        taxiProfile.setDeleteDate(date);
        taxiProfileRepository.save(taxiProfile);

        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                ,"Success"
                ,Collections.singletonList(taxiProfile));

        return ResponseEntity.ok().body(getObjectResponse);

    }

    @Override
    public ResponseEntity<?> getTaxiProfileById(String TOKEN, Long userId, Long taxiProfileId){
        logger.info("************************ GetTaxiProfileById STARTED ***************************");
        if(TOKEN.equals("")) {

            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }
        if(taxiProfileId == 0 || userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "taxiProfileId  and logged user Id are  Required",null);
            logger.info("************************ GetTaxiProfileById ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }
        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
            logger.info("************************ GetTaxiProfileById ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }

        TaxiProfile taxiProfile = taxiProfileRepository.findOne(taxiProfileId);
        if (taxiProfile == null) {

            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This taxiProfile is not found",null);
            logger.info("************************ GetTaxiProfileById ENDED ***************************");
            return ResponseEntity.ok().body(getObjectResponse);
        }
        else
        {
            if(taxiProfile.getDeleteDate() != null) {
                List<Device> devices = null;
                getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This taxiProfile is not found",devices);
                logger.info("************************ GetTaxiProfileById ENDED ***************************");
                return ResponseEntity.ok().body(getObjectResponse);
            }
            if(!Objects.equals(taxiProfile.getCompanyId(), userId) && (!loggedUser.getAccountType().equals(1) && !loggedUser.getAccountType().equals(2))){
                List<Device> devices = null;
                getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You are not allowed to edit this taxi profile",devices);
                logger.info("************************ GetTaxiProfileById ENDED ***************************");
                return ResponseEntity.ok().body(getObjectResponse);
            }
            List<TaxiProfile> taxiProfiles = new ArrayList<>();

            taxiProfiles.add(taxiProfile);
            getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",taxiProfiles);
            logger.info("************************ GetTaxiProfileById ENDED ***************************");
            return ResponseEntity.ok().body(getObjectResponse);
        }

    }

    @Override
    public ResponseEntity<?> assignTaxiProfileToDevice(String TOKEN, Long userId, Long taxiProfileId, Long deviceId){
        logger.info("************************ assignDeviceToTaxiProfile STARTED ***************************");
        if(TOKEN.equals("")) {

            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }
        if(userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is Required",null);
            logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }
        User loggedUser = userService.findById(userId);
        if(loggedUser == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
            logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }
        if(deviceId == 0 ) {

            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",null);
            logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        Device device = deviceRepository.findOne(deviceId);
        if(device == null || device.getDelete_date() != null){
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found or it was deleted",null);
            logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }

        TaxiProfile taxiProfile = taxiProfileRepository.findOne(taxiProfileId);
        if(taxiProfile == null || taxiProfile.getDeleteDate() != null){
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This taxiProfile is not found or it was deleted",null);
            logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
            return ResponseEntity.status(404).body(getObjectResponse);
        }

        if(device.getTaxiprofileId() == null || device.getTaxiprofileId() != taxiProfile.getId().intValue()){
            device.setTaxiprofileId(taxiProfile.getId().intValue());
        }

        deviceRepository.save(device);

        List<Device> devices = null;
        getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
        logger.info("************************ assignDeviceToTaxiProfile ENDED ***************************");
        return ResponseEntity.ok().body(getObjectResponse);
    }

    @Override
    public ResponseEntity<?> getTaxiProfileListForSelect(String TOKEN, Long userId){
        logger.info("************************ getTaxiProfileListForSelect STARTED ***************************");
        if(TOKEN.equals("")) {

            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }
        if(userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "user Id is  Required",null);
            logger.info("************************ getTaxiProfileListForSelect ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        User user = userService.findById(userId);
        if(user == null || user.getDelete_date() != null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged user is deleted or doesn't exist",null);
            logger.info("************************ getTaxiProfileListForSelect ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        List<Long>usersIds= new ArrayList<>();

        if(user.getAccountType().equals(4)) {
            usersIds.add(userId);
        }
        else {
            List<User>childernUsers = userService.getAllChildernOfUser(userId);
            if(childernUsers.isEmpty()) {
                usersIds.add(userId);
            }
            else {
                usersIds.add(user.getId());
                for(User object : childernUsers) {
                    usersIds.add(object.getId());
                }
            }
        }

        List<TaxiProfileListSelect> taxiProfileListSelect = taxiProfileRepository.getTaxiProfileByCompanyIds(usersIds);

        if(taxiProfileListSelect.size() == 0){
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user and his children doesn't have any taxi profiles",null);
            logger.info("************************ getTaxiProfileListForSelect ENDED ***************************");
            return ResponseEntity.badRequest().body(getObjectResponse);
        }
        else {
            getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",taxiProfileListSelect);
            logger.info("************************ getTaxiProfileListForSelect ENDED ***************************");
            return ResponseEntity.ok().body(getObjectResponse);
        }
    }
}

//    ----------------------------------Commented functions----------------------------------------------
//    @Override
//    public ResponseEntity<?> getTaxiProfileByName(String name) {
//        List<TaxiProfile> TaxiProfileByName = taxiProfileRepository.findTaxiProfileByName(name);
//        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",TaxiProfileByName);
//        return ResponseEntity.ok().body(getObjectResponse);
//    }
// ***********************
//        ResponseEntity<?> authHandler = reusableMethods.TokenValidation(TOKEN, loggerId);
//        if (!authHandler.getBody().equals(true)){
//            return authHandler;
//        }
