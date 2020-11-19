package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

/**
 * Service test
 * @author fuinco
 *
 */
@RestController
public class HelloRestController {

	@Autowired
	UserRepository userRepository;

	@GetMapping("/users")
	public String helloCats() {
		return "success";
	}
	
	
	
}
