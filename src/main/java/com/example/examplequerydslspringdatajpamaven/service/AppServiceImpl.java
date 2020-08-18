package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.StopReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.example.examplequerydslspringdatajpamaven.entity.TripReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.PositionSqlRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

@Component
public class AppServiceImpl extends RestServiceController implements AppService{

	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);
	private GetObjectResponse getObjectResponse;
	
	 @Value("${stopsUrl}")
	 private String stopsUrl;
	 
	 @Value("${tripsUrl}")
	 private String tripsUrl;
	 
	 @Value("${eventsUrl}")
	 private String eventsUrl;
	 
	 @Value("${summaryUrl}")
	 private String summaryUrl;
	
	@Autowired
	private MongoPositionsRepository mongoPositionsRepository;
	
	@Autowired
	private MongoPositionRepo mongoPositionsRepo;
	 
	 @Autowired
	 private PositionSqlRepository positionSqlRepository;
	 
	 @Autowired
	 private GeofenceRepository geofenceRepository;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @Autowired
	 private GeofenceServiceImpl geofenceServiceImpl;

	 @Autowired
	 private DriverServiceImpl driverServiceImpl;

	 @Autowired
	 private DeviceRepository deviceRepository;
	 
	 @Autowired
	 private DriverRepository driverRepository;
	 
	 @Autowired
	 private GroupsServiceImpl groupsServiceImpl;
	 
	@Autowired
	private GroupRepository groupRepository;	
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private JWKValidator jwkValidator;
	
	@Autowired
	private UserRoleService userRoleService;

	@Override
	public ResponseEntity<?> loginApp(String authtorization) {
		
		logger.info("************************ Login STARTED ***************************");
		if(authtorization != "" && authtorization.toLowerCase().startsWith("basic")) {
			
			 
			String base64Credentials = authtorization.substring("Basic".length()).trim();
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

	@Override
	public ResponseEntity<?> logoutApp(String TOKEN) {
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(TOKEN == "") {
			List<User> loggedUser = null ;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id required",loggedUser);
			logger.info("************************ Login ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			  Boolean removed = TokenSecurity.getInstance().removeActiveUser(TOKEN);
			  if(removed) {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "loggedOut successfully",loggedUser);
				  logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.ok().body(getObjectResponse);
			  }else {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged token is not Found",loggedUser);
					 logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.status(404).body(getObjectResponse);
			  }
			
			 
		}
				
	}

	@Override
	public ResponseEntity<?> getAllDeviceLiveDataMapApp(String TOKEN, Long userId) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 List<CustomDeviceLiveData> allDevicesLiveData=	null;
		    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",allDevicesLiveData);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
	    User loggedUser = userServiceImpl.findById(userId);
	    if( loggedUser == null) {
	    	 List<CustomDeviceLiveData> allDevicesLiveData=	null;
			    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found ",allDevicesLiveData);
				
				logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
	    }
	    userServiceImpl.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
	    if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;

				 for(User object : parentClients) {
					 parentClient = object;
					 usersIds.add(parentClient.getId());

				 }
			 }
		 }
	    else {
	    	List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
	    }
	     
		List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesLiveDataMap(usersIds);
		
		if(allDevicesLiveData.size() > 0) {
			for(int i=0;i<allDevicesLiveData.size();i++) {
				long minutes = 0;

				if(allDevicesLiveData.get(i).getLastUpdate() != null) {
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					Date now = new Date();
					String strDate = formatter.format(now);
					try {
						
						Date dateLast = formatter.parse(allDevicesLiveData.get(i).getLastUpdate());
						Date dateNow = formatter.parse(strDate);
						
						minutes = getDateDiff (dateLast, dateNow, TimeUnit.MINUTES);  
						
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(minutes < 3) {
	                	allDevicesLiveData.get(i).setVehicleStatus("online");
					}
					if(minutes > 8) {
	                	allDevicesLiveData.get(i).setVehicleStatus("unknown");
					}
					if(minutes < 8 && minutes > 3) {
	                	allDevicesLiveData.get(i).setVehicleStatus("offline");
					}
				}
				else {
               	allDevicesLiveData.get(i).setVehicleStatus("offline");

				}

				
				
				if(allDevicesLiveData.get(i).getPositionId() != null) {

					MongoPositions mongoPosition = mongoPositionsRepository.findById(allDevicesLiveData.get(i).getPositionId());
					JSONObject obj = new JSONObject(mongoPosition.getAttributes());
					allDevicesLiveData.get(i).setLatitude(mongoPosition.getLatitude());
					allDevicesLiveData.get(i).setLongitude(mongoPosition.getLongitude());
					allDevicesLiveData.get(i).setAttributes(mongoPosition.getAttributes());
					allDevicesLiveData.get(i).setAddress(mongoPosition.getAddress());
					allDevicesLiveData.get(i).setSpeed(mongoPosition.getSpeed());
					if(mongoPosition.getValid() >= 1) {
						allDevicesLiveData.get(i).setValid(true);

					}
					else {
						allDevicesLiveData.get(i).setValid(false);

					}
					
					if(minutes > 8) {
                   	allDevicesLiveData.get(i).setStatus("In active");
						
					}
					else {
						if(obj.has("ignition")) {

							if(obj.get("ignition").equals(true)) {
								if(obj.has("motion")) {

				                    if(obj.get("motion").equals(false)) {
				                    	allDevicesLiveData.get(i).setStatus("Idle");
									}
				                    if(obj.get("motion").equals(true)) {
				                    	allDevicesLiveData.get(i).setStatus("Running");
									}
								}
							}
		                    if(obj.get("ignition").equals(false)) {
		                    	allDevicesLiveData.get(i).setStatus("Stopped");

							}
						}
						
					}
					
					if(obj.has("power")) {
						allDevicesLiveData.get(i).setPower(obj.getDouble("power"));

					}
					if(obj.has("operator")) {
						allDevicesLiveData.get(i).setOperator(obj.getDouble("operator"));

					}
					if(obj.has("ignition")) {
						allDevicesLiveData.get(i).setIgnition(obj.getBoolean("ignition"));

					}
				}
				else {
               	allDevicesLiveData.get(i).setStatus("No data");
				}
				
			}
		}
		
	    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData);
		
		logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);


	}
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) 
    {
        long diffInMillies = date2.getTime() - date1.getTime();
         
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

	@Override
	public ResponseEntity<?> vehicleInfoApp(String TOKEN, Long deviceId, Long userId) {
		
		logger.info("************************ vehicleInfo STARTED ***************************");

		List<CustomDeviceList> vehicleInfo= new ArrayList<CustomDeviceList>();
	    
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",vehicleInfo);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!deviceId.equals(0) && !userId.equals(0)) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",vehicleInfo);
				 return  ResponseEntity.status(404).body(getObjectResponse);
			}
			Device device = deviceServiceImpl.findById(deviceId);
			if(device != null ) {
				if(device.getDeleteDate()==null) {
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ editDevice ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> deviceParent = device.getUser();
								if(deviceParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ editDevice ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									
									for(User deviceUser : deviceParent) {
										if(deviceUser.getId().equals(parent.getId())) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this device ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					vehicleInfo = deviceRepository.vehicleInfoData(deviceId);
				    Map<Object, Object> sensorList =new HashMap<Object, Object>();

					List<Map> data = new ArrayList<>();
					if(vehicleInfo.size()>0) {
						if(vehicleInfo.get(0).getPositionId() != null) {
							MongoPositions mongoPosition = mongoPositionsRepository.findById(vehicleInfo.get(0).getPositionId());
		                    vehicleInfo.get(0).setLatitude(mongoPosition.getLatitude());
		                    vehicleInfo.get(0).setLongitude(mongoPosition.getLongitude());
		                    vehicleInfo.get(0).setSpeed(mongoPosition.getSpeed());
		                    vehicleInfo.get(0).setAddress(mongoPosition.getAddress());
		                    vehicleInfo.get(0).setAttributes(mongoPosition.getAttributes());
						}
	                   

	                    
						
					    Map<Object, Object> attrbuitesList =new HashMap<Object, Object>();
						if(vehicleInfo.get(0).getAttributes() != null) {
							JSONObject obj = new JSONObject(vehicleInfo.get(0).getAttributes().toString());
							
							if(obj.has("power")) {
								if(obj.get("power") != null) {
									if(obj.get("power") != "") {
										double p = Double.valueOf(obj.get("power").toString());
										double round = Math.round(p * 100.0 / 100.0);
										obj.put("power",String.valueOf(round));


									}
									else {
										obj.put("power", "0");
									}
								}
								else {
									obj.put("power", "0");
								}
							}
							if(obj.has("battery")) {
								if(obj.get("battery") != null) {
									if(obj.get("battery") != "") {
										double p = Double.valueOf(obj.get("battery").toString());
										double round = Math.round(p * 100.0 / 100.0);
										obj.put("battery",String.valueOf(round));


									}
									else {
										obj.put("battery", "0");
									}
								}
								else {
									obj.put("battery", "0");
								}
							}
							
							
							Iterator<String> keys = obj.keys();

							while(keys.hasNext()) {
							    String key = keys.next();
							    attrbuitesList.put(key , obj.get(key).toString());
							    vehicleInfo.get(0).setPositionAttributes(attrbuitesList);
							}
							 vehicleInfo.get(0).setAttributes(null);
						}
						if(device.getSensorSettings() != null) {
							JSONObject obj = new JSONObject(device.getSensorSettings().toString());
							Iterator<String> keys = obj.keys();
							while(keys.hasNext()) {
							    String key = keys.next();
							    sensorList.put(key , obj.get(key).toString());
							}
						}
						    
					}
				    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",vehicleInfo,sensorList);
					logger.info("************************ vehicleInfo ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				}
				else {
				    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",vehicleInfo);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}
			else {
			    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",vehicleInfo);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
		    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device Id and loggedUser Id are  required",vehicleInfo);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> getDevicesListApp(String TOKEN, Long userId, int offset, String search) {
		// TODO Auto-generated method stub
 
		logger.info("************************ getDevicesListApp STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN) != null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId.equals(0)) {
			 List<CustomDeviceList> devices= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
			 logger.info("************************ getDevicesListApp ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			 List<CustomDeviceList> devices= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",devices);
			 logger.info("************************ getDevicesListApp ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get devices list",null);
				 logger.info("************************ getDevicesListApp ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		userServiceImpl.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getDevicesListApp ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 usersIds.add(parentClient.getId());

			 }
		 }
		 else {
			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
		 }
		 
		
		 
		 List<CustomDeviceList> devices= deviceRepository.getDevicesListApp(usersIds,offset,search);
		 Integer size=  deviceRepository.getDevicesListSize(usersIds);
		 
		 if(devices.size() > 0) {
			for(int i=0;i<devices.size();i++) {
				
				long minutes = 0;

				if(devices.get(i).getLastUpdate() != null) {
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					Date now = new Date();
					String strDate = formatter.format(now);
					try {
						
						Date dateLast = formatter.parse(devices.get(i).getLastUpdate());
						Date dateNow = formatter.parse(strDate);
						
						minutes = getDateDiff (dateLast, dateNow, TimeUnit.MINUTES);  
						
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(minutes < 3) {
						devices.get(i).setStatus("online");
					}
					if(minutes > 8) {
						devices.get(i).setStatus("unknown");
					}
					if(minutes < 8 && minutes > 3) {
						devices.get(i).setStatus("offline");
					}
				}
				else {
					devices.get(i).setStatus("offline");

				}
				
				
				if(devices.get(i).getPositionId() != null) {
					
					MongoPositions mongoPosition = mongoPositionsRepository.findById(devices.get(i).getPositionId());
					
					devices.get(i).setAttributes(mongoPosition.getAttributes());
					devices.get(i).setSpeed(mongoPosition.getSpeed());
					devices.get(i).setLatitude(mongoPosition.getLatitude());
					devices.get(i).setLongitude(mongoPosition.getLongitude());
					devices.get(i).setAddress(mongoPosition.getAddress());
					
					JSONObject obj = new JSONObject(devices.get(i).getAttributes().toString());

					
					
					if(obj.has("power")) {
						devices.get(i).setPower(obj.getDouble("power"));

					}
					
					if(obj.has("ignition")) {
						devices.get(i).setIgnition(obj.getBoolean("ignition"));

					}
					
					if(obj.has("sat")) {
						devices.get(i).setSat(obj.getInt("sat"));

					}
				}
					
					
			}
		}
		 
		 
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices,size);
		 logger.info("************************ getDevicesListApp ENDED ***************************");
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getAllDriversApp(String TOKEN, Long id, int offset, String search) {
		logger.info("************************ getAllDriversApp STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
	    List<CustomDriverList> customDrivers = new ArrayList<CustomDriverList>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver list",null);
						 logger.info("************************ getAllDriversApp ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				   userServiceImpl.resetChildernArray();
					if(user.getAccountType().equals(4)) {
						Set<User>parentClients = user.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get drivers of this user",null);
							 logger.info("************************ getAllDriversApp ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object;
							}
							List<Long>usersIds= new ArrayList<>();
						    usersIds.add(parent.getId());
						     
							//drivers = driverRepository.getAllDrivers(usersIds,offset,search);
						    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);
						    Integer size= driverRepository.getAllDriversSize(usersIds);
							
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
							logger.info("************************ getAllDriversApp ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
						}
					}
					List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }
					//drivers = driverRepository.getAllDrivers(usersIds,offset,search);
				    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);

					Integer size= driverRepository.getAllDriversSize(usersIds);
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
					logger.info("************************ getAllDriversApp ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				
				
			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	}

	@Override
	public ResponseEntity<?> getStopsReportApp(String TOKEN, Long deviceId, Long groupId, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}

			
			 if(groupId != 0) {
			    	
			    	Group group= groupRepository.findOne(groupId);

					if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : groupParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							List<Long>allDevices= new ArrayList<>();
							if(group.getType().equals("driver")) {
								allDevices = groupRepository.getDevicesFromDriver(groupId);

							}
							else if(group.getType().equals("device")) {
								allDevices = groupRepository.getDevicesFromGroup(groupId);
								
							}
							else if(group.getType().equals("geofence")) {
								allDevices = groupRepository.getDevicesFromGeofence(groupId);

							}
							else {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
							
							String appendString="";
							if(deviceId !=0) {
								appendString = deviceId.toString();

							}
							if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  if(appendString != "") {
										  appendString +=","+allDevices.get(i);
									  }
									  else {
										  appendString +=allDevices.get(i);
									  }
								  }
							 }
							allDevices = new ArrayList<Long>();
					        String[] data = appendString.split(",");

					        for(String d:data) {
					        	
					        	allDevices.add(Long.parseLong(d));
					        }
							
							if(from.equals("0") || to.equals("0")) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
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
									dateFrom = inputFormat.parse(from);
									dateTo = inputFormat.parse(to);

									from = outputFormat.format(dateFrom);
									to = outputFormat.format(dateTo);
									
									Date today=new Date();

									if(dateFrom.getTime() > dateTo.getTime()) {
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);

								}
								
								String plainCreds = "admin@fuinco.com:admin";
								byte[] plainCredsBytes = plainCreds.getBytes();
								
								byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
								String base64Creds = new String(base64CredsBytes);

								HttpHeaders headers = new HttpHeaders();
								headers.add("Authorization", "Basic " + base64Creds);
								
								  String GET_URL = stopsUrl;
								  RestTemplate restTemplate = new RestTemplate();
								  restTemplate.getMessageConverters()
							        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

								  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
									        .queryParam("type", type)
									        .queryParam("from", from)
									        .queryParam("to", to)
									        .queryParam("page", page)
									        .queryParam("start", start)
									        .queryParam("limit",limit).build();
								  HttpEntity<String> request = new HttpEntity<String>(headers);
								  String URL = builder.toString();
								  if(allDevices.size()>0) {
									  for(int i=0;i<allDevices.size();i++) {
										  URL +="&deviceId="+allDevices.get(i);
									  }
								  }
								  ResponseEntity<List<StopReport>> rateResponse =
									        restTemplate.exchange(URL,
									                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
									            });
								  stopReport = rateResponse.getBody();
								  
								  
								  
								  if(stopReport.size()>0) {

									  for(StopReport stopReportOne : stopReport ) {
										  Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
										  Long timeDuration = (long) 0;
										  Long timeEngine= (long) 0;
										  String totalDuration = "00:00:00";
										  String totalEngineHours = "00:00:00";

										  
										  Set<Driver>  drivers = device.getDriver();
										  for(Driver driver : drivers ) {

											 stopReportOne.setDriverName(driver.getName());
											 stopReportOne.setDriverUniqueId(driver.getUniqueid());
										}
										  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

											  timeDuration = Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

			     							  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
											  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
											  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
											  
											  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);
											  stopReportOne.setDuration(totalDuration.toString());

										  }
										  
										  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

											  timeEngine = Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

			     							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
											  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
											  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
											  
											  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
											  stopReportOne.setEngineHours(totalEngineHours.toString());

										  }
										  
									  }
								  }
								  
								  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport,stopReport.size());
								  logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.ok().body(getObjectResponse);

							}
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			
			Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",stopReport);
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
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",stopReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);


					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = stopsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  stopReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();

					  ResponseEntity<List<StopReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
						            });
					  stopReport = rateResponse.getBody();
					  
					  
					  
					  if(stopReport.size()>0) {
						  Set<Driver>  drivers = device.getDriver();
						  Long timeDuration = (long) 0;
						  Long timeEngine= (long) 0;
						  String totalDuration = "00:00:00";
						  String totalEngineHours = "00:00:00";

						  for(StopReport stopReportOne : stopReport ) {
							  for(Driver driver : drivers ) {

								 stopReportOne.setDriverName(driver.getName());
								 stopReportOne.setDriverUniqueId(driver.getUniqueid());
							}
							  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

								  timeDuration = Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

     							  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
								  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
								  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
								  
								  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);
								  stopReportOne.setDuration(totalDuration.toString());

							  }
							  
							  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

								  timeEngine = Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

     							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
								  
								  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
								  stopReportOne.setEngineHours(totalEngineHours.toString());

							  }
						  }
					  }
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport,stopReport.size());
					  logger.info("************************ getStopsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);


					
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",stopReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> getTripsReportApp(String TOKEN, Long deviceId, Long groupId, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}

			
		    if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						String appendString="";
						if(deviceId !=0) {
							appendString = deviceId.toString();

						}
						if(allDevices.size()>0) {
							  for(int i=0;i<allDevices.size();i++) {
								  if(appendString != "") {
									  appendString +=","+allDevices.get(i);
								  }
								  else {
									  appendString +=allDevices.get(i);
								  }
							  }
						 }
						allDevices = new ArrayList<Long>();
				        String[] data = appendString.split(",");

				        for(String d:data) {
				        	
				        	allDevices.add(Long.parseLong(d));
				        }
						
						
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
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
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = tripsUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }
							  ResponseEntity<List<TripReport>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
								            });
							  tripReport = rateResponse.getBody();
							  if(tripReport.size()>0) {

								  for(TripReport tripReportOne : tripReport ) {
									  Double totalDistance = 0.0 ;
									  double roundOffDistance = 0.0;
									  double roundOffFuel = 0.0;
									  
									  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());
									  Set<Driver>  drivers = device.getDriver();
									  for(Driver driver : drivers ) {

										 tripReportOne.setDriverName(driver.getName());
										 tripReportOne.setDriverUniqueId(driver.getUniqueid());

										
										 
									}
									  if( device.getFuel() != null) {
											 
											Double litres=0.0;
											Double Fuel =0.0;
											Double distance=0.0;
											
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=Double.parseDouble(obj.get("fuelPerKM").toString());
											}

											distance = Double.parseDouble(tripReportOne.getDistance());
											if(distance > 0) {
												Fuel = (distance/100)*litres;
											}


											roundOffFuel = Math.round(Fuel * 100.0 / 100.0);

											tripReportOne.setSpentFuel(Double.toString(roundOffFuel));

										 }
									  
									  
									  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
										  Long time=(long) 0;

										  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

										  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
										  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
										  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
										  
										  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
										  tripReportOne.setDuration(totalHours);
									  }
									  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  tripReportOne.setDistance(Double.toString(roundOffDistance));


									  }
									  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed())  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


									  }
									  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed())  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


									  }
									  
									  

								  }
							  }

							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
							  logger.info("************************ getTripsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
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
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = tripsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  tripReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  ResponseEntity<List<TripReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
						            });
					  tripReport = rateResponse.getBody();
					  if(tripReport.size()>0) {

						  for(TripReport tripReportOne : tripReport ) {
							  Double totalDistance = 0.0 ;
							  double roundOffDistance = 0.0;
							  double roundOffFuel = 0.0;
							  
							  Set<Driver>  drivers = device.getDriver();
							  for(Driver driver : drivers ) {

								 tripReportOne.setDriverName(driver.getName());
								 tripReportOne.setDriverUniqueId(driver.getUniqueid());

								
								 
							}
							  if( device.getFuel() != null) {
									 
									Double litres=0.0;
									Double Fuel =0.0;
									Double distance=0.0;
									
									JSONObject obj = new JSONObject(device.getFuel());	
									if(obj.has("fuelPerKM")) {
										litres=Double.parseDouble(obj.get("fuelPerKM").toString());
									}

									distance = Double.parseDouble(tripReportOne.getDistance());
									if(distance > 0) {
										Fuel = (distance/100)*litres;
									}


									roundOffFuel = Math.round(Fuel * 100.0 / 100.0);

									tripReportOne.setSpentFuel(Double.toString(roundOffFuel));

								 }
							  
							  
							  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
								  Long time=(long) 0;

								  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

								  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
								  tripReportOne.setDuration(totalHours);
							  }
							  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  tripReportOne.setDistance(Double.toString(roundOffDistance));


							  }
							  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed())  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


							  }
							  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed())  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


							  }
							  
							  

						  }
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
					  logger.info("************************ getTripsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is Required",tripReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> getSummaryReportApp(String TOKEN, Long deviceId, Long groupId, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getEventsReport STARTED ***************************");

		List<SummaryReport> summaryReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",summaryReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",summaryReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}

			
            if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						String appendString="";
						if(deviceId !=0) {
							appendString = deviceId.toString();

						}
					   
						if(allDevices.size()>0) {
							  for(int i=0;i<allDevices.size();i++) {
								  if(appendString != "") {
									  appendString +=","+allDevices.get(i);
								  }
								  else {
									  appendString +=allDevices.get(i);
								  }
							  }
						 }
						allDevices = new ArrayList<Long>();
				        String[] dataa = appendString.split(",");

				        for(String d:dataa) {
				        	
				        	allDevices.add(Long.parseLong(d));
				        }
						
						
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
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
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = summaryUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }
							  ResponseEntity<List<SummaryReport>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
								            });
							  summaryReport = rateResponse.getBody();

							  if(summaryReport.size()>0) {
								  Double totalDistance = 0.0 ;
								  double roundOffDistance = 0.0;
								  double roundOffFuel = 0.0;
								  
								  for(SummaryReport summaryReportOne : summaryReport ) {
									  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
									  if(device != null) {
									  Set<Driver>  drivers = device.getDriver();
									  for(Driver driver : drivers ) {

										 summaryReportOne.setDriverName(driver.getName());
									  }
										 if(device.getFuel() != null) {
											 
											
											Double litres=0.0;
											Double Fuel =0.0;
											Double distance=0.0;
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=obj.getDouble("fuelPerKM");
											}

											distance = Double.parseDouble(summaryReportOne.getDistance());
											if(distance > 0) {
												Fuel = (distance/100)*litres;
											}


											roundOffFuel = Math.round(Fuel * 100.0 / 100.0);

											summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
											

										 }
									
								  }
									  if(summaryReportOne.getEngineHours() != null && summaryReportOne.getEngineHours() != "") {
										  Long time=(long) 0;

										  time = Math.abs( Long.parseLong(summaryReportOne.getEngineHours().toString()) );

										  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
										  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
										  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
										  
										  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
										  summaryReportOne.setEngineHours(totalHours);
									  }
									  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  summaryReportOne.setDistance(Double.toString(roundOffDistance));


									  }
									  if(summaryReportOne.getAverageSpeed() != null && summaryReportOne.getAverageSpeed() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getAverageSpeed())  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  summaryReportOne.setAverageSpeed(Double.toString(roundOffDistance));


									  }
									  if(summaryReportOne.getMaxSpeed() != null && summaryReportOne.getMaxSpeed() != "") {
										  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getMaxSpeed())  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
										  summaryReportOne.setMaxSpeed(Double.toString(roundOffDistance));


									  }
									  
								}
							  }
							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
							  logger.info("************************ getEventsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",summaryReport);
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
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",summaryReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = summaryUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//                    summaryReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  ResponseEntity<List<SummaryReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
						            });
					  summaryReport = rateResponse.getBody();
					  if(summaryReport.size()>0) {
						  Double totalDistance = 0.0 ;
						  double roundOffDistance = 0.0;
						  double roundOffFuel = 0.0;
						  
						  for(SummaryReport summaryReportOne : summaryReport ) {
							  if(device != null) {
							  Set<Driver>  drivers = device.getDriver();
							  for(Driver driver : drivers ) {

								 summaryReportOne.setDriverName(driver.getName());
							  }
								 if(device.getFuel() != null) {
									 
									
									Double litres=0.0;
									Double Fuel =0.0;
									Double distance=0.0;
									JSONObject obj = new JSONObject(device.getFuel());	
									if(obj.has("fuelPerKM")) {
										litres=obj.getDouble("fuelPerKM");
									}

									distance = Double.parseDouble(summaryReportOne.getDistance());
									if(distance > 0) {
										Fuel = (distance/100)*litres;
									}


									roundOffFuel = Math.round(Fuel * 100.0 / 100.0);

									summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
									

								 }
							
						  }
							  if(summaryReportOne.getEngineHours() != null && summaryReportOne.getEngineHours() != "") {
								  Long time=(long) 0;

								  time = Math.abs( Long.parseLong(summaryReportOne.getEngineHours().toString()) );

								  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
								  summaryReportOne.setEngineHours(totalHours);
							  }
							  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  summaryReportOne.setDistance(Double.toString(roundOffDistance));


							  }
							  if(summaryReportOne.getAverageSpeed() != null && summaryReportOne.getAverageSpeed() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getAverageSpeed())  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  summaryReportOne.setAverageSpeed(Double.toString(roundOffDistance));


							  }
							  if(summaryReportOne.getMaxSpeed() != null && summaryReportOne.getMaxSpeed() != "") {
								  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getMaxSpeed())  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
								  summaryReportOne.setMaxSpeed(Double.toString(roundOffDistance));


							  }
							  
						}
					  }
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
					  logger.info("************************ getEventsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",summaryReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",summaryReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> viewTripApp(String TOKEN, Long deviceId, String startTime , String endTime) {
		
		logger.info("************************ viewTripApp STARTED ***************************");

		List<TripPositions> positions = new ArrayList<TripPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positions);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

			    
		Device device = deviceServiceImpl.findById(deviceId);
		
		if(device != null) {
			positions = mongoPositionsRepo.getTripPositions(deviceId, startTime, endTime);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positions,positions.size());
			logger.info("************************ viewTripApp ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positions);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getGeoListApp(String TOKEN, Long id, int offset, String search) {
		
        logger.info("************************ getAllUserGeofences STARTED ***************************");
		
		List<Geofence> geofences = new ArrayList<Geofence>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					
				    userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							
							 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
							 logger.info("************************ getAllUserDevices ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						 }else {
							 User parentClient = new User() ;
							 for(User object : parentClients) {
								 parentClient = object;
							 }
							 List<Long>usersIds= new ArrayList<>();
							 usersIds.add(parentClient.getId());
							 geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
							 Integer size=geofenceRepository.getAllGeofencesSize(usersIds);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
							logger.info("************************ getAllUserGeofences ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						 }
					 }
				    List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }

					
					
				    geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
					Integer size=geofenceRepository.getAllGeofencesSize(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

		
	}

	@Override
	public ResponseEntity<?> getGeofenceByIdApp(String TOKEN, Long geofenceId, Long userId) {
		logger.info("************************ getGeofenceById STARTED ***************************");

		List<Geofence> geofences = new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
       	 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
       }
       User loggedUser = userServiceImpl.findById(userId);
       if(loggedUser == null) {
       	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",geofences);
			return  ResponseEntity.status(404).body(getObjectResponse);
       }
		if(!geofenceId.equals(0)) {
			
			Geofence geofence=geofenceRepository.findOne(geofenceId);

			if(geofence != null) {
				if(geofence.getDelete_date() == null) {
					boolean isParent = false;
					if(loggedUser.getAccountType().equals(4)) {
						Set<User> clientParents = loggedUser.getUsersOfUser();
						if(clientParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : clientParents) {
								parent = object ;
							}
							Set<User>geofneceParents = geofence.getUserGeofence();
							if(geofneceParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User parentObject : geofneceParents) {
									if(parentObject.getId().equals(parent.getId())) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!geofenceServiceImpl.checkIfParent(geofence , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					geofences.add(geofence);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);
					logger.info("************************ getDriverById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}

	@Override
	public ResponseEntity<?> deleteGeofenceApp(String TOKEN, Long geofenceId, Long userId) {
		logger.info("************************ deleteGeofence STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Geofence> geofences = new ArrayList<Geofence>();
		User user = userServiceImpl.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "GEOFENCE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete",null);
				 logger.info("************************ deleteGeo ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(geofenceId != 0) {
			Geofence geofence= geofenceServiceImpl.getById(geofenceId);
			if(geofence != null) {
				
				if(geofence.getDelete_date()==null) {
					 boolean isParent = false;
					 if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 User parent = null;
							 for(User object : parentClients) {
								 parent = object;
							 }
							 Set<User>geofneceParent = geofence.getUserGeofence();
							 if(geofneceParent.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User parentObject : geofneceParent) {
									 if(parentObject.getId().equals(parent.getId())) {
										 isParent = true;
										 break;
									 }
								 }
							 }
						 }
					 }
					 if(!geofenceServiceImpl.checkIfParent(geofence , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece ",geofences);
							logger.info("************************ deleteGeofence ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						geofenceRepository.deleteGeofence(geofenceId,currentDate);
						geofenceRepository.deleteGeofenceId(geofenceId);
						geofenceRepository.deleteGeofenceDeviceId(geofenceId);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",geofences);
						logger.info("************************ deleteGeofence ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					
					
					

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was Deleted before",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was not found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}



	}

	@Override
	public ResponseEntity<?> addGeofenceApp(String TOKEN, Geofence geofence, Long id) {
		logger.info("************************ addGeofence STARTED ***************************");

		List<Geofence> geofences= new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date()==null) {
					if(geofence.getName()== null || geofence.getType()== null
							   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
							   || geofence.getArea() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						List<Geofence> geofenceCheck=geofenceServiceImpl.checkDublicateGeofenceInAdd(id,geofence.getName());
					    List<Integer> duplictionList =new ArrayList<Integer>();
						if(!geofenceCheck.isEmpty()) {
							for(int i=0;i<geofenceCheck.size();i++) {
								if(geofenceCheck.get(i).getName().equalsIgnoreCase(geofence.getName())) {
									duplictionList.add(1);						
								}
							}
					    	getObjectResponse = new GetObjectResponse( 401, "This Geofence was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}
						else {
							Set<User> userDriver = new HashSet<>();
							if(geofence.getId()==null || geofence.getId()==0) {
								boolean isParent = false;
								 if(user.getAccountType().equals(4)) {
									 Set<User> parentClients = user.getUsersOfUser();
									 if(parentClients.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									 }else {
										 User parent = null;
										 for(User object : parentClients) {
											 parent = object;
										 }
										userDriver.add(parent);


									 }
								 }
								 else {
									userDriver.add(user);

								 }
								
								
								geofence.setUserGeofence(userDriver);
								geofenceRepository.save(geofence);
								geofences.add(geofence);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",geofences);
								logger.info("************************ addGeofence ENDED ***************************");

								return ResponseEntity.ok().body(getObjectResponse);

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update Geofence ID",geofences);
								return ResponseEntity.badRequest().body(getObjectResponse);

							}
						}
					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}
           			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}	
		
		
	}

	@Override
	public ResponseEntity<?> editGeofenceApp(String TOKEN, Geofence geofence, Long id) {
		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				 if(user.getDelete_date()==null) {
					 if(geofence.getId() != null) {
						 Geofence geofneceCheck = geofenceServiceImpl.getById(geofence.getId());
						if(geofneceCheck != null) {
							if(geofneceCheck.getDelete_date() == null) {
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>geofenceParent = geofneceCheck.getUserGeofence();
									if(geofenceParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : geofenceParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!geofenceServiceImpl.checkIfParent(geofneceCheck , user) && ! isParent) {

									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this geofence ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(geofence.getName()== null || geofence.getType()== null
										   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
										   || geofence.getArea() == "") {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									List<Geofence> checkDublicateInEdit=geofenceServiceImpl.checkDublicateGeofenceInEdit(geofence.getId(),id,geofence.getName());
								    List<Integer> duplictionList =new ArrayList<Integer>();
									if(!checkDublicateInEdit.isEmpty()) {
				    					for(int i=0;i<checkDublicateInEdit.size();i++) {
				    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(geofence.getName())) {
												duplictionList.add(1);						
			
				    						}
				    						
				    						
				    					}
								    	getObjectResponse = new GetObjectResponse( 401, "This Geofence was found before",duplictionList);
										return ResponseEntity.ok().body(getObjectResponse);

				    				}
				    				else {
				    					

				    					Set<User> userCreater=new HashSet<>();
				    					userCreater = geofneceCheck.getUserGeofence();
										geofence.setUserGeofence(userCreater);
										
										geofenceRepository.save(geofence);
										geofences.add(geofence);
										getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",geofences);
										logger.info("************************ editGeofence ENDED ***************************");
										return ResponseEntity.ok().body(getObjectResponse);

										
										
				    					
				    				}	
								}
								

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
								return ResponseEntity.status(404).body(getObjectResponse);

							}

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Geofence ID is Required",geofences);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	@Override
	public ResponseEntity<?> getGeofenceSelectApp(String TOKEN, Long userId) {
		
		logger.info("************************ getGeofenceSelect STARTED ***************************");
		List<DriverSelect> geofences = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 Set<User>parentClient = user.getUsersOfUser();
	   					if(parentClient.isEmpty()) {
	   						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
	   						logger.info("************************ getGeofenceSelect ENDED ***************************");
	   						return ResponseEntity.badRequest().body(getObjectResponse);
	   					}else {
	   					  
	   						User parent =null;
	   						for(User object : parentClient) {
	   							parent = object;
	   						}
	   						if(parent != null) {

					   			List<Long>usersIds= new ArrayList<>();
			   					usersIds.add(parent.getId());
			   					geofences = geofenceRepository.getGeofenceSelect(usersIds);
	   							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",geofences);
	   							logger.info("************************ getGeofenceSelect ENDED ***************************");
	   							return ResponseEntity.ok().body(getObjectResponse);
	   						}
	   						else {
	   							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
	   							return ResponseEntity.badRequest().body(getObjectResponse);
	   						}
	   						
	   					}
	   			 }
	    			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
		   			 List<Long>usersIds= new ArrayList<>();
		   			 if(childernUsers.isEmpty()) {
		   				 usersIds.add(userId);
		   			 }
		   			 else {
		   				 usersIds.add(userId);
		   				 for(User object : childernUsers) {
		   					 usersIds.add(object.getId());
		   				 }
		   			 }
	    			
		   			geofences = geofenceRepository.getGeofenceSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",geofences);
					logger.info("************************ getGeofenceSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",geofences);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	}

	@Override
	public ResponseEntity<?> getDriverByIdApp(String TOKEN, Long driverId, Long userId) {
		
		logger.info("************************ getDriverById STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
        if(userId.equals(0)) {
        	 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
        }
        User loggedUser = userServiceImpl.findById(userId);
        if(loggedUser == null) {
        	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
        }
		if(!driverId.equals(0)) {
			
			Driver driver= driverRepository.findOne(driverId);

			if(driver != null) {
				if(driver.getDelete_date() == null) {
					boolean isParent = false;
					if(loggedUser.getAccountType().equals(4)) {
						Set<User> clientParents = loggedUser.getUsersOfUser();
						if(clientParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this user",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : clientParents) {
								parent = object ;
							}
							Set<User>driverParents = driver.getUserDriver();
							if(driverParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User parentObject : driverParents) {
									if(parentObject.getId().equals(parent.getId())) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					drivers.add(driver);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
					logger.info("************************ getDriverById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
			return  ResponseEntity.badRequest().body(getObjectResponse);


		}

	}

	@Override
	public ResponseEntity<?> deleteDriverApp(String TOKEN, Long driverId, Long userId) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		logger.info("************************ deleteDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN  is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser  is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete driver",null);
				 logger.info("************************ deleteDriver ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(!driverId.equals(0)) {
			Driver driver= driverRepository.findOne(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
				 boolean isParent = false;
				 if(loggedUser.getAccountType().equals(4)) {
					 Set<User> parentClients = loggedUser.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver",drivers);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>driverParent = driver.getUserDriver();
						 if(driverParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver",drivers);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : driverParent) {
								 if(parentObject.getId().equals(parent.getId())) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }
				 }
				 if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					driverRepository.deleteDriver(driverId,currentDate);
					driverRepository.deleteDriverId(driverId);
					driverRepository.deleteDriverDeviceId(driverId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",drivers);
					logger.info("************************ deleteDriver ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver was Deleted Before",drivers);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> addDriverApp(String TOKEN, Driver driver, Long id) {
		logger.info("************************ addDriver STARTED ***************************");

		String image = driver.getPhoto();
		driver.setPhoto("not_available.png");
		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create driver",null);
						 logger.info("************************ createDriver ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
					
					if(driver.getName()== null || driver.getUniqueid()== null
							   || driver.getMobile_num() == null || driver.getName()== "" || driver.getUniqueid()== ""
							   || driver.getMobile_num() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						DecodePhoto decodePhoto=new DecodePhoto();
				    	if(image !=null) {
					    	if(image !="") {
					    		if(image.startsWith("data:image")) {
						        	driver.setPhoto(decodePhoto.Base64_Image(image,"driver"));				
					    		
					    		}
					    	}
						}
							
						
						List<Driver> res1=driverServiceImpl.checkDublicateDriverInAddEmail(id,driver.getEmail());					    
					    List<Driver> res2=driverServiceImpl.checkDublicateDriverInAddUniqueMobile(driver.getUniqueid(),driver.getMobile_num());
					    List<Integer> duplictionList =new ArrayList<Integer>();

						if(!res1.isEmpty()) {
							for(int i=0;i<res1.size();i++) {
								if(res1.get(i).getEmail().equals(driver.getEmail())) {
									duplictionList.add(1);				
								}
					
							}
					    	

						}
						
						if(!res2.isEmpty()) {
							for(int i=0;i<res2.size();i++) {
								
								if(res2.get(i).getUniqueid().equals(driver.getUniqueid())) {
									duplictionList.add(2);				
				
								}
								if(res2.get(i).getMobile_num().equals(driver.getMobile_num())) {
									duplictionList.add(3);				

								}
								
							}
					    	

						}
						
						if(!res1.isEmpty() || !res2.isEmpty()) {
							getObjectResponse = new GetObjectResponse( 301, "This Driver was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);	
						}
						
						else {
							if(driver.getId() == null || driver.getId() == 0) {
								User driverParent = new User();
								if(user.getAccountType().equals(4)) {
									Set<User> parentClients = user.getUsersOfUser();
									if(parentClients.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to add driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User object : parentClients) {
										driverParent = object;
									}
								}else {
									driverParent = user;
								}
								Set<User> userDriver = new HashSet<>();
								userDriver.add(driverParent);
								driver.setUserDriver(userDriver);
								driverRepository.save(driver);
								drivers.add(driver);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",drivers);
								logger.info("************************ addDriver ENDED ***************************");

								return ResponseEntity.ok().body(getObjectResponse);

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update this Driver ID",drivers);
								return ResponseEntity.badRequest().body(getObjectResponse);

							}
							
						
						}
						
					}
				
			}
			
			

		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	@Override
	public ResponseEntity<?> editDriverApp(String TOKEN, Driver driver, Long id) {
		logger.info("************************ editDriver STARTED ***************************");
    	String newPhoto= driver.getPhoto();
		
		driver.setPhoto("not_available.png");
			
		
		
		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit driver",null);
						 logger.info("************************ editDriver ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
               
                	if(driver.getId() != null) {
                		   	
						Driver driverCheck = driverRepository.findOne(driver.getId());

						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								boolean isParent = false;
								
								if(user.getAccountType().equals(4)) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>driverParent = driverCheck.getUserDriver();
									if(driverParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : driverParent) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
								if(!driverServiceImpl.checkIfParent(driverCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this driver ",null);
									logger.info("************************ editDevice ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(driver.getName()== null || driver.getUniqueid()== null
										   || driver.getMobile_num() == null || driver.getName()== "" || driver.getUniqueid()== ""
										   || driver.getMobile_num() == "") {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									DecodePhoto decodePhoto=new DecodePhoto();
						        	String oldPhoto=driverCheck.getPhoto();

						        	if(!oldPhoto.equals("")) {
										if(oldPhoto != null) {
											if(!oldPhoto.equals("not_available.png")) {
												decodePhoto.deletePhoto(oldPhoto, "driver");
											}
										}
									}

									if(newPhoto.equals("")) {
										
										driver.setPhoto("not_available.png");				
									}
									else {
										if(newPhoto.equals(oldPhoto)) {
											driver.setPhoto(oldPhoto);				
										}
										else{
											if(newPhoto.startsWith("data:image")) {
									        	driver.setPhoto(decodePhoto.Base64_Image(newPhoto,"driver"));				
								    		
								    		}

										}

								    }
									
										
									
									List<Driver> res1=driverServiceImpl.checkDublicateDriverInEditEmail(driver.getId(),id,driver.getEmail());
									List<Driver> res2=driverServiceImpl.checkDublicateDriverInEditMobileUnique(driver.getId(),driver.getUniqueid(),driver.getMobile_num());

									List<Integer> duplictionList =new ArrayList<Integer>();
									
									if(!res1.isEmpty()) {
										for(int i=0;i<res1.size();i++) {
											if(res1.get(i).getEmail().equals(driver.getEmail())) {
												duplictionList.add(1);				
											}
											
											
										}
								    	

									}
									
									
									if(!res2.isEmpty()) {
										for(int i=0;i<res2.size();i++) {
											
											if(res2.get(i).getUniqueid().equals(driver.getUniqueid())) {
												duplictionList.add(2);				
							
											}
											if(res2.get(i).getMobile_num().equals(driver.getMobile_num())) {
												duplictionList.add(3);				
			
											}
											
										}
								    	

									}
									if(!res1.isEmpty() || !res2.isEmpty()) {
										
										getObjectResponse = new GetObjectResponse( 301, "This Driver was found before",duplictionList);
										return ResponseEntity.ok().body(getObjectResponse);
									}
									
									else {
										
										   Set<User> userDriver = new HashSet<>();
										
									    	userDriver = driverCheck.getUserDriver();
										
										   driver.setUserDriver(userDriver);
										
											driverRepository.save(driver);
											drivers.add(driver);
											getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",drivers);
											logger.info("************************ editDriver ENDED ***************************");
											return ResponseEntity.ok().body(getObjectResponse);

										
										
										
										
									
									}
								}
							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
								return ResponseEntity.status(404).body(getObjectResponse);

							}
							
							
							
						}
						else{
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
                	}
                	else {
            			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
            			return ResponseEntity.badRequest().body(getObjectResponse);

                	}
					
				
				
				
			}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
		

	}

	@Override
	public ResponseEntity<?> getUnassignedDriversApp(String TOKEN, Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
		if(TOKEN.equals("")) {
			List<Driver> unAssignedDrivers = new ArrayList<>();

			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",unAssignedDrivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			List<Driver> unAssignedDrivers = new ArrayList<>();
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			 if(user.getAccountType().equals(4)) {
				 Set<User>parentClient = user.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
					  
						User parent =null;
						for(User object : parentClient) {
							parent = object;
						}
						if(parent != null) {
							List<Long>usersIds= new ArrayList<>();
						    usersIds.add(parent.getId());
							List<Driver> unAssignedDrivers = driverRepository.getUnassignedDrivers(usersIds);
							
							getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
							logger.info("************************ getUnassignedDrivers ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
						}
						else {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						
					}
			 }
			
			
			
			
			

			if(user == null) {
				List<Driver> unAssignedDrivers = new ArrayList<>();
				
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",unAssignedDrivers);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
				 List<Long>usersIds= new ArrayList<>();
				 if(childernUsers.isEmpty()) {
					 usersIds.add(userId);
				 }
				 else {
					 usersIds.add(userId);
					 for(User object : childernUsers) {
						 usersIds.add(object.getId());
					 }
				 }
				 
				List<Driver> unAssignedDrivers = driverRepository.getUnassignedDrivers(usersIds);
				
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
				logger.info("************************ getUnassignedDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
		}
	}

	@Override
	public ResponseEntity<?> getDriverSelectApp(String TOKEN, Long userId) {
		logger.info("************************ getDriverSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 Set<User>parentClient = user.getUsersOfUser();
	   					if(parentClient.isEmpty()) {
	   						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
	   						logger.info("************************ getDriverSelect ENDED ***************************");
	   						return ResponseEntity.badRequest().body(getObjectResponse);
	   					}else {
	   					  
	   						User parent =null;
	   						for(User object : parentClient) {
	   							parent = object;
	   						}
	   						if(parent != null) {

					   			List<Long>usersIds= new ArrayList<>();
			   					usersIds.add(parent.getId());
	   							drivers = driverRepository.getDriverSelect(usersIds);
	   							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	   							logger.info("************************ getDriverSelect ENDED ***************************");
	   							return ResponseEntity.ok().body(getObjectResponse);
	   						}
	   						else {
	   							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
	   							return ResponseEntity.badRequest().body(getObjectResponse);
	   						}
	   						
	   					}
	   			 }
	    			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
		   			 List<Long>usersIds= new ArrayList<>();
		   			 if(childernUsers.isEmpty()) {
		   				 usersIds.add(userId);
		   			 }
		   			 else {
		   				 usersIds.add(userId);
		   				 for(User object : childernUsers) {
		   					 usersIds.add(object.getId());
		   				 }
		   			 }
	    			
	    			drivers = driverRepository.getDriverSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getDriverSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	}

	@Override
	public ResponseEntity<?> createDeviceApp(String TOKEN, Device device, Long userId) {
logger.info("************************ createDevice STARTED ***************************");
		
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);
		
		device.setCreate_date(nowTime);
				
		String image = device.getPhoto();
		device.setPhoto("not_available.png");

		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create device",null);
				 logger.info("************************ createDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if( (device.getId() != null && device.getId() != 0) ) {
            List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id not allowed in create new device",devices);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(device.getName() == null ||device.getName().equals("")
				|| device.getUniqueId() == null || device.getUniqueId() == null
				|| device.getSequenceNumber() == null || device.getSequenceNumber().equals("")
				|| device.getPlateNum() == null|| device.getPlateNum().equals("")
				|| device.getLeftLetter() == null || device.getLeftLetter().equals("")
                || device.getMiddleLetter() == null|| device.getMiddleLetter().equals("")
                || device.getRightLetter() == null|| device.getRightLetter().equals("")) {
			
			List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "atrributes [name, trackerImei , sequence"
					+ "Number , plate num , leftLetter , middleLetter,RightLetter ] are required",devices);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		else
		{
			Set<User> user=new HashSet<>() ;
			User userCreater ;
			userCreater=userServiceImpl.findById(userId);
			if(userCreater == null)
			{

				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "Assigning to not found user",null);
				logger.info("************************ createDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				User parent = null;
				if(userCreater.getAccountType().equals(4)) {
					Set<User>parentClient = userCreater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this user cannot add user",null);
						logger.info("************************ createDevice ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}else {
					
					 for(User object : parentClient) {
						 parent = object ;
					 }
					 
					}
				}else {
					parent = userCreater;
				}
				
				user.add(parent);	
		        device.setUser(user);
			    List<Integer> duplictionList = deviceServiceImpl.checkDeviceDuplication(device);
			    if(duplictionList.size()>0)
			    {
			    	getObjectResponse = new GetObjectResponse( 201, "Duplication in data",duplictionList);
			    	logger.info("************************ createDevice ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse);
			    }
			    else
			    {
					
			    	DecodePhoto decodePhoto=new DecodePhoto();
			    	if(image !=null) {
				    	if(image !="") {
				    		if(image.startsWith("data:image")) {
					    		device.setPhoto(decodePhoto.Base64_Image(image,"vehicle"));				

				    		}
				    	}
					}
			    	
			    	
			    	deviceRepository.save(device);
			    	List<Device> devices = null;
			    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",devices);
					logger.info("************************ createDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
			    }
			}
			
	        
		}
		
	}

	@Override
	public ResponseEntity<?> editDeviceApp(String TOKEN, Device device, Long userId) {
		logger.info("************************ editDevice STARTED ***************************");
    	String newPhoto= device.getPhoto();
		
    	device.setPhoto("not_available.png");
    	
    	
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ editDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ editDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit device",null);
				 logger.info("************************ editDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
	
		if(device.getId() == null || device.getName() == null ||device.getName().equals("") 
			|| device.getUniqueId() == null || device.getUniqueId().equals("")
			|| device.getSequenceNumber() == null || device.getSequenceNumber().equals("")
			|| device.getPlateNum() == null || device.getPlateNum().equals("")
			|| device.getLeftLetter() == null || device.getLeftLetter() == null
			|| device.getRightLetter() == null || device.getRightLetter().equals("")
			|| device.getMiddleLetter() == null || device.getMiddleLetter().equals("")	) {
			
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "atrributes [id ,name, trackerImei , sequence" + 
					"					Number , plate num , leftLetter , middleLetter,RightLetter ] are required",null);
			logger.info("************************ editDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		else {
			  boolean	isParent = false;
			  Device oldDevice = deviceServiceImpl.findById(device.getId());
			if(oldDevice == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This device not found",devices);
		    	logger.info("************************ createDevice ENDED ***************************");
		    	return ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType().equals(4)) {
				Set<User>parentClient = loggedUser.getUsersOfUser();
				if(parentClient.isEmpty()) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}else {
				  
					User parent =null;
					for(User object : parentClient) {
						parent = object;
					}
					Set<User> deviceParent = oldDevice.getUser();
					if(deviceParent.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
						
						for(User deviceUser : deviceParent) {
							if(deviceUser.getId().equals(parent.getId())) {
								
								isParent = true;
								break;
							}
						}
					}
				}
			}
			if(!deviceServiceImpl.checkIfParent(oldDevice , loggedUser) && ! isParent) {
				getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
				logger.info("************************ editDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			Set<User> userCreater=new HashSet<>();
			userCreater = oldDevice.getUser();
	        device.setUser(userCreater);
	        List<Integer> duplictionList = deviceServiceImpl.checkDeviceDuplication(device);
	        if(duplictionList.size()>0)
		    {
		    	getObjectResponse = new GetObjectResponse( 201, "Duplication in data",duplictionList);
		    	logger.info("************************ createDevice ENDED ***************************");
		    	return ResponseEntity.ok().body(getObjectResponse);
		    }
	        else {
				DecodePhoto decodePhoto=new DecodePhoto();
	        	String oldPhoto=oldDevice.getPhoto();

	        	if(!oldPhoto.equals("")) {
					if(oldPhoto != null) {
						if(!oldPhoto.equals("not_available.png")) {
							decodePhoto.deletePhoto(oldPhoto, "vehicle");
						}
					}
				}

				if(newPhoto.equals("")) {
					
					device.setPhoto("not_available.png");				
				}
				else {
					if(newPhoto.equals(oldPhoto)) {
						device.setPhoto(oldPhoto);				
					}
					else{
			    		if(newPhoto.startsWith("data:image")) {

			    			device.setPhoto(decodePhoto.Base64_Image(newPhoto,"vehicle"));
			    		}
					}

			    }
				
				
				
				
				
		    	deviceRepository.save(device);
		    	List<Device> devices = null;
		    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",devices);
				logger.info("************************ editDevice ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
	        }
	        
	        
	        
		}
		
	}

	@Override
	public ResponseEntity<?> deleteDeviceApp(String TOKEN, Long userId, Long deviceId) {
		logger.info("************************ deleteDevice ENDED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		 if(deviceId.equals(0) || userId.equals(0)) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID and Device ID are Required",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "User ID is not found",devices);
		    logger.info("************************ deleteDevice ENDED ***************************");
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete device",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 Device device = deviceServiceImpl.findById(deviceId);
		 if(device == null)
		 {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 else
		 {
			 boolean isParent = false;
			 User creater= userServiceImpl.findById(userId);
			 if(creater == null) {
				 List<Device> devices = null;
				 getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",devices);
			     logger.info("************************ deleteDevice ENDED ***************************");
			     return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 if(creater.getAccountType().equals(4)) {
				 Set<User>parentClient = creater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
					  
						User parent =null;
						for(User object : parentClient) {
							parent = object;
						}
						Set<User> deviceParent = device.getUser();
						if(deviceParent.isEmpty()) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							
							for(User deviceUser : deviceParent) {
								if(deviceUser.getId().equals(parent.getId())) {
									
									isParent = true;
									break;
								}
							}
						}
					}
			 }
			 if(!deviceServiceImpl.checkIfParent(device , creater)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this user ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 Calendar cal = Calendar.getInstance();
			 int day = cal.get(Calendar.DATE);
		     int month = cal.get(Calendar.MONTH) + 1;
		     int year = cal.get(Calendar.YEAR);
		     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
		     device.setDeleteDate(date);
		     Set<Driver> drivers =new HashSet<>() ;
		     drivers = device.getDriver();
		     Set<Driver> oldDrivers =new HashSet<>() ;
	         oldDrivers= drivers;
	         drivers.removeAll(oldDrivers);
	         device.setDriver(drivers);
			 Set<User> user =new HashSet<>() ;
		     user = device.getUser();
		     Set<User> oldUser =new HashSet<>() ;
	         oldUser= user;
	         user.removeAll(oldUser);
	         device.setUser(user);
			 deviceRepository.save(device);
		     
		     List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.OK.value(), "success",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.ok().body(getObjectResponse);
		 }
		 
	}

	@Override
	public ResponseEntity<?> findDeviceByIdApp(String TOKEN, Long deviceId, Long userId) {
		logger.info("************************ getDeviceById STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId.equals(0) || userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID  and logged user Id are  Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		Device device = deviceRepository.findOne(deviceId);
		if (device == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			if(device.getDeleteDate() != null) {
				//throw not found 
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
				logger.info("************************ getDeviceById ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else
			{
				boolean isParent = false;
			   if(loggedUser.getAccountType().equals(4)) {
				   Set<User>parentClient = loggedUser.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
					  
						User parent =null;
						for(User object : parentClient) {
							parent = object;
						}
						Set<User> deviceParent = device.getUser();
						if(deviceParent.isEmpty()) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							
							for(User deviceUser : deviceParent) {
								if(deviceUser.getId().equals(parent.getId())) {
									
									isParent = true;
									break;
								}
							}
						}
					}
			   }
			   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this user ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			   }
				List<Device> devices = new ArrayList<>();
				devices.add(device);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
				logger.info("************************ getDeviceById ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
		}
	}

	@Override
	public ResponseEntity<?> assignDeviceToDriverApp(String TOKEN, Long deviceId, Long driverId, Long userId) {
		logger.info("************************ assignDeviceToDriver STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is Required",null);
			logger.info("************************ assignDeviceToDriver ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignDeviceToDriver ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "assignDeviceToDriver")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(deviceId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",null);
			logger.info("************************ assignDeviceToDriver ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Device device = deviceServiceImpl.findById(deviceId);
			if(device == null) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
				logger.info("************************ assignDeviceToDriver ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				boolean isParent = false;
				   if(loggedUser.getAccountType().equals(4)) {
					   Set<User>parentClient = loggedUser.getUsersOfUser();
						if(parentClient.isEmpty()) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}else {
						  
							User parent =null;
							for(User object : parentClient) {
								parent = object;
							}
							Set<User> deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
								logger.info("************************ editDevice ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								
								for(User deviceUser : deviceParent) {
									if(deviceUser.getId().equals(parent.getId())) {
										
										isParent = true;
										break;
									}
								}
							}
						}
				   }
				   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this device ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
				   }
				if(driverId.equals(0)) {
					Set<Driver> drivers=new HashSet<>() ;
					drivers= device.getDriver();
			        if(drivers.isEmpty()) {
			        	List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
			        }
			        else {
			        	//check if parent in drivers
			        	Set<Driver> oldDrivers =new HashSet<>() ;
			        	oldDrivers= drivers;
			        	drivers.removeAll(oldDrivers);
			        	 device.setDriver(drivers);
						 deviceRepository.save(device);
			        	List<Device> devices = null;
			        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Driver removed successfully",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
			        }
				}
				Driver driver = driverServiceImpl.getDriverById(driverId);
				if(driver == null) {
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver is not found",devices);
					logger.info("************************ assignDeviceToDriver ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					if(driver.getDelete_date() != null) {
						List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver is not found",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						Set<Driver> OldAssignedDrivers=new HashSet<>() ;
						OldAssignedDrivers= device.getDriver();
						if(!OldAssignedDrivers.isEmpty()) {
							Set<Driver> oldDrivers =new HashSet<>() ;
				        	oldDrivers= OldAssignedDrivers;
				        	OldAssignedDrivers.removeAll(oldDrivers);
						}
						Set<Device> assignedDevices=driver.getDevice();
						if(!assignedDevices.isEmpty()) {
							for( Device assignedDevice :assignedDevices) {
							 if(assignedDevice.getId().equals(device.getId())) {
								 List<Device> devices = null;
									getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
									logger.info("************************ assignDeviceToDriver ENDED ***************************");
									return ResponseEntity.ok().body(getObjectResponse); 
							 }
							 else {
								 List<Device> devices = null;
								 getObjectResponse = new GetObjectResponse(203, "This driver is assigned to another device",devices);
									logger.info("************************ assignDeviceToDriver ENDED ***************************");
									return ResponseEntity.ok().body(getObjectResponse); 
							 }
							}
						}
						
						Set<Driver> drivers=new HashSet<>() ;
						drivers.add(driver);
				        device.setDriver(drivers);
						deviceRepository.save(device);
						List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
				}
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignGeofencesToDeviceApp(String TOKEN, Long deviceId, Long[] geoIds, Long userId) {
		logger.info("************************ assignDeviceToGeofences STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignGeofenceToDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "assignGeofenceToDevice")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignGeofenceToDevice",null);
				 logger.info("************************ assignGeofenceToDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(deviceId.equals(0)){
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",devices);
			logger.info("************************ assignDeviceToGeofences ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
//			return ResponseEntity.status(404).body(getObjectResponse);
		}else {
			 Device device = deviceServiceImpl.findById(deviceId);
			 if(device == null) {
				  List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
//					return ResponseEntity.ok().body(getObjectResponse);
					return ResponseEntity.status(404).body(getObjectResponse);
			 }
			if(geoIds.length == 0) {
				//if device has geofences remove it 
                Set<Geofence> geofences = device.getGeofence();
                if(geofences.isEmpty()) {
                	 List<Device> devices = null;
 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No geofences to assign or remove",devices);
 					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
 					return ResponseEntity.status(404).body(getObjectResponse);
                }
                else {
                	// else if device hasn't geofences return error
    				
                	Set<Geofence> oldGeofences = geofences;
                	geofences.removeAll(oldGeofences);
                	device.setGeofence(geofences);
                	deviceRepository.save(device);
                	List<Device> devices = null;
                	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Geofences removed successfully",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
                }
			   
			}else {
				    List<Device> devices = null;
				    Set<Geofence> newGeofences = geofenceServiceImpl.getMultipleGeofencesById(geoIds);
					if(newGeofences.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",devices);
						logger.info("************************ assignDeviceToGeofences ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					Set<Geofence> geofences = device.getGeofence();
					Set<Geofence> oldGeoffences = geofences;
					geofences.removeAll(oldGeoffences);
					device.setGeofence(geofences);
					deviceRepository.save(device);
					
					device.setGeofence(newGeofences);
					deviceRepository.save(device);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
			}
			
		}
	}

	@Override
	public ResponseEntity<?> getDeviceDriverApp(String TOKEN, Long deviceId) {
		logger.info("************************ getDeviceToDriver STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",devices);
			logger.info("************************ getDeviceToDriver ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {

			Device device = deviceServiceImpl.findById(deviceId);
			if(device == null) {

				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
				logger.info("************************ getDeviceToDriver ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else
			{

				Set<Driver> drivers=new HashSet<>() ;
				drivers = device.getDriver();
				if(drivers.isEmpty()) {

					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "No drivers assigned to this device",devices);
					logger.info("************************ getDeviceToDriver ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}
				else {

					List<Driver> deviceDriver = new ArrayList<>();
					for(Driver driver : drivers ) {
						//hint only one driver assigned to device

						deviceDriver.add(driver);
					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",deviceDriver);
					logger.info("************************ getDeviceToDriver ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				}
			}
		}
		
		
	}

	@Override
	public ResponseEntity<?> getDeviceGeofencesApp(String TOKEN, Long deviceId) {
		logger.info("************************ getDeviceGeofences STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",devices);
			logger.info("************************ getDeviceGeofences ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Device device = deviceServiceImpl.findById(deviceId);
			if(device == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
				logger.info("************************ getDeviceGeofences ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else
			{
				Set<Geofence> geofences=new HashSet<>() ;
				geofences = device.getGeofence();
				if(geofences.isEmpty()) {
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "No geofences assigned to this device",devices);
					logger.info("************************ getDeviceGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}
				else {
					List<Geofence> deviceGeofences = new ArrayList<>();
					for(Geofence geofence : geofences ) {
						//hint only one driver assigned to device
						deviceGeofences.add(geofence);
					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",deviceGeofences);
					logger.info("************************ getDeviceGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				}
			}
		}
	}

}
