package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;



@Component
public class DeviceServiceImpl extends RestServiceController implements DeviceService {

	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	@Autowired 
	DeviceRepository deviceRepository;
	
	
	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private DriverServiceImpl driverService;
	
	@Autowired
	 private GeofenceServiceImpl geofenceService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Override
	public ResponseEntity<?> getAllUserDevices(String TOKEN,Long userId , int offset, String search) {
		// TODO Auto-generated method stub
		 
		logger.info("************************ getAllUserDevices STARTED ***************************");
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

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get devices list",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 userService.resetChildernArray();
		 if(loggedUser.getAccountType() == 4) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 List<CustomDeviceList> devices= deviceRepository.getDevicesList(usersIds,offset,search);
				 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",usersIds);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
		 
		 List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
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
		 System.out.println("Ids"+usersIds.toString());
		 List<CustomDeviceList> devices= deviceRepository.getDevicesList(usersIds,offset,search);
		 Integer size=  deviceRepository.getDevicesListSize(userId);
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices,size);
		 logger.info("************************ getAllUserDevices ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> createDevice(String TOKEN,Device device,Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ createDevice STARTED ***************************");
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
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create device",null);
				 logger.info("************************ createDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if((device.getId() != null && device.getId() != 0) || device.getName()== null ||device.getName() == ""
				|| device.getUniqueId()== null || device.getUniqueId() == null
				|| device.getSequenceNumber() == null || device.getSequenceNumber()==""
				|| device.getPlateNum() == null || device.getPlateNum() == ""
				|| device.getLeftLetter() == null || device.getLeftLetter() == ""
                || device.getMiddleLetter() == null || device.getMiddleLetter() == ""
                || device.getRightLetter() == null || device.getRightLetter() == "") {
			
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
			userCreater=userService.findById(userId);
			if(userCreater == null)
			{

				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "Assigning to not found user",null);
				logger.info("************************ createDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				User parent = null;
				if(userCreater.getAccountType() == 4) {
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
			    List<Integer> duplictionList = checkDeviceDuplication(device);
			    if(duplictionList.size()>0)
			    {
			    	getObjectResponse = new GetObjectResponse( 201, "Duplication in data",duplictionList);
			    	logger.info("************************ createDevice ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse);
			    }
			    else
			    {
			    	if(device.getPhoto() !=null) {
						
						//base64_Image
			        	String photo=device.getPhoto();
						DecodePhoto decodePhoto=new DecodePhoto();
						device.setPhoto(decodePhoto.Base64_Image(photo));				
						
					}
					else {
						device.setPhoto("Not-available.png");
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
	public ResponseEntity<?> editDevice(String TOKEN,Device device, Long userId) {
		logger.info("************************ editDevice STARTED ***************************");
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
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit device",null);
				 logger.info("************************ editDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
	
		if(device.getId() == null || device.getName()== null ||device.getName() == "" 
			|| device.getUniqueId()== null || device.getUniqueId() == "" 
			|| device.getSequenceNumber() == null || device.getSequenceNumber() == ""
			|| device.getPlateNum()  == null || device.getPlateNum() == ""
			|| device.getLeftLetter() == "" || device.getLeftLetter() == null
			|| device.getRightLetter() == null || device.getRightLetter() == ""
			|| device.getMiddleLetter() == null || device.getMiddleLetter() == ""	) {
			
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "atrributes [id ,name, trackerImei , sequence" + 
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
			if(loggedUser.getAccountType() == 4) {
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
							System.out.println("here"+deviceUser.getId()+"parent"+parent.getId());
							if(deviceUser.getId() == parent.getId()) {
								
								isParent = true;
								break;
							}
						}
					}
				}
			}
			if(!checkIfParent(oldDevice , loggedUser) && ! isParent) {
				getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
				logger.info("************************ editDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			Set<User> userCreater=new HashSet<>();
			userCreater = oldDevice.getUser();
	        device.setUser(userCreater);
	        List<Integer> duplictionList = checkDeviceDuplication(device);
	        if(duplictionList.size()>0)
		    {
		    	getObjectResponse = new GetObjectResponse( 201, "Duplication in data",duplictionList);
		    	logger.info("************************ createDevice ENDED ***************************");
		    	return ResponseEntity.ok().body(getObjectResponse);
		    }
	        else {
	        	if(device.getPhoto() !=null) {
					
					//base64_Image
		        	String photo=device.getPhoto();
					DecodePhoto decodePhoto=new DecodePhoto();
					device.setPhoto(decodePhoto.Base64_Image(photo));				
					
				}
				else {
					device.setPhoto("Not-available.png");
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
			String deviceUniqueId = device.getUniqueId();
			String deviceSequenceNumber = device.getSequenceNumber();
			String devicePlateNum = device.getPlateNum();
			String deviceLeftLetter = device.getLeftLetter();
			String deviceMiddleLetter = device.getMiddleLetter();
			String deviceRightLetter = device.getRightLetter();
			System.out.println("left_letter"+deviceLeftLetter );
		    List<Device>duplicatedDevices = deviceRepository.checkDeviceDuplication(deviceName,deviceUniqueId,deviceSequenceNumber,devicePlateNum,deviceLeftLetter,deviceMiddleLetter,deviceRightLetter);
		    List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (Device matchedDevice : duplicatedDevices) 
		    { 
//		        Set<User> userCreater = device.getUser();
		    	if(matchedDevice.getId() != device.getId()) {
		    		if(matchedDevice.getName() != null) {
				        if(matchedDevice.getName().equals(device.getName()))
				        {
				        	
				        	duplicationCodes.add(1);
				        }
			    	}
			    	if(matchedDevice.getUniqueId() != null) {
			    		if(matchedDevice.getUniqueId().equals(device.getUniqueId()) ) {
				        	duplicationCodes.add(2);
				        }
			    	}
			        if(matchedDevice.getSequenceNumber() != null) {
			        	if(matchedDevice.getSequenceNumber().equals(device.getSequenceNumber()) ) {
				        	duplicationCodes.add(3);
				        }
			        }
			        if(matchedDevice.getPlateNum() != null || matchedDevice.getLeftLetter() != null 
			        	|| matchedDevice.getMiddleLetter() != null || matchedDevice.getRightLetter() != null) {
			        	if(matchedDevice.getPlateNum().equals(device.getPlateNum())  
			 		           && matchedDevice.getLeftLetter().equals(device.getLeftLetter())
			 		           && matchedDevice.getMiddleLetter().equals(device.getMiddleLetter())
			 		           && matchedDevice.getRightLetter().equals(device.getRightLetter())) {
			 		        	duplicationCodes.add(4);
			 		        }
			        }
		    	}
		    	
		    	
		        
		        
		    }
		    logger.info("************************ checkDeviceDuplication ENDED ***************************");
		    return duplicationCodes;
		}
		
		
	}

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
		 if(deviceId == 0 || userId == 0) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID and Device ID are Required",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		User loggedUser = userService.findById(userId);
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete device",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 System.out.println(deviceId);
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
			 if(creater.getAccountType() == 4) {
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
								System.out.println("here"+deviceUser.getId()+"parent"+parent.getId());
								if(deviceUser.getId() == parent.getId()) {
									
									isParent = true;
									break;
								}
							}
						}
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
	public Device findById(Long deviceId) {
		// TODO Auto-generated method stub
		
		Device device = deviceRepository.findOne(deviceId);
		if(device == null) {
			return null;
		}
		if(device.getDeleteDate() != null) {
			//throw not found 
			return null;
		}
		else
		{
			return device;
		}
		
	}

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
		if(deviceId == 0 || userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID  and logged user Id are  Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser  == null) {
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
			   if(loggedUser.getAccountType()== 4) {
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
								System.out.println("here"+deviceUser.getId()+"parent"+parent.getId());
								if(deviceUser.getId() == parent.getId()) {
									
									isParent = true;
									break;
								}
							}
						}
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
		if(loggedUser.getAccountType()!= 1) {
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
				   if(loggedUser.getAccountType()== 4) {
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
									System.out.println("here"+deviceUser.getId()+"parent"+parent.getId());
									if(deviceUser.getId() == parent.getId()) {
										
										isParent = true;
										break;
									}
								}
							}
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
							 if(assignedDevice.getId() == device.getId()) {
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
		User loggedUser = userService.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignGeofenceToDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType()!= 1) {
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
//			return ResponseEntity.status(404).body(getObjectResponse);
		}else {
			 Device device = findById(deviceId);
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
					Set<Geofence> geofences = device.getGeofence();
					Set<Geofence> oldGeoffences = geofences;
					geofences.removeAll(oldGeoffences);
					device.setGeofence(geofences);
					deviceRepository.save(device);
					Set<Geofence> newGeofences = geofenceService.getMultipleGeofencesById(geoIds);
					device.setGeofence(newGeofences);
					deviceRepository.save(device);
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
			}
			
		}
		
	}

	@Override

	public ResponseEntity<?> testgetDeviceById() {
		// TODO Auto-generateds method stub
		Device device ;
		 device = deviceRepository.findOne((long) 73);
		List<Device> devices = new ArrayList<>();
		devices.add(device);
	
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "login successfully",devices);
		return  ResponseEntity.ok().body(getObjectResponse) ;
	}

	public  ResponseEntity<?> getDeviceSelect(String TOKEN,Long userId) {

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
	    	User user = userService.findById(userId);
	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			devices = deviceRepository.getDeviceSelect(userId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
					logger.info("************************ getDeviceSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",devices);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",devices);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
		

	}

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
		
		
		if(userId == 0) {
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
		 if(loggedUser.getAccountType() == 4) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 
				    Integer onlineDevices = deviceRepository.getNumberOfOnlineDevices(usersIds);
					Integer outOfNetworkDevices = deviceRepository.getNumberOfOutOfNetworkDevices(usersIds);
					Integer totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
					Integer offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
					Integer drivers = driverService.getTotalNumberOfUserDrivers(usersIds);
					
					Map devicesStatus = new HashMap();
					devicesStatus.put("online_devices", onlineDevices);
					devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
					devicesStatus.put("offline_devices", offlineDevices);
					devicesStatus.put("total_drivers", drivers);
					List<Map> data = new ArrayList<>();
					data.add(devicesStatus);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
		 List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
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
		Integer onlineDevices = deviceRepository.getNumberOfOnlineDevices(usersIds);
		Integer outOfNetworkDevices = deviceRepository.getNumberOfOutOfNetworkDevices(usersIds);
		Integer totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
		Integer offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
		Integer drivers = driverService.getTotalNumberOfUserDrivers(usersIds);
		
		Map devicesStatus = new HashMap();
		devicesStatus.put("online_devices", onlineDevices);
		devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
		devicesStatus.put("offline_devices", offlineDevices);
		devicesStatus.put("total_drivers", drivers);
		List<Map> data = new ArrayList<>();
		data.add(devicesStatus);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		System.out.println("online devices"+ onlineDevices);
		logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getAllDeviceLiveData(String TOKEN,Long userId, int offset, String search) {
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
		if(userId==0) {
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
	    if(loggedUser.getAccountType() == 4) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesLiveData(usersIds, offset, search);
				 Integer size=deviceRepository.getAllDevicesLiveDataSize(userId);
				  getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData,size);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
	    List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
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
	    List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesLiveData(usersIds, offset, search);
	    Integer size=deviceRepository.getAllDevicesLiveDataSize(userId);
	    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData,size);
		
		logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	
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
		if(userId==0) {
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
	    if(loggedUser.getAccountType() == 4) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesLiveDataMap(usersIds);
				    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData);
					
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
	    List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
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
	    List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getAllDevicesLiveDataMap(usersIds);
	    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData);
		
		logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}
		
		

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
		if(deviceId != 0 && userId !=0) {
			User loggedUser = userService.findById(userId);
			if(loggedUser == null) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",vehicleInfo);
				 return  ResponseEntity.status(404).body(getObjectResponse);
			}
			Device device = findById(deviceId);
			if(device != null) {
				if(device.getDeleteDate()==null) {
					boolean isParent = false;
					   if(loggedUser.getAccountType()== 4) {
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
										System.out.println("here"+deviceUser.getId()+"parent"+parent.getId());
										if(deviceUser.getId() == parent.getId()) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!checkIfParent(device , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this device ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					vehicleInfo = deviceRepository.vehicleInfo(deviceId);
					List<Map> data = new ArrayList<>();
					if(vehicleInfo.size()>0) {
//						HashMap<Object, Object> map1 = new HashMap<Object, Object>();			   
//					    map1.put("key", "id");
//					    map1.put("value", vehicleInfo.get(0).getId());			
//						data.add(map1);
//						
//						HashMap<Object, Object> map2 = new HashMap<Object, Object>();			   
//					    map2.put("key", "deviceName");
//					    map2.put("value", vehicleInfo.get(0).getDeviceName());			
//						data.add(map2);
						
						HashMap<Object, Object> map3 = new HashMap<Object, Object>();			   
					    map3.put("key", "uniqueId");
					    map3.put("value", vehicleInfo.get(0).getUniqueId());			
						data.add(map3);
						
						HashMap<Object, Object> map4 = new HashMap<Object, Object>();			   
					    map4.put("key", "sequenceNumber");
					    map4.put("value", vehicleInfo.get(0).getSequenceNumber());			
						data.add(map4);

//						HashMap<Object, Object> map5 = new HashMap<Object, Object>();			   
//					    map5.put("key", "lastUpdate");
//					    map5.put("value", vehicleInfo.get(0).getLastUpdate());			
//						data.add(map5);
//						
//						HashMap<Object, Object> map6 = new HashMap<Object, Object>();			   
//					    map6.put("key", "referenceKey");
//					    map6.put("value", vehicleInfo.get(0).getReferenceKey());			
//						data.add(map6);
			          
						HashMap<Object, Object> map7 = new HashMap<Object, Object>();			   
					    map7.put("key", "driverName");
					    map7.put("value", vehicleInfo.get(0).getDriverName());			
						data.add(map7);
//						
//						HashMap<Object, Object> map8 = new HashMap<Object, Object>();			   
//					    map8.put("key", "geofenceName");
//					    map8.put("value", vehicleInfo.get(0).getGeofenceName());			
//						data.add(map8);
//						
//						HashMap<Object, Object> map9 = new HashMap<Object, Object>();			   
//					    map9.put("key", "driverId");
//					    map9.put("value", vehicleInfo.get(0).getDriverId());			
//						data.add(map9);

						HashMap<Object, Object> map10 = new HashMap<Object, Object>();			   
						map10.put("key", "driverPhoto");
					    map10.put("value", vehicleInfo.get(0).getDriverPhoto());			
						data.add(map10);

						HashMap<Object, Object> map11 = new HashMap<Object, Object>();			   
						map11.put("key", "plateType");
						map11.put("value", vehicleInfo.get(0).getPlateType());			
						data.add(map11);
						
						HashMap<Object, Object> map12 = new HashMap<Object, Object>();			   
						map12.put("key", "plateNum");
						map12.put("value", vehicleInfo.get(0).getPlateNum());			
						data.add(map12);
						
						HashMap<Object, Object> map13 = new HashMap<Object, Object>();			   
						map13.put("key", "rightLetter");
						map13.put("value", vehicleInfo.get(0).getRightLetter());			
						data.add(map13);
						
						HashMap<Object, Object> map14 = new HashMap<Object, Object>();			   
						map14.put("key", "middleLetter");
						map14.put("value", vehicleInfo.get(0).getMiddleLetter());			
						data.add(map14);
						
						HashMap<Object, Object> map15 = new HashMap<Object, Object>();			   
						map15.put("key", "leftLetter");
						map15.put("value", vehicleInfo.get(0).getLeftLetter());			
						data.add(map15);

						HashMap<Object, Object> map16 = new HashMap<Object, Object>();			   
						map16.put("key", "ownerName");
						map16.put("value", vehicleInfo.get(0).getOwnerName());			
						data.add(map16);
//
//						HashMap<Object, Object> map17 = new HashMap<Object, Object>();			   
//						map17.put("key", "ownerId");
//						map17.put("value", vehicleInfo.get(0).getOwnerId());			
//						data.add(map17);

						HashMap<Object, Object> map18 = new HashMap<Object, Object>();			   
						map18.put("key", "userName");
						map18.put("value", vehicleInfo.get(0).getUserName());			
						data.add(map18);
						
						HashMap<Object, Object> map19 = new HashMap<Object, Object>();			   
						map19.put("key", "brand");
						map19.put("value", vehicleInfo.get(0).getBrand());			
						data.add(map19);
						
						HashMap<Object, Object> map20 = new HashMap<Object, Object>();			   
						map20.put("key", "model");
						map20.put("value", vehicleInfo.get(0).getModel());			
						data.add(map20);
						
						HashMap<Object, Object> map21 = new HashMap<Object, Object>();			   
						map21.put("key", "madeYear");
						map21.put("value", vehicleInfo.get(0).getMadeYear());			
						data.add(map21);
						
						HashMap<Object, Object> map22 = new HashMap<Object, Object>();			   
						map22.put("key", "color");
						map22.put("value", vehicleInfo.get(0).getColor());			
						data.add(map22);
						
						HashMap<Object, Object> map23 = new HashMap<Object, Object>();			   
						map23.put("key", "licenceExptDate");
						map23.put("value", vehicleInfo.get(0).getLicenceExptDate());			
						data.add(map23);
						
						HashMap<Object, Object> map24 = new HashMap<Object, Object>();			   
						map24.put("key", "carWeight");
						map24.put("value", vehicleInfo.get(0).getCarWeight());			
						data.add(map24);

							
						    
					}
				    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
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
	public ResponseEntity<?> getDeviceLiveData(String TOKEN,Long deviceId,Long userId) {
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
		if(deviceId == 0 || userId == 0) {
			 
			    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device Id  and UserId are required",null);
				
				logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			 User loggedUser = userService.findById(userId);
			 if(loggedUser == null) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
					
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 Device device = findById(deviceId);
			 if(device == null) {
				
				    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
					
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 boolean isParent = false;
		
			 if(loggedUser.getAccountType() == 4) {
				 Set<User>parentClients = loggedUser.getUsersOfUser();
				 if(parentClients.isEmpty()) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
						
						logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
				}else {
					User parent = null;
					for(User object : parentClients) {
						parent = object;
					}
					Set<User>deviceParent = device.getUser();
					if(deviceParent.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
							logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
						for(User parentObject : deviceParent) {
							if(parent.getId() == parentObject.getId()) {
								isParent = true;
								break;
							}
						}
					}
				}
			}
			 if(!checkIfParent(device , loggedUser)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			   }
			List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getDeviceLiveData(deviceId);
			List<Map> data = new ArrayList<>();
			if(allDevicesLiveData.size()>0) {
				
//			    HashMap<Object, Object> map1 = new HashMap<Object, Object>();			   
//			    map1.put("key", "id");
//			    map1.put("value", allDevicesLiveData.get(0).getId());			
//				data.add(map1);
//				
//			    HashMap<Object, Object> map2 = new HashMap<Object, Object>();			   
//				map2.put("key", "deviceName");
//			    map2.put("value", allDevicesLiveData.get(0).getDeviceName());			
//				data.add(map2);
				
			    HashMap<Object, Object> map3 = new HashMap<Object, Object>();			   
				map3.put("key", "lastUpdate");
			    map3.put("value", allDevicesLiveData.get(0).getLastUpdate());			
				data.add(map3);
				
			    HashMap<Object, Object> map4 = new HashMap<Object, Object>();			   
				map4.put("key", "weight");
			    map4.put("value", allDevicesLiveData.get(0).getWeight());			
				data.add(map4);
				
			    HashMap<Object, Object> map5 = new HashMap<Object, Object>();			   
				map5.put("key", "latitude");
			    map5.put("value", allDevicesLiveData.get(0).getLatitude());			
				data.add(map5);
				
			    HashMap<Object, Object> map6 = new HashMap<Object, Object>();			   
				map6.put("key", "longitude");
			    map6.put("value", allDevicesLiveData.get(0).getLongitude());			
				data.add(map6);
				
//			    HashMap<Object, Object> map7 = new HashMap<Object, Object>();			   
//				map7.put("key", "address");
//			    map7.put("value", allDevicesLiveData.get(0).getAddress());			
//				data.add(map7);
				
//			    HashMap<Object, Object> map8 = new HashMap<Object, Object>();			   
//				map8.put("key", "attributes");
//			    map8.put("value", allDevicesLiveData.get(0).getAttributes());			
//				data.add(map8);

			    HashMap<Object, Object> map9 = new HashMap<Object, Object>();			   
				map9.put("key", "crash");
			    map9.put("value", allDevicesLiveData.get(0).getCrash());			
				data.add(map9);
				
			    HashMap<Object, Object> map10 = new HashMap<Object, Object>();			   
				map10.put("key", "batteryUnpluged");
			    map10.put("value", allDevicesLiveData.get(0).getBatteryUnpluged());			
				data.add(map10);
				
			    HashMap<Object, Object> map11 = new HashMap<Object, Object>();			   
				map11.put("key", "todayHoursString");
			    map11.put("value", allDevicesLiveData.get(0).getAddress());			
				data.add(map11);
				
			    HashMap<Object, Object> map12 = new HashMap<Object, Object>();			   
				map12.put("key", "deviceWorkingHoursPerDay");
			    map12.put("value", allDevicesLiveData.get(0).getDeviceWorkingHoursPerDay());			
				data.add(map12);
				
			    HashMap<Object, Object> map13 = new HashMap<Object, Object>();			   
				map13.put("key", "driverWorkingHoursPerDay");
			    map13.put("value", allDevicesLiveData.get(0).getDriverWorkingHoursPerDay());			
				data.add(map13);
				
			    HashMap<Object, Object> map14 = new HashMap<Object, Object>();			   
				map14.put("key", "power");
			    map14.put("value", allDevicesLiveData.get(0).getPower());			
				data.add(map14);
//				
//			    HashMap<Object, Object> map15 = new HashMap<Object, Object>();			   
//				map15.put("key", "photo");
//			    map15.put("value", allDevicesLiveData.get(0).getPhoto());			
//				data.add(map15);
//				
			    HashMap<Object, Object> map16 = new HashMap<Object, Object>();			   
				map16.put("key", "speed");
			    map16.put("value", allDevicesLiveData.get(0).getSpeed());			
				data.add(map16);
				
			    HashMap<Object, Object> map17 = new HashMap<Object, Object>();			   
				map17.put("key", "status");
			    map17.put("value", allDevicesLiveData.get(0).getStatus());			
				data.add(map17);
				
//			    HashMap<Object, Object> map18 = new HashMap<Object, Object>();			   
//			    map18.put("key", "positionId");
//				map18.put("value", allDevicesLiveData.get(0).getPositionId());			
//				data.add(map18);
//				
//			    HashMap<Object, Object> map19 = new HashMap<Object, Object>();			   
//			    map19.put("key", "jsonAttributes");
//				map19.put("value", allDevicesLiveData.get(0).getJsonAttributes());			
//				data.add(map19);
//				
			    HashMap<Object, Object> map20 = new HashMap<Object, Object>();			   
			    map20.put("key", "sensor1");
				map20.put("value", allDevicesLiveData.get(0).getSensor1());			
				data.add(map20);
				
			    HashMap<Object, Object> map21 = new HashMap<Object, Object>();			   
			    map21.put("key", "sensor2");
			    map21.put("value", allDevicesLiveData.get(0).getSensor2());			
				data.add(map21);
			    
			    HashMap<Object, Object> map22 = new HashMap<Object, Object>();			   
			    map22.put("key", "hours");
				map22.put("value", allDevicesLiveData.get(0).getHours());			
				data.add(map22);
				
			    HashMap<Object, Object> map23 = new HashMap<Object, Object>();			   
			    map23.put("key", "motion");
				map23.put("value", allDevicesLiveData.get(0).getMotion());			
				data.add(map23);
				
			    HashMap<Object, Object> map24 = new HashMap<Object, Object>();			   
			    map24.put("key", "totalDistance");
				map24.put("value", allDevicesLiveData.get(0).getTotalDistance());			
				data.add(map24);
				
			    HashMap<Object, Object> map25 = new HashMap<Object, Object>();			   
			    map25.put("key", "ignition");
			    map25.put("value", allDevicesLiveData.get(0).getIgnition());			
				data.add(map25);
				
			    HashMap<Object, Object> map26 = new HashMap<Object, Object>();			   
			    map26.put("key", "alarm");
				map26.put("value", allDevicesLiveData.get(0).getAlarm());			
				data.add(map26);
				
			    HashMap<Object, Object> map27 = new HashMap<Object, Object>();			   
			    map27.put("key", "battery");
				map27.put("value", allDevicesLiveData.get(0).getBattery());			
				data.add(map27);
				
//			    HashMap<Object, Object> map28 = new HashMap<Object, Object>();			   
//			    map28.put("key", "driverName");
//				map28.put("value", allDevicesLiveData.get(0).getDriverName());			
//				data.add(map28);
//				
//			    HashMap<Object, Object> map29 = new HashMap<Object, Object>();			   
//			    map29.put("key", "leftLetter");
//				map29.put("value", allDevicesLiveData.get(0).getLeftLetter());			
//				data.add(map29);
//				
//			    HashMap<Object, Object> map30 = new HashMap<Object, Object>();			   
//			    map30.put("key", "middleLetter");
//				map30.put("value", allDevicesLiveData.get(0).getMiddleLetter());			
//				data.add(map30);
//				
//			    HashMap<Object, Object> map31 = new HashMap<Object, Object>();			   
//			    map31.put("key", "rightLetter");
//				map31.put("value", allDevicesLiveData.get(0).getRightLetter());			
//				data.add(map31);
//				
			    HashMap<Object, Object> map32 = new HashMap<Object, Object>();			   
			    map32.put("key", "powerUnpluged");
			    map32.put("value", allDevicesLiveData.get(0).getPowerUnpluged());			
				data.add(map32);
	            
			}
			
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
	
		}
	
	}
	
	@Override
	public ResponseEntity<?> getDeviceLiveDataMap(String TOKEN,Long deviceId,Long userId) {
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
		if(deviceId == 0 || userId == 0) {
			
			    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device Id and User id are required",null);
				
				logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			 User loggedUser = userService.findById(userId);
			 if(loggedUser == null) {
				
				    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
					
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 Device device = findById(deviceId);
			 if(device == null) {
				
				    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
					
					logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 boolean isParent = false;
			 if(loggedUser.getAccountType()==4) {
				 Set<User>parentClients = loggedUser.getUsersOfUser();
				 if(parentClients.isEmpty()) {
					  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
						
						logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse); 
				 }else {
					 User parent = null;
					 for(User object : parentClients) {
						 parent = object ;
					 }
					 Set<User>deviceParent = device.getUser();
					 if(deviceParent.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to get this device",null);
							
							logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);  
					 }else {
						 for(User parentObject : deviceParent) {
							 if(parent.getId() == parentObject.getId()) {
								  isParent =  true;
								  break;
							 }
						 }
					 }
				 }
				 
			 }
			 if(!checkIfParent(device , loggedUser)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			}
			List<CustomDeviceLiveData> allDevicesLiveData=	deviceRepository.getDeviceLiveData(deviceId);		
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",allDevicesLiveData);
			
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
	
		}
	
	}

	@Override
	public ResponseEntity<?> assignDeviceToUser(Long userId, Long deviceId, Long toUserId) {
		// TODO Auto-generated method stub
		if(userId == 0 || deviceId == 0 || toUserId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId , deviceId and toUserId  are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userService.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}else {
				if(loggedUser.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "assignToUser")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToUser",null);
						 logger.info("************************ assignToUser ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggedUser.getAccountType() == 3 || loggedUser.getAccountType() == 4) {
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
					    	  if(toUser.getAccountType()== 4) {
					    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user",null);
									
									return ResponseEntity.status(404).body(getObjectResponse);
					    	  }
					    	  else if(loggedUser.getAccountType() == toUser.getAccountType()) {
					    		  if(loggedUser.getId() == toUser.getId()) {
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
					    			 if(loggedUser.getId() ==  object.getId()) {
					    				 isParent = true;
					    				 break;
					    			 }
					    		 }
					    		 if(isParent) {
					    			 
					    			// assign user to another user
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
		   if(parent.getId() == loggedUser.getId()) {
			   return true;
		   }
		   if(parent.getAccountType() == 1) {
			   if(parent.getId() == loggedUser.getId()) {
				   return true;
			   }
		   }else {
			   List<User> parents = userService.getAllParentsOfuser(parent, parent.getAccountType());
			   if(parents.isEmpty()) {
				   
				   return false;
			   }else {
				   for(User object :parents) {
					   if(object.getId() == loggedUser.getId()) {
						   return true;
					   }
				   }
			   }
		   }
		  
	   }
	   return false;
   }


}
