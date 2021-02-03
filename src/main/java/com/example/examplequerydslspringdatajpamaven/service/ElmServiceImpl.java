package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.examplequerydslspringdatajpamaven.entity.CompanyElmData;
import com.example.examplequerydslspringdatajpamaven.entity.CompanyElmDataUpdate;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceElmData;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceElmDataUpdate;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.VehiclePlate;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverElmDataGregorian;
import com.example.examplequerydslspringdatajpamaven.entity.DriverElmDataHijri;
import com.example.examplequerydslspringdatajpamaven.entity.DriverElmDataUpdate;
import com.example.examplequerydslspringdatajpamaven.entity.ElmReturn;
import com.example.examplequerydslspringdatajpamaven.entity.ExpiredVehicles;
import com.example.examplequerydslspringdatajpamaven.entity.IndividualGregorianElmData;
import com.example.examplequerydslspringdatajpamaven.entity.IndividualHijriElmData;
import com.example.examplequerydslspringdatajpamaven.entity.LastElmData;
import com.example.examplequerydslspringdatajpamaven.entity.LastPositionData;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLastLocations;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLiveLocation;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoElmLastLocationsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoElmLiveLocationRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoElmLogsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsElmRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to elm
 * @author fuinco
 *
 */
@Component
@Service
public class ElmServiceImpl extends RestServiceController implements ElmService{


	
	private static final Log logger = LogFactory.getLog(ElmServiceImpl.class);
	GetObjectResponse getObjectResponse;
	
	@Value("${elmCompanies}")
	private String elmCompanies;
	
	@Value("${elmLocations}")
	private String elmLocations;
	
	@Value("${elmVehicles}")
	private String elmVehicles;
	
	@Value("${elmDrivers}")
	private String elmDrivers;
	
	@Value("${middleWare}")
	private String middleWare;
	
	@Value("${elm}")
	private String elm;
	
	@Autowired
	MongoPositionsElmRepository mongoPositionsElmRepository;
	
	@Autowired
	MongoElmLiveLocationRepository mongoElmLiveLocationRepository;
	
	@Autowired
	MongoPositionRepo mongoPositionRepo;
	
	@Autowired
	private MongoElmLogsRepository elmLogsRepository;
	
	@Autowired
	UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	UserClientDriverRepository userClientDriverRepository;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MongoElmLastLocationsRepository elmLastLocationsRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private DriverServiceImpl driverServiceImpl;
	
