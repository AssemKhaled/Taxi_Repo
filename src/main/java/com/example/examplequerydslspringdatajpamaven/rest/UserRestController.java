package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


@RestController
@RequestMapping(path = "/users")
@CrossOrigin
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
	
	@GetMapping("/usersList")
	public ResponseEntity<?> usersList(@RequestParam (value = "userId") Long userId,@RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "search", defaultValue = "") String search) {
		return userService.usersOfUser(userId,offset,search);
	}
	
	@GetMapping("/getUserById")
	public ResponseEntity<?> getUserById(@RequestParam (value = "userId") Long userId) {
		return userService.findUserById(userId);
	}

	@PostMapping(path ="/createUser/{userId}")
	public ResponseEntity<?> createDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
         if(user.getId() != null) {
        	 new ResponseEntity<>("badRequest", HttpStatus.BAD_REQUEST);
         }
	   
		return userService.createUser(user,userId);
				
	}
	
	@PostMapping(path ="/editUser/{userId}")
	public ResponseEntity<?> editDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
		
		 if(user.getId() == null) {
        	 new ResponseEntity<>("badRequest", HttpStatus.BAD_REQUEST);
         }
		 return  userService.editUser(user,userId);
		
				
	}
	@PostMapping(path ="/deleteUser/{userId}")
	public ResponseEntity<?> deleteDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) User user) {
		 if(user.getId() == null) {
        	 new ResponseEntity<>("badRequest", HttpStatus.BAD_REQUEST);
         }
			 
		return userService.deleteUser(user);
		
				
	}


}
