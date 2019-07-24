package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

@CrossOrigin
@RestController
@RequestMapping(path = "/profile")
public class ProfileRestController {

	@Autowired
	ProfileServiceImpl profileServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@RequestMapping(value = "/getProfileInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfo(@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  ResponseEntity.ok(profileServiceImpl.getUserInfo(userId).getBody());

	}
	
	@RequestMapping(value = "/changePassowrd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrd(@RequestBody Map<String, String> data ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {

		
    	return  ResponseEntity.ok(profileServiceImpl.updateProfilePassword(data,userId).getBody());

	}
	
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updateProfile(@RequestBody(required = false) User user ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  ResponseEntity.ok(profileServiceImpl.updateProfileInfo(user,userId).getBody());

	}
	
	@RequestMapping(value = "/updatePhoto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhoto(@RequestBody Map<String, String> data ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  ResponseEntity.ok(profileServiceImpl.updateProfilePhoto(data,userId).getBody());

	}
	
}
