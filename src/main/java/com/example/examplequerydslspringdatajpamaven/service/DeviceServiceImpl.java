package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientDevice;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * services functionality related to devices
 * @author fuinco
 *
 */

@Component
@Service
public class DeviceServiceImpl extends RestServiceController implements DeviceService {

	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	@Autowired
	private MongoPositionRepo mongoPositionRepo;
	
	@Autowired
	private UserClientDriverRepository userClientDriverRepository;

	@Autowired 
	private GroupRepository groupRepository;
	
	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired 
	private DeviceRepository deviceRepository;
	
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private DriverServiceImpl driverService;
	
	@Autowired
	private GeofenceServiceImpl geofenceService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private MongoPositionsRepository mongoPositionsRepository;
	
	@Value("${sendCommand}")
	private String sendCommand;
	
	/**
	 * get list device by limit 10
	 */
	@Override
	public ResponseEntity<?> getAllUserDevices(String TOKEN,Long userId , int offset, String search, String exportData) {
		// TODO Auto-generated method stub
		 
		logger.info("************************ getAllUserDevices STARTED ***************************");
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
			 logger.info("************************ getAllUserDevices ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser == null) {
			 List<CustomDeviceList> devices= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",devices);
			 logger.info("************************ getAllUserDevices ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get devices list",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 userService.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 Integer size=0;
		 List<CustomDeviceList> devices = new ArrayList<CustomDeviceList>();
		 if(loggedUser.getAccountType().equals(4)) {
			 
			 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			 
			 if(deviceIds.size()>0) {

				 
				 
				 if(exportData.equals("exportData")) {
			    	 devices= deviceRepository.getDevicesListByIdsExport(deviceIds,search);

			     }
			     else {
			    	 devices= deviceRepository.getDevicesListByIds(deviceIds,offset,search);
					 size=  deviceRepository.getDevicesListSizeByIds(deviceIds,search);
			     }
			 }

			 
		 }
		 else {
			 List<User>childernUsers = userService.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
		 
		 
		     if(exportData.equals("exportData")) {
		    	 devices= deviceRepository.getDevicesListExport(usersIds,search);

		     }
		     else {
		    	 devices= deviceRepository.getDevicesList(usersIds,offset,search);
				 size=  deviceRepository.getDevicesListSize(usersIds,search); 
		     }
		 
			 

		}
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices,size);
		 logger.info("************************ getAllUserDevices ENDED ***************************");
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * create device with data in body
	 */
	@Override
	public ResponseEntity<?> createDevice(String TOKEN,Device device,Long userId) {
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
		User loggedUser = userService.findById(userId);
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
			userCreater=userService.findById(userId);
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
			    List<Integer> duplictionList = checkDeviceDuplication(device);
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
			    	
			    	device.setIs_deleted(null);
			    	device.setDelete_date(null);
			    	
			    	deviceRepository.save(device);
			    	List<Device> devices = null;
			    	
			    	if(userCreater.getAccountType().equals(4)) {
			    		userClientDevice saveData = new userClientDevice();
				        
				        Long devId = deviceRepository.getDeviceIdByName(parent.getId(),device.getName(),device.getUniqueid());
			    		if(devId != null) {
				    		saveData.setUserid(userId);
				    		saveData.setDeviceid(devId);
					        userClientDeviceRepository.save(saveData);
			    		}
			    		
			    	}
			    	
			    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",devices);
					logger.info("************************ createDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
			    }
			}
			
	        
		}
		
	}
	
