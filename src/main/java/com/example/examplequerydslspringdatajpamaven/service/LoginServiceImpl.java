package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

/**
 * services functionality related to login
 * @author fuinco
 *
 */
@Component
@Service
public class LoginServiceImpl extends RestServiceController implements LoginService  {
	
	private static final Log logger = LogFactory.getLog(LoginServiceImpl.class);

	
	
	 @Autowired
	 UserRepository userRepository;
	
	 @Autowired
	 DeviceRepository deviceRepository;
	 
	 
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	JWKValidator jwkValidator;
	
	@Autowired
	UserRoleService userRoleService;
	
	GetObjectResponse getObjectResponse;
	
	
	/**
	 * login using email and password return data of user with token
	 */
	@Override
	public ResponseEntity<?> login(String authorization) {
		
		logger.info("************************ Login STARTED ***************************");
		 if(authorization != "" && authorization.toLowerCase().startsWith("basic")) {
			 

				String base64Credentials = authorization.substring("Basic".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);

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
					if(user.getAccountType() != 1) {
						if(user.getRoleId() == null ) {

							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No roles assigned to this user yet",null);
							logger.info("************************ Login ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}else {
							UserRole userRole = userRoleService.findById(user.getRoleId());
							userInfo.put("userRole", userRole);
						}
					}
					if(user.getAccountType() != 1 && user.getAccountType() != 2 ) {
						
						

						if(user.getExp_date() == null || user.getCreate_date() == null) {
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please",null);
							logger.info("************************ Login ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}
						else {
							Date date2 = null;
							Date date1 = new Date();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

							try {
								date2 = format.parse(user.getExp_date());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							long diff = date2.getTime() - date1.getTime();
							long days = 0;
							days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
							userInfo.put("leftDays" , days);

							if(days <= 0) {
								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please",null);
								logger.info("************************ Login ENDED ***************************");
								return  ResponseEntity.status(404).body(getObjectResponse);
							}

						}

					}
					
					
					List<Map> loggedUser = new ArrayList<>();
					loggedUser.add(userInfo);
					SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
			    	TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh");
			         
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

			 List<User> loggedUser = null ;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",loggedUser);
			 logger.info("************************ Login ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			 
			 
		 }
	
	}


	/**
	 * logout and remove token of user 
	 */
	@Override
	public ResponseEntity<?> logout(String token) {

		
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
