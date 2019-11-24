package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;


@RestController
public class HelloRestController {

	@Autowired
	UserRepository userRepository;

	@Autowired
//	UserServiceImpl userService;

	/*@GetMapping
	public String helloWorld() {
		return "Hello World!";
	}*/

	@GetMapping("/users")
	public String helloCats() {
		
		//S x = userService.getName();
//		return userService.getName();
		return "555fff";
	}
	
	
	
}
