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
import java.util.Set;
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
import com.example.examplequerydslspringdatajpamaven.entity.BillingsList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Token;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
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
	 DeviceRepository deviceRepository;
	 
	 
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
					if(user.getAccountType() != 1) {
						if(user.getRoleId() == null ) {
							//userInfo.put("userRole", null);
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No roles assigned to this user yet",null);
							logger.info("************************ Login ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}else {
							UserRole userRole = userRoleService.findById(user.getRoleId());
							userInfo.put("userRole", userRole);
						}
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
	
	@Override
	public ResponseEntity<?> getBilling(String TOKEN,Long loggedId,Long userId,String start,String end,int offset,String search) {
		logger.info("************************ getBilling STARTED ***************************");

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		

		if(loggedId == 0) {
       	     getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedId id is required",null);
			 logger.info("************************ getBilling END ***************************");
       	     return  ResponseEntity.badRequest().body(getObjectResponse);

       }
       User loggedUser = userServiceImpl.findById(loggedId);
       if(loggedUser == null) {
       	    getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedId is not Found",null);
			logger.info("************************ getBilling END ***************************");
       	    return  ResponseEntity.status(404).body(getObjectResponse);
       }
		
       


		User parentChilds = new User() ;
 		boolean isParent =false;
 		
 		userServiceImpl.resetChildernArray();
	    List<User>childs = userServiceImpl.getAllChildernOfUser(loggedId);
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().equals(userId)) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to get billings as you are not parent of this userId ",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
       
       
       
		if(userId == 0) {
       	     getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId id is required",null);
			 logger.info("************************ getBilling END ***************************");
       	     return  ResponseEntity.badRequest().body(getObjectResponse);

       }
       User userBillings = userServiceImpl.findById(userId);
       if(userBillings == null) {
       	    getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "userId is not Found",null);
			logger.info("************************ getBilling END ***************************");
       	    return  ResponseEntity.status(404).body(getObjectResponse);
       }
       
       if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getBilling END ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

	   }
       else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			Date dateFrom;
			Date dateTo;
			try {
				dateFrom = inputFormat.parse(start);
				dateTo = inputFormat.parse(end);
				
				start = outputFormat.format(dateFrom);
				end = outputFormat.format(dateTo);
				
				Date today=new Date();

				if(dateFrom.getTime() > dateTo.getTime()) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
					logger.info("************************ getBilling END ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
					logger.info("************************ getBilling END ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				


			} catch (ParseException e) {
				// TODO Auto-generated catch block
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
				logger.info("************************ getBilling END ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);

			}
		

	  }
       List<BillingsList> billings= deviceRepository.billingInfo(userId, start, end,offset,search); 
       Integer size= deviceRepository.getBillingInfotSize(userId, start, end); 
       getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",billings,size);
	   logger.info("************************ getBilling END ***************************");
       return  ResponseEntity.ok().body(getObjectResponse);

	}

	
	
	
	

}
