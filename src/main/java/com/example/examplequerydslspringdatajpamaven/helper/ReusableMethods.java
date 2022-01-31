package com.example.examplequerydslspringdatajpamaven.helper;

import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class ReusableMethods extends RestServiceController{


    private final UserRepository userRepository;
    GetObjectResponse getObjectResponse;

    public ReusableMethods(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Long> allUsersId(List<User> users, Long ParentId){
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(ParentId);
        for(User user: users){
            usersIds.add(user.getId());
        }
        return usersIds;
    }

    public List<Long> GetUserChildrenId(Long UserId){
        List<User> childrenUsers = getAllChildrenOfUser(UserId);
        List<Long> usersIds = new ArrayList<>();
        if(childrenUsers.isEmpty()){
            usersIds.add(UserId);
        }else {
            usersIds = allUsersId(childrenUsers, UserId);
        }

        return usersIds;
    }

    public boolean checkDuplicationOfTaxiProfileName(List<TaxiProfile> taxiProfiles, String TaxiProfileName){
        for (TaxiProfile taxiProfile: taxiProfiles){
            if(TaxiProfileName.equals(taxiProfile.getName()))
                return true;
        }
        return false;
    }


    public ResponseEntity<?> TokenValidation(String TOKEN, Long loggerId){

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
        return ResponseEntity.ok().body(true);
    }

    public List<User> getAllChildrenOfUser(Long userId) {
        // TODO Auto-generated method stub
        List<User> childrenUsers = new ArrayList<>();


        User user = userRepository.findOne(userId);
        if(user != null) {
            if(user.getAccountType() == 1) {
                List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
                if(!childrenReturned1.isEmpty()) {
                    for(User object1 : childrenReturned1) {
                        childrenUsers.add(object1);


                        if(object1.getAccountType() == 2) {
                            List<User>childrenReturned2 = userRepository.getChildrenOfUser(object1.getId());
                            if(!childrenReturned2.isEmpty()) {
                                for(User object2 : childrenReturned2) {
                                    childrenUsers.add(object2);



                                    if(object2.getAccountType() == 3) {
                                        List<User>childrenReturned3 = userRepository.getChildrenOfUser(object2.getId());
                                        if(!childrenReturned3.isEmpty()) {
                                            childrenUsers.addAll(childrenReturned3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(user.getAccountType() == 2) {
                List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
                if(!childrenReturned1.isEmpty()) {
                    for(User object1 : childrenReturned1) {
                        childrenUsers.add(object1);


                        if(object1.getAccountType() == 3) {
                            List<User>childrenReturned2 = userRepository.getChildrenOfUser(object1.getId());
                            if(!childrenReturned2.isEmpty()) {
                                childrenUsers.addAll(childrenReturned2);
                            }
                        }
                    }
                }
            }
            if(user.getAccountType() == 3) {
                List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
                if(!childrenReturned1.isEmpty()) {
                    childrenUsers.addAll(childrenReturned1);
                }
            }
        }

        return childrenUsers;
    }

    public  String getMd5(String input) {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
