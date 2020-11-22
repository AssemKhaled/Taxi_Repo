package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

/**
 * Services of profile component
 * @author fuinco
 *
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/profile")
public class ProfileRestController {

	@Autowired
	ProfileServiceImpl profileServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@RequestMapping(value = "/getProfileInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  profileServiceImpl.getUserInfo(TOKEN,userId);

	}
	
	@RequestMapping(value = "/changePassowrd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrd(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestBody Map<String, String> data ,
			                                              @RequestParam (value = "check", defaultValue = "") String check,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {

		
    	return profileServiceImpl.updateProfilePassword(TOKEN,data,check,userId);

	}
	
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updateProfile(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestBody(required = false) User user ,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  profileServiceImpl.updateProfileInfo(TOKEN,user,userId);

	}
	
	@RequestMapping(value = "/updatePhoto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhoto(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestBody Map<String, String> data ,
			                                           @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  profileServiceImpl.updateProfilePhoto(TOKEN,data,userId);

	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> restPassword(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                        @RequestBody Map<String, String> data ,
			                                            @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  profileServiceImpl.restPassword(TOKEN,loggedUserId,userId,data);

	}
	
}