	/**
	 * edit device by id in body mandatory
	 */
	@Override
	public ResponseEntity<?> editDevice(String TOKEN,Device device, Long userId) {
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
		User loggedUser = userService.findById(userId);
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
			  Device oldDevice = findById(device.getId());
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
			if(!checkIfParent(oldDevice , loggedUser) && ! isParent) {
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
	        
	        List<Integer> duplictionList = checkDeviceDuplication(device);
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
				
				
				if(newPhoto == null) {
					device.setPhoto("not_available.png");
				}
				else {
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
	public List<Integer> checkDeviceDuplication(Device device) {
		// TODO Auto-generated method stub
		logger.info("************************ checkDeviceDuplication STARTED ***************************");
		if(device.getName() == null)
		{
			//throw bad request exception 
			 logger.info("************************ checkDeviceDuplication ENDED ***************************");
			 return null;
		}
		else
		{
			String deviceName = device.getName();
			String deviceUniqueId = device.getUniqueid();
			String deviceSequenceNumber = device.getSequence_number();
			String devicePlateNum = device.getPlate_num();
			String deviceLeftLetter = device.getLeft_letter();
			String deviceMiddleLetter = device.getMiddle_letter();
			String deviceRightLetter = device.getRight_letter();
		    List<Device>duplicatedDevices = deviceRepository.checkDeviceDuplication(deviceName,deviceUniqueId,deviceSequenceNumber,devicePlateNum,deviceLeftLetter,deviceMiddleLetter,deviceRightLetter);
		    List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (Device matchedDevice : duplicatedDevices) 
		    { 
		    	if(!matchedDevice.getId().equals(device.getId())) {
		    		if(matchedDevice.getName() != null) {
				        if(matchedDevice.getName().equals(device.getName()))
				        {
				        	
				        	duplicationCodes.add(1);
				        }
			    	}
			    	if(matchedDevice.getUniqueid() != null) {
			    		if(matchedDevice.getUniqueid().equals(device.getUniqueid()) ) {
				        	duplicationCodes.add(2);
				        }
			    	}
			        if(matchedDevice.getSequence_number() != null) {
			        	if(matchedDevice.getSequence_number().equals(device.getSequence_number()) ) {
				        	duplicationCodes.add(3);
				        }
			        }
			        if(matchedDevice.getPlate_num() != null || matchedDevice.getLeft_letter() != null
			        	|| matchedDevice.getMiddle_letter() != null || matchedDevice.getRight_letter() != null) {
			        	if(matchedDevice.getPlate_num().equals(device.getPlate_num())  
			 		           && matchedDevice.getLeft_letter().equals(device.getLeft_letter())
			 		           && matchedDevice.getMiddle_letter().equals(device.getMiddle_letter())
			 		           && matchedDevice.getRight_letter().equals(device.getRight_letter())) {
			 		        	duplicationCodes.add(4);
			 		        }
			        }
		    	}
		    	
		    	
		        
		        
		    }
		    logger.info("************************ checkDeviceDuplication ENDED ***************************");
		    return duplicationCodes;
		}
		
		
	}

	/**
	 * delete device by id
	 */
	@Override
	public  ResponseEntity<?> deleteDevice(String TOKEN,Long userId,Long deviceId) {
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
		User loggedUser = userService.findById(userId);
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
		 Device device = findById(deviceId);
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
			 User creater= userService.findById(userId);
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
			 if(!checkIfParent(device , creater)&& ! isParent) {
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
	
	@Override
	public Device findById(Long deviceId) {
		// TODO Auto-generated method stub
		
		Device device = deviceRepository.findOne(deviceId);
		if(device == null ) {
			return null;
		}
		if(device.getDelete_date() != null) {

			return null;
		}
		else
		{
			return device;
		}
		
	}

	/**
	 * get device by id
	 */
	@Override
	public ResponseEntity<?>  findDeviceById(String TOKEN,Long deviceId, Long userId) {
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
		User loggedUser = userService.findById(userId);
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
			   if(!checkIfParent(device , loggedUser)&& ! isParent) {
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
	 * assign driver to device
	 */
	@Override
	public ResponseEntity<?> assignDeviceToDriver(String TOKEN,Long deviceId,Long driverId,Long userId) {
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
		User loggedUser = userService.findById(userId);
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
			Device device = findById(deviceId);
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
				   if(!checkIfParent(device , loggedUser)&& ! isParent) {
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
				Driver driver = driverService.getDriverById(driverId);
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
	 * assgin multi geofences to device
	 */
	@Override
	public ResponseEntity<?> assignDeviceToGeofences(String TOKEN,Long deviceId , Long [] geoIds,Long userId) {
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
		User loggedUser = userService.findById(userId);
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
			 Device device = findById(deviceId);
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
			   if(!checkIfParent(device , loggedUser)&& ! isParent) {
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
				    Set<Geofence> newGeofences = geofenceService.getMultipleGeofencesById(geoIds);
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
	 * get selected devices for group
	 */
	@Override
	public  ResponseEntity<?> getDeviceSelectGroup(String TOKEN,Long loggedUserId,Long userId,Long groupId) {

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
		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
		List<DeviceSelect> groups = new ArrayList<DeviceSelect>();

		if(groupId != 0) {
			groups = groupRepository.getGroupDevicesSelect(groupId);

		}
		obj.put("selectedDevice", groups);

		
		if(loggedUserId != 0) {
	    	User loggedUser = userService.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(loggedUserId);
	   				 
		   				 if(deviceIds.size()>0) {
		   						devices = deviceRepository.getDeviceSelectByIds(deviceIds);
	
		   				 }
						 obj.put("devices", devices);
						 data.add(obj);
		   				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		   				 logger.info("************************ getDeviceSelect ENDED ***************************");
		   				 return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
	    if(userId != 0) {
	    	
			userService.resetChildernArray();

	    	User user = userService.findById(userId);
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
				 obj.put("devices", devices);
				 data.add(obj);
				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				 logger.info("************************ getDeviceSelect ENDED ***************************");
				 return ResponseEntity.ok().body(getObjectResponse);
			 }
	    	
	    	
	    	
	    			
    		 List<User>childernUsers = userService.getAllChildernOfUser(userId);
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
			
			 obj.put("devices", devices);
			 data.add(obj);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDeviceSelect ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
		

	}

	/**
	 * get select device list
	 */
	@Override
	public  ResponseEntity<?> getDeviceSelect(String TOKEN,Long loggedUserId,Long userId) {

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
		

		
		if(loggedUserId != 0) {
	    	User loggedUser = userService.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(loggedUserId);
	   				 
		   				 if(deviceIds.size()>0) {
		   						devices = deviceRepository.getDeviceSelectByIds(deviceIds);
	
		   				 }

		   				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
		   				 logger.info("************************ getDeviceSelect ENDED ***************************");
		   				 return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
	    if(userId != 0) {
	    	
			userService.resetChildernArray();

	    	User user = userService.findById(userId);
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
	    	
	    	
	    	
	    			
    		 List<User>childernUsers = userService.getAllChildernOfUser(userId);
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

	/**
	 * get assigned driver
	 */
	@Override
	public ResponseEntity<?> getDeviceDriver(String TOKEN,Long deviceId) {
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

			Device device = findById(deviceId);
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
	 * get assigned geofence
	 */
	@Override
	public ResponseEntity<?> getDeviceGeofences(String TOKEN,Long deviceId) {
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
			Device device = findById(deviceId);
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
	 * get numbers of status (online,offline,out of network,ignitionON,ignitionOFF,moving,stopped,totalDevices,drivers)
	 */
	@Override
	public ResponseEntity<?> getDeviceStatus(String TOKEN,Long userId) {
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
		User loggedUser = userService.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		 userService.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();

		 
		Integer ignitionON = 0;
		Integer ignitionOFF = 0;
		Integer moving = 0;
		Integer stopped = 0;
		Integer onlineDevices =0;
		Integer outOfNetworkDevices=0;
		Integer totalDevices =0;
		Integer offlineDevices=0;
		Integer drivers =0;
			
		 if(loggedUser.getAccountType().equals(4)) {
			 
			 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);


			 if(deviceIds.size()>0) {
				 List<String> onlineDeviceIds = deviceRepository.getNumberOfOnlineDevicesListByIds(deviceIds);
				 List<String> OutDeviceIds = deviceRepository.getNumberOfOutOfNetworkDevicesListByIds(deviceIds);

				 onlineDevices = onlineDeviceIds.size();
				 outOfNetworkDevices = OutDeviceIds.size();
					
			     totalDevices = deviceRepository.getTotalNumberOfUserDevicesByIds(deviceIds);
				 offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
				 drivers = userClientDriverRepository.getDriverIds(userId).size();

			
				 ignitionON = mongoPositionRepo.getCountFromAttrbuites(onlineDeviceIds, "ignition", true);
				 ignitionOFF = mongoPositionRepo.getCountFromAttrbuites(onlineDeviceIds, "ignition", false);
				 moving = mongoPositionRepo.getCountFromSpeedGreaterThanZero(onlineDeviceIds);
		         stopped = mongoPositionRepo.getCountFromSpeedEqualZero(onlineDeviceIds);
				
			 }
			 
			 
					
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("all_devices", onlineDevices+offlineDevices+outOfNetworkDevices);
			devicesStatus.put("total_drivers", drivers);
			devicesStatus.put("ignition_off", ignitionOFF);
			devicesStatus.put("ignition_on", ignitionON);
			devicesStatus.put("stopped", stopped);
			devicesStatus.put("moving", moving);
	
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		 }
		 else {
			 List<User>childernUsers = userService.getAllChildernOfUser(userId);
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
			drivers = driverService.getTotalNumberOfUserDrivers(usersIds);
			
				
			ignitionON = mongoPositionRepo.getCountFromAttrbuites(onlineDeviceIds, "ignition", true);
			ignitionOFF = mongoPositionRepo.getCountFromAttrbuites(onlineDeviceIds, "ignition", false);
			moving = mongoPositionRepo.getCountFromSpeedGreaterThanZero(onlineDeviceIds);
            stopped = mongoPositionRepo.getCountFromSpeedEqualZero(onlineDeviceIds);
            
					
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("all_devices", onlineDevices+offlineDevices+outOfNetworkDevices);
			devicesStatus.put("total_drivers", drivers);
			devicesStatus.put("ignition_off", ignitionOFF);
			devicesStatus.put("ignition_on", ignitionON);
			devicesStatus.put("stopped", stopped);
			devicesStatus.put("moving", moving);
	
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
	}

	/**
	 * get data of devices by limit 10 if it has position get data from mongo if no get only intial data
	 */
	@Override
	public ResponseEntity<?> getAllDeviceLiveData(String TOKEN,Long userId,int offset,String search) {
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
	    User loggedUser = userService.findById(userId);
	    if( loggedUser == null) {
	    	 List<CustomDeviceLiveData> allDevicesLiveData=	null;
			    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found ",allDevicesLiveData);
				
				logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
	    }
	    userService.resetChildernArray();
		List<Long>usersIds= new ArrayList<>();
	    if(loggedUser.getAccountType().equals(4)) {
			 
			List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			List<CustomDeviceLiveData> allDevicesLiveData = new ArrayList<CustomDeviceLiveData>();
			Integer size=0;
			if(deviceIds.size() >0) {
				allDevicesLiveData=	deviceRepository.getAllDevicesDataByIds(deviceIds, offset, search);
			    size=deviceRepository.getAllDevicesLiveDataSizeByIds(deviceIds,search);
			    if(size > 0) {
					for(int i=0;i<allDevicesLiveData.size();i++) {
						long minutes = 0;
		            	allDevicesLiveData.get(i).setVehicleStatus("offline");
		
						if(allDevicesLiveData.get(i).getLastUpdate() != null) {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		                    	allDevicesLiveData.get(i).setVehicleStatus("offline");
							}
							if(minutes < 8 && minutes > 3) {
		                    	allDevicesLiveData.get(i).setVehicleStatus("unknown");
		
							}	
						}
						else {
		                	allDevicesLiveData.get(i).setVehicleStatus("offline");
		
						}
						
						if(allDevicesLiveData.get(i).getPositionId() != null) {
							
							MongoPositions mongoPosition = mongoPositionsRepository.findById(allDevicesLiveData.get(i).getPositionId());
							
							if(mongoPosition != null) {
		
							   ObjectMapper mapper = new ObjectMapper();
		                	   String json = null;
		                	   try {
		                		   json = mapper.writeValueAsString(mongoPosition.getAttributes());
							   } catch (JsonProcessingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		
		                    	JSONObject obj = new JSONObject(json);						
								
		                    	Integer countTemp = 0;
		                    	Integer countHum = 0;
		                    	
		                    	Double Temp = (double) 0;
		                    	Double Hum = (double) 0;
		                    	
		                    	if(obj.has("temp1")) {
		                    		Temp = Temp + obj.getDouble("temp1");
		                    		countTemp = countTemp + 1;
								}
		                    	if(obj.has("temp2")) {
		                    		Temp = Temp + obj.getDouble("temp2");
		                    		countTemp = countTemp + 1;
		
								}
		                    	if(obj.has("temp3")) {
		                    		Temp = Temp + obj.getDouble("temp3");
		                    		countTemp = countTemp + 1;
		
								}
		                    	if(obj.has("temp4")) {
		                    		Temp = Temp + obj.getDouble("temp4");
		                    		countTemp = countTemp + 1;
		
								}
		                    	
		                    	if(obj.has("hum1")) {
		                    		Hum = Hum + obj.getDouble("hum1");
		                    		countHum = countHum + 1;
								}
		                    	if(obj.has("hum2")) {
		                    		Hum = Hum + obj.getDouble("hum2");
		                    		countHum = countHum + 1;
		
								}
		                    	if(obj.has("hum3")) {
		                    		Hum = Hum + obj.getDouble("hum3");
		                    		countHum = countHum + 1;
		
								}
		                    	if(obj.has("hum4")) {
		                    		Hum = Hum + obj.getDouble("hum4");
		                    		countHum = countHum + 1;
		
								}
		                    	Double avgTemp = (double) 0;
		                    	Double avgHum = (double) 0;
		                    	if(countTemp != 0) {
			                    	avgTemp = Temp / countTemp;

		                    	}
		                    	if(countHum != 0) {
		                    		avgHum = Hum / countHum;

		                    	}
		                    	
		                    	allDevicesLiveData.get(i).setTemperature(avgTemp);
		                    	allDevicesLiveData.get(i).setHumidity(avgHum);
		                    	
								allDevicesLiveData.get(i).setAttributes(mongoPosition.getAttributes());
								allDevicesLiveData.get(i).setSpeed(mongoPosition.getSpeed() * (1.852));
								allDevicesLiveData.get(i).setLatitude(mongoPosition.getLatitude());
								allDevicesLiveData.get(i).setLongitude(mongoPosition.getLongitude());
								
								if(mongoPosition.getValid() == true) {
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
		
						                    if(mongoPosition.getSpeed() == 0) {
						                    	allDevicesLiveData.get(i).setStatus("Idle");
											}
						                    if(mongoPosition.getSpeed() > 0) {
						                    	allDevicesLiveData.get(i).setStatus("Running");
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
							
						}
						else {
		                	allDevicesLiveData.get(i).setStatus("No data");
						}
						
					}
				}
			}
	    	
		    
		    
		    
		    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData,size);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

	    }
	    else {
	    	List<User>childernUsers = userService.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
	    
	     
			List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesData(usersIds, offset, search);
		    Integer size=deviceRepository.getAllDevicesLiveDataSize(usersIds,search);
		    if(size > 0) {
				for(int i=0;i<allDevicesLiveData.size();i++) {
					long minutes = 0;
	            	allDevicesLiveData.get(i).setVehicleStatus("offline");
	
					if(allDevicesLiveData.get(i).getLastUpdate() != null) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	                    	allDevicesLiveData.get(i).setVehicleStatus("offline");
						}
						if(minutes < 8 && minutes > 3) {
	                    	allDevicesLiveData.get(i).setVehicleStatus("unknown");
	
						}	
					}
					else {
	                	allDevicesLiveData.get(i).setVehicleStatus("offline");
	
					}
					
					if(allDevicesLiveData.get(i).getPositionId() != null) {
						
						MongoPositions mongoPosition = mongoPositionsRepository.findById(allDevicesLiveData.get(i).getPositionId());
						
						if(mongoPosition != null) {
	
						   ObjectMapper mapper = new ObjectMapper();
	                	   String json = null;
	                	   try {
	                		   json = mapper.writeValueAsString(mongoPosition.getAttributes());
						   } catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	
	                    	JSONObject obj = new JSONObject(json);						
							
	                    	Integer countTemp = 0;
	                    	Integer countHum = 0;
	                    	
	                    	Double Temp = (double) 0;
	                    	Double Hum = (double) 0;
	                    	
	                    	if(obj.has("temp1")) {
	                    		Temp = Temp + obj.getDouble("temp1");
	                    		countTemp = countTemp + 1;
							}
	                    	if(obj.has("temp2")) {
	                    		Temp = Temp + obj.getDouble("temp2");
	                    		countTemp = countTemp + 1;
	
							}
	                    	if(obj.has("temp3")) {
	                    		Temp = Temp + obj.getDouble("temp3");
	                    		countTemp = countTemp + 1;
	
							}
	                    	if(obj.has("temp4")) {
	                    		Temp = Temp + obj.getDouble("temp4");
	                    		countTemp = countTemp + 1;
	
							}
	                    	
	                    	if(obj.has("hum1")) {
	                    		Hum = Hum + obj.getDouble("hum1");
	                    		countHum = countHum + 1;
							}
	                    	if(obj.has("hum2")) {
	                    		Hum = Hum + obj.getDouble("hum2");
	                    		countHum = countHum + 1;
	
							}
	                    	if(obj.has("hum3")) {
	                    		Hum = Hum + obj.getDouble("hum3");
	                    		countHum = countHum + 1;
	
							}
	                    	if(obj.has("hum4")) {
	                    		Hum = Hum + obj.getDouble("hum4");
	                    		countHum = countHum + 1;
	
							}
	                    	Double avgTemp = (double) 0;
	                    	Double avgHum = (double) 0;
	                    	if(countTemp != 0) {
		                    	avgTemp = Temp / countTemp;

	                    	}
	                    	if(countHum != 0) {
	                    		avgHum = Hum / countHum;

	                    	}
	                    	
	                    	allDevicesLiveData.get(i).setTemperature(avgTemp);
	                    	allDevicesLiveData.get(i).setHumidity(avgHum);
	                    	
							allDevicesLiveData.get(i).setAttributes(mongoPosition.getAttributes());
							allDevicesLiveData.get(i).setSpeed(mongoPosition.getSpeed() * (1.852));
							allDevicesLiveData.get(i).setLatitude(mongoPosition.getLatitude());
							allDevicesLiveData.get(i).setLongitude(mongoPosition.getLongitude());
							
							if(mongoPosition.getValid() == true) {
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
										if(mongoPosition.getSpeed() == 0) {
					                    	allDevicesLiveData.get(i).setStatus("Idle");
										}
					                    if(mongoPosition.getSpeed() > 0) {
					                    	allDevicesLiveData.get(i).setStatus("Running");
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
						
					}
					else {
	                	allDevicesLiveData.get(i).setStatus("No data");
					}
					
				}
			}
		    
		    
		    
		    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData,size);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
	    }
	 

	}

	
	/**
	 * get all devices of user if has position or not and check for if it online get status of it moving or stopped 
	 */
	@Override
	public ResponseEntity<?> getAllDeviceLiveDataMap(String TOKEN,Long userId) {
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
	    User loggedUser = userService.findById(userId);
	    if( loggedUser == null) {
	    	 List<CustomDeviceLiveData> allDevicesLiveData=	null;
			    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found ",allDevicesLiveData);
				
				logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
	    }
	    userService.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();

	    if(loggedUser.getAccountType().equals(4)) {
			 
			List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			List<CustomMapData> allDevicesLiveDataNoPosition = new ArrayList<CustomMapData>();
            List<CustomMapData> allDevices = new ArrayList<CustomMapData>();

			if(deviceIds.size()>0) {
				allDevicesLiveDataNoPosition =	deviceRepository.getAllDevicesDataMapByIds(deviceIds);
				
				if(allDevicesLiveDataNoPosition.size() > 0 ) {
					allDevices.addAll(allDevicesLiveDataNoPosition);

				}

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
	    	 List<User>childernUsers = userService.getAllChildernOfUser(userId);
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
			if(allDevicesNoPosition.size() > 0 ) {
				allDevices.addAll(allDevicesNoPosition);

			}
			
			

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
		
		
	/**
	 * get vehicle info if has position or not
	 */
    @Override
	public ResponseEntity<?> vehicleInfo(String TOKEN,Long deviceId,Long userId) {
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
			User loggedUser = userService.findById(userId);
			if(loggedUser == null ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",vehicleInfo);
				 return  ResponseEntity.status(404).body(getObjectResponse);
			}
			Device device = findById(deviceId);
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
					   if(!checkIfParent(device , loggedUser) && ! isParent) {
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
			                    vehicleInfo.get(0).setCarWeight(mongoPosition.getWeight());
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
     * assign device to another user only admin or client
     */
	@Override
	public ResponseEntity<?> assignDeviceToUser(String TOKEN,Long userId, Long deviceId, Long toUserId) {
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
		if(userId.equals(0) || deviceId.equals(0) || toUserId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId , deviceId and toUserId  are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}else {
				if(!loggedUser.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "assignToUser")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToUser",null);
						 logger.info("************************ assignToUser ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggedUser.getAccountType().equals(3) || loggedUser.getAccountType().equals(4)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to any user",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}else {
					if(checkIfParent( device ,  loggedUser)) {
					     User toUser = userService.findById(toUserId);
					     if(toUser == null) {
					    	 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to assign to  is not found",null);
								
								return ResponseEntity.status(404).body(getObjectResponse);
					     }else {
					    	  if(toUser.getAccountType().equals(4)) {
					    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user type 4 assign to his parents",null);
									
									return ResponseEntity.status(404).body(getObjectResponse);
					    	  }
					    	  else if(loggedUser.getAccountType().equals(toUser.getAccountType())) {
					    		  if(loggedUser.getId().equals(toUser.getId())) {
					    			  Set<User> deviceOldUser = device.getUser();
						    			 Set<User> temp = deviceOldUser;
						    			 deviceOldUser.removeAll(temp);
						    			 device.setUser(deviceOldUser);
						    		     deviceOldUser.add(toUser);
						    		     device.setUser(deviceOldUser);
						    		     deviceRepository.save(device);
						    		     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "device assigned successfully",null);
											
										return ResponseEntity.ok().body(getObjectResponse);
					    		  }else {
					    			  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user",null);
										
										return ResponseEntity.status(404).body(getObjectResponse);
					    		  }
					    	  }
					    	 List<User>toUserParents = userService.getAllParentsOfuser(toUser, toUser.getAccountType());
					    	 if(toUserParents.isEmpty()) {
					    		 
					    		 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user",null);
									
									return ResponseEntity.status(404).body(getObjectResponse);
					    	 }else {
					    		
					    		 boolean isParent = false;
					    		 for(User object : toUserParents) {
					    			 if(loggedUser.getId().equals(object.getId())) {
					    				 isParent = true;
					    				 break;
					    			 }
					    		 }
					    		 if(isParent) {
					    			 

					    			 Set<User> deviceOldUser = device.getUser();
					    			 Set<User> temp = deviceOldUser;
					    			 deviceOldUser.removeAll(temp);
					    			 device.setUser(deviceOldUser);
					    		     deviceOldUser.add(toUser);
					    		     device.setUser(deviceOldUser);
					    		     deviceRepository.save(device);
					    		     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "device assigned successfully",null);
										
									return ResponseEntity.ok().body(getObjectResponse);
					    		     
					    		 }else {
					    			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user",null);
										
									 return ResponseEntity.status(404).body(getObjectResponse);
					    		 }
					    	 }
					     }
						
						
					}else {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not allowed  to assign this device",null);
						
						return ResponseEntity.status(404).body(getObjectResponse);
					}
				}
			}
		}
		
	}
	
   public Boolean checkIfParent(Device device , User loggedUser) {

	   Set<User> deviceParent = device.getUser();
	   if(deviceParent.isEmpty()) {
		  
		   return false;
	   }else {
		   User parent = null;
		   for (User object : deviceParent) {
			   parent = object;
		   }
		   if(parent.getId().equals(loggedUser.getId())) {
			   return true;
		   }
		   if(parent.getAccountType().equals(1)) {
			   if(parent.getId().equals(loggedUser.getId())) {
				   return true;
			   }
		   }else {
			   List<User> parents = userService.getAllParentsOfuser(parent, parent.getAccountType());
			   if(parents.isEmpty()) {
				   
				   return false;
			   }else {
				   for(User object :parents) {
					   if(object.getId().equals(loggedUser.getId())) {
						   return true;
					   }
				   }
			   }
		   }
		  
	   }
	   return false;
   }

   
   /**
    * get calibration data of device
    */
    @Override
	public ResponseEntity<?> getCalibrationData(String TOKEN,Long userId, Long deviceId) {
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
			if(userId == 0 || deviceId == 0) {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			else {
				User loggedUser = userService.findById(userId);

				if(loggedUser == null) {

					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {

					Device device = findById(deviceId);
					if(device == null) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						boolean isParent = true;
						User parent = null;

						 if(loggedUser.getAccountType().equals(4)) {
							 Set<User>parentClients = loggedUser.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
							 }else {
								 for(User object : parentClients) {
									 parent = object ;
								 }
								 loggedUser=parent;
							 }
							List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
							if(devices.isEmpty()) {
                                isParent = false;
									
							}
							else {
								isParent = true;
							}
								 
							 
						 }
						
						
						
						if(checkIfParent( device ,  loggedUser) && isParent) {
							if(!loggedUser.getAccountType().equals(1)) {
								if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "calibration")) {
									 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit calibration",null);
									 logger.info("************************ calibration ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
							}
							

                            String calibrationData=deviceRepository.getCalibrationDataCCC(deviceId);
							List<Map> data=new ArrayList<Map>();
							if(calibrationData != null) {

								 String str = calibrationData.toString(); 
							     String arrOfStr[] = str.split(" "); 
							     for (String a : arrOfStr) {
							    	 JSONObject obj =new JSONObject(a);
									 Map list   = new HashMap<>();
									 list.put("s1",obj.get("s1"));
									 list.put("s2",obj.get("s2"));
									 list.put("w",obj.get("w"));
							         data.add(list);

							     }
								
								 
							}

							getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",data);
							return ResponseEntity.ok().body(getObjectResponse);
						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);	
						}
					}
					
				}
			}
   }
    
    /**
     * add calibration dat in body for device "calibrationData"
     */
    @Override
	public ResponseEntity<?> addDataToCaliberation(String TOKEN,Long userId, Long deviceId,Map<String, List> data) {
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
		if(userId.equals(0) || deviceId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						}
							 
						 
					 }
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						if(!loggedUser.getAccountType().equals(1)) {
							if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "calibration")) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit calibration",null);
								 logger.info("************************ calibration ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
						}
						
						
						if(data.get("calibrationData") == null) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Caliberation data shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<?> calibrationData=new ArrayList<>();
						calibrationData=data.get("calibrationData");
						JSONArray jsArray = new JSONArray(calibrationData);
						String calc = calculateSlopeAndFactor(jsArray);
						device.setLineData(calc);
						String req="";
						for (Object pushed : jsArray) {
							if(req.equals("")) {
							   req+=pushed.toString();
							}
							else {
								req+=" "+pushed.toString();
							}
						}
						if(req.equals("")) {
							   req = null;
						}
						device.setCalibrationData(req);
						deviceRepository.save(device);

						
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Add successfully",null);
						return ResponseEntity.ok().body(getObjectResponse);
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
   }
    
    /**
     * add fuel data to device in body "fuel"
     */
    @Override
	public ResponseEntity<?> addDataToFuel(String TOKEN,Long userId, Long deviceId,Map<String, Object> data) {
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
			if(userId.equals(0) || deviceId.equals(0)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			else {
				User loggedUser = userService.findById(userId);
				
				if(loggedUser == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					Device device = findById(deviceId);
					if(device == null) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						boolean isParent = true;
						User parent = null;

						 if(loggedUser.getAccountType().equals(4)) {
							 Set<User>parentClients = loggedUser.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
									
									logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse); 
							 }else {
								 for(User object : parentClients) {
									 parent = object ;
								 }
								 loggedUser=parent;
							 }
							 
							List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
							if(devices.isEmpty()) {
	                            isParent = false;
									
							}
							else {
								isParent = true;
							} 
							 
						 }
						
						
						if(checkIfParent( device ,  loggedUser) && isParent) {
							if(!loggedUser.getAccountType().equals(1)) {
								if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "GetSpentFuel")) {
									 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit Fuel",null);
									 logger.info("************************ calibration ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
							}

							if(!data.containsKey("fuel")) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Fuel data shouldn't be null",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}

							if(data.get("fuel") == null) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Fuel data shouldn't be null",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
							
							JSONObject obj = new JSONObject(data);
							device.setFuel(obj.get("fuel").toString());
							deviceRepository.save(device);
							
							
							
							
							
						

								
							
							getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Add successfully",null);
							return ResponseEntity.ok().body(getObjectResponse);
						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);	
						}
					}
					
				}
			}
   }
    
    /**
     * get fuel data of device
     */
    @Override
	public ResponseEntity<?> getFuelData(String TOKEN,Long userId, Long deviceId) {
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
			if(userId.equals(0) || deviceId.equals(0)) {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			else {
				User loggedUser = userService.findById(userId);

				if(loggedUser == null) {

					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {

					Device device = findById(deviceId);
					if(device == null) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						boolean isParent = true;
						User parent = null;

						 if(loggedUser.getAccountType().equals(4)) {
							 Set<User>parentClients = loggedUser.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
									
									logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse); 
							 }else {
								 for(User object : parentClients) {
									 parent = object ;
								 }
								 loggedUser=parent;
							 }
								 
							List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
							if(devices.isEmpty()) {
                                isParent = false;
									
							}
							else {
								isParent = true;
							}
						 }
						
						
						
						if(checkIfParent( device ,  loggedUser) && isParent) {
							if(!loggedUser.getAccountType().equals(1)) {
								if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "GetSpentFuel")) {
									 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit calibration",null);
									 logger.info("************************ calibration ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
							}
							


							String data = deviceRepository.getFuelData(deviceId);
							List<Map> dataMap=new ArrayList<Map>();
							if(data != null) {

							JSONObject obj = new JSONObject(data.toString());
						     Map list   = new HashMap<>();
						     if(obj.has("calculateFillingVolumeByRawDataCheckBox")) {
								 list.put("calculateFillingVolumeByRawDataCheckBox",obj.get("calculateFillingVolumeByRawDataCheckBox"));
							 }
						     if(obj.has("calculateTheftVolumeByRawDataCheckBox")) {
								 list.put("calculateTheftVolumeByRawDataCheckBox",obj.get("calculateTheftVolumeByRawDataCheckBox"));

						     }
						     if(obj.has("detectFuelFillingOnlyCheckBox")) {
								 list.put("detectFuelFillingOnlyCheckBox",obj.get("detectFuelFillingOnlyCheckBox"));

						     }
						     if(obj.has("detectFuelTheftInMotionCheckBox")) {
								 list.put("detectFuelTheftInMotionCheckBox",obj.get("detectFuelTheftInMotionCheckBox"));

						     }
						     if(obj.has("fuelPerKM")) {
								 list.put("fuelPerKM",obj.get("fuelPerKM"));

						     }
						     if(obj.has("ignoreMessageSec")) {
								 list.put("ignoreMessageSec",obj.get("ignoreMessageSec"));

						     }
						     if(obj.has("minimumDetectFuelTheftSec")) {
								 list.put("minimumDetectFuelTheftSec",obj.get("minimumDetectFuelTheftSec"));

						     }
						     if(obj.has("minimumFuelFillingVolume")) {
								 list.put("minimumFuelFillingVolume",obj.get("minimumFuelFillingVolume"));

						     }
						     if(obj.has("minimumFuelTheftVolume")) {
								 list.put("minimumFuelTheftVolume",obj.get("minimumFuelTheftVolume"));

						     }
						     if(obj.has("timeBasedCalculationOfFillingsCheckBox")) {
								 list.put("timeBasedCalculationOfFillingsCheckBox",obj.get("timeBasedCalculationOfFillingsCheckBox"));

						     }
						     if(obj.has("timeBasedCalculationOfTheftsCheckBox")) {
								 list.put("timeBasedCalculationOfTheftsCheckBox",obj.get("timeBasedCalculationOfTheftsCheckBox"));

						     }
						     if(obj.has("timeoutDetectFillingVolume")) {
								 list.put("timeoutDetectFillingVolume",obj.get("timeoutDetectFillingVolume"));

						     }
						     if(obj.has("timeoutSeparateFillings")) {
								 list.put("timeoutSeparateFillings",obj.get("timeoutSeparateFillings"));

						     }
						     if(obj.has("timeoutSeparateThefts")) {
								 list.put("timeoutSeparateThefts",obj.get("timeoutSeparateThefts"));

						     }
							 
							 dataMap.add(list);
 
							
							}
							

							getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",dataMap);
							return ResponseEntity.ok().body(getObjectResponse);
									 

							
						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);	
						}
					}
					
				}
			}
   }

    /**
     * get sensor setting of device
     */
	@Override
	public ResponseEntity<?> getSensorSettings(String TOKEN, Long userId, Long deviceId) {
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
		if(userId.equals(0) || deviceId.equals(0)) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);

			if(loggedUser == null) {

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {

				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
							 
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						}
						 
					 }
					
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						if(!loggedUser.getAccountType().equals(1)) {
							if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "GetSensorSetting")) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit sensorSettings",null);
								 logger.info("************************ calibration ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
						}
						
						String data = deviceRepository.getSensorSettings(deviceId);
						
						List<Map> dataMap=new ArrayList<>();
					    Map<String, String> list =new HashMap<String, String>();
						
						if(data != null) {
							JSONObject obj = new JSONObject(data.toString());
							Iterator<String> keys = obj.keys();
							while(keys.hasNext()) {
							    String key = keys.next();

							    list.put(key , obj.get(key).toString());
							}
							dataMap.add(list);
						}
						
						
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",dataMap);
						return ResponseEntity.ok().body(getObjectResponse);
								 

						
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
	}
	
	/**
	 * get icon name of device
	 */
	@Override
	public ResponseEntity<?> getIcon(String TOKEN, Long userId, Long deviceId) {
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
		if(userId.equals(0) || deviceId.equals(0)) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);

			if(loggedUser == null) {

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {

				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						} 
						 
					 }
					
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						
						String data = deviceRepository.getIcon(deviceId);
						
						List<Map> dataMap=new ArrayList<>();
					    Map<Object, Object> list =new HashMap<Object, Object>();
						DecodePhoto decodePhoto=new DecodePhoto();

						List<Map> icons = decodePhoto.getAllIcons();
						list.put("icons", icons);
						if(data != null) {
							
							if(decodePhoto.checkIconDefault(data, "icon")) {
								list.put("type","default");
							}
							else {
								list.put("type","random");
							}

							
							
							list.put("icon",data);
							dataMap.add(list);
						}
						else {
							list.put("type","default");
							list.put("icon","not_available.png");
							dataMap.add(list);

						}
						
						
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",dataMap);
						return ResponseEntity.ok().body(getObjectResponse);
								 

						
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
	}

	
	/**
	 * add data of sensor setting to device in body "sensorSettings"
	 */
	@Override
	public ResponseEntity<?> addSensorSettings(String TOKEN, Long userId, Long deviceId, Map<String, Object> data) {
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
		if(userId.equals(0) || deviceId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						}	 
						 
					 }
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						if(!loggedUser.getAccountType().equals(1)) {
							if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "GetSensorSetting")) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit sensorSettings",null);
								 logger.info("************************ sensorSettings ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
						}

						if(!data.containsKey("sensorSettings")) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Sensor Settings data shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

						if(data.get("sensorSettings") == null) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Sensor Settings data shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						JSONObject obj = new JSONObject(data);
						device.setSensorSettings(obj.get("sensorSettings").toString());
						deviceRepository.save(device);
						
						
						
						
						
					

							
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Add successfully",null);
						return ResponseEntity.ok().body(getObjectResponse);
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
	}
	
	
	

	/**
	 * get selected items of device for notification , attributes
	 */
	@Override
	public ResponseEntity<?> getDeviceDataSelected(String TOKEN, Long deviceId, String type) {
		// TODO Auto-generated method stub
		logger.info("************************ getDeviceDataSelected STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId.equals(0)) {
			 List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "device ID is Required",devices);
			logger.info("************************ getDeviceDataSelected ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Device device = deviceRepository.findOne(deviceId);
			if(device == null) {
				List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
				logger.info("************************ getDeviceDataSelected ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else
			{
				if(type == null) {
					List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "type is Required",devices);
					logger.info("************************ getDeviceDataSelected ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					if(type.equals("")) {
						List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "type is Required",devices);
						logger.info("************************ getDeviceDataSelected ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
						if(type.equals("notifications")) {
							devices = deviceRepository.getNotificationsDeviceSelect(deviceId);
						}
						if(type.equals("attributes")) {
							devices = deviceRepository.getAttributesDeviceSelect(deviceId);
						}
						
						
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
						logger.info("************************ getDeviceDataSelected ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
					
				}
				 
					
				
			}
		}
	}
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) 
    {
        long diffInMillies = date2.getTime() - date1.getTime();
         
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

	/**
	 * add icon to device by "icon" in body
	 */
	@Override
	public ResponseEntity<?> addIcon(String TOKEN, Long userId, Long deviceId, Map<String, Object> data) {
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
		if(userId.equals(0) || deviceId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ addIcon ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						}	 
						 
					 }
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						

						if(!data.containsKey("icon")) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Icon shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

						if(data.get("icon") == null) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Icon shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						String icon = data.get("icon").toString();
						DecodePhoto decodePhoto=new DecodePhoto();
						if(icon !=null) {
					    	if(icon !="") {
					    		if(icon.startsWith("data:image")) {
									decodePhoto.deleteIcon(device.getIcon(), "icon");
						    		device.setIcon(decodePhoto.Base64_Image(icon,"icon"));
					    		}
					    		else {
					    			if(decodePhoto.checkIconDefault(icon, "icon")) {
										decodePhoto.deleteIcon(device.getIcon(), "icon");
					    			}
					    			device.setIcon(icon);
					    		}
					    	}

						}

						deviceRepository.save(device);
						

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Add successfully",null);
						return ResponseEntity.ok().body(getObjectResponse);
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
		
	}

	
	/**
	 * send command to traccar 
	 */
	@Override
	public ResponseEntity<?> sendCommand(String TOKEN, Long userId, Long deviceId, Map<String, Object> data) {
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
		if(userId.equals(0) || deviceId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId and deviceId are required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				Device device = findById(deviceId);
				if(device == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					boolean isParent = true;
					User parent = null;

					 if(loggedUser.getAccountType().equals(4)) {
						 Set<User>parentClients = loggedUser.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
								
								logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse); 
						 }else {
							 for(User object : parentClients) {
								 parent = object ;
							 }
							 loggedUser=parent;
						 }
						List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
						if(devices.isEmpty()) {
                            isParent = false;
								
						}
						else {
							isParent = true;
						}
						 
					 }
					
					
					if(checkIfParent( device ,  loggedUser) && isParent) {
						if(!loggedUser.getAccountType().equals(1)) {
							if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "command")) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit command",null);
								 logger.info("************************ sensorSettings ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
						}

						if(!data.containsKey("command")) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "command data shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

						if(data.get("command") == null) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "command data shouldn't be null",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						JSONObject obj = new JSONObject(data);
						String command = obj.get("command").toString();


						  Map<String, Object> objectData = new HashMap<String, Object>();
							objectData.put("deviceId", deviceId);
							objectData.put("id", 0);
							objectData.put("description", "New…");
							objectData.put("textChannel", false);
							objectData.put("type", "custom");


						if(command.equals("restart")) {
							Map<String, Object> commandData = new HashMap<String, Object>();
							commandData.put("data", "cpureset");
							objectData.put("attributes", commandData);

						}
						
						else if(command.equals("ignitionOff")) {
							Map<String, Object> commandData = new HashMap<String, Object>();
							commandData.put("data", "setdigout 11");
							objectData.put("attributes", commandData);

						}
						else if(command.equals("ignitionOn")) {
							Map<String, Object> commandData = new HashMap<String, Object>();
							commandData.put("data", "setdigout 00");
							objectData.put("attributes", commandData);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "select command please.",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						String output = sendCommandToServer(objectData);


						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), output ,null);
						return ResponseEntity.ok().body(getObjectResponse);
				
						
						
					}
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId not from parents of this device",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);	
					}
				}
				
			}
		}
	}

	@Override
	public String sendCommandToServer(Map<String, Object> objectData) {
		// TODO Auto-generated method stub
		
		String plainCreds = "admin@fuinco.com:admin";
		byte[] plainCredsBytes = plainCreds.getBytes();
		
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

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
		  
	  

		  HttpEntity<Map<String, Object>> request = new HttpEntity<>(objectData, headers);


		  ResponseEntity<String> response = restTemplate.postForEntity(sendCommand, request, String.class);

		if (response.getStatusCode() == HttpStatus.ACCEPTED) {
		    return "success";
		} else {
		    return "faild";
		}

		

	}

	
	/**
	 * assign device to user type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientDevices(String TOKEN, Long loggedUserId, Long userId, Long[] deviceIds) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userService.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		
        if(deviceIds.length > 0 && deviceIds[0] != 0) {
			
        	List<userClientDevice> checkData = userClientDeviceRepository.getDevicesByDevIds(deviceIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is device assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
			for(Long id:deviceIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Device assignedDevice = findById(id);
				if(assignedDevice == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Device is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientDeviceRepository.deleteDevicesByUserId(userId);
			for(Long assignedId:deviceIds) {
				userClientDevice userDevice = new userClientDevice();
				userDevice.setUserid(userId);
				userDevice.setDeviceid(assignedId);
				userClientDeviceRepository.save(userDevice);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientDevice> devices = userClientDeviceRepository.getDevicesOfUser(userId);
			
			if(devices.size() > 0) {

				userClientDeviceRepository.deleteDevicesByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no devices for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get devices of type 4
	 */
	@Override
	public ResponseEntity<?> getClientDevices(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userService.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}

		List<DeviceSelect> devices = userClientDeviceRepository.getDeviceOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",devices);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
	
	/**
	 * list of unassigned devices to type 4
	 */
	@Override
	public ResponseEntity<?> getDeviceUnSelect(String TOKEN,Long loggedUserId, Long userId) {
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
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userService.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		


		devices = deviceRepository.getDeviceUnSelectOfClient(loggedUserId,userId);
		List<DeviceSelect> selectedDevices = userClientDeviceRepository.getDeviceOfUserList(userId);

		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedDevices", selectedDevices);
	    obj.put("devices", devices);

	    data.add(obj);
	    
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getDeviceSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	
	}

	@Override
	public String calculateSlopeAndFactor(JSONArray data) {
		// TODO Auto-generated method stub
		
		if(data.length()>0) {
			if(data.length()>1) {
				JSONObject start = (JSONObject) data.get(0);
				Integer length = data.length()-1;
				JSONObject end = (JSONObject) data.get(length);

				Double x1 = (start.getDouble("s1") + start.getDouble("s2") )/2;
				Double y1 = start.getDouble("w");
				
				Double x2 = (end.getDouble("s1") + end.getDouble("s2") )/2;
				Double y2 = end.getDouble("w");
				
				Double slope = ((y2 - y1) / (x2 - x1));
				
				Double b = y1-(slope*x1);
				
				JSONObject result = new JSONObject();
				result.put("slope", slope);
				result.put("factor", b);
				
				return result.toString();


			}
			else {
				Integer length = data.length()-1;
				JSONObject end = (JSONObject) data.get(length);
				
				Double x1 = 0.0;
				Double y1 = 0.0;
				
				Double x2 = (end.getDouble("s1") + end.getDouble("s2") )/2;
				Double y2 = end.getDouble("w");
				
				Double slope = ((y2 - y1) / (x2 - x1));
				
				Double b = y2-(slope*x2);
				
				JSONObject result = new JSONObject();
				result.put("slope", slope);
				result.put("factor", b);
				
				return result.toString();
				
			}
		}

		
		return null;
	}

	/**
	 * update devices have calibration and no line data 
	 */
	@Override
	public ResponseEntity<?> updateLineData() {
		// TODO Auto-generated method stub
		
		List<Device> devices=deviceRepository.getAllDevicesNotHaveLineData();
		

	    for(Device device:devices) {
	    	
	    	String calibrationData = device.getCalibrationData();
	    	
	    	List<Map> data=new ArrayList<Map>();
			if(calibrationData != null) {

				 String str = calibrationData.toString(); 
			     String arrOfStr[] = str.split(" "); 
			     for (String a : arrOfStr) {
			    	 JSONObject obj =new JSONObject(a);
					 Map list   = new HashMap<>();
					 list.put("s1",obj.get("s1"));
					 list.put("s2",obj.get("s2"));
					 list.put("w",obj.get("w"));
			         data.add(list);

			     }
				
				 
			}

			JSONArray jsArray = new JSONArray(data);
			String calc = calculateSlopeAndFactor(jsArray);

		    device.setLineData(calc);
		    
		    deviceRepository.save(device);
	    	
	    }
		
		
		return null;
	}

	/**
	 * update positions with missing data
	 */
	@Override
	public ResponseEntity<?> updatePositionData() {
		// TODO Auto-generated method stub
		
		/*List<Long> deviceIds = deviceRepository.getAllDeviceIds();
		List<Device> devices= deviceRepository.findAll();
		System.out.println(devices.size());
		List<MongoPositions> positions = new ArrayList<MongoPositions>();
		positions = mongoPositionsRepository.findByDeviceIdIn(deviceIds, new PageRequest(0, 1000));
		System.out.println(positions.get(0));
		System.out.println(positions.get(0).getDeviceid());
		System.out.println(positions.get(0).getDeviceName());
		System.out.println(positions.size());*/

		return null;
	}
}
