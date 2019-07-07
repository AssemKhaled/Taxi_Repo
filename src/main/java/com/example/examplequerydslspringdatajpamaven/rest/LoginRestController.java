	package com.example.examplequerydslspringdatajpamaven.rest;
	
	import java.nio.charset.StandardCharsets;
	import java.util.Base64;
	
	import org.omg.CORBA.UserException;
	import org.springframework.beans.factory.annotation.Autowired;	
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestHeader;
	import org.springframework.web.bind.annotation.RestController;

	import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
	import com.example.examplequerydslspringdatajpamaven.entity.User;
	import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
	import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;
import com.example.examplequerydslspringdatajpamaven.service.LoginService;

	
	@RestController
	public class LoginRestController {
	
		@Autowired
		LoginService loginService;
//		@Autowired
		JWKValidator jwkValidator;
	
		@GetMapping(path = "/login/{username}/{password}")
		public String login(@PathVariable(name = "username", required = true) String username,
				@PathVariable(name = "password", required = true) String password) throws UserExceptions   {
			if (username.equals("mariam")) {
				
				UserExceptionEnum userExceptionEnum = UserExceptionEnum.Invalid_username;
	
				System.out.println(userExceptionEnum.Invalid_username.name() + " "
						+ UserExceptionEnum.Invalid_username.getErrorCode());
//				UserExceptions u= new UserExceptions(userExceptionEnum.Invalid_username.name(),
//						UserExceptionEnum.Invalid_username.getErrorCode());
//				System.out.println(u.getDescription()+" "+u.getStatus());
				
//				u.setDescription(userExceptionEnum.Invalid_username.name());
//				u.setStatus(UserExceptionEnum.Invalid_username.getErrorCode());
//				System.out.println(u.getDescription());
	
				throw new UserExceptions("test","101");
	
				/*
				 * UserExceptionEnum userExceptionEnum = UserExceptionEnum.Invalid_username;
				 * throw new UserExceptions(userExceptionEnum.Invalid_username.name(),
				 * UserExceptionEnum.Invalid_username.getErrorCode());
				 */
			}
	
			return username + password;
		}
	
		/*@GetMapping(path = "/login")
		public User login(@RequestHeader(value = "Authorization") String authorization) {
			if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
				// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);
				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				String email = values[0].toString();
				String password = values[1].toString();
				User user = loginService.login(email, password);
				if (user == null) {
					System.out.println("no user");
					return null;
				} else {
					return user;
				}
	
			}
			return null;
	
		}*/
		@GetMapping(path = "/login")
		public 	String login(@RequestHeader(value = "Authorization")String authtorization ){
			String token = jwkValidator.createJWT("mariam", null);
//			loginService.login(authtorization);
//			String token = "mariam";
			return token;
		}
		
		
	}
