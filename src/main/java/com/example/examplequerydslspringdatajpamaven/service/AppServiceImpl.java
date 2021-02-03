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
import java.util.stream.Collectors;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.EventReportByCurl;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.entity.StopReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.example.examplequerydslspringdatajpamaven.entity.TripReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.entity.userClientDevice;
import com.example.examplequerydslspringdatajpamaven.entity.userClientDriver;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoEventsRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.ProfileRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * services functionality related to app
 * @author fuinco
 *
 */
@Component
@Service
public class AppServiceImpl extends RestServiceController implements AppService{

	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private TokenSecurity tokenSecurity;
	
	 @Value("${stopsUrl}")
	 private String stopsUrl;
	 
	 @Value("${tripsUrl}")
	 private String tripsUrl;
	 
	 @Value("${eventsUrl}")
	 private String eventsUrl;
	 
	 @Value("${summaryUrl}")
	 private String summaryUrl;

	@Autowired
	private UserClientDriverRepository userClientDriverRepository;

	@Autowired
	private UserClientGeofenceRepository userClientGeofenceRepository;
	
	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	private UserClientGroupRepository userClientGroupRepository;
	
		
	@Autowired
	private MongoEventsRepo mongoEventsRepo;
		
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private MongoPositionRepo mongoPositionRepo;
	
	@Autowired
	private ProfileServiceImpl profileServiceImpl;

	
	@Autowired
	private MongoPositionsRepository mongoPositionsRepository;
	
	 
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
	
	
	
	/**
	 * login of app
	 */
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
//			    TokenSecurity.getInstance().addActiveUser(user.getId(),token,requestLastUpdate); 
		        
		        //TokenSecurity.getInstance().addActiveUser(user.getId(),token); 
		        tokenSecurity.addActiveUser(user.getId(),token); 

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
	 * logout of app
	 */
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
			  //Boolean removed = TokenSecurity.getInstance().removeActiveUser(TOKEN);
			  Boolean removed = tokenSecurity.removeActiveUser(TOKEN);

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

	/**
	 * get data of all devices on map
	 */
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
			 
			List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			List<CustomMapData> allDevicesLiveDataNoPosition = new ArrayList<CustomMapData>();
            List<CustomMapData> allDevices = new ArrayList<CustomMapData>();

