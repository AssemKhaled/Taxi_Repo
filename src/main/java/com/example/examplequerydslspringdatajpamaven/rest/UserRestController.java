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
import org.springframework.web.bind.annotation.RequestHeader;
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
	public ResponseEntity<?> usersList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
									   @RequestParam (value = "userId", defaultValue = "0") Long userId,
									   @RequestParam(value = "offset", defaultValue = "0") int offset,
							           @RequestParam(value = "search", defaultValue = "") String search) {
		return userService.usersOfUser(TOKEN,userId,offset,search);
	}
	
	@GetMapping("/getUserById")
	public ResponseEntity<?> getUserById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                             @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return userService.findUserById(TOKEN,userId);
	}

	@PostMapping(path ="/createUser")
	public ResponseEntity<?> createUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                              @RequestBody(required = false) User user) {
        
	   
		return userService.createUser(TOKEN,user,userId);
				
	}
	
	@PostMapping(path ="/editUser")
	public ResponseEntity<?> editUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                            @RequestBody(required = false) User user) {
		
		
		 return  userService.editUser(TOKEN,user,userId);
		
				
	}
	@GetMapping(path ="/deleteUser")
	public ResponseEntity<?> deleteUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                              @RequestParam (value = "deleteUserId", defaultValue = "0") Long deleteUserId) {
		
		return userService.deleteUser(TOKEN,userId,deleteUserId);
				
	}
	
	@GetMapping(path="/getUserRole")
	public ResponseEntity<?> getUserRole(@RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return userService.getUserRole(userId);
	}


}