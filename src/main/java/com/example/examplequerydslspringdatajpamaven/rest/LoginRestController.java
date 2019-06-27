package com.example.examplequerydslspringdatajpamaven.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;



@RestController
public class LoginRestController {
	
	/*@GetMapping(path = "/login/{username}/{password}")
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
		/*}
		
		return username+password;
	}*/
	@GetMapping(path = "/login")
	public String login( @RequestHeader(value="Authorization") String authorization) {
		if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
		    // Authorization: Basic base64credentials
		    String base64Credentials = authorization.substring("Basic".length()).trim();
		    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
		    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		    // credentials = username:password
		    final String[] values = credentials.split(":", 2);
		    return values[0].toString()+values[1].toString();
		} 
		return null;
		
	}
}
