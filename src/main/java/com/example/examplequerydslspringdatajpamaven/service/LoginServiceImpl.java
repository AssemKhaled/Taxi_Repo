package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.Token;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

@Component
public class LoginServiceImpl extends RestServiceController implements LoginService  {
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);

	
	
	@Autowired
	 UserRepository userRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
//	JWKValidator jwkValidator;
	@Autowired
	JWKValidator jwkValidator;
	
	@Autowired
	UserRoleService userRoleService;
	
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
					
					String loggedEmail= user.getEmail();
					
					
					String token =  jwkValidator.createJWT(loggedEmail, null);
					Map userInfo = new HashMap();
					userInfo.put("userId", user.getId());
					userInfo.put("name" ,user.getName());
					userInfo.put("email", user.getEmail());
					userInfo.put("photo", user.getPhoto());
					userInfo.put("accountType", user.getAccountType());
					userInfo.put("token",token);
					if(user.getRoleId() == null) {
						userInfo.put("userRole", null);
					}else {
						UserRole userRole = userRoleService.findById(user.getRoleId());
						userInfo.put("userRole", userRole);
					}
					
					List<Map> loggedUser = new ArrayList<>();
					loggedUser.add(userInfo);
					SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
			    	TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh"); //Target timezone
			         
			        Date currentDate = new Date();
			        String requestLastUpdate = FORMATTER.format(currentDate);
				    TokenSecurity.getInstance().addActiveUser(user.getId(),token,requestLastUpdate); 
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
	
	}


	@Override
	public ResponseEntity<?> logout(String token) {
		// TODO Auto-generated method stubif
		
		if(super.checkActive(token)!= null)
		{
			return super.checkActive(token);
		}
		if(token == "") {
			List<User> loggedUser = null ;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id required",loggedUser);
			 logger.info("************************ Login ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			  Boolean removed = TokenSecurity.getInstance().removeActiveUser(token);
			  if(removed) {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "loggedOut successfully",loggedUser);
					 logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.ok().body(getObjectResponse);
			  }else {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this logged token is not Found",loggedUser);
					 logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.status(404).body(getObjectResponse);
			  }
			
			 
		}
		
	}


	
	
	
	

}
