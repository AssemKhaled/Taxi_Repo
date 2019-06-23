package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}
