package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.data.dtos.TaxiProfileDto;
import com.example.examplequerydslspringdatajpamaven.data.mapper.TaxiProfileMapper;
import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.helper.ReusableMethods;
import com.example.examplequerydslspringdatajpamaven.repository.TaxiProfileRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
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
    private final TaxiProfileMapper taxiProfileMapper ;

    GetObjectResponse getObjectResponse;
    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    public TaxiProfileServiceImpl(UserRepository userRepository, TaxiProfileRepository taxiProfileRepository) {
        this.userRepository = userRepository;
        this.taxiProfileRepository = taxiProfileRepository;
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
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName())){
                getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                        ,"Name Of Taxi Profile Already exist", Collections.singletonList(taxiProfile));
                return ResponseEntity.ok().body(getObjectResponse);
            }
            taxiProfile.setCompanyId(creater.getId());
        }else if(creater.getAccountType() == 3){
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName())){
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
            if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName())){
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
                allTaxiProfileDto.add(taxiProfileMapper.entityToDto(taxiProfile));
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
                taxiProfileDto.add(taxiProfileMapper.entityToDto(taxiProfile));
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

        if(reusableMethods.checkDuplicationOfTaxiProfileName(taxiProfiles, taxiProfile.getName())){
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
    public ResponseEntity<?> deleteTaxiProfile(String TOKEN, Long loggerId, Long userId){
        TaxiProfile taxiProfile = taxiProfileRepository.findOne(userId);
        if (taxiProfile == null){
            getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value()
                    ,"Taxi Profile Not Found"
                    ,null);
            return ResponseEntity.badRequest().body(getObjectResponse);
        }

        Date date = new Date();
        taxiProfile.setDeleteDate(date);
        taxiProfileRepository.save(taxiProfile);

        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value()
                ,"Success"
                ,Collections.singletonList(taxiProfile));

        return ResponseEntity.ok().body(getObjectResponse);

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