	/**
	 * delete company from elm using id to get reference key
	 */
	@Override
	public ResponseEntity<?> companyDelete(String TOKEN, Long userId,Long loggedUserId) {
		logger.info("************************ companyDelete STARTED ***************************");


		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Company";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userRepository.findOne(userId);
		
        if(user == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(user.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		
       if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "loggedUserId is Required",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUserToCheck = userRepository.findOne(loggedUserId);
		
        if(loggedUserToCheck == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUserToCheck.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is deleted",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUserToCheck.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "deleteFromElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged user doesnot has permission to deleteFromElm",null);
				 logger.info("************************ companyDelete ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		if(user.getAccountType().equals(4)) {
			 Set<User> parentClients = user.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
				 logger.info("************************ companyDelete ENDED ***************************");
				 return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
					 break;
				 }
				 
				 boolean isParent = false; 

				 
				 if(parentClient.getId() == loggedUserId) {
					 isParent =true;
				 }
				 
				 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
				 
				 User parentCli = new User();
				 for(User object : parents) {
					 parentCli = object;
					 if(loggedUserId.equals(parentCli.getId())) {
						isParent =true;
						break;
					 }
					 
				 }
				 
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
				 
			 }
		}
		else {
			 boolean isParent = false; 

			if(loggedUserToCheck.getAccountType().equals(1)) {
				isParent =true;
			}
			 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
			 User parentClient = new User();
			 for(User object : parents) {
				 parentClient = object;
				 if(loggedUserId == parentClient.getId()) {
					isParent =true;
					break;
				 }
			 }
			 if(userId == loggedUserId) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You can't register your self in elm.",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
		}
		
		
		
		
         if(user.getReference_key() == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not connected to elm.",null);
			logger.info("************************ companyDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		

		  Map body = new HashMap();
		  Map bodyToMiddleWare = new HashMap();
		  
		  String url = elmCompanies+"/"+user.getReference_key();
		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");
		  		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);


		  ResponseEntity<ElmReturn> rateResponse;
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,userId,user.getName(),null,null,null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();

		  if(resp.containsKey("errorCode")) {
			  
			  user.setReject_reason(resp.get("errorMsg").toString());
			  userRepository.save(user);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ companyDelete ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  user.setReject_reason(null);
				  user.setReference_key(null);
					  
					  userRepository.save(user);
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					  logger.info("************************ companyDelete ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
					
			  }
			  else{
				  user.setReject_reason(resp.get("resultCode").toString());
					  userRepository.save(user);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
					  logger.info("************************ companyDelete ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ companyDelete ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
			

		  
	}



	/**
	 * register company in elm using id to get data
	 */
	@Override
	public ResponseEntity<?> companyRegistrtaion(String TOKEN, Long userId, Long loggedUserId) {
		// TODO Auto-generated method stub
		logger.info("************************ companyRegistrtaion STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Company";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userRepository.findOne(userId);
		
        if(user == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(user.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "loggedUserId is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUserToCheck = userRepository.findOne(loggedUserId);
		
        if(loggedUserToCheck == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUserToCheck.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUserToCheck.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged user doesnot has permission to connectToElm",null);
				 logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if(user.getAccountType().equals(4)) {
			 Set<User> parentClients = user.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
				 logger.info("************************ companyRegistrtaion ENDED ***************************");
				 return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
					 break;
				 }
				 
				 boolean isParent = false; 

				 
				 if(parentClient.getId() == loggedUserId) {
					 isParent =true;
				 }
				 
				 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
				 
				 User parentCli = new User();
				 for(User object : parents) {
					 parentCli = object;
					 if(loggedUserId == parentCli.getId()) {
						isParent =true;
						break;
					 }
					 
				 }
				 
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
				 
			 }
		}
		else {
			 boolean isParent = false; 

			if(loggedUserToCheck.getAccountType().equals(1)) {
				isParent =true;
			}
			 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
			 User parentClient = new User();
			 for(User object : parents) {
				 parentClient = object;
				 if(loggedUserId == parentClient.getId()) {
					isParent =true;
					break;
				 }
			 }
			 if(userId == loggedUserId) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You can't register your self in elm.",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
		}
		
		
		

		  Map bodyToMiddleWare = new HashMap();

		  bodyToMiddleWare.put("url",elmCompanies);
		  bodyToMiddleWare.put("methodType","POST");
		  
		  if(user.getIsCompany().equals(1)) {
			  CompanyElmData companyElmData = new CompanyElmData();

			  companyElmData.setCommercialRecordIssueDateHijri(user.getCommercial_reg());
			  companyElmData.setCommercialRecordNumber(user.getCommercial_num());
			  companyElmData.setManagerMobileNumber(user.getManager_mobile());
			  companyElmData.setManagerName(user.getManager_name());
			  companyElmData.setManagerPhoneNumber( user.getManager_phone());
		
			  companyElmData.setEmailAddress(user.getEmail());
			  companyElmData.setExtensionNumber(user.getPhone());
			  companyElmData.setIdentityNumber(user.getIdentity_num());
			  companyElmData.setPhoneNumber(user.getCompany_phone());
			  

			  bodyToMiddleWare.put("dataObject", companyElmData);

		  }
		  else {

			  if(user.getDateType().equals(1)) {
				  
				  IndividualHijriElmData  individualHijriElmData = new IndividualHijriElmData();
				  individualHijriElmData.setEmailAddress(user.getEmail());
				  individualHijriElmData.setExtensionNumber(user.getPhone());
				  individualHijriElmData.setIdentityNumber(user.getIdentity_num());
				  individualHijriElmData.setPhoneNumber(user.getCompany_phone());
				  individualHijriElmData.setDateOfBirthHijri(user.getDateOfBirth());
				  
				  
				  bodyToMiddleWare.put("dataObject", individualHijriElmData);


			  }
			  else {
				  IndividualGregorianElmData  individualGregorianElmData = new IndividualGregorianElmData();
				  
				  individualGregorianElmData.setEmailAddress(user.getEmail());
				  individualGregorianElmData.setExtensionNumber(user.getPhone());
				  individualGregorianElmData.setIdentityNumber(user.getIdentity_num());
				  individualGregorianElmData.setPhoneNumber(user.getCompany_phone());
				  individualGregorianElmData.setDateOfBirthGregorian(user.getDateOfBirth());
				 
				  
				  bodyToMiddleWare.put("dataObject", individualGregorianElmData);


			  }

		  }
		  

		  
		  requet = bodyToMiddleWare;
		  		  
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);
		  ResponseEntity<ElmReturn> rateResponse ;
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		 try {
			 rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	     } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		 
		 
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,userId,user.getName(),null,null,null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  
		  data.add(elmReturn);
		 
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();

		  if(resp.containsKey("errorCode")) {
			  
			  user.setReject_reason(resp.get("errorMsg").toString());
			  userRepository.save(user);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
					Map res = (Map) resp.get("result");	
					user.setReject_reason(null);
					  user.setReference_key(res.get("referenceKey").toString());
					  userRepository.save(user);
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					  logger.info("************************ companyRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
					
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
					Map res = (Map) resp.get("result");	
					user.setReject_reason(null);
					  user.setReference_key(res.get("referenceKey").toString());
					  userRepository.save(user);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
					  logger.info("************************ companyRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  user.setReject_reason(resp.get("resultMsg").toString());
				  userRepository.save(user);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultMsg").toString(),data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
			
	}


	/**
	 * update company in elm using  data in body (identityNumber,commercialRecordNumber,managerPhoneNumber,managerName,managerMobileNumber)
	 */
	@Override
	public ResponseEntity<?> companyUpdate(String TOKEN,Map<String, String> dataObject,Long userId,Long loggedUserId) {
		
		logger.info("************************ companyUpdate STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Company";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userRepository.findOne(userId);
		
        if(user == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(user.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		
       if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "loggedUserId is Required",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUserToCheck = userRepository.findOne(loggedUserId);
		
        if(loggedUserToCheck == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUserToCheck.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is deleted",null);
			logger.info("************************ companyUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUserToCheck.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "updateInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged user doesnot has permission to updateInElm",null);
				 logger.info("************************ companyUpdate ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if(user.getAccountType().equals(4)) {
			 Set<User> parentClients = user.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
				 logger.info("************************ companyUpdate ENDED ***************************");
				 return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
					 break;
				 }
				 
				 boolean isParent = false; 

				 
				 if(parentClient.getId() == loggedUserId) {
					 isParent =true;
				 }
				 
				 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
				 
				 User parentCli = new User();
				 for(User object : parents) {
					 parentCli = object;
					 if(loggedUserId == parentCli.getId()) {
						isParent =true;
						break;
					 }
					 
				 }
				 
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
				 
			 }
		}
		else {
			 boolean isParent = false; 

			if(loggedUserToCheck.getAccountType().equals(1)) {
				isParent =true;
			}
			 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
			 User parentClient = new User();
			 for(User object : parents) {
				 parentClient = object;
				 if(loggedUserId == parentClient.getId()) {
					isParent =true;
					break;
				 }
			 }
			 if(userId == loggedUserId) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You can't register your self in elm.",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
		}
		
		
		
		  Map bodyToMiddleWare = new HashMap();
	
		  if(user.getIsCompany().equals(1)) {

			  
			 if(!dataObject.containsKey("identityNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "identityNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(!dataObject.containsKey("commercialRecordNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "commercialRecordNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(!dataObject.containsKey("managerName")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "managerName shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(!dataObject.containsKey("managerPhoneNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "managerPhoneNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(!dataObject.containsKey("managerMobileNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "managerMobileNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }

			 CompanyElmDataUpdate companyElmDataUpdate = new CompanyElmDataUpdate();
			 
			 companyElmDataUpdate.setCommercialRecordNumber(dataObject.get("commercialRecordNumber"));
			 companyElmDataUpdate.setIdentityNumber(dataObject.get("identityNumber"));
			 companyElmDataUpdate.setManagerMobileNumber(dataObject.get("managerMobileNumber"));
			 companyElmDataUpdate.setManagerName(dataObject.get("managerName"));
			 companyElmDataUpdate.setManagerPhoneNumber(dataObject.get("managerPhoneNumber"));
			 
			  String url = elmCompanies+"/contact-info";
					  
			  bodyToMiddleWare.put("dataObject", companyElmDataUpdate);
			  bodyToMiddleWare.put("url",url);
			  bodyToMiddleWare.put("methodType","PATCH");
			  
			  requet = bodyToMiddleWare; 
			  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

				SSLContext sslContext = null;
				try {
					sslContext = org.apache.http.ssl.SSLContexts.custom()
					        .loadTrustMaterial(null, acceptingTrustStrategy)
					        .build();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

				CloseableHttpClient httpClient = HttpClients.custom()
				        .setSSLSocketFactory(csf)
				        .build();

				HttpComponentsClientHttpRequestFactory requestFactory =
				        new HttpComponentsClientHttpRequestFactory();

				requestFactory.setHttpClient(httpClient);

				RestTemplate restTemplate = new RestTemplate(requestFactory);
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

			  ResponseEntity<ElmReturn> rateResponse;
			  List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
		     } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  ElmReturn elmReturn = rateResponse.getBody();

			  response.put("body", elmReturn.getBody());
			  response.put("statusCode", elmReturn.getStatusCode());
			  response.put("message", elmReturn.getMessage());


			  MongoElmLogs elmLogs = new MongoElmLogs(null,userId,user.getName(),null,null,null,null,time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			  
			  data.add(elmReturn);

			 
			  
			  Map resp = new HashMap();
			  resp = elmReturn.getBody();
			  
			  if(resp.containsKey("errorCode")) {
				  
				  user.setReject_reason(resp.get("errorMsg").toString());
				  userRepository.save(user);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
				  logger.info("************************ companyUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else if(resp.containsKey("resultCode")) {
				  if(resp.get("success").equals(true)) {
					  user.setReject_reason(null);
						  
					  user.setIdentity_num(dataObject.get("identityNumber").toString());
					  user.setCommercial_num(dataObject.get("commercialRecordNumber").toString());
					  user.setManager_name(dataObject.get("managerName").toString());
					  user.setManager_phone(dataObject.get("managerPhoneNumber").toString());
					  user.setManager_mobile(dataObject.get("managerMobileNumber").toString());

						  
						  
						  userRepository.save(user);
						  
						  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
						  logger.info("************************ companyUpdate ENDED ***************************");
						  return  ResponseEntity.ok().body(getObjectResponse);
						
				  }
				  else {
					  
					  user.setReject_reason(resp.get("resultCode").toString());
						  userRepository.save(user);

						  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
						  logger.info("************************ companyUpdate ENDED ***************************");
						  return  ResponseEntity.ok().body(getObjectResponse);
				  }
				  
				 
			  }
			  else {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
				logger.info("************************ companyUpdate ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);	
			  }
			  
		  }
		  else {
			  getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This account is individual and companies only can update",null);
			  logger.info("************************ companyUpdate ENDED ***************************");
			  return ResponseEntity.status(404).body(getObjectResponse);
		  }
		
	}

	/**
	 * update device using data in body (sequenceNumber,imeiNumber)
	 */
	@Override
	public ResponseEntity<?> deviceUpdate(String TOKEN, Map<String, String> dataObject, Long deviceId,Long userId) {
		logger.info("************************ deviceUpdate STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateRegUpdate = null;
		try {
			dateRegUpdate = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "updateInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to updateInElm",null);
				 logger.info("************************ deviceUpdate ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
       if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		
       if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceUpdate ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		 
		     if(!dataObject.containsKey("sequenceNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "sequenceNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(!dataObject.containsKey("imeiNumber")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "imeiNumber shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }

			  Map bodyToMiddleWare = new HashMap();
			  
			  DeviceElmDataUpdate deviceElmDataUpdate = new DeviceElmDataUpdate();
			  
			  deviceElmDataUpdate.setSequenceNumber(dataObject.get("sequenceNumber"));
			  deviceElmDataUpdate.setImeiNumber(dataObject.get("imeiNumber"));
			  
			  String url = elmVehicles+"/imei";
			  
			  bodyToMiddleWare.put("dataObject", deviceElmDataUpdate);
			  bodyToMiddleWare.put("url",url);
			  bodyToMiddleWare.put("methodType","PUT");
			  
			  requet = bodyToMiddleWare;
			  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

				SSLContext sslContext = null;
				try {
					sslContext = org.apache.http.ssl.SSLContexts.custom()
					        .loadTrustMaterial(null, acceptingTrustStrategy)
					        .build();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

				CloseableHttpClient httpClient = HttpClients.custom()
				        .setSSLSocketFactory(csf)
				        .build();

				HttpComponentsClientHttpRequestFactory requestFactory =
				        new HttpComponentsClientHttpRequestFactory();

				requestFactory.setHttpClient(httpClient);

				RestTemplate restTemplate = new RestTemplate(requestFactory);
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);



			  ResponseEntity<ElmReturn> rateResponse;
			  
				 List<ElmReturn> data = new ArrayList<ElmReturn>();

				  try {
					  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
		          } catch (Exception e) {
			        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
					logger.info("************************ companyRegistrtaion ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
			     }

				 
				 if(rateResponse.getStatusCode().OK == null) {
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
					  logger.info("************************ companyRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
				  }
			  
			  
			  
			  ElmReturn elmReturn = rateResponse.getBody();

			  response.put("body", elmReturn.getBody());
			  response.put("statusCode", elmReturn.getStatusCode());
			  response.put("message", elmReturn.getMessage());


			  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			  
			  data.add(elmReturn);

			 
			  Map resp = new HashMap();
			  resp = elmReturn.getBody();
			 			  
	          if(resp.containsKey("errorCode")) {
				  
				  device.setReject_reason(resp.get("errorMsg").toString());
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
				  logger.info("************************ deviceUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else if(resp.containsKey("resultCode")) {
				  if(resp.get("success").equals(true)) {
					  device.setReject_reason(null);
					  device.setUniqueid(dataObject.get("imeiNumber"));
					  device.setSequence_number(dataObject.get("sequenceNumber"));
					  device.setUpdate_date_in_elm(dateRegUpdate);
					  device.setExpired(0);
					  
					  deviceRepository.save(device);
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					  logger.info("************************ deviceUpdate ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
					
				  }
				  else{
						device.setReject_reason(resp.get("resultCode").toString());
						deviceRepository.save(device);

						  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
						  logger.info("************************ deviceUpdate ENDED ***************************");
						  return  ResponseEntity.ok().body(getObjectResponse);
				 
				  }
				 
			  }
			  else {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
				logger.info("************************ deviceUpdate ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);	
			  }
	}
	



	/**
	 * delete device from elm by id to get reference key
	 */
	@Override
	public ResponseEntity<?> deviceDelete(String TOKEN, Long deviceId,Long userId) {

		logger.info("************************ deviceDelete STARTED ***************************");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateRegDelete = null;
		try {
			dateRegDelete = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "deleteFromElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to deleteFromElm",null);
				 logger.info("************************ deviceDelete ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
        if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		
        if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 
		 
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceDelete ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
				 
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		 
		 if(parent.getReference_key() == null) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This parent is not connected to elm.",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
	 
		 if(device.getReference_key() == null) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This device is not connected to elm.",null);
			logger.info("************************ deviceDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 
		  String url = elm+"/operationCompany/"+parent.getReference_key()+"/vehicle/"+device.getReference_key();

		  Map bodyToMiddleWare = new HashMap();
		  
		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");
		 		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);


		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  
		  data.add(elmReturn);

		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
        if(resp.containsKey("errorCode")) {
			  
			  device.setReject_reason(resp.get("errorMsg").toString());
			  deviceRepository.save(device);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceDelete ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  
				  device.setReject_reason(null);
				  device.setReference_key(null);
				  device.setDelete_from_elm_date(dateRegDelete);
				  device.setUpdate_date_in_elm(dateRegDelete);
				  device.setExpired(1);
				  
				  
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceDelete ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else {
					device.setReject_reason(resp.get("resultCode").toString());
					deviceRepository.save(device);

				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceDelete ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceDelete ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}

	/**
	 * register device in elm using id to get data 
	 */
	@Override
	public ResponseEntity<?> deviceRegistrtaion(String TOKEN, Long deviceId, Long userId) {
		logger.info("************************ deviceUpdate STARTED ***************************");


		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		
        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ deviceRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
        if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		
        if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =new User();

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceRegistrtaion ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
				 
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		 
		 

		  Map bodyToMiddleWare = new HashMap();

		  DeviceElmData deviceElmData = new DeviceElmData();
		  VehiclePlate vehiclePlate = new VehiclePlate();

		  vehiclePlate.setNumber(device.getPlate_num());
		  vehiclePlate.setRightLetter(device.getRight_letter());
		  vehiclePlate.setMiddleLetter(device.getMiddle_letter());
		  vehiclePlate.setLeftLetter(device.getLeft_letter());
		  
		  deviceElmData.setImeiNumber(device.getUniqueid());
		  deviceElmData.setPlateType(device.getPlate_type());
		  deviceElmData.setSequenceNumber(device.getSequence_number());
		  deviceElmData.setVehiclePlate(vehiclePlate);
		  

		  
		  String url = elmCompanies+"/"+parent.getReference_key()+"/vehicles";


		  bodyToMiddleWare.put("dataObject", deviceElmData);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","POST");

		  
		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
 
		 List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
          if(resp.containsKey("errorCode")) {
			  
			  device.setReject_reason(resp.get("errorMsg").toString());
			  deviceRepository.save(device);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  Map res = (Map) resp.get("result");	
				  
				  device.setReject_reason(null);
				  device.setReference_key(res.get("referenceKey").toString());
				  device.setRegestration_to_elm_date(dateReg);
				  device.setUpdate_date_in_elm(dateReg);
				  device.setExpired(0);
				 
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
				  Map res = (Map) resp.get("result");	
					
					device.setReject_reason(null);
					device.setReference_key(res.get("referenceKey").toString());
					device.setRegestration_to_elm_date(dateReg);
					device.setUpdate_date_in_elm(dateReg);
					device.setExpired(0);
					
					deviceRepository.save(device);

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
					logger.info("************************ deviceRegistrtaion ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  device.setReject_reason(resp.get("resultCode").toString());
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}

	/**
	 * register driver in elm using id to get data
	 */
	@Override
	public ResponseEntity<?> driverRegistrtaion(String TOKEN, Long driverId,Long userId) {
		logger.info("************************ driverRegistrtaion STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Driver";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ getAllUses ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
        if(driverId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Driver Id is Required",null);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Driver driver = driverRepository.findOne(driverId);
		
        if(driver == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not found",null);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(driver.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is deleted",null);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = driver.getUserDriver();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not have parent company",null);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = driverServiceImpl.checkIfParent( driver ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
				 
			 
		 }
		 else {
			 isParent = driverServiceImpl.checkIfParent( driver ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 

		  Map bodyToMiddleWare = new HashMap();

		  String url = elmCompanies+"/"+parent.getReference_key()+"/drivers";

		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","POST");

		  
		  if(driver.getDate_type().equals(1)) {
			  
			  DriverElmDataHijri driverElmDataHijri = new DriverElmDataHijri();
			  
			  driverElmDataHijri.setDateOfBirthHijri(driver.getBirth_date());
			  driverElmDataHijri.setIdentityNumber(driver.getUniqueid());
			  driverElmDataHijri.setMobileNumber(driver.getMobile_num());
			  
			  bodyToMiddleWare.put("dataObject", driverElmDataHijri);
			  

		  }
		  else {
			  DriverElmDataGregorian driverElmDataGregorian = new DriverElmDataGregorian();
			  

			  driverElmDataGregorian.setDateOfBirthGregorian(driver.getBirth_date());
			  driverElmDataGregorian.setIdentityNumber(driver.getUniqueid());
			  driverElmDataGregorian.setMobileNumber(driver.getMobile_num());
			  
			  bodyToMiddleWare.put("dataObject", driverElmDataGregorian);

		  }
		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

	
		  
		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),driverId,driver.getName(),null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();          

          if(resp.containsKey("errorCode")) {
			  
			  driver.setReject_reason(resp.get("errorMsg").toString());
			  driverRepository.save(driver);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ driverRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  Map res = (Map) resp.get("result");	
				  driver.setReject_reason(null);
				  driver.setReference_key(res.get("referenceKey").toString());
				  driverRepository.save(driver);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ driverRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
					Map res = (Map) resp.get("result");	
					driver.setReject_reason(null);
					driver.setReference_key(res.get("referenceKey").toString());
					driverRepository.save(driver);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
					  logger.info("************************ driverRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  driver.setReject_reason(resp.get("resultCode").toString());
				  driverRepository.save(driver);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ driverRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}

	/**
	 * Verify data of device in elm by id to get reference key
	 */
	@Override
	public ResponseEntity<?> deviceInquery(String TOKEN, Long deviceId,Long userId) {
		logger.info("************************ deviceInquery STARTED ***************************");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Verify Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		 if(userId == 0) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "verifyInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to verifyInElm",null);
				 logger.info("************************ deviceInquery ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
        if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		
        if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This vehicle is deleted from elm",null);
			logger.info("************************ deviceInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		 Set<User> parentClients = device.getUser();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceInquery ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
				if(CheckData.isEmpty()) {
						isParent = false;
				}
				else {
						isParent = true;
				}
				 
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		  
		 String url = elmVehicles+"?sequenceNumber="+device.getSequence_number();
		 
		 Map bodyToMiddleWare = new HashMap();



		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","GET");
		 
		  requet = bodyToMiddleWare;
		  
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();   		 

		  
          if(resp.containsKey("operatingCompanies")) {
        	  
        	  List<Map> jsonArr = new ArrayList<Map>();
        	  jsonArr = (List<Map>) resp.get("operatingCompanies");
              Map obj = jsonArr.get(0);	

              if(obj.get("isVehicleValid").equals(true)) {
            	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"This vehicle is valid",data);
    			  logger.info("************************ deviceInquery ENDED ***************************");
    			  return  ResponseEntity.ok().body(getObjectResponse);
              }
              else {
            	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),obj.get("vehicleRejectionReason").toString(),data);
    			  logger.info("************************ deviceInquery ENDED ***************************");
    			  return  ResponseEntity.ok().body(getObjectResponse);
              }

              
			  
		  }
          else if(resp.containsKey("resultCode")) {
        	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
			  logger.info("************************ deviceInquery ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
          }
          else {
  			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
  			logger.info("************************ deviceInquery ENDED ***************************");
  			return  ResponseEntity.ok().body(getObjectResponse);	
  		  }
          

	}

	/**
	 * Verify company in elm with id to get reference key
	 */
	@Override
	public ResponseEntity<?> companyInquery(String TOKEN, Long userId,Long loggedUserId) {
		logger.info("************************ companyInquery STARTED ***************************");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Verify Company";
		Map requet = new HashMap();
		Map response = new HashMap();

		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userRepository.findOne(userId);
		
        if(user == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(user.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This company is deleted from elm",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "loggedUserId is Required",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUserToCheck = userRepository.findOne(loggedUserId);
		
        if(loggedUserToCheck == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUserToCheck.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is deleted",null);
			logger.info("************************ companyInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUserToCheck.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "verifyInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged user doesnot has permission to verifyInElm",null);
				 logger.info("************************ companyInquery ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if(user.getAccountType().equals(4)) {
			 Set<User> parentClients = user.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
				 logger.info("************************ companyInquery ENDED ***************************");
				 return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
					 break;
				 }
				 
				 boolean isParent = false; 

				 
				 if(parentClient.getId() == loggedUserId) {
					 isParent =true;
				 }
				 
				 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
				 
				 User parentCli = new User();
				 for(User object : parents) {
					 parentCli = object;
					 if(loggedUserId == parentCli.getId()) {
						isParent =true;
						break;
					 }
					 
				 }
				 
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
				 
			 }
		}
		else {
			 boolean isParent = false; 

			if(loggedUserToCheck.getAccountType().equals(1)) {
				isParent =true;
			}
			 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
			 User parentClient = new User();
			 for(User object : parents) {
				 parentClient = object;
				 if(loggedUserId == parentClient.getId()) {
					isParent =true;
					break;
				 }
			 }
			 if(userId == loggedUserId) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You can't register your self in elm.",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
		}
		

		
		 String url = "";

		if(user.getIsCompany().equals(1)) {

			url = elmCompanies+"?identityNumber="+user.getIdentity_num()
			+"&commercialRecordNumber="+user.getCommercial_num();		

			 
			  
		}
		else {
			url = elmCompanies+"?identityNumber="+user.getIdentity_num();
			
		}
			
		 
		  Map bodyToMiddleWare = new HashMap();



		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","GET");

		 
		  requet = bodyToMiddleWare;
		  
		  
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);


		  ResponseEntity<ElmReturn> rateResponse ;
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		 try {
			 rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	     } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  

		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,userId,user.getName(),null,null,null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ companyInquery ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();   
		  

         if(resp.containsKey("resultCode")) {
       	    getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
			logger.info("************************ companyInquery ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
         }
         else {
        	 if(resp.get("isValid").equals(true)) {
           	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"This Company is vaild",data);
   			  logger.info("************************ companyInquery ENDED ***************************");
   			  return  ResponseEntity.ok().body(getObjectResponse);
             }
             else {
           	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"This Company is not vaild",data);
   			  logger.info("************************ companyInquery ENDED ***************************");
   			  return  ResponseEntity.ok().body(getObjectResponse);
             }
 		 }
	}

	


	/**
	 * delete driver from elm using id to get reference key
	 */
	@Override
	public ResponseEntity<?> driverDelete(String TOKEN, Long driverId,Long userId) {
		logger.info("************************ driverDelete STARTED ***************************");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Driver";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "deleteFromElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to deleteFromElm",null);
				 logger.info("************************ getAllUses ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
        if(driverId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Driver Id is Required",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Driver driver = driverRepository.findOne(driverId);
		
        if(driver == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not found",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(driver.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is deleted",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = driver.getUserDriver();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not have parent company",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ driverDelete ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = driverServiceImpl.checkIfParent( driver ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
				
			 List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
				if(CheckData.isEmpty()) {
						isParent = false;
				}
				else {
						isParent = true;
				}
			 
		 }
		 else {
			 isParent = driverServiceImpl.checkIfParent( driver ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		
		 
		 if(parent.getReference_key() == null) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This parent is not connected to elm.",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
	 
		 if(driver.getReference_key() == null) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This driver is not connected to elm.",null);
			logger.info("************************ driverDelete ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		  String url = elm+"/operationCompany/"+parent.getReference_key()+"/driver/"+driver.getReference_key();

		  Map bodyToMiddleWare = new HashMap();
		  
		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");

		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),driverId,driver.getName(),null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		  
		 
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();          

         if(resp.containsKey("errorCode")) {
			  
			  driver.setReject_reason(resp.get("errorMsg").toString());
			  driverRepository.save(driver);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ driverRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  driver.setReject_reason(null);
				  driver.setReference_key(null);
				  driverRepository.save(driver);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ driverRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else{
					driver.setReject_reason(resp.get("resultCode").toString());
					driverRepository.save(driver);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
					  logger.info("************************ driverRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ driverRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }

	}

	
	/**
	 * Verify driver in elm using id to get reference key
	 */
	@Override
	public ResponseEntity<?> driverInquery(String TOKEN, Long driverId, Long userId) {
		logger.info("************************ driverInquery STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Verify Driver";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		
		 if(userId == 0) {
				
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "verifyInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to verifyInElm",null);
				 logger.info("************************ driverInquery ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
        if(driverId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Driver Id is Required",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Driver driver = driverRepository.findOne(driverId);
		
        if(driver == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not found",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(driver.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This driver is deleted from elm",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Set<User> parentClients = driver.getUserDriver();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not have parent company",null);
			logger.info("************************ driverInquery ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ driverInquery ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = driverServiceImpl.checkIfParent( driver ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 
			 List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
				if(CheckData.isEmpty()) {
						isParent = false;
				}
				else {
						isParent = true;
				}
				 
			 
		 }
		 else {
			 isParent = driverServiceImpl.checkIfParent( driver ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		
		 Map body = new HashMap();

		  
		 String url = elmDrivers +"?identityNumber="+driver.getUniqueid();
		 
		 Map bodyToMiddleWare = new HashMap();



		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","GET");
		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),driverId,driver.getName(),null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		  Map resp = new HashMap();
		  resp = elmReturn.getBody();  
		  
       	  
         if(resp.containsKey("operatingCompanies")) {
       	  
        	 List<Map> jsonArr = new ArrayList<Map>();
       	     jsonArr = (List<Map>) resp.get("operatingCompanies");
             Map obj = jsonArr.get(0);	
             
             if(obj.get("isDriverValid").equals(true)) {
           	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"This Driver is valid",data);
   			  logger.info("************************ driverInquery ENDED ***************************");
   			  return  ResponseEntity.ok().body(getObjectResponse);
             }
             else {
           	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),obj.get("driverRejectionReason").toString(),data);
   			  logger.info("************************ driverInquery ENDED ***************************");
   			  return  ResponseEntity.ok().body(getObjectResponse);
             }

             
			  
		  }
         else if(resp.containsKey("resultCode")) {
       	  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
			  logger.info("************************ driverInquery ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
         }
         else {
 			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
 			logger.info("************************ driverInquery ENDED ***************************");
 			return  ResponseEntity.ok().body(getObjectResponse);	
 		  }
		
		 
	}

	
	/**
	 * update data of driver in elm by data in body (identityNumber,mobileNumber,email)
	 */
	@Override
	public ResponseEntity<?> driverUpdate(String TOKEN, Map<String, String> dataObject, Long driverId, Long userId) {
		logger.info("************************ driverUpdate STARTED ***************************");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Driver";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		
       if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "updateInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to updateInElm",null);
				 logger.info("************************ driverUpdate ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
        if(driverId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Driver Id is Required",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Driver driver = driverRepository.findOne(driverId);
		
        if(driver == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not found",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(driver.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This driver is deleted from elm",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		Set<User> parentClients = driver.getUserDriver();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not have parent company",null);
			logger.info("************************ driverUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 
		 
		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ driverUpdate ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = driverServiceImpl.checkIfParent( driver ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
			 
		 }
		 else {
			 isParent = driverServiceImpl.checkIfParent( driver ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 

		 String url = elmCompanies +"/"+parent.getReference_key()+"/drivers";
		 
		  Map bodyToMiddleWare = new HashMap();



	    if(!dataObject.containsKey("identityNumber")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "identityNumber shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("mobileNumber")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "mobileNumber shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("email")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "email shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }

		 DriverElmDataUpdate driverElmDataUpdate = new DriverElmDataUpdate();
		  
		 driverElmDataUpdate.setEmail(dataObject.get("email"));
		 driverElmDataUpdate.setIdentityNumber(dataObject.get("identityNumber"));
		 driverElmDataUpdate.setMobileNumber(dataObject.get("mobileNumber"));
		 
		 

		  bodyToMiddleWare.put("dataObject", driverElmDataUpdate);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","PUT");

		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),driverId,driver.getName(),null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  
		  data.add(elmReturn);

		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();          

          if(resp.containsKey("errorCode")) {
			  
			  driver.setReject_reason(resp.get("errorMsg").toString());
			  driverRepository.save(driver);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ driverUpdate ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {

				  driver.setUniqueid(dataObject.get("identityNumber"));
				  driver.setMobile_num(dataObject.get("mobileNumber"));
				  driver.setEmail(dataObject.get("email"));
				  
				  driver.setReject_reason(null);
				  
				  driverRepository.save(driver);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ driverUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else{
					driver.setReject_reason(resp.get("resultCode").toString());
					driverRepository.save(driver);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
					  logger.info("************************ driverUpdate ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ driverUpdate ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
          
	}
	
	/**
	 * send locations of devices from mongo collection tc_positions_elm
	 */
	@Override
	public ResponseEntity<?> lastLocations() {
		
		// TODO Auto-generated method stub
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Location";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		logger.info("************************ lastLocations STARTED ***************************");

		List<Map> dataArray = new ArrayList<>();
		List<String> ids = new ArrayList<>();

		List<MongoElmLastLocations> elm_connection_logs = new ArrayList<MongoElmLastLocations>();
		List<MongoElmLiveLocation> positions = mongoElmLiveLocationRepository.findByIdsIn(new PageRequest(0, 1000));
		
		for(MongoElmLiveLocation position:positions) {
			Map record = new HashMap();
			
			record.put("referenceKey", position.getReferenceKey());
			record.put("driverReferenceKey", position.getDriverReferenceKey());
			record.put("latitude", position.getLatitude());
			record.put("longitude", position.getLongitude());
			record.put("velocity", position.getVelocity() * (1.852) );
			record.put("weight", position.getWeight());
			record.put("locationTime", position.getLocationTime());
			record.put("vehicleStatus", position.getVehicleStatus());
			record.put("address", position.getAddress());
			record.put("roleCode", position.getRoleCode());

			ids.add(position.get_id().toString());
			
			dataArray.add(record);
			
		}
		List<ElmReturn> data = new ArrayList<ElmReturn>();
		if(dataArray.size() > 0) {
	    	 Map body = new HashMap();
	
			 body.put("vehicleLocations", dataArray);
			
			  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	
				SSLContext sslContext = null;
				try {
					sslContext = org.apache.http.ssl.SSLContexts.custom()
					        .loadTrustMaterial(null, acceptingTrustStrategy)
					        .build();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	
				CloseableHttpClient httpClient = HttpClients.custom()
				        .setSSLSocketFactory(csf)
				        .build();
	
				HttpComponentsClientHttpRequestFactory requestFactory =
				        new HttpComponentsClientHttpRequestFactory();
	
				requestFactory.setHttpClient(httpClient);
	
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
				  Map bodyToMiddleWare = new HashMap();
	
	
				  
	
				  bodyToMiddleWare.put("dataObject", body);
				  bodyToMiddleWare.put("url",elmLocations);
				  bodyToMiddleWare.put("methodType","POST");
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);
	
			  ResponseEntity<ElmReturn> rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
				  
			  ElmReturn elmReturn = rateResponse.getBody();
	
			  data.add(elmReturn);
	
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  Map resp = new HashMap();
			  resp = elmReturn.getBody();  
			  
			  requet = bodyToMiddleWare;
			  ElmReturn elmReturnd = rateResponse.getBody();
	
			  response.put("body", elmReturnd.getBody());
			  response.put("statusCode", elmReturnd.getStatusCode());
			  response.put("message", elmReturnd.getMessage());
			  
			  MongoElmLogs elmLogs = new MongoElmLogs(null,null,null,null,null,null,null,time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			  mongoElmLiveLocationRepository.deleteByIdIn(ids);
	
		}
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data,dataArray.size());
		  logger.info("************************ lastLocations ENDED ***************************");
		  return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get logs of driver company or device by ids
	 */
	@Override
	public ResponseEntity<?> getLogs(String TOKEN, Long loggedUserId,Long userId,Long driverId,Long deviceId, int offset, String search) {
		logger.info("************************ getLogs STARTED ***************************");
		
		List<MongoElmLogs> logs = new ArrayList<MongoElmLogs>();
		 Integer size = 0;

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",logs);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId != 0) {
			
			User loggeduser = userService.findById(loggedUserId);
			if(loggeduser == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged User is not Found",logs);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(loggeduser.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(loggedUserId, "ELM", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged User doesnot has permission to get Elm list",logs);
						 logger.info("************************ getLogs ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggeduser.getDelete_date() == null) {
					
					if(userId == 0 && driverId == 0 && deviceId == 0) {
						
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "userId ,driverId ,deviceId isn't valid should be at least one sent.",logs);
						logger.info("************************ getLogs ENDED ***************************");
						return  ResponseEntity.status(400).body(getObjectResponse);
					}
					else {

						if(userId != 0) {
							
							User user = userRepository.findOne(userId);
							
					        if(user == null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
								logger.info("************************ getLogs ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}


							if(user.getDelete_date() != null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
								logger.info("************************ getLogs ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}
							
							if(user.getAccountType().equals(4)) {
								 Set<User> parentClients = user.getUsersOfUser();
								 if(parentClients.isEmpty()) {
									
									 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
									 logger.info("************************ companyRegistrtaion ENDED ***************************");
									 return  ResponseEntity.status(404).body(getObjectResponse);
								 }else {
									 User parentClient = new User() ;
									 for(User object : parentClients) {
										 parentClient = object;
										 break;
									 }
									 
									 boolean isParent = false; 
									 
									 if(userId == loggedUserId) {
											isParent =true;

									 }
									 
									 if(parentClient.getId() == loggedUserId) {
										 isParent =true;
									 }
									 
									 
									 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
									 
									 User parentCli = new User();
									 for(User object : parents) {
										 parentCli = object;
										 if(loggedUserId == parentCli.getId()) {
											isParent =true;
											break;
										 }
										 
									 }
									 
									 if(isParent == false) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to get logs.",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									 }
									 
									 
								 }
							}
							else {
								 boolean isParent = false; 

								if(loggeduser.getAccountType().equals(1)) {
									isParent =true;
								}
								 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
								 User parentClient = new User();
								 for(User object : parents) {
									 parentClient = object;
									 if(loggedUserId == parentClient.getId()) {
										isParent =true;
										break;
									 }
								 }
								 if(loggedUserId.toString().equals(userId.toString())) {
										isParent =true;

								 }
								 if(isParent == false) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to get in elm Logs.",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								 }
							}
							
							 offset = offset / 10;
							 logs = elmLogsRepository.findByUserId(userId, new PageRequest(offset, 10));
							 size = elmLogsRepository.countByUserId(userId);
							
						}
						 
						if(driverId != 0) {
							
							Driver driver = driverRepository.findOne(driverId);

							if(driver == null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not found",null);
								logger.info("************************ driverRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}


							if(driver.getDelete_date() != null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is deleted",null);
								logger.info("************************ driverRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}
							
							 Set<User> parentClients = driver.getUserDriver();
							 User parent =null;

							 for(User object : parentClients) {
								 parent = object;
								 break;
							 }
							 if(parent == null) {
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Driver is not have parent company",null);
								logger.info("************************ driverRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
							 boolean isParent = false;

							 if(loggeduser.getAccountType().equals(4)) {
								 User parentUser =new User();

								 Set<User> parentClient = loggeduser.getUsersOfUser();
								 if(parentClient.isEmpty()) {
									    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
										logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
										return ResponseEntity.badRequest().body(getObjectResponse); 
								 }else {
									 for(User object : parentClient) {
										 parentUser = object ;
										 break;
									 }
									 isParent = driverServiceImpl.checkIfParent( driver ,  parentUser);
									 if(parent.getId() == loggedUserId) {
										 isParent=true;
									 }


								 }
								 
								 List<Long> CheckData = userClientDriverRepository.getDriver(loggedUserId,driverId);
									if(CheckData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
									 
								 
							 }
							 else {
								 isParent = driverServiceImpl.checkIfParent( driver ,  loggeduser);
								 if(loggeduser.getAccountType().equals(1)) {
									 isParent=true;
								 }
								 if(parent.getId() == loggedUserId) {
									 isParent=true;
								 }
							 }
							 
							 if(isParent == false) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							 }
							 offset = offset / 10;
							 logs = elmLogsRepository.findByDriverId(driverId, new PageRequest(offset, 10));
							 size = elmLogsRepository.countByDriverId(driverId); 
							
							
						}
						if(deviceId != 0) {
							
							Device device = deviceRepository.findOne(deviceId);
							
					        if(device == null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
								logger.info("************************ deviceRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}


							if(device.getDelete_date() != null) {
								
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
								logger.info("************************ deviceRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							}
							
							 Set<User> parentClients = device.getUser();
							 User parent =new User();

							 for(User object : parentClients) {
								 parent = object;
								 break;
							 }
							 if(parent == null) {
								getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
								logger.info("************************ deviceRegistrtaion ENDED ***************************");
								return ResponseEntity.status(404).body(getObjectResponse);
							 }

							 boolean isParent = false;

							 if(loggeduser.getAccountType().equals(4)) {
								 User parentUser =new User();

								 Set<User> parentClient = loggeduser.getUsersOfUser();
								 if(parentClient.isEmpty()) {
									    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
										logger.info("************************ deviceRegistrtaion ENDED ***************************");
										return ResponseEntity.badRequest().body(getObjectResponse); 
								 }else {
									 for(User object : parentClient) {
										 parentUser = object ;
										 break;
									 }
									 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
									 if(parent.getId() == loggedUserId) {
										 isParent=true;
									 }


								 }
								 List<Long> CheckData = userClientDeviceRepository.getDevice(loggedUserId,deviceId);
									if(CheckData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								 
							 }
							 else {
								 isParent = deviceServiceImpl.checkIfParent(device ,  loggeduser);
								 if(loggeduser.getAccountType().equals(1)) {
									 isParent=true;
								 }
								 if(parent.getId() == loggedUserId) {
									 isParent=true;
								 }
							 }
							 
							 if(isParent == false) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							 }
							 offset = offset / 10;
							 logs = elmLogsRepository.findByDeviceId(deviceId, new PageRequest(offset, 10));
							 size = elmLogsRepository.countByDeviceId(deviceId); 
							
							 
							
							
						}
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",logs,size);
						logger.info("************************ getLogs ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					}
					

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged User is not Found",logs);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",logs);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}




	/**
	 * set expired true if device is extend date without renew
	 */
	@Override
	public ResponseEntity<?> getExpiredVehicles() {
		// TODO Auto-generated method stub
		

		List<ExpiredVehicles> devicList = new ArrayList<>();

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		String currentDate=formatter.format(date);
		String type = "Expired Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		Date dateCheck = null;
		try {
			dateCheck = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		devicList = deviceRepository.getAllExpiredIds(dateCheck);
		
		if(devicList.size()>0) {
			for(ExpiredVehicles ExpiredDevice:devicList) {

				Device device = deviceRepository.findOne(ExpiredDevice.getDeviceId());
				
				device.setExpired(1);
				deviceRepository.save(device);

				if(ExpiredDevice.getUser_referenceKey() != null && ExpiredDevice.getUser_referenceKey() != "" 
						&& ExpiredDevice.getVehicle_referenceKey() != null && ExpiredDevice.getVehicle_referenceKey() != ""){
					
					
				  String url = elm+"/operationCompany/"+ExpiredDevice.getUser_referenceKey()+"/vehicle/"+ExpiredDevice.getVehicle_referenceKey();

				  Map bodyToMiddleWare = new HashMap();
				  
				  bodyToMiddleWare.put("dataObject", null);
				  bodyToMiddleWare.put("url",url);
				  bodyToMiddleWare.put("methodType","DELETE");
				 		  
				  requet = bodyToMiddleWare;
				  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

					SSLContext sslContext = null;
					try {
						sslContext = org.apache.http.ssl.SSLContexts.custom()
						        .loadTrustMaterial(null, acceptingTrustStrategy)
						        .build();
					} catch (KeyManagementException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (KeyStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

					CloseableHttpClient httpClient = HttpClients.custom()
					        .setSSLSocketFactory(csf)
					        .build();

					HttpComponentsClientHttpRequestFactory requestFactory =
					        new HttpComponentsClientHttpRequestFactory();

					requestFactory.setHttpClient(httpClient);

					RestTemplate restTemplate = new RestTemplate(requestFactory);
					
					
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
					  
				  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

				  ResponseEntity<ElmReturn> rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);

				  
				  ElmReturn elmReturn = rateResponse.getBody();

				  
				  response.put("body", elmReturn.getBody());
				  response.put("statusCode", elmReturn.getStatusCode());
				  response.put("message", elmReturn.getMessage());


				  MongoElmLogs elmLogs = new MongoElmLogs(null,ExpiredDevice.getUserId(),ExpiredDevice.getUserName(),null,null,ExpiredDevice.getDeviceId(),ExpiredDevice.getDeviceName(),currentDate,type,requet,response);
				  elmLogsRepository.save(elmLogs);
				  
				  
				  
				  Map resp = new HashMap();
				  resp = elmReturn.getBody();
				  
		        if(resp.containsKey("errorCode")) {
					  
					  device.setReject_reason(resp.get("errorMsg").toString());
					  deviceRepository.save(device);

				  }
				  else if(resp.containsKey("resultCode")) {
					  if(resp.get("success").equals(true)) {
						  
						  device.setReject_reason(null);
						  device.setReference_key(null);
						  device.setDelete_from_elm_date(dateCheck);
						  device.setUpdate_date_in_elm(dateCheck);
						  device.setDelete_from_elm(resp.get("resultCode").toString());
						  
						  deviceRepository.save(device);
						  
						
					  }
					  else {
							device.setReject_reason(resp.get("resultCode").toString());
							deviceRepository.save(device);

					  }
					  
					 
				  }
					
					
				}
			}
		}
		
		
		
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}




	/**
	 * URL elm that get summary of data using sequenceNumber
	 */
	@Override
	public ResponseEntity<?> checkBySequenceNumber(String sequenceNumber) {
		// TODO Auto-generated method stub
		
		
		if(sequenceNumber.equals("")) {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Sequence Number Selected",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);
		
		if(device == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Data For This Sequence Number In DB",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		
		 List<Map<Object,Object>> result = new ArrayList<Map<Object,Object>>();
		 
		 
	     Map dataFinal= new HashMap();

	     Map deviceData= new HashMap();
	     deviceData.put("Name", device.getName());
	     deviceData.put("Unique Id", device.getUniqueid());
	     deviceData.put("Sequence Number", device.getSequence_number());
	     
	     if(device.getExpired() == 1) {
		     deviceData.put("Expired", "Expired");

	     }
	     else {
		     deviceData.put("Expired", "Not Expired");
	     }
	     
	     if(device.getReference_key() != null) {
		     deviceData.put("Reference Key", device.getReference_key());

	     }
	     else {
		     deviceData.put("Reference Key", "No Reference Key");
	     }
	     
	     List<Map> calibrationData=new ArrayList<Map>();
	     deviceData.put("Calibration Data", calibrationData);

		 if(device.getCalibrationData() != null) {

			 String str = device.getCalibrationData().toString(); 
		     String arrOfStr[] = str.split(" "); 
		     for (String a : arrOfStr) {
		    	 JSONObject obj =new JSONObject(a);
				 Map list   = new HashMap<>();
				 list.put("s1",obj.get("s1"));
				 list.put("s2",obj.get("s2"));
				 list.put("w",obj.get("w"));
				 calibrationData.add(list);

		     }
		     deviceData.put("Calibration Data", calibrationData);
			 
		 }
		 Map lineData   = new HashMap<>();
	     deviceData.put("Line Data", lineData);
	     
	     if(device.getLineData() != null) {
	    	 JSONObject obj= new JSONObject(device.getLineData().toString());
			 lineData.put("slope",obj.get("slope"));
			 lineData.put("factor",obj.get("factor"));

		     deviceData.put("Line Data", lineData);

	     }
	     
	     dataFinal.put("deviceData", deviceData);

	     
	     LastPositionData position = new LastPositionData();

	     List<LastPositionData> positionsZeroSpeed = new ArrayList<LastPositionData>();
	     List<LastPositionData> positionsGreaterZeroSpeed = new ArrayList<LastPositionData>();
	     List<LastElmData> positionsZeroVelocity = new ArrayList<LastElmData>();
	     List<LastElmData> positionsGreaterZeroVelocity = new ArrayList<LastElmData>();

		 position = mongoPositionRepo.getLastPosition(device.getId());
		 	     
	     positionsZeroSpeed = mongoPositionRepo.getLastPositionSpeedZero(device.getId());
	     positionsGreaterZeroSpeed = mongoPositionRepo.getLastPositionGreaterSpeedZero(device.getId());

	     if(device.getReference_key() != null && position.getPositionId() != null) {
	    	 
	    	 positionsZeroVelocity = mongoPositionRepo.getLastPositionVelocityZero(device.getReference_key());
		     positionsGreaterZeroVelocity = mongoPositionRepo.getLastPositionGreaterVelocityZero(device.getReference_key());
 
	     }
	     
	    
	     
	     dataFinal.put("positionData", position);
	     
	     dataFinal.put("positionZeroSpeed", positionsZeroSpeed);
	     dataFinal.put("positionsGreaterZeroSpeed", positionsGreaterZeroSpeed);
	     
	     dataFinal.put("positionsZeroVelocity", positionsZeroVelocity);
	     dataFinal.put("positionsGreaterZeroVelocity", positionsGreaterZeroVelocity);

	     result.add(dataFinal);

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		return  ResponseEntity.ok().body(getObjectResponse);
		

	}




	/**
	 * delete vehicle using data in body (companyReferenceKey,deviceReferenceKey)
	 */
	@Override
	public ResponseEntity<?> deleteVehicleFromElm(String TOKEN, Long deviceId, Long userId, Map<String, String> dataObject) {
		// TODO Auto-generated method stub
		
		logger.info("************************ deviceDelete STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Vehicle From Inquery";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateRegDelete = null;
		try {
			dateRegDelete = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "deleteFromElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to deleteFromElm",null);
				 logger.info("************************ deviceUpdate ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
       if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		
       if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDelete_date() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceUpdate ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
			 List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
			if(CheckData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 
		 if(!dataObject.containsKey("companyReferenceKey")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "companyReferenceKey shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("deviceReferenceKey")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "deviceReferenceKey shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 
		 
		  String url = elm+"/operationCompany/"+dataObject.get("companyReferenceKey")+"/vehicle/"+dataObject.get("deviceReferenceKey");


		  Map bodyToMiddleWare = new HashMap();
		  
		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");
		 		  
		  
		  requet = bodyToMiddleWare;
		  TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
				        .loadTrustMaterial(null, acceptingTrustStrategy)
				        .build();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();

			HttpComponentsClientHttpRequestFactory requestFactory =
			        new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);


		  ResponseEntity<ElmReturn> rateResponse;
		  
			 List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  
		  data.add(elmReturn);

		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
         if(resp.containsKey("errorCode")) {
			  
			  device.setReject_reason(resp.get("errorMsg").toString());
			  deviceRepository.save(device);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceDelete ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  
				  device.setReject_reason(null);
				  device.setReference_key(null);
				  device.setDelete_from_elm_date(dateRegDelete);
				  device.setUpdate_date_in_elm(dateRegDelete);
				  device.setExpired(1);
				  
				  
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceDelete ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else {
					device.setReject_reason(resp.get("resultCode").toString());
					deviceRepository.save(device);

				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceDelete ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceDelete ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}




	
	



}