			if(deviceIds.size()>0) {
				allDevicesLiveDataNoPosition =	deviceRepository.getAllDevicesDataMapByIds(deviceIds);
				allDevices.addAll(allDevicesLiveDataNoPosition);

				List<String> positionIdsOffline =  deviceRepository.getNumberOfOfflineDevicesListByIds(deviceIds);
				List<String> positionIdsOutOfNetwork =  deviceRepository.getNumberOfOutOfNetworkDevicesListByIds(deviceIds);
				List<String> positionIdsOnline =  deviceRepository.getNumberOfOnlineDevicesListByIds(deviceIds);



				if(positionIdsOffline.size() > 0 ) {
					List<CustomMapData> allDevicesPositionOffline = mongoPositionRepo.getOfflineList(positionIdsOffline);
					allDevices.addAll(allDevicesPositionOffline);

				}
				
				if(positionIdsOutOfNetwork.size() > 0 ) {
					List<CustomMapData> allDevicesPositionOutOfNetwork= mongoPositionRepo.getOutOfNetworkList(positionIdsOutOfNetwork);
					allDevices.addAll(allDevicesPositionOutOfNetwork);

				}
				
				if(positionIdsOnline.size() > 0 ) {
					List<CustomMapData> allDevicesPositionOnline= mongoPositionRepo.getOnlineList(positionIdsOnline);
					allDevices.addAll(allDevicesPositionOnline);

				}

			
			}
			
			
		    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevices);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
	    	
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
	    
            List<CustomMapData> allDevices = new ArrayList<CustomMapData>();

			List<CustomMapData> allDevicesNoPosition = deviceRepository.getAllDevicesDataMap(usersIds);
			allDevices.addAll(allDevicesNoPosition);

			
			

			List<String> positionIdsOffline =  deviceRepository.getNumberOfOfflineDevicesList(usersIds);
			List<String> positionIdsOutOfNetwork =  deviceRepository.getNumberOfOutOfNetworkDevicesList(usersIds);
			List<String> positionIdsOnline =  deviceRepository.getNumberOfOnlineDevicesList(usersIds);

			if(positionIdsOffline.size() > 0 ) {
				List<CustomMapData> allDevicesPositionOffline = mongoPositionRepo.getOfflineList(positionIdsOffline);
				allDevices.addAll(allDevicesPositionOffline);

			}
			
			if(positionIdsOutOfNetwork.size() > 0 ) {
				List<CustomMapData> allDevicesPositionOutOfNetwork= mongoPositionRepo.getOutOfNetworkList(positionIdsOutOfNetwork);
				allDevices.addAll(allDevicesPositionOutOfNetwork);

			}
			
			if(positionIdsOnline.size() > 0 ) {
				List<CustomMapData> allDevicesPositionOnline= mongoPositionRepo.getOnlineList(positionIdsOnline);
				allDevices.addAll(allDevicesPositionOnline);

			}
		    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevices);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
	    }

	}
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) 
    {
        long diffInMillies = date2.getTime() - date1.getTime();
         
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

	/**
	 * get summary info by device
	 */
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
				if(device.getDelete_date()==null) {
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
							 List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
							 if(devices.isEmpty()) {
									isParent = false;
							 }
							 else {
									isParent = true;
							 }

					   }
					   if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					vehicleInfo = deviceRepository.vehicleInfoData(deviceId);
				    Map<Object, Object> sensorList =new HashMap<Object, Object>();

					List<Map> data = new ArrayList<>();
					if(vehicleInfo.size()>0) {
						if(vehicleInfo.get(0).getPositionId() != null) {
							
							
							MongoPositions mongoPosition = mongoPositionsRepository.findById(vehicleInfo.get(0).getPositionId());
		                    
							if(mongoPosition != null) {
								vehicleInfo.get(0).setLatitude(mongoPosition.getLatitude());
			                    vehicleInfo.get(0).setLongitude(mongoPosition.getLongitude());
			                    vehicleInfo.get(0).setSpeed(mongoPosition.getSpeed() * (1.852) );
			                    vehicleInfo.get(0).setAddress(mongoPosition.getAddress());
			                    vehicleInfo.get(0).setAttributes(mongoPosition.getAttributes());
			                    
			                    
							}
							
						}
	                   

	                    
						
					    Map<Object, Object> attrbuitesList =new HashMap<Object, Object>();
						if(vehicleInfo.get(0).getAttributes() != null) {
						   ObjectMapper mapper = new ObjectMapper();
	                	   String json = null;
	                	   try {
	                		   json = mapper.writeValueAsString(vehicleInfo.get(0).getAttributes());
						   } catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

	                    	JSONObject obj = new JSONObject(json);
							
	                    	if(obj.has("ignition")) {
    							if(obj.getBoolean("ignition")==true) {
				                    if(vehicleInfo.get(0).getSpeed() > 0) {
				    			    	ArrayList<Map<Object,Object>> lastPoints = new ArrayList<Map<Object,Object>>();

				    			    	lastPoints = mongoPositionRepo.getLastPoints(deviceId);
				    			    	vehicleInfo.get(0).setLastPoints(lastPoints);
				                    
									}
    								
    							}
    		                    
    						}
	                    	
	                    	
							if(obj.has("power")) {
								if(obj.get("power") != null) {
									if(obj.get("power") != "") {
										double p = Double.valueOf(obj.get("power").toString());
										double round = Math.round(p * 100.0 )/ 100.0;
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
										double round = Math.round(p * 100.0 )/ 100.0;
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

	/**
	 * get device list with limit 3
	 */
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
		 List<CustomDeviceList> devices = new ArrayList<CustomDeviceList>();
		 Integer size= 0;
		 if(loggedUser.getAccountType().equals(4)) {
			 

             List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			 
			 if(deviceIds.size()>0) {

				 devices= deviceRepository.getDevicesListAppByIds(deviceIds,offset,search);
				 size=  deviceRepository.getDevicesListSizeByIds(deviceIds,search);
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
			 devices= deviceRepository.getDevicesListApp(usersIds,offset,search);
			 size=  deviceRepository.getDevicesListSize(usersIds,search);
		 }
		 
		
		 
		 if(devices.size() > 0) {
			for(int i=0;i<devices.size();i++) {
				
				long minutes = 0;
				devices.get(i).setStatus("offline");

				if(devices.get(i).getLastUpdate() != null) {

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
					
					if(mongoPosition != null) {
						devices.get(i).setAttributes(mongoPosition.getAttributes());
						devices.get(i).setSpeed(mongoPosition.getSpeed() * (1.852) );
						devices.get(i).setLatitude(mongoPosition.getLatitude());
						devices.get(i).setLongitude(mongoPosition.getLongitude());
						devices.get(i).setAddress(mongoPosition.getAddress());
						

					   ObjectMapper mapper = new ObjectMapper();
                	   String json = null;
                	   try {
                		   json = mapper.writeValueAsString(devices.get(i).getAttributes());
					   } catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

                    	JSONObject obj = new JSONObject(json);
						
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
		}
		 
		 
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices,size);
		 logger.info("************************ getDevicesListApp ENDED ***************************");
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get driver list
	 */
	@Override
	public ResponseEntity<?> getAllDriversApp(String TOKEN, Long id, int offset, String search) {
		logger.info("************************ getAllDrivers STARTED ***************************");
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
						 logger.info("************************ getAllUserDrivers ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				   userServiceImpl.resetChildernArray();
					if(user.getAccountType().equals(4)) {
						
						List<Long> driverIds = userClientDriverRepository.getDriverIds(id);
						Integer size = 0;
						if(driverIds.size()>0) {

							customDrivers= driverRepository.getAllDriversCustomByIds(driverIds,offset,search);
						    size= driverRepository.getAllDriversSizeByIds(driverIds,search);
						}
						 
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
						logger.info("************************ getAllDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
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
				    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);

					Integer size= driverRepository.getAllDriversSize(usersIds,search);
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
					logger.info("************************ getAllDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				
				
			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
		
	}
	

	/**
	 * get data of stops of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getStopsReportApp(String TOKEN, Long [] deviceIds, Long [] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<StopReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "STOP", "list") && !userRoleService.checkUserHasPermission(userId, "DURATIONINSTOP", "list")
					&& !userRoleService.checkUserHasPermission(userId, "ENGINEINSTOP", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get stop or duration in stop or engine in stop list",stopReport);
					logger.info("************************ getStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										logger.info("************************ getStopsReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								logger.info("************************ getStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
								
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							logger.info("************************ getStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								logger.info("************************ getStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						logger.info("************************ getStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		
		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		
		
		 stopReport = (List<StopReport>) returnFromTraccarApp(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();

		  if(stopReport.size()>0) {

			  
			  Long timeDuration = (long) 0;
			  Long timeEngine= (long) 0;
			  String totalDuration = "00:00:00";
			  String totalEngineHours = "00:00:00";
			  


			  for(StopReport stopReportOne : stopReport ) {
				  Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
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

	/**
	 * get data of trips of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getTripsReportApp(String TOKEN,  Long [] deviceIds,  Long [] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "TRIP", "list") && !userRoleService.checkUserHasPermission(userId, "TRIPDISTANCESPEED", "list")
					&& !userRoleService.checkUserHasPermission(userId, "TRIPSPENTFUEL", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
					logger.info("************************ getTripsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									logger.info("************************ getTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										logger.info("************************ getTripsReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								logger.info("************************ getTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
                            }

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							logger.info("************************ getTripsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								logger.info("************************ getTripsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						logger.info("************************ getTripsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		
		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 tripReport = (List<TripReport>) returnFromTraccarApp(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 if(tripReport.size()>0) {

			  for(TripReport tripReportOne : tripReport ) {
				  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());

				  Double totalDistance = 0.0 ;
				  double roundOffDistance = 0.0;
				  double roundOffFuel = 0.0;
				  Double litres=10.0;
				  Double Fuel =0.0;
				  Double distance=0.0;
				  
				  Set<Driver>  drivers = device.getDriver();
				  for(Driver driver : drivers ) {

					 tripReportOne.setDriverName(driver.getName());
					 tripReportOne.setDriverUniqueId(driver.getUniqueid());

					
					 
				  }

				  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setDistance(Double.toString(roundOffDistance));


				  }
				  
				  if(device.getFuel() != null) {
						if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
							JSONObject obj = new JSONObject(device.getFuel());	
							if(obj.has("fuelPerKM")) {
								litres=obj.getDouble("fuelPerKM");
								
							}
						}
				   }
					  
				  distance = Double.parseDouble(tripReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }

				  roundOffFuel = Math.round(Fuel * 100.0) / 100.0;
				  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));
				  
				  
				  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
					  Long time=(long) 0;

					  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

					  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
					  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
					  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
					  
					  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
					  tripReportOne.setDuration(totalHours);
				  }
				 
				  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed()) * (1.852)  );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


				  }
				  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed()) * (1.852) );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


				  }
				  
				  
				  
				  

			  }
		  }
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
		logger.info("************************ getTripsReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
		

	}

	
	@Override
	public ResponseEntity<?> returnFromTraccarApp(String url,String report,List<Long> allDevices,String from,String to,String type,int page,int start,int limit) {
		
		 logger.info("************************ returnFromTraccarApp STARTED ***************************");

		
		String plainCreds = "admin@fuinco.com:admin";
		byte[] plainCredsBytes = plainCreds.getBytes();
		
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		
		  String GET_URL = url;
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
		  
		  if(report.equals("stops")) {
			  ResponseEntity<List<StopReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
		            });
				 logger.info("************************ returnFromTraccarApp StopReport ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("trips")) {
			  ResponseEntity<List<TripReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
		            });
				 logger.info("************************ returnFromTraccarApp TripReport ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("events")) {
			  ResponseEntity<List<EventReportByCurl>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<EventReportByCurl>>() {
		            });
				 logger.info("************************ returnFromTraccarApp EventReportByCurl ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("summary")) {
			  ResponseEntity<List<SummaryReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
		            });
				 logger.info("************************ returnFromTraccarApp SummaryReport ENDED ***************************");

			  return rateResponse;
		  }
		  
		  
			 logger.info("************************ returnFromTraccarApp ENDED ***************************");

	     return null;
	}
	
	/**
	 * get data of summary of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getSummaryReportApp(String TOKEN, Long [] deviceIds, Long [] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getSummaryReport STARTED ***************************");
		List<SummaryReport> summaryReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",summaryReport);
			 logger.info("************************ getSummaryReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "SUMMARY", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SUMMARY list",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",summaryReport);
									 logger.info("************************ getSummaryReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",summaryReport);
										 logger.info("************************ getSummaryReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",summaryReport);
								 logger.info("************************ getSummaryReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
							 logger.info("************************ getSummaryReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
								 logger.info("************************ getSummaryReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",summaryReport);
						 logger.info("************************ getSummaryReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 summaryReport = (List<SummaryReport>) returnFromTraccarApp(summaryUrl,"summary",allDevices, from, to, type, page, start, limit).getBody();
		 if(summaryReport.size()>0) {
			  Double totalDistance = 0.0 ;
			  double roundOffDistance = 0.0;
			  double roundOffFuel = 0.0;
			  Double litres=10.0;
			  Double Fuel =0.0;
			  Double distance=0.0;
			  
			  for(SummaryReport summaryReportOne : summaryReport ) {
				  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
				  if(device != null) {
				  Set<Driver>  drivers = device.getDriver();
				  for(Driver driver : drivers ) {

					 summaryReportOne.setDriverName(driver.getName());
				  }
				  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setDistance(Double.toString(roundOffDistance));


				  }
				  
				  if(device.getFuel() != null) {
						if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
							JSONObject obj = new JSONObject(device.getFuel());	
							if(obj.has("fuelPerKM")) {
								litres=obj.getDouble("fuelPerKM");
								
							}
						}
				   }
				  
				  
				  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }
	
				  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
				  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
				  
				
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

				  if(summaryReportOne.getAverageSpeed() != null && summaryReportOne.getAverageSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getAverageSpeed())  * (1.852) );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setAverageSpeed(Double.toString(roundOffDistance));


				  }
				  if(summaryReportOne.getMaxSpeed() != null && summaryReportOne.getMaxSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getMaxSpeed())  * (1.852) );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setMaxSpeed(Double.toString(roundOffDistance));


				  }
				  
			}
		  }
		  
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
			 logger.info("************************ getSummaryReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get lat and long of trip or location by id and time from ,to from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> viewTripApp(String TOKEN, Long deviceId, String from , String to) {
		logger.info("************************ getviewTrip STARTED ***************************");

		List<TripPositions> positions = new ArrayList<TripPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positions);
				logger.info("************************ getviewTrip ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		

		Date dateFrom;
		Date dateTo;

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS SSSS");
		SimpleDateFormat inputFormat1 = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
		inputFormat1.setLenient(false);
		inputFormat.setLenient(false);
		outputFormat.setLenient(false);

		
		try {
			dateFrom = inputFormat2.parse(from);
			from = outputFormat.format(dateFrom);
			

		} catch (ParseException e2) {
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or yyyy-MM-dd'T'HH:mm:ss.SSS SSSS",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		
		try {
			dateTo = inputFormat2.parse(to);
			to = outputFormat.format(dateTo);
			

		} catch (ParseException e2) {
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		Device device = deviceServiceImpl.findById(deviceId);
		
		if(device != null) {
			positions = mongoPositionRepo.getTripPositions(deviceId, dateFrom, dateTo);

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positions,positions.size());
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positions);
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
	}

	/**
	 * get geofence list by limit 10
	 */
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
						 
				    	List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(id);
				    	Integer size = 0;
						 if(geofenceIds.size()>0) {

							 geofences = geofenceRepository.getAllGeofencesByIds(geofenceIds,offset,search);
							 size=geofenceRepository.getAllGeofencesSizeByIds(geofenceIds,search);
						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
						logger.info("************************ getAllUserGeofences ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
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
					Integer size=geofenceRepository.getAllGeofencesSize(usersIds,search);
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

	/**
	 * get geofence by id 
	 */
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
						List<Long> geofencesData = userClientGeofenceRepository.getGeofence(userId,geofenceId);
						if(geofencesData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
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


	/**
	 * delete geofence by id
	 */
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
						List<Long> geofencesData = userClientGeofenceRepository.getGeofence(userId,geofenceId);
						if(geofencesData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					 }
					 if(!geofenceServiceImpl.checkIfParent(geofence , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece ",geofences);
							logger.info("************************ deleteGeofence ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						geofenceRepository.deleteGeofence(geofenceId,currentDate);
						geofenceRepository.deleteGeofenceGroupId(geofenceId);
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

	/**
	 * add geofence by data in body
	 */
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

	/**
	 * edit geofence by id in body is mandatory
	 */
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

									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this geofence",null);
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
										
										Set<Device> device=new HashSet<>();
				    					device = geofneceCheck.getDevice();
										geofence.setDevice(device);

				    					Set<Group> groups=new HashSet<>();
				    					groups = geofneceCheck.getGroups();
										geofence.setGroups(groups);
										
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

	/**
	 * get geofence select list
	 */
	@Override
	public ResponseEntity<?> getGeofenceSelectApp(String TOKEN, Long userId) {
		logger.info("************************ getGeofenceSelect STARTED ***************************");
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
	   				
	    				
	    				List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(userId);

						if(geofenceIds.size()>0) {

							drivers = geofenceRepository.getGeofenceSelectByIds(geofenceIds);

						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
						logger.info("************************ getGeofenceSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
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
	    			
	    			drivers = geofenceRepository.getGeofenceSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getGeofenceSelect ENDED ***************************");
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

	/**
	 * get driver by id to edit
	 */
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
						List<Long> driversData = userClientDriverRepository.getDriver(userId,driverId);
						if(driversData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
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

	/**
	 * delete driver by id
	 */
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
					List<Long> driversData = userClientDriverRepository.getDriver(userId,driverId);
					if(driversData.isEmpty()) {
							isParent = false;
					}
					else {
							isParent = true;
					}
				 }
				 if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					driverRepository.deleteDriver(driverId,currentDate);
					driverRepository.deleteDriverDeviceId(driverId);
					driverRepository.deleteDriverGroupId(driverId);

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

	/**
	 * add driver with data in body
	 */
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
						
				    	List<Driver> res1=driverServiceImpl.checkDublicateDriverInAddEmail(driverParent.getId(),driver.getName());					    
					    List<Driver> res2=driverServiceImpl.checkDublicateDriverInAddUniqueMobile(driver.getUniqueid(),driver.getMobile_num(),driver.getEmail());
					    List<Integer> duplictionList =new ArrayList<Integer>();

						if(!res1.isEmpty()) {
							for(int i=0;i<res1.size();i++) {
								
								if(res1.get(i).getName().equals(driver.getName())) {
									duplictionList.add(4);				
								}
					
							}
					    	

						}
						
						if(!res2.isEmpty()) {
							for(int i=0;i<res2.size();i++) {
								if(res2.get(i).getEmail().equals(driver.getEmail())) {
									duplictionList.add(1);				
								}
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
								
								Set<User> userDriver = new HashSet<>();
								userDriver.add(driverParent);
								driver.setUserDriver(userDriver);
								driverRepository.save(driver);
								drivers.add(driver);
								
								if(user.getAccountType().equals(4)) {
						    		userClientDriver saveData = new userClientDriver();
						    		saveData.setUserid(id);
						    		saveData.setDriverid(driver.getId());
							        userClientDriverRepository.save(saveData);
						    		
						    	}
								
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

	/**
	 * edit driver with id in body is mandatory
	 */
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
								User parent = null;
								if(user.getAccountType().equals(4)) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
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
									
									List<Long> driversData = userClientDriverRepository.getDriver(id,driver.getId());
									if(driversData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								}
								else {
									parent=user;
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
									if(oldPhoto != null) {
							        	if(!oldPhoto.equals("")) {
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
									
										
									
									List<Driver> res1=driverServiceImpl.checkDublicateDriverInEditEmail(driver.getId(),parent.getId(),driver.getName());
									List<Driver> res2=driverServiceImpl.checkDublicateDriverInEditMobileUnique(driver.getId(),driver.getUniqueid(),driver.getMobile_num(),driver.getEmail());

									List<Integer> duplictionList =new ArrayList<Integer>();
									
									if(!res1.isEmpty()) {
										for(int i=0;i<res1.size();i++) {
											
											if(res1.get(i).getName().equals(driver.getName())) {
												duplictionList.add(4);				
											}
											
											
										}
								    	

									}
									
									
									if(!res2.isEmpty()) {
										for(int i=0;i<res2.size();i++) {
											if(res2.get(i).getEmail().equals(driver.getEmail())) {
												duplictionList.add(1);				
											}
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
									
									    Set<Device> device = new HashSet<>();
									    device = driverCheck.getDevice();
									    driver.setDevice(device);
									    
									    Set<Group> groups = new HashSet<>();
									    groups = driverCheck.getGroups();
									    driver.setGroups(groups);
									    							
									    
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

	/**
	 * get unassigned list to but in select list 
	 */
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
		List<Driver> unAssignedDrivers = new ArrayList<>();

		
		
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			if(user == null) {
				
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",unAssignedDrivers);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if(user.getAccountType().equals(4)) {
					 

					List<Long>usersIds= new ArrayList<>();
				    usersIds.add(userId);
                    unAssignedDrivers = driverRepository.getUnassignedDriversByIds(usersIds);
										
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
					logger.info("************************ getUnassignedDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				 }
				
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
				 
				unAssignedDrivers = driverRepository.getUnassignedDrivers(usersIds);
				
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
				logger.info("************************ getUnassignedDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
		}
	}
	
	/**
	 * get driver list to put in selection
	 */
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
	   				 
	    				 List<Long> driverIds = userClientDriverRepository.getDriverIds(userId);

	    				 if(driverIds.size()>0) {
	    					 drivers = driverRepository.getDriverSelectByIds(driverIds);

	    				 }
	    				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	    				 logger.info("************************ getDriverSelect ENDED ***************************");
	    				 return ResponseEntity.ok().body(getObjectResponse);
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

	/**
	 * create device with data in body
	 */
	@Override
	public ResponseEntity<?> createDeviceApp(String TOKEN, Device device, Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ createDevice STARTED ***************************");
		
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);
		
		device.setCreate_date(nowTime);
				
		String image = device.getPhoto();
		device.setPhoto("not_available.png");

		device.setExpired(0);
		
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
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This user doesn't have permission to create device",null);
				 logger.info("************************ createDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if( (device.getId() != null && device.getId() != 0) ) {
            List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id not allowed to create a new device",devices);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(device.getName() == null ||device.getName().equals("")
				|| device.getUniqueid() == null|| device.getUniqueid() == null
				|| device.getSequence_number() == null || device.getSequence_number().equals("")
				|| device.getPlate_num() == null|| device.getPlate_num().equals("")
				|| device.getLeft_letter() == null || device.getLeft_letter().equals("")
                || device.getMiddle_letter() == null|| device.getMiddle_letter().equals("")
                || device.getRight_letter() == null|| device.getRight_letter().equals("")) {
			
			List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Attributes[name, trackerImei , sequence"
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

				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "Assigning to not found the user",null);
				logger.info("************************ createDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				User parent = null;
				if(userCreater.getAccountType().equals(4)) {
					Set<User>parentClient = userCreater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user cannot add a device",null);
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
			    	return ResponseEntity.status(201).body(getObjectResponse);
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
			    	
			    	if(userCreater.getAccountType().equals(4)) {
			    		userClientDevice saveData = new userClientDevice();
			    		saveData.setUserid(userId);
			    		saveData.setDeviceid(device.getId());
				        userClientDeviceRepository.save(saveData);
			    		
			    	}
			    	
			    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",devices);
					logger.info("************************ createDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
			    }
			}
			
	        
		}
		

		
	}

	
	/**
	 * edit device data by id of device in body is mandatory
	 */
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
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This user doesn't have permission to edit device",null);
				 logger.info("************************ editDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
	
		if(device.getId() == null || device.getName() == null ||device.getName().equals("") 
			|| device.getUniqueid() == null || device.getUniqueid().equals("")
			|| device.getSequence_number() == null || device.getSequence_number().equals("")
			|| device.getPlate_num() == null || device.getPlate_num().equals("")
			|| device.getLeft_letter() == null || device.getLeft_letter() == null
			|| device.getRight_letter() == null || device.getRight_letter().equals("")
			|| device.getMiddle_letter() == null || device.getMiddle_letter().equals("")	) {
			
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Atrributes[id ,name, trackerImei , sequence" + 
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
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "You are not allowed to edit this device",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}else {
				  
					User parent =null;
					for(User object : parentClient) {
						parent = object;
					}
					Set<User> deviceParent = oldDevice.getUser();
					if(deviceParent.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "This device is not assigned to another user ,you are not allowed to edit this device",null);
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
				
				List<Long> devices = userClientDeviceRepository.getDevice(userId,device.getId());
				if(devices.isEmpty()) {
						isParent = false;
				}
				else {
						isParent = true;
				}
			}
			if(!deviceServiceImpl.checkIfParent(oldDevice , loggedUser) && ! isParent) {
				getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "You are not allowed to edit this device",null);
				logger.info("************************ editDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Set<User> userCreater=new HashSet<>();
			userCreater = oldDevice.getUser();
	        device.setUser(userCreater);
	        
			Set<Driver> driver=new HashSet<>();
			driver = oldDevice.getDriver();
	        device.setDriver(driver);

			Set<com.example.examplequerydslspringdatajpamaven.entity.Attribute> attributeDevice=new HashSet<>();
			attributeDevice = oldDevice.getAttributeDevice();
	        device.setAttributeDevice(attributeDevice);
	        
	        Set<Geofence> geofences=new HashSet<>();
	        geofences = oldDevice.getGeofence();
	        device.setGeofence(geofences);
	        
	        Set<Group> groups=new HashSet<>();
	        groups = oldDevice.getGroups();
	        device.setGroups(groups);
	        
	        Set<Notification> notificationDevice=new HashSet<>();
	        notificationDevice = oldDevice.getNotificationDevice();
	        device.setNotificationDevice(notificationDevice);
	        
	        List<Integer> duplictionList = deviceServiceImpl.checkDeviceDuplication(device);
	        if(duplictionList.size()>0)
		    {
		    	getObjectResponse = new GetObjectResponse( 201, "Duplication in data",duplictionList);
		    	logger.info("************************ createDevice ENDED ***************************");
		    	return ResponseEntity.status(201).body(getObjectResponse);
		    }
	        else {
				DecodePhoto decodePhoto=new DecodePhoto();
	        	String oldPhoto=oldDevice.getPhoto();

				if(oldPhoto != null) {
		        	if(!oldPhoto.equals("")) {
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

	/**
	 * delete device by id
	 */
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
					List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
					if(devices.isEmpty()) {
							isParent = false;
					}
					else {
							isParent = true;
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
		     device.setDelete_date(date);
		     
		     Set<Driver> drivers =new HashSet<>() ;
	         device.setDriver(drivers);

	         Set<Group> groups =new HashSet<>() ;
	         device.setGroups(groups);
	         
	         Set<com.example.examplequerydslspringdatajpamaven.entity.Attribute> attributeDevice=new HashSet<>();
	         device.setAttributeDevice(attributeDevice);
	        
	         Set<Geofence> geofences=new HashSet<>();
	         device.setGeofence(geofences);
	         
	         Set<Notification> notificationDevice=new HashSet<>();
	         device.setNotificationDevice(notificationDevice);
	         
			 deviceRepository.save(device);
		     
		     List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.OK.value(), "success",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.ok().body(getObjectResponse);
		 }
		 
	}

	/**
	 * get device by id to edit
	 */
	@Override
	public ResponseEntity<?> findDeviceByIdApp(String TOKEN, Long deviceId, Long userId) {
		// TODO Auto-generated method stub
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
			if(device.getDelete_date() != null) {

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
					List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
					if(devices.isEmpty()) {
							isParent = false;
					}
					else {
							isParent = true;
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

	/**
	 * assign driver to one device only
	 */
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
		if(userId == 0) {
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
		
		
		

		if(deviceId == 0 ) {
			
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
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
				   }
				   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this device ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
				   }
				if(driverId == 0) {
					Set<Driver> drivers=new HashSet<>() ;
					drivers= device.getDriver();
			        if(drivers.isEmpty()) {
			        	List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
			        }
			        else {

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

	/**
	 * assign multi geofences to one device
	 */
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
		if(userId == 0) {
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
		if(deviceId == 0){
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",devices);
			logger.info("************************ assignDeviceToGeofences ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}else {
			 Device device = deviceServiceImpl.findById(deviceId);
			 if(device == null) {
				  List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
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
					List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
					if(devices.isEmpty()) {
							isParent = false;
					}
					else {
							isParent = true;
					}
			   }
			   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this device ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			   }
			}
			if(geoIds.length == 0) {
                Set<Geofence> geofences = device.getGeofence();
                if(geofences.isEmpty()) {
                	 List<Device> devices = null;
 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No geofences to assign or remove",devices);
 					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
 					return ResponseEntity.status(404).body(getObjectResponse);
                }
                else {
    				
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

	/**
	 * get device driver to view in selected
	 */
	@Override
	public ResponseEntity<?> getDeviceDriverApp(String TOKEN, Long deviceId) {
		// TODO Auto-generated method stub
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
		if(deviceId == 0) {
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

						deviceDriver.add(driver);
					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",deviceDriver);
					logger.info("************************ getDeviceToDriver ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				}
			}
		}
		
		
		
	}

	/**
	 * get selected geofences to view
	 */
	@Override
	public ResponseEntity<?> getDeviceGeofencesApp(String TOKEN, Long deviceId) {
		// TODO Auto-generated method stub
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
		if(deviceId == 0) {
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

						deviceGeofences.add(geofence);
					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",deviceGeofences);
					logger.info("************************ getDeviceGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				}
			}
		}
		
		
	}

	/**
	 * edit photo of user by data in body "photo"
	 */
	@Override
	public ResponseEntity<?> updateProfilePhotoApp(String TOKEN, Map<String, String> data, Long userId) {
		logger.info("************************ updateProfile STARTED ***************************");

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User user = profileServiceImpl.getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					if(data.get("photo") == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Photo is Required",users);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						
						
						DecodePhoto decodePhoto=new DecodePhoto();
						String photo = data.get("photo").toString();
						if(user.getPhoto() != null) {
							if(!user.getPhoto().equals("")) {
								if(!user.getPhoto().equals("not_available.png")) {
									decodePhoto.deletePhoto(user.getPhoto(), "user");
								}
							}
						}
						
						if(photo == "") {
							
							user.setPhoto("not_available.png");				
						}
						else {
							if(photo.startsWith("data:image")) {
								user.setPhoto(decodePhoto.Base64_Image(photo,"user"));
							}
					    }
						profileRepository.save(user);
						users.add(user);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
						logger.info("************************ updateProfile ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);

					}
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	}

	/**
	 * get info of user profile
	 */
	@Override
	public ResponseEntity<?> getUserInfoApp(String TOKEN, Long userId) {
		logger.info("************************ getUserInfo STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = profileRepository.findOne(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					
					users.add(user);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",users);
					logger.info("************************ getUserInfo ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}

	/**
	 * cahnge password of user by data in body (oldPassword,newPassword) and check to get edit in profile or in user component
	 */
	@Override
	public ResponseEntity<?> updateProfilePasswordApp(String TOKEN, Map<String, String> data, String check,
			Long userId) {
		logger.info("************************ updateProfilePassword STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = profileServiceImpl.getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					if(check.equals("")) {
						if(data.get("oldPassword") == null || data.get("newPassword") == null ||
								data.get("oldPassword") == "" || data.get("newPassword") == "") {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "newPassword and oldPassword is Required",users);
							return ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							String hashedPassword = userServiceImpl.getMd5(data.get("oldPassword").toString());
							String newPassword= userServiceImpl.getMd5(data.get("newPassword").toString());
							String oldPassword= user.getPassword();
							
							if(hashedPassword.equals(oldPassword)){
								user.setPassword(newPassword);
								profileRepository.save(user);
								users.add(user);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
								logger.info("************************ updateProfilePassword ENDED ***************************");
								return ResponseEntity.ok().body(getObjectResponse);

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Wrong oldPassword",users);
								return ResponseEntity.status(404).body(getObjectResponse);

							}
							

						}
					}
					else {


						if(data.get("newPassword") == null || data.get("newPassword") == "") {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "newPassword is Required",users);
							return ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							String newPassword= userServiceImpl.getMd5(data.get("newPassword").toString());
							
							user.setPassword(newPassword);
							profileRepository.save(user);
							users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
							logger.info("************************ updateProfilePassword ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);

							
							

						}
					}
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
		
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	}

	/**
	 * get chart number of status online , offline , out of network and total devices
	 */
	@Override
	public ResponseEntity<?> getStatusApp(String TOKEN, Long userId) {
		logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
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
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		Integer onlineDevices = 0;
		Integer outOfNetworkDevices = 0;
		Integer totalDevices = 0;
		Integer offlineDevices = 0;
		
		
		userServiceImpl.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 if(loggedUser.getAccountType().equals(4)) {
			 
			List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);

			
			if(deviceIds.size()>0) {
				List<String> onlineDeviceIds = deviceRepository.getNumberOfOnlineDevicesListByIds(deviceIds);
				List<String> OutDeviceIds = deviceRepository.getNumberOfOutOfNetworkDevicesListByIds(deviceIds);
				
				 onlineDevices = onlineDeviceIds.size();
				 outOfNetworkDevices = OutDeviceIds.size();
				
				totalDevices = deviceRepository.getTotalNumberOfUserDevicesByIds(deviceIds);
				offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;

			}
			
			
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("total_devices", totalDevices);
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

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
			 


			
			List<String> onlineDeviceIds = deviceRepository.getNumberOfOnlineDevicesList(usersIds);
			List<String> OutDeviceIds = deviceRepository.getNumberOfOutOfNetworkDevicesList(usersIds);
			
			 onlineDevices = onlineDeviceIds.size();
			 outOfNetworkDevices = OutDeviceIds.size();
			
			totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
			offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;

			
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("total_devices", totalDevices);
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
	}

	/**
	 * get chart of drier and device working hours today and number of ignition on off
	 */
	@Override
	public ResponseEntity<?> getMergeHoursIgnitionApp(String TOKEN, Long userId) {
		logger.info("************************ getIgnitionMotion STARTED ***************************");
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
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getIgnition ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userServiceImpl.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 if(loggedUser.getAccountType().equals(4)) {
			
			 List<Long> allDevices = userClientDeviceRepository.getDevicesIds(userId);
			 List<Map> finalData = new ArrayList<>();

			 if(allDevices.size()>0) {
				 List<String> positionIds = deviceRepository.getAllPositionsObjectIdsByIds(allDevices);
				
				 
				 Integer ignitionON= 0;
				 Integer ignitionOFF= 0;

				 ignitionON = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", true);
				 ignitionOFF = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", false);
				 

				List<Map> data = new ArrayList<>();
				
				data = mongoPositionRepo.getCharts(positionIds);

			    
			    
			    Map dev = new HashMap();
			    dev.put("ignition_on", ignitionON);
			    dev.put("ignition_off" ,ignitionOFF);
				
				
			    Map ig = new HashMap();
			    ig.put("status",dev);
			    ig.put("hours",data);

			    finalData.add(ig);
				
				
			 }
			 
			 
			
			
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",finalData,finalData.size());
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

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
			 
			 List<String> positionIds = deviceRepository.getAllPositionsObjectIds(usersIds);

			 Integer ignitionON= 0;
			 Integer ignitionOFF= 0;

			 ignitionON = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", true);
			 ignitionOFF = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", false);



			List<Map> data = new ArrayList<>();
			
			data = mongoPositionRepo.getCharts(positionIds);
			List<Map> finalData = new ArrayList<>();

		   
		    
		    Map dev = new HashMap();
		    dev.put("ignition_on", ignitionON);
		    dev.put("ignition_off" ,ignitionOFF);
			
			
		    Map ig = new HashMap();
		    ig.put("status",dev);
		    ig.put("hours",data);

		    finalData.add(ig);
			
			
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",finalData,finalData.size());
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
	}

	/**
	 * get chart of top 10 distance and fuel from traccar summary report
	 */
	@Override
	public ResponseEntity<?> getDistanceFuelEngineApp(String TOKEN, Long userId) {
		logger.info("************************ getDistanceFuelEngine STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userServiceImpl.resetChildernArray();
		 List<Long>userIds= new ArrayList<>();

		 if(loggedUser.getAccountType().equals(4)) {
			
			List<Long> allDevices = userClientDeviceRepository.getDevicesIds(userId);
			List<Map> data = new ArrayList<>();

			if(allDevices.size()>0) {

				
				List<SummaryReport> summaryReport = new ArrayList<>();
			 
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
			  
			    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
				inputFormat.setLenient(false);
				outputFormat.setLenient(false);
				Date dateTo;
				Date dateFrom = new Date();
				Calendar c = Calendar.getInstance(); 
				c.setTime(dateFrom); 
				c.add(Calendar.DATE, 1);
				dateTo = c.getTime();
				String from = "";
				String to = "";
				from = outputFormat.format(dateFrom);
				to = outputFormat.format(dateTo);
					
				

			  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
				        .queryParam("type", "allEvents")
				        .queryParam("from", from)
				        .queryParam("to", to)
				        .queryParam("page", 1)
				        .queryParam("start", 0)
				        .queryParam("limit",25).build();
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
		
					  for(SummaryReport summaryReportOne : summaryReport ) {
						  
							Map devicesList = new HashMap();
		
		
						  Double totalDistance = 0.0 ;
						  double roundOffDistance = 0.0;
						  Double litres=10.0;
						  double roundOffFuel=0.0;
						  Double Fuel =0.0;
						  Double distance=0.0;
						  
						  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
							  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
							  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
							  summaryReportOne.setDistance(Double.toString(roundOffDistance));


						  }
						  
						  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
						  if(device != null) {
							  Set<Driver>  drivers = device.getDriver();
							  for(Driver driver : drivers ) {
		
								 summaryReportOne.setDriverName(driver.getName());
								 
							  }

							  
							  if(device.getFuel() != null) {
									if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=obj.getDouble("fuelPerKM");
											
										}
									}
							   }
							  
							 
						   }
						  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
						  if(distance > 0) {
							Fuel = (distance*litres)/100;
						  }
	
						  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
						  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
						  
							devicesList.put("spentFuel", summaryReportOne.getSpentFuel());
							devicesList.put("distance", summaryReportOne.getDistance());
							devicesList.put("deviceId", summaryReportOne.getDeviceId());
							devicesList.put("deviceName", summaryReportOne.getDeviceName());
							
							if(data.size() == 10) {
					    		Double newData = Double.parseDouble( devicesList.get("distance").toString() );
		
						    	for(int k=0;k<data.size();k++) {
						    		Double oldData = Double.parseDouble( data.get(k).get("distance").toString() );
		
						    		if(newData > oldData) {
		
						    			data.get(k).replace("spentFuel", devicesList.get("spentFuel"));
						    			data.get(k).replace("distance", devicesList.get("distance"));
						    			data.get(k).replace("deviceId", devicesList.get("deviceId"));
						    			data.get(k).replace("deviceName", devicesList.get("deviceName"));
						    			break;
		
						    		}
		
						    	
		
						    	}
		
						    }
						    if(data.size() < 10) {
								data.add(devicesList);
						    }
							
						 }
						  
				  }
		    
			}


		
		
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		 }
		 else {
			userIds.add(userId);
			List<SummaryReport> summaryReport = new ArrayList<>();


			List<Long>allDevices= new ArrayList<>();
			allDevices = deviceRepository.getDevicesUsers(userIds);

		 
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
		  
		    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);
			Date dateTo;
			Date dateFrom = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(dateFrom); 
			c.add(Calendar.DATE, 1);
			dateTo = c.getTime();
			String from = "";
			String to = "";
			from = outputFormat.format(dateFrom);
			to = outputFormat.format(dateTo);
				
			

		  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
			        .queryParam("type", "allEvents")
			        .queryParam("from", from)
			        .queryParam("to", to)
			        .queryParam("page", 1)
			        .queryParam("start", 0)
			        .queryParam("limit",25).build();
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
			  List<Map> data = new ArrayList<>();
	
			  if(summaryReport.size()>0) {
	
				  for(SummaryReport summaryReportOne : summaryReport ) {
					  
						Map devicesList = new HashMap();
	
					  Double totalDistance = 0.0 ;
					  double roundOffDistance = 0.0;
					  Double litres=10.0;
					  double roundOffFuel=0.0;
					  Double Fuel =0.0;
					  Double distance=0.0;
					  
					  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  summaryReportOne.setDistance(Double.toString(roundOffDistance));


					  }
					  
					  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
					  if(device != null) {
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {
	
							 summaryReportOne.setDriverName(driver.getName());
							 
						  }
						  
						  
						  if(device.getFuel() != null) {
								if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
									JSONObject obj = new JSONObject(device.getFuel());	
									if(obj.has("fuelPerKM")) {
										litres=obj.getDouble("fuelPerKM");
										
									}

								}
						  }
						  
					   }
						  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
						  if(distance > 0) {
							Fuel = (distance*litres)/100;
						  }
	
						  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
						  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
					  
						devicesList.put("spentFuel", summaryReportOne.getSpentFuel());
						devicesList.put("distance", summaryReportOne.getDistance());
						devicesList.put("deviceId", summaryReportOne.getDeviceId());
						devicesList.put("deviceName", summaryReportOne.getDeviceName());
						
						if(data.size() == 10) {
				    		Double newData = Double.parseDouble( devicesList.get("distance").toString() );
	
					    	for(int k=0;k<data.size();k++) {
					    		Double oldData = Double.parseDouble( data.get(k).get("distance").toString() );
	
					    		if(newData > oldData) {
	
					    			data.get(k).replace("spentFuel", devicesList.get("spentFuel"));
					    			data.get(k).replace("distance", devicesList.get("distance"));
					    			data.get(k).replace("deviceId", devicesList.get("deviceId"));
					    			data.get(k).replace("deviceName", devicesList.get("deviceName"));
					    			break;
	
					    		}
	
					    	
	
					    	}
	
					    }
					    if(data.size() < 10) {
							data.add(devicesList);
					    }
						
					 }
					  
			  }
	    
		
		
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
	}

	/**
	 * get chart of notifications number of each one (offline,online ..etc)
	 */
	@Override
	public ResponseEntity<?> getNotificationsChartApp(String TOKEN, Long userId) {
        logger.info("************************ getNotifications STARTED ***************************");
		
		List<EventReport> notifications = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId != 0) {
		
			User user = userServiceImpl.findById(userId);
			if(user != null) {
				userServiceImpl.resetChildernArray();
				 List<Long>usersIds= new ArrayList<>();

				 if(user.getAccountType() == 4) {
					 Set<User> parentClients = user.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						
						 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.status(404).body(getObjectResponse);
					 }else {
						 User parentClient = new User() ;
						 for(User object : parentClients) {
							 parentClient = object;
						 }
					 }
				 }
				 else {
					 usersIds.add(userId);

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

				    List<Long> allDevices = deviceRepository.getDevicesUsers(usersIds);

				    notifications = mongoEventsRepo.getAllNotificationsTodayChart(allDevices);
					
					List<Map> data = new ArrayList<>();

					Integer deviceOverspeed = 0;
					Integer ignitionOn = 0;
					Integer driverChanged = 0;
					Integer deviceOffline = 0;
					Integer geofenceEnter = 0;
					Integer commandResult = 0;
					Integer deviceMoving = 0;
					Integer textMessage = 0;
					Integer deviceOnline = 0;
					Integer deviceUnknown = 0;
					Integer maintenance = 0;
					Integer alarm = 0;
					Integer deviceFuelDrop = 0;
					Integer ignitionOff = 0;
					Integer geofenceExit = 0;
					Integer deviceStopped = 0;

					if(notifications.size()>0) {
						for(int i=0;i<notifications.size();i++) {

							if(notifications.get(i).getEventType().equals("deviceStopped")) {
								deviceOverspeed = deviceOverspeed+1;
							}
							if(notifications.get(i).getEventType().equals("ignitionOn")) {
								ignitionOn = ignitionOn+1;
							}
							if(notifications.get(i).getEventType().equals("driverChanged")) {
								driverChanged = driverChanged+1;
							}
							if(notifications.get(i).getEventType().equals("deviceOffline")) {
								deviceOffline = deviceOffline+1;
							}
							if(notifications.get(i).getEventType().equals("geofenceEnter")) {
								geofenceEnter = geofenceEnter+1;
							}
							if(notifications.get(i).getEventType().equals("commandResult")) {
								commandResult = commandResult+1;
							}
							if(notifications.get(i).getEventType().equals("deviceMoving")) {
								deviceMoving = deviceMoving+1;
							}
							if(notifications.get(i).getEventType().equals("textMessage")) {
								textMessage = textMessage+1;
							}
							if(notifications.get(i).getEventType().equals("deviceOnline")) {
								deviceOnline = deviceOnline+1;
							}
							if(notifications.get(i).getEventType().equals("deviceUnknown")) {
								deviceUnknown = deviceUnknown+1;
							}
							if(notifications.get(i).getEventType().equals("maintenance")) {
								maintenance = maintenance+1;
							}
							if(notifications.get(i).getEventType().equals("alarm")) {
								alarm = alarm+1;
							}
							if(notifications.get(i).getEventType().equals("deviceFuelDrop")) {
								deviceFuelDrop = deviceFuelDrop+1;
							}
							if(notifications.get(i).getEventType().equals("ignitionOff")) {
								ignitionOff = ignitionOff+1;
							}
							if(notifications.get(i).getEventType().equals("geofenceExit")) {
								geofenceExit = geofenceExit+1;
							}
							if(notifications.get(i).getEventType().equals("deviceStopped")) {
								deviceStopped = deviceStopped+1;
							}
							
						}
							
					}
					
					
					Map notificationList1 = new HashMap();
					notificationList1.put("type", "deviceOverspeed");
					notificationList1.put("count", deviceOverspeed);
					data.add(notificationList1);
					
					Map notificationList2 = new HashMap();
					notificationList2.put("type", "ignitionOn");
					notificationList2.put("count", ignitionOn);
					data.add(notificationList2);
					
					Map notificationList3 = new HashMap();
					notificationList3.put("type", "driverChanged");
					notificationList3.put("count", driverChanged);
					data.add(notificationList3);
					
					Map notificationList4 = new HashMap();
					notificationList4.put("type", "deviceOffline");
					notificationList4.put("count", deviceOffline);
					data.add(notificationList4);
					
					Map notificationList5 = new HashMap();
					notificationList5.put("type", "geofenceEnter");
					notificationList5.put("count", geofenceEnter);
					data.add(notificationList5);
					
					Map notificationList6 = new HashMap();
					notificationList6.put("type", "commandResult");
					notificationList6.put("count", commandResult);
					data.add(notificationList6);
					
					Map notificationList7 = new HashMap();
					notificationList7.put("type", "deviceMoving");
					notificationList7.put("count", deviceMoving);
					data.add(notificationList7);
					
					Map notificationList8 = new HashMap();
					notificationList8.put("type", "textMessage");
					notificationList8.put("count", textMessage);
					data.add(notificationList8);
					
					Map notificationList9 = new HashMap();
					notificationList9.put("type", "deviceOnline");
					notificationList9.put("count", deviceOnline);
					data.add(notificationList9);
					
					Map notificationList10 = new HashMap();
					notificationList10.put("type", "deviceUnknown");
					notificationList10.put("count", deviceUnknown);
					data.add(notificationList10);
					

					Map notificationList11 = new HashMap();
					notificationList11.put("type", "maintenance");
					notificationList11.put("count", maintenance);
					data.add(notificationList11);
					

					Map notificationList12 = new HashMap();
					notificationList12.put("type", "alarm");
					notificationList12.put("count", alarm);
					data.add(notificationList12);
					
					Map notificationList13 = new HashMap();
					notificationList13.put("type", "deviceFuelDrop");
					notificationList13.put("count", deviceFuelDrop);
					data.add(notificationList13);
					
					Map notificationList14 = new HashMap();
					notificationList14.put("type", "ignitionOff");
					notificationList14.put("count", ignitionOff);
					data.add(notificationList14);
					

					Map notificationList15 = new HashMap();
					notificationList15.put("type", "geofenceExit");
					notificationList15.put("count", geofenceExit);
					data.add(notificationList15);
					
					Map notificationList16 = new HashMap();
					notificationList16.put("type", "deviceStopped");
					notificationList16.put("count", deviceStopped);
					data.add(notificationList16);
					
					
					
							
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,notifications.size());
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	/**
	 * get data of events of one or more device and group from mongo collection tc_events
	 */
	@Override
	public ResponseEntity<?> getEventsReportApp(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String type, String search, Long userId) {
		
		
		logger.info("************************ getEventsReport STARTED ***************************");		
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "EVENT", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCENTER", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCEEXIT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENT or GEOFENCENTER or GEOFENCEEXIT list",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									logger.info("************************ getEventsReport ENDED ***************************");		 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
										logger.info("************************ getEventsReport ENDED ***************************");		 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								logger.info("************************ getEventsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							logger.info("************************ getEventsReport ENDED ***************************");		
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								logger.info("************************ getEventsReport ENDED ***************************");		
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",eventReport);
						logger.info("************************ getEventsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}


			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
			
			if(type.equals("")) {
				if(!TOKEN.equals("Schedule")) {
					eventReport = mongoEventsRepo.getEventsWithoutType(allDevices, offset, dateFrom, dateTo);
					if(eventReport.size()>0) {
						size = mongoEventsRepo.getEventsWithoutTypeSize(allDevices, dateFrom, dateTo);
						for(int i=0;i<eventReport.size();i++) {
							
							MongoPositions pos = mongoPositionsRepository.findById(eventReport.get(i).getPositionId());
							
							if(pos != null) {
								eventReport.get(i).setLatitude(pos.getLatitude());
								eventReport.get(i).setLongitude(pos.getLongitude());
								
							}
							
							Device device = deviceRepository.getOne(eventReport.get(i).getDeviceId());
							eventReport.get(i).setDeviceName(device.getName());
							Set<Driver> drivers = device.getDriver();
							for(Driver driver : drivers) {
								eventReport.get(i).setDriverId(driver.getId());
								eventReport.get(i).setDriverName(driver.getName());
							}
							
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes().toString());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
				}
				else {
					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);
					if(eventReport.size()>0) {
						for(int i=0;i<eventReport.size();i++) {
							
							MongoPositions pos = mongoPositionsRepository.findById(eventReport.get(i).getPositionId());
							
							if(pos != null) {
								eventReport.get(i).setLatitude(pos.getLatitude());
								eventReport.get(i).setLongitude(pos.getLongitude());
								
							}
							
							Device device = deviceRepository.getOne(eventReport.get(i).getDeviceId());
							eventReport.get(i).setDeviceName(device.getName());
							Set<Driver> drivers = device.getDriver();
							for(Driver driver : drivers) {
								eventReport.get(i).setDriverId(driver.getId());
								eventReport.get(i).setDriverName(driver.getName());
							}
							
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
				}
				
			}
			else {
				if(!TOKEN.equals("Schedule")) {
					eventReport = mongoEventsRepo.getEventsWithType(allDevices, offset, dateFrom, dateTo, type);
					if(eventReport.size()>0) {
						size = mongoEventsRepo.getEventsWithTypeSize(allDevices, dateFrom, dateTo, type);
						for(int i=0;i<eventReport.size();i++) {
							MongoPositions pos = mongoPositionsRepository.findById(eventReport.get(i).getPositionId());
							
							if(pos != null) {
								eventReport.get(i).setLatitude(pos.getLatitude());
								eventReport.get(i).setLongitude(pos.getLongitude());
								
							}
							Device device = deviceRepository.getOne(eventReport.get(i).getDeviceId());
							eventReport.get(i).setDeviceName(device.getName());
							Set<Driver> drivers = device.getDriver();
							for(Driver driver : drivers) {
								eventReport.get(i).setDriverId(driver.getId());
								eventReport.get(i).setDriverName(driver.getName());
							}
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
				}
				else {
					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);
					if(eventReport.size()>0) {
						for(int i=0;i<eventReport.size();i++) {
							
							MongoPositions pos = mongoPositionsRepository.findById(eventReport.get(i).getPositionId());
							
							if(pos != null) {
								eventReport.get(i).setLatitude(pos.getLatitude());
								eventReport.get(i).setLongitude(pos.getLongitude());
								
							}
							Device device = deviceRepository.getOne(eventReport.get(i).getDeviceId());
							eventReport.get(i).setDeviceName(device.getName());
							Set<Driver> drivers = device.getDriver();
							for(Driver driver : drivers) {
								eventReport.get(i).setDriverId(driver.getId());
								eventReport.get(i).setDriverName(driver.getName());
							}
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
				}
				
			}
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
			logger.info("************************ getEventsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}	
		
	}

	/**
	 * get data of device hours for each day of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getDeviceWorkingHoursApp(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId) {
		
		logger.info("************************ getDeviceWorkingHours STARTED ***************************");

		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get device working hours list",deviceHours);
					logger.info("************************ getDeviceWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
									logger.info("************************ getDeviceWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
										logger.info("************************ getDeviceWorkingHours ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",deviceHours);
								logger.info("************************ getDeviceWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									
									
									

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									
								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length !=0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							logger.info("************************ getDeviceWorkingHours ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								logger.info("************************ getDeviceWorkingHours ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",deviceHours);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					

					allDevices.add(deviceId);
					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
			logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}


			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }

			if(!TOKEN.equals("Schedule")) {
				
				deviceHours = mongoPositionRepo.getDeviceWorkingHours(allDevices,offset,dateFrom,dateTo);			
					
				if(deviceHours.size()>0) {
   				    size=mongoPositionRepo.getDeviceWorkingHoursSize(allDevices,dateFrom, dateTo);

					
				
				}
			}
			else {
				deviceHours = mongoPositionRepo.getDeviceWorkingHoursScheduled(allDevices,dateFrom,dateTo);			

				
			}
				
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
			logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}		  
				  
	}
	
	/**
	 * get data of custom attributes (ignition,motion ...etc) of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getCustomReportApp(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String search, Long userId, String custom, String value) {
		
		
		logger.info("************************ getCustomReport STARTED ***************************");

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
			 logger.info("************************ getCustomReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "CUSTOMREPORT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get custom report list",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
									 logger.info("************************ getCustomReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
										 logger.info("************************ getCustomReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",deviceHours);
								 logger.info("************************ getCustomReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									
									
									

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									
								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length !=0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							 logger.info("************************ getCustomReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								 logger.info("************************ getCustomReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",deviceHours);
						 logger.info("************************ getCustomReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					

					allDevices.add(deviceId);
					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);
			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	        if(custom.equals("")) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no custom selected",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	        if(value.equals("")) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no value selected",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
			
			if(!TOKEN.equals("Schedule")) {

				deviceHours = mongoPositionRepo.getDeviceCustom(allDevices, offset, dateFrom, dateTo, custom, value);
				if(deviceHours.size()>0) {
					size=mongoPositionRepo.getDeviceCustomSize(allDevices,dateFrom, dateTo,custom,value);

					
				
				}
			}
			else {

				deviceHours = mongoPositionRepo.getDeviceCustomScheduled(allDevices,dateFrom,dateFrom,custom,value);
				
			}
				
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
			 logger.info("************************ getCustomReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}		    
			
	}
	
	/**
	 * get data of driver hours for each day of one or more driver and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getDriverWorkingHoursApp(String TOKEN, Long[] driverIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId) {
		 logger.info("************************ getDriverWorkingHours STARTED ***************************");

			
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
			 logger.info("************************ getDriverWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVERWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver working hours list",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();
		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
									 logger.info("************************ getDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
										 logger.info("************************ getDriverWorkingHours ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",driverHours);
								 logger.info("************************ getDriverWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								
								if(group.getType().equals("driver")) {
									allDrivers.addAll(groupRepository.getDriversFromGroup(groupId));

								}
								else if(group.getType().equals("device")) {
									allDrivers.addAll(groupRepository.getDriverFromDevices(groupId));
									
								}
								else if(group.getType().equals("geofence")) {
									allDrivers.addAll(groupRepository.getDriversFromGeofence(groupId));

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								 logger.info("************************ getDriverWorkingHours ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
									 logger.info("************************ getDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",driverHours);
							 logger.info("************************ getDriverWorkingHours ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}
		
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for drivers of groups or drivers that you selected",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	       
	       
			
			if(!TOKEN.equals("Schedule")) {
				
				driverHours = mongoPositionRepo.getDriverWorkingHours(allDevices,offset,dateFrom,dateTo);			

				if(driverHours.size()>0) {
  				    size=mongoPositionRepo.getDriverWorkingHoursSize(allDevices,dateFrom,dateTo);

					
				
				}
				
			}
			else {
				driverHours = mongoPositionRepo.getDriverWorkingHoursScheduled(allDevices,dateFrom,dateFrom);			

			}
				
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
			 logger.info("************************ getDriverWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}	
	}
	
	/**
	 * get data of driving more than 4 hours of one or more device , driver and group from traccar
	 */
	@Override
	public ResponseEntity<?> getDriveMoreThanReportApp(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getDriveMoreThanReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		List<TripReport> tripData = new ArrayList<>();


		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVEMORETHAN", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
					logger.info("************************ getDriveMoreThanReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									logger.info("************************ getDriveMoreThanReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										logger.info("************************ getDriveMoreThanReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
                            }
                            

							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									logger.info("************************ getDriveMoreThanReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
							logger.info("************************ getDriveMoreThanReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							logger.info("************************ getDriveMoreThanReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						logger.info("************************ getDriveMoreThanReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 tripReport = (List<TripReport>) returnFromTraccarApp(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 if(tripReport.size()>0) {

			  for(TripReport tripReportOne : tripReport ) {
				  
				  Long hours = (long) 0;
				  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
					  Long data = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) ); 
					  hours=   TimeUnit.MILLISECONDS.toHours(data) ;
				  
				  }
				  

				  if(hours > 4) {


					  Double totalDistance = 0.0 ;
					  double roundOffDistance = 0.0;
					  double roundOffFuel = 0.0;
					  Double litres=10.0;
					  Double Fuel =0.0;
					  Double distance=0.0;
					  
					  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());
					  Set<Driver>  drivers = device.getDriver();
					  for(Driver driver : drivers ) {

						 tripReportOne.setDriverName(driver.getName());
						 tripReportOne.setDriverUniqueId(driver.getUniqueid());

						
						 
					  }
					  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setDistance(Double.toString(roundOffDistance));


					  }
					  if(device.getFuel() != null) {
							if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
								JSONObject obj = new JSONObject(device.getFuel());	
								if(obj.has("fuelPerKM")) {
									litres=obj.getDouble("fuelPerKM");
									
								}
							}
					   }
					  
					 
				   
				  distance = Double.parseDouble(tripReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }

				  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
				  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));
					  
					  
					  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
						  Long time=(long) 0;

						  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

						  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
						  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
						  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
						  
						  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
						  tripReportOne.setDuration(totalHours);
					  }
					  
					  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed()) * (1.852) );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


					  }
					  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed())  * (1.852) );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


					  }
					  
					  
					  
					  
					  tripData.add(tripReportOne);
					  
				  }
				  
				  

			  }
		  }

		  
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripData,tripData.size());
			logger.info("************************ getDriveMoreThanReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get data of events of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getEventsReportByTypeApp(String TOKEN, Long[] deviceIds, Long[] groupIds, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getEventsReportByType STARTED ***************************");

		List<EventReportByCurl> eventReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "EVENT", "list") && !userRoleService.checkUserHasPermission(userId, "GEOFENCENTER", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCEEXIT", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENT or GEOFENCENTER or GEOFENCEEXIT list",eventReport);
					logger.info("************************ getEventsReportByType ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",eventReport);
									logger.info("************************ getEventsReportByType ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",eventReport);
										logger.info("************************ getEventsReportByType ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",eventReport);
								logger.info("************************ getEventsReportByType ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							logger.info("************************ getEventsReportByType ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								logger.info("************************ getEventsReportByType ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",eventReport);
						logger.info("************************ getEventsReportByType ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 eventReport = (List<EventReportByCurl>) returnFromTraccarApp(eventsUrl,"events",allDevices, from, to, type, page, start, limit).getBody();

		 if(eventReport.size()>0) {

			  for(EventReportByCurl eventReportOne : eventReport ) {
				  Device device= deviceServiceImpl.findById(eventReportOne.getDeviceId());
				  Set<Driver>  drivers = device.getDriver();
				  for(Driver driver : drivers ) {

					  eventReportOne.setDriverName(driver.getName());
					  eventReportOne.setDeviceName(device.getName());


					 
				}
				  
				  if(eventReportOne.getType().equals("alarm")) {

			        ObjectMapper objectMapper = new ObjectMapper();
			        Map<String, String> map = objectMapper.convertValue(eventReportOne.getAttributes(), Map.class);
					eventReportOne.setType(map.get("alarm"));
				 }
				  
				  

				
					
			  }
		  }	  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
			logger.info("************************ getEventsReportByType ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get data of sensor (weight,sensor1,sensor2) of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getSensorsReportApp(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String search, Long userId) {
		 logger.info("************************ getSensorsReport STARTED ***************************");

		List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
			 logger.info("************************ getSensorsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
									 logger.info("************************ getSensorsReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
										 logger.info("************************ getSensorsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",positionsList);
								 logger.info("************************ getSensorsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
							 logger.info("************************ getSensorsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
								 logger.info("************************ getSensorsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",positionsList);
						 logger.info("************************ getSensorsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		Integer size = 0;

		if(!TOKEN.equals("Schedule")) {
			search = "%"+search+"%";
			positionsList = mongoPositionRepo.getSensorsList(allDevices, offset, dateFrom, dateTo);
			if(positionsList.size()>0) {
				    size=mongoPositionRepo.getSensorsListSize(allDevices,dateFrom, dateTo);

			}
			
		}
		else {
			positionsList = mongoPositionRepo.getPositionsListScheduled(allDevices,dateFrom, dateTo);

		}
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionsList,size);
		 logger.info("************************ getSensorsReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	
	}
	
	/**
	 * get data of all trips number of one or more device, driver and group from traccar
	 */
	@Override
	public ResponseEntity<?> getNumTripsReportApp(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		 logger.info("************************ getNumTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 logger.info("************************ getNumTripsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				 logger.info("************************ getNumTripsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMTRIPS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
				 logger.info("************************ getNumTripsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									 logger.info("************************ getNumTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										 logger.info("************************ getNumTripsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									 logger.info("************************ getNumTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
							 logger.info("************************ getNumTripsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							 logger.info("************************ getNumTripsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						 logger.info("************************ getNumTripsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
	   		    logger.info("************************ getNumTripsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		tripReport = (List<TripReport>) returnFromTraccarApp(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 List<Map> data = new ArrayList<>();
		  if(tripReport.size()>0) {

			  for(Long dev:allDevices) {

				  int count=0;
				  Map devicesStatus = new HashMap();
				  for(TripReport trip: tripReport) {


					  devicesStatus.put("deviceName", null);
					  devicesStatus.put("deviceId" ,null);
					  devicesStatus.put("driverName", null);
					  devicesStatus.put("driverUniqueId",null);
					  devicesStatus.put("trips" ,count);
					  
					  Device device= deviceServiceImpl.findById(dev);
					  
				      devicesStatus.put("deviceName", device.getName());
					  devicesStatus.put("deviceId" ,device.getId());
					  Set<Driver>  drivers = device.getDriver();

					  for(Driver driver : drivers ) {

						  devicesStatus.put("driverName", driver.getName());
						  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  
					  }
					  
					  
					  if(trip.getDeviceId() == dev) {
						  
						  count= count+1;
						  devicesStatus.put("trips" ,count);
					  }
				  }
				  data.add(devicesStatus);

			  }
			  
		  }
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
		  logger.info("************************ getNumTripsReport ENDED ***************************");
		  return  ResponseEntity.ok().body(getObjectResponse);

	}

	/**
	 * get data of all stops number of one or more driver ,device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getNumStopsReportApp(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		 logger.info("************************ getNumStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 logger.info("************************ getNumStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			 
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMSTOPS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									 logger.info("************************ getNumStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										 logger.info("************************ getNumStopsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
									 logger.info("************************ getNumStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",stopReport);
							 logger.info("************************ getNumStopsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							 logger.info("************************ getNumStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						 logger.info("************************ getNumStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		 stopReport = (List<StopReport>) returnFromTraccarApp(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();
		 List<Map> data = new ArrayList<>();

		  if(stopReport.size()>0) {

			  for(Long dev:allDevices) {

				  int count=0;
				  Map devicesStatus = new HashMap();
				  for(StopReport stop: stopReport) {


					  devicesStatus.put("deviceName", null);
					  devicesStatus.put("deviceId" ,null);
					  devicesStatus.put("driverName", null);
					  devicesStatus.put("driverUniqueId",null);
					  devicesStatus.put("stops" ,count);
					  
					  Device device= deviceServiceImpl.findById(dev);
					  
				      devicesStatus.put("deviceName", device.getName());
					  devicesStatus.put("deviceId" ,device.getId());
					  Set<Driver>  drivers = device.getDriver();

					  for(Driver driver : drivers ) {

						  devicesStatus.put("driverName", driver.getName());
						  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  
					  }
					  
					  
					  if(stop.getDeviceId() == dev) {
						  
						  count= count+1;
						  devicesStatus.put("stops" ,count);
					  }
				  }
				  data.add(devicesStatus);

			  }
			 
		  }
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
			 logger.info("************************ getNumStopsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get totalDrivingHours , totalDistance , totalSpentFuel for trips of driver , device and group from traccar
	 */
	@Override
	public ResponseEntity<?> geTotalTripsReportApp(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		 logger.info("************************ geTotalTripsReport STARTED ***************************");

			List<TripReport> tripReport = new ArrayList<>();

			List<Long>allDrivers= new ArrayList<>();
			List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();
			if(TOKEN.equals("")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
				 logger.info("************************ geTotalTripsReport ENDED ***************************");
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			
			if(!TOKEN.equals("Schedule")) {
				if(super.checkActive(TOKEN)!= null)
				{
					return super.checkActive(TOKEN);
				}
			}
			
			
			User loggedUser = new User();
			if(userId != 0) {
				
				loggedUser = userServiceImpl.findById(userId);
				if(loggedUser == null) {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
					 logger.info("************************ geTotalTripsReport ENDED ***************************");
					return  ResponseEntity.status(404).body(getObjectResponse);
				}
			}	
			
			if(!loggedUser.getAccountType().equals(1)) {
				if(!userRoleService.checkUserHasPermission(userId, "TOTALDISTANCE", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
					 logger.info("************************ geTotalTripsReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			
			
			List<Long>allDevices= new ArrayList<>();

			if(groupIds.length != 0) {
				for(Long groupId:groupIds) {
					if(groupId != 0) {
				    	Group group=groupRepository.findOne(groupId);
				    	if(group != null) {
							if(group.getIs_deleted() == null) {
								boolean isParent = false;
								if(loggedUser.getAccountType().equals(4)) {
									Set<User> clientParents = loggedUser.getUsersOfUser();
									if(clientParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										 logger.info("************************ geTotalTripsReport ENDED ***************************");
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										User parent = null;
										for(User object : clientParents) {
											parent = object ;
										}

										Set<User>groupParents = group.getUserGroup();
										if(groupParents.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
											 logger.info("************************ geTotalTripsReport ENDED ***************************");
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
									List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
									if(CheckData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								}
								if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
									 logger.info("************************ geTotalTripsReport ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(group.getType() != null) {
									if(group.getType().equals("driver")) {
										
										allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									

									}
									else if(group.getType().equals("device")) {
										
										allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
										
										
									}
									else if(group.getType().equals("geofence")) {
										
										allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
										

									}
								}

								
							}
				    	}
				    	

					}
				}
			}
			
			if(driverIds.length !=0 ) {
				for(Long driverId : driverIds) {
					if(driverId !=0) {
						
						Driver driver =driverServiceImpl.getDriverById(driverId);
						if(driver != null) {
							boolean isParent = false;
							if(loggedUser.getAccountType() == 4) {
								Set<User>parentClients = loggedUser.getUsersOfUser();
								if(parentClients.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									 logger.info("************************ geTotalTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : parentClients) {
										parent = object ;
									}
									Set<User>driverParent = driver.getUserDriver();
									if(driverParent.isEmpty()) {
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
										 logger.info("************************ geTotalTripsReport ENDED ***************************");
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User  parentObject : driverParent) {
											if(parent.getId() == parentObject.getId()) {
												isParent = true;
												break;
											}
										}
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
							if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
								 logger.info("************************ geTotalTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							allDrivers.add(driverId);
							
							
						}
						
		
					}
				}
			}
			if(allDrivers.size()>0) {
				allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
			}
			
			for(DriverSelect object : allDevicesList) {
				allDevices.add(object.getId());
			}
			
			if(deviceIds.length != 0 ) {
				for(Long deviceId:deviceIds) {
					if(deviceId !=0) {
						Device device =deviceServiceImpl.findById(deviceId);
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								 logger.info("************************ geTotalTripsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>deviceParent = device.getUser();
								if(deviceParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
									 logger.info("************************ geTotalTripsReport ENDED ***************************");
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
							List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
							 logger.info("************************ geTotalTripsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						allDevices.add(deviceId);

						
		
					}
				}
			}

			Date dateFrom;
			Date dateTo;
			if(from.equals("0") || to.equals("0")) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);

			}
			else {
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				inputFormat1.setLenient(false);
				inputFormat.setLenient(false);
				outputFormat.setLenient(false);

				
				try {
					dateFrom = inputFormat.parse(from);
					from = outputFormat.format(dateFrom);
					

				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					try {
						dateFrom = inputFormat1.parse(from);
						from = outputFormat.format(dateFrom);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
				try {
					dateTo = inputFormat.parse(to);
					to = outputFormat.format(dateTo);
					

				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					try {
						dateTo = inputFormat1.parse(to);
						to = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
				
				
				Date today=new Date();

				if(dateFrom.getTime() > dateTo.getTime()) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				Integer size = 0;
				
				String appendString="";
		
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
				
				String[] data = {};
				if(!appendString.equals("")) {
			        data = appendString.split(",");

				}
		        

		        for(String d:data) {

		        	if(!allDevices.contains(Long.parseLong(d))) {
			        	allDevices.add(Long.parseLong(d));
		        	}
		        }
		        
		        if(allDevices.isEmpty()) {

		        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
		   		 logger.info("************************ geTotalTripsReport ENDED ***************************");
		        	return  ResponseEntity.badRequest().body(getObjectResponse);
		        }
			}
			tripReport = (List<TripReport>) returnFromTraccarApp(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();
			
			List<Map> data = new ArrayList<Map>();

			if(tripReport.size()>0) {

				  for(Long dev:allDevices) {
					  
					  Double totalDistance = 0.0 ;
					  double roundOffDistance = 0.0;
					  Long time = (long) 0;
					  Double totalFuel=0.0;
					  double roundOffFuel = 0.0;
					  String totalDuration = "00:00:00";

					  Map devicesStatus = new HashMap();
					  for(TripReport tripReportOne: tripReport) {
						  devicesStatus.put("deviceName", null);
						  devicesStatus.put("deviceId" ,null);
						  devicesStatus.put("driverName", null);
						  devicesStatus.put("driverUniqueId",null);
						  devicesStatus.put("totalDrivingHours",totalDuration);
					      devicesStatus.put("totalDistance", roundOffDistance);
					      devicesStatus.put("totalSpentFuel", roundOffFuel);
						  
						  Device device= deviceServiceImpl.findById(dev);
						  
					      devicesStatus.put("deviceName", device.getName());
						  devicesStatus.put("deviceId" ,device.getId());
						  Set<Driver>  drivers = device.getDriver();

						  for(Driver driver : drivers ) {

							  devicesStatus.put("driverName", driver.getName());
							  devicesStatus.put("driverUniqueId", driver.getUniqueid());
							  
						  }
						  
						  if((long) tripReportOne.getDeviceId() == (long) dev) {
							  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
								  totalDistance += Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;


							  }
							  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {

								  time += Math.abs(  Long.parseLong(tripReportOne.getDuration())  );
								  
								  Long hours =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  totalDuration = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds);

							  }
							  if(tripReportOne.getSpentFuel() != null && tripReportOne.getSpentFuel() != "") {
								  Double litres=10.0;
								  Double Fuel =0.0;
								  Double distance=0.0;
								  
								  if(device.getFuel() != null) {
										if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=obj.getDouble("fuelPerKM");
												
											}
										}
								   }
								  
								  
								  distance = Double.parseDouble(tripReportOne.getDistance().toString());
								  if(distance > 0) {
									Fuel = (distance*litres)/100;
								  }
					
								  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
								  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));
								  

								  totalFuel += Double.parseDouble(tripReportOne.getSpentFuel());
								  roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;
									

							  }
							  devicesStatus.put("totalDrivingHours",totalDuration);
						      devicesStatus.put("totalDistance", roundOffDistance);
						      devicesStatus.put("totalSpentFuel", roundOffFuel);
						  }
					      
						  
					  }
					  data.add(devicesStatus);

				  }
				  
			  }
			  
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
				 logger.info("************************ geTotalTripsReport ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	/**
	 * get totalDuration , totalEngineHours , totalSpentFuel for stops of driver , device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getTotalStopsReportApp(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		

		 logger.info("************************ getTotalStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 logger.info("************************ getTotalStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMVISITEDPLACES", "list") &&
					!userRoleService.checkUserHasPermission(userId, "ENGINEHOURSNOTMOVING", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									 logger.info("************************ getTotalStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										 logger.info("************************ getTotalStopsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
									 logger.info("************************ getTotalStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",stopReport);
							 logger.info("************************ getTotalStopsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							 logger.info("************************ getTotalStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						 logger.info("************************ getTotalStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
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
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 stopReport = (List<StopReport>) returnFromTraccarApp(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();
		 List<Map> data = new ArrayList<Map>();

			if(stopReport.size()>0) {

				  for(Long dev:allDevices) {
					  
					  Long timeDuration = (long) 0;
					  Long timeEngine= (long) 0;
					  String totalDuration = "00:00:00";
					  String totalEngineHours = "00:00:00";
					  Double totalFuel=0.0;
					  double roundOffFuel = 0.0;
					  
					  List<String> duplicateAddressList = new ArrayList<String>();				  
					  duplicateAddressList.clear();

					  Map devicesStatus = new HashMap();
					  for(StopReport stopReportOne: stopReport) {
						 
						  
						  if((long) stopReportOne.getDeviceId() == (long) dev) {
							  
							  
							  devicesStatus.put("deviceName", null);
							  devicesStatus.put("deviceId" ,null);
							  devicesStatus.put("driverName", null);
							  devicesStatus.put("driverUniqueId",null);
							  
							  devicesStatus.put("totalDuration", totalDuration);
						      devicesStatus.put("totalEngineHours", totalEngineHours);
						      devicesStatus.put("totalSpentFuel", roundOffFuel);
							  devicesStatus.put("totalVisitedPlace" ,0);

							  
							  Device device= deviceServiceImpl.findById(dev);
							  
						      devicesStatus.put("deviceName", device.getName());
							  devicesStatus.put("deviceId" ,device.getId());
							  Set<Driver>  drivers = device.getDriver();

							  for(Driver driver : drivers ) {

								  devicesStatus.put("driverName", driver.getName());
								  devicesStatus.put("driverUniqueId", driver.getUniqueid());
								  
							  }
							  
							  
							  if(stopReportOne.getAddress() != null && stopReportOne.getAddress() != "") {
								  duplicateAddressList.add(stopReportOne.getAddress());
							  
							  }
							  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

								  timeDuration += Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

    							  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
								  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
								  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
								  
								  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);

							  }
							  
							  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

								  timeEngine += Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

    							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
								  
								  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

							  }


							  if(stopReportOne.getSpentFuel() != null && stopReportOne.getSpentFuel() != "") {
								  Double litres=10.0;
								  Double Fuel =0.0;
								  Double distance=0.0;
								  
								  if(device.getFuel() != null) {
										if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=obj.getDouble("fuelPerKM");
												
											}
										}
								   }
								  
								  
								  distance = Double.parseDouble(stopReportOne.getDistance().toString());
								  if(distance > 0) {
									Fuel = (distance*litres)/100;
								  }
					
								  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
								  stopReportOne.setSpentFuel(Double.toString(roundOffFuel));
								  

								  totalFuel += Double.parseDouble(stopReportOne.getSpentFuel());
								  roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;
									

							  }
							  devicesStatus.put("totalDuration", totalDuration);
						      devicesStatus.put("totalEngineHours", totalEngineHours);
						      devicesStatus.put("totalSpentFuel", roundOffFuel);
						  }
					     
					  }
					  Map<String, Long> couterMap = duplicateAddressList.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting()));
					  devicesStatus.put("totalVisitedPlace" ,couterMap.size());
					  data.add(devicesStatus);

				  }
				  
			  }
			  
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * number of drivering hours during period for driver and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getNumberDriverWorkingHoursApp(String TOKEN, Long[] driverIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId) {
		logger.info("************************ getNumberDriverWorkingHours STARTED ***************************");

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMBERDRIVERWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver working hours list",driverHours);
					logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();
		List<Long>allDrivers= new ArrayList<>();
		List<DriverSelect>allDevicesList= new ArrayList<DriverSelect>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
									logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
										logger.info("************************ getNumberDriverWorkingHours ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",driverHours);
								logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									allDrivers.addAll(groupRepository.getDriversFromGroup(groupId));

								}
								else if(group.getType().equals("device")) {
									allDrivers.addAll(groupRepository.getDriverFromDevices(groupId));
									
								}
								else if(group.getType().equals("geofence")) {
									allDrivers.addAll(groupRepository.getDriversFromGeofence(groupId));

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
									logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : driverParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
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
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",driverHours);
							logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {
			allDevicesList.addAll(driverRepository.devicesOfDrivers(allDrivers));
		}
		for(DriverSelect object : allDevicesList) {
			allDevices.add(object.getId());
		}


		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}	
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
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
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for drivers of groups or drivers that you selected",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
			List<Map> dataAll = new ArrayList<Map>();
			
			
	        driverHours = mongoPositionRepo.getDriverWorkingHoursScheduled(allDevices, dateFrom, dateTo);
			  if(driverHours.size()>0) {

				  for(Long dev:allDevices) {

						String totalHours = "00:00:00";
						Long time= (long) 0;

					  Map devicesStatus = new HashMap();
					  for(DriverWorkingHours driverH: driverHours) {

						  if( (long) driverH.getDeviceId() == (long) dev ) {
							  
							  devicesStatus.put("deviceId" ,driverH.getDeviceId());

							  if(driverH.getDeviceName() != null) {
								  devicesStatus.put("deviceName", driverH.getDeviceName());
							  }
							  if(driverH.getDriverName() != null) {
								  devicesStatus.put("driverName", driverH.getDriverName());
							  }

						      devicesStatus.put("totalHours", totalHours);
							  

						      
							JSONObject obj = new JSONObject(driverH.getAttributes().toString());

							if(obj.has("todayHours")) {
								  time += Math.abs(  obj.getLong("todayHours") );
								  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

							}
						      devicesStatus.put("totalHours", totalHours);
						  }
					  }
					  dataAll.add(devicesStatus);

				  }
				 
			  }

			

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",dataAll,dataAll.size());
			logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
			
			
	}

	/**
	 * get devices select list for selection
	 */
	@Override
	public ResponseEntity<?> getDeviceSelectApp(String TOKEN, Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getDeviceSelect STARTED ***************************");
		List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		

		
	    if(userId != 0) {
	    	
			userServiceImpl.resetChildernArray();

	    	User user = userServiceImpl.findById(userId);
	    	if(user == null) {
	    		getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",devices);
				return ResponseEntity.status(404).body(getObjectResponse);
	    	}
	    	if(user != null) {
	    		if(user.getDelete_date() != null) {
	    			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",devices);
					return ResponseEntity.status(404).body(getObjectResponse);
	    		}
	    	}
	    	if(user.getAccountType().equals(4)) {
			   
	    		 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
				 
				 if(deviceIds.size()>0) {
						devices = deviceRepository.getDeviceSelectByIds(deviceIds);

				 }

				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
				 logger.info("************************ getDeviceSelect ENDED ***************************");
				 return ResponseEntity.ok().body(getObjectResponse);
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
			devices = deviceRepository.getDeviceSelect(usersIds);
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
			logger.info("************************ getDeviceSelect ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	}
	
	
	@Override
	public ResponseEntity<?> registerToken(String TOKEN, Map<Object, Object> data) {
		// TODO Auto-generated method stub
	    logger.info("************************registerToken STARTED ***************************");

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 logger.info("************************registerToken ENDED ***************************");

			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(!data.containsKey("userId")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is required",null);
			 logger.info("************************registerToken ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(!data.containsKey("registerToken")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "registerToken is required",null);
			 logger.info("************************registerToken ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		Long userId = Long.parseLong(data.get("userId").toString());
		String registerToken = (String) data.get("registerToken");

		if(registerToken == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "registerToken is required",null);
			 logger.info("************************registerToken ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(registerToken.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "registerToken is required",null);
			 logger.info("************************registerToken ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is required",null);
		    logger.info("************************registerToken ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

		User user = userServiceImpl.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
		    logger.info("************************registerToken ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		String attr = user.getAttributes();
		
		if(attr != null && attr != "" && attr.startsWith("{")) {
			JSONObject obj = new JSONObject(attr);	
			if(obj.has("notificationTokens")) {
				String notificationTokens = obj.getString("notificationTokens");
				if(notificationTokens.length() > 0) {
					notificationTokens = notificationTokens + "," + registerToken;
				}
				else {
					notificationTokens = registerToken;
				}
				obj.put("notificationTokens", notificationTokens);

			}
			else {
				obj.put("notificationTokens", registerToken);
			}
			
			user.setAttributes(obj.toString());
		}
		else {
			JSONObject obj = new JSONObject();	
			obj.put("notificationTokens", registerToken);
			user.setAttributes(obj.toString());

		}
		
		userRepository.save(user);
		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		logger.info("************************ registerToken ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
	@Override
	public ResponseEntity<?> logoutTokenApp(String TOKEN, Map<Object, Object> data) {
		
		
		logger.info("************************ Logout STARTED ***************************");

		// TODO Auto-generated method stub
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(TOKEN == "") {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id required",null);
			logger.info("************************ Logout ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			
			//Boolean removed = TokenSecurity.getInstance().removeActiveUser(TOKEN);
			Boolean removed = tokenSecurity.removeActiveUser(TOKEN);

			if(removed) {

				if(data.containsKey("userId") && data.containsKey("registerToken")) {

					Long userId = Long.parseLong(data.get("userId").toString());
				    String registerToken = (String) data.get("registerToken");
	 
					User user = userServiceImpl.findById(userId);
					if(user != null) {
						
						if(registerToken != null) {
							if(!registerToken.equals("")) {
								
								String attr = user.getAttributes();
								
								if(attr != null && attr != "" && attr.startsWith("{")) {
									JSONObject obj = new JSONObject(attr);	
						        	String noti = "";
									if(obj.has("notificationTokens")) {
										String notificationTokens = obj.getString("notificationTokens");
										
										if(notificationTokens.length() > 0) {
											String[] numberStrs = notificationTokens.split(",");

								        	for(String str :numberStrs) {
								        		if(!str.equals(registerToken)) {
								        			if(noti .length() > 0) {
									        			noti = noti + ","+ str ;
									        		}
								        			else {
									        			noti = str ;
								        			}
								        		}
								        	}
										}
										
										obj.put("notificationTokens", noti);

									}
									
									user.setAttributes(obj.toString());
									userRepository.save(user);
								}
							}
						}
					}
	            }
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "loggedOut successfully",null);
				logger.info("************************ Logout ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				List<User> loggedUser = null ;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged token is not Found",null);
				logger.info("************************ Logout ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}
	}

}
