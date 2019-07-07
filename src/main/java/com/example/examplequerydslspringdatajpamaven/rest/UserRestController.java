package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


@RestController
@RequestMapping(path = "/users")
public class UserRestController {

	
	@Autowired

	UserServiceImpl userService;
	
	@RequestMapping(value = "/")
	public ResponseEntity<?> noService1() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "")
	public ResponseEntity<?> noService2() {
		return ResponseEntity.ok("no service available");
		
	}
	
	@GetMapping("/userslist/{userId}")
	public Set<User> usersList(@PathVariable (value = "userId") Long userId) {
		
		//S x = userService.getName();
		return userService.usersOfUser(userId);
	}

	@PostMapping(path ="/createUser/{userId}")
	public ResponseEntity<User> createDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
		Set<User> userCreater=new HashSet<>() ;
		userCreater.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
        user.setUsersOfUser(userCreater);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			 User newUser= userService.createUser(user);
			 return new ResponseEntity<>(newUser, HttpStatus.OK);
		//}	 
				
	}
	
	@PostMapping(path ="/editUser/{userId}")
	public ResponseEntity<User> editDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
		Set<User> userCreater=new HashSet<>() ;
		userCreater.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
        user.setUsersOfUser(userCreater);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			 User newUser= userService.createUser(user);
			 return new ResponseEntity<>(newUser, HttpStatus.OK);
		//}	 
				
	}
	@PostMapping(path ="/deleteUser/{userId}")
	public ResponseEntity<String> deleteDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
	
//		
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			 String deleted= userService.deleteUser(user);
			 return new ResponseEntity<>(deleted, HttpStatus.OK);
		//}	 
				
	}


}
