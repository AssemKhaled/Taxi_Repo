package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;



@RestController
public class LoginRestController {
	@GetMapping(path = "/login/{username}/{password}")
	public String login(@PathVariable(name = "username", required = true) String username,
			@PathVariable(name = "password", required = true) String password)throws UserExceptions {
		if(username.equals("mariam")) {
			System.out.println("get here");
			UserExceptionEnum userExceptionEnum = UserExceptionEnum.Invalid_username;
			throw new UserExceptions(userExceptionEnum.Invalid_username.name(),
					UserExceptionEnum.Invalid_username.getErrorCode());
			
			/*UserExceptionEnum userExceptionEnum = UserExceptionEnum.Invalid_username;
			throw new UserExceptions(userExceptionEnum.Invalid_username.name(),
					UserExceptionEnum.Invalid_username.getErrorCode());*/
		}
		
		return username+password;
	}
}
