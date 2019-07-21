package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class LoginServiceImpl implements LoginService  {
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);

	@Autowired
	 UserRepository userRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
//	JWKValidator jwkValidator;
	@Autowired
	JWKValidator jwkValidator;
	
	GetObjectResponse getObjectResponse;
	
	
	@Override
	public ResponseEntity<?> login(String authorization) {
		
		logger.info("************************ Login STARTED ***************************");
		 if(authorization != "" && authorization.toLowerCase().startsWith("basic")) {
			 
			// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);
				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				String email = values[0].toString();
				String password = values[1].toString();
				String hashedPassword = userServiceImpl.getMd5(password);
				User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
				if(user == null)
				{
					List<Map> loggedUser = null;
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Invalid email or Password",loggedUser);
					logger.info("************************ Login ENDED ***************************");
					return  ResponseEntity.status(404).body(getObjectResponse);
					
				}
				else {
					
					String token =  jwkValidator.createJWT("mariam", null);
					System.out.println("token"+ token);
//					String token = jwkValidator.createJWT("mariam", null);
//					System.out.println("User----->" + token);
					Map userInfo = new HashMap();
					userInfo.put("userId", user.getId());
					userInfo.put("name" ,user.getName());
					userInfo.put("email", user.getEmail());
					userInfo.put("photo", user.getPhoto());
					userInfo.put("token",null);
					List<Map> loggedUser = new ArrayList<>();
					loggedUser.add(userInfo);
					
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",loggedUser);
					logger.info("************************ Login ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				}
		 }
		 else
		 {
			 //throw ecxeptions bad request
			 List<User> loggedUser = null ;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",loggedUser);
			 logger.info("************************ Login ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			 
			 
		 }
//		 String hashedPassword = userServiceImpl.getMd5(password);
//		 User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
		// TODO Auto-generated method stub
//		return user;
	
	}
	
	

}
