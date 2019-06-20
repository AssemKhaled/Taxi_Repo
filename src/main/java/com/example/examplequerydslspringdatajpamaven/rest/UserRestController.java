package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.UserServiceImpl;

@RestController
@RequestMapping(path = "/users")
public class UserRestController {

	@Autowired
	UserServiceImpl userServiceImpl;
	
	@RequestMapping(value = "/")
	public ResponseEntity<?> noService1() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "")
	public ResponseEntity<?> noService2() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "/get_all_users/{userId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getDrivers(@PathVariable (value = "userId") String id) {
		
		if(Integer.parseInt(id) != 0) {
			
			return ResponseEntity.ok(userServiceImpl.getAllUsers(Integer.parseInt(id)));

		}
		else {
			
			return ResponseEntity.ok("no user selected to get his own drivers");			
		
		}
		
		
	}
	
}
