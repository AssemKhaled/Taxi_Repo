package com.example.examplequerydslspringdatajpamaven.rest;


import org.springframework.beans.factory.annotation.Autowired;	
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.service.LoginService;

/**
 * Services Login and Logout Component
 * @author fuinco
 *
 */
@CrossOrigin
@RestController
public class LoginRestController {
	
	@Autowired
	private LoginService loginService;

	@GetMapping(path = "/login")
	public 	ResponseEntity<?> login(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

		
		return loginService.login(authtorization);
	}
	
	@GetMapping(path = "/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
		
		return loginService.logout(TOKEN);
	}
	
		

		
}
