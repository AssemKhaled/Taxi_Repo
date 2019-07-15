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

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;



@Component
public class DeviceServiceImpl implements DeviceService {

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
	
	@Override
	public ResponseEntity<?> getAllUserDevices(Long userId , int offset, String search) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getAllUserDevices STARTED ***************************");
		
		 List<CustomDeviceList> devices= deviceRepository.getDevicesList(userId,offset,search);
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
		 logger.info("************************ getAllUserDevices ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> createDevice(Device device,Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ createDevice STARTED ***************************");
		if(device.getId() != null || device.getName()== null || device.getUniqueId()== null
				   || device.getSequenceNumber() == null) {
			
			List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "BadRequest",devices);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
					
		}
		else
		{
			Set<User> user=new HashSet<>() ;
			User userCreater ;
			userCreater=userService.findById(userId);
			if(userCreater == null)
			{

				List<Device> devices = null;
				
				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "assigning to not found user",devices);
				logger.info("************************ createDevice ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				user.add(userCreater);	
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
	public ResponseEntity<?> editDevice(Device device, Long userId) {
		logger.info("************************ editDevice STARTED ***************************");
		if(device.getId() == null || device.getName()== null || device.getUniqueId()== null
				   || device.getSequenceNumber() == null) {
			
			List<Device> devices = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "BadRequest",devices);
			logger.info("************************ editDevice ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
					
		}
		else {
			Device oldDevice = findById(device.getId());
			if(oldDevice == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this device not found to be edited",devices);
		    	logger.info("************************ createDevice ENDED ***************************");
		    	return ResponseEntity.ok().body(getObjectResponse);
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
	public  ResponseEntity<?> deleteDevice(Long userId,Long deviceId) {
		 logger.info("************************ deleteDevice ENDED ***************************");
		 Device device = findById(deviceId);
		 if(device == null)
		 {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this device not found to be deleted",devices);
		     logger.info("************************ deleteDevice ENDED ***************************");
		     return ResponseEntity.ok().body(getObjectResponse);
		 }
		 else
		 {
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
	public ResponseEntity<?>  findDeviceById(Long deviceId) {
		// TODO Auto-generated method stub
		logger.info("************************ getDeviceById STARTED ***************************");
		Device device = deviceRepository.findOne(deviceId);
		if (device == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no device with this id",devices);
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			if(device.getDeleteDate() != null) {
				//throw not found 
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no device with this id",devices);
				logger.info("************************ getDeviceById ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else
			{
				List<Device> devices = new ArrayList<>();
				devices.add(device);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
				logger.info("************************ getDeviceById ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
		}
		
		
	}

	@Override
	public ResponseEntity<?> assignDeviceToDriver(Long deviceId,Long driverId) {
		logger.info("************************ assignDeviceToDriver STARTED ***************************");
		if(deviceId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",devices);
			logger.info("************************ assignDeviceToDriver ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			Device device = findById(deviceId);
			if(device == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
				logger.info("************************ assignDeviceToDriver ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				if(driverId == 0) {
					Set<Driver> drivers=new HashSet<>() ;
					drivers= device.getDriver();
			        if(drivers.isEmpty()) {
			        	List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
			        }
			        else {
			        	Set<Driver> oldDrivers =new HashSet<>() ;
			        	oldDrivers= drivers;
			        	drivers.removeAll(oldDrivers);
			        	 device.setDriver(drivers);
						 deviceRepository.save(device);
			        	List<Device> devices = null;
			        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "driver removed successfully",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
			        }
				}
				Driver driver = driverService.getDriverById(driverId);
				if(driver == null) {
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver is not found",devices);
					logger.info("************************ assignDeviceToDriver ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}
				else {
					if(driver.getDelete_date() != null) {
						List<Device> devices = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver is not found",devices);
						logger.info("************************ assignDeviceToDriver ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
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
								 getObjectResponse = new GetObjectResponse(203, "this driver is assigned to another device",devices);
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
	public ResponseEntity<?> assignDeviceToGeofences(Long deviceId , Long [] geoIds) {
		logger.info("************************ assignDeviceToGeofences STARTED ***************************");
		
		if(deviceId ==0){
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",devices);
			logger.info("************************ assignDeviceToGeofences ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}else {
			 Device device = findById(deviceId);
			 if(device == null) {
				  List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this device is not found",devices);
					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
			 }
			if(geoIds.length == 0) {
				//if device has geofences remove it 
                Set<Geofence> geofences = device.getGeofence();
                if(geofences.isEmpty()) {
                	 List<Device> devices = null;
 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no geofences to assign or remove",devices);
 					logger.info("************************ assignDeviceToGeofences ENDED ***************************");
 					return ResponseEntity.ok().body(getObjectResponse);
                }
                else {
                	// else if device hasn't geofences return error
    				
                	Set<Geofence> oldGeofences = geofences;
                	geofences.removeAll(oldGeofences);
                	device.setGeofence(geofences);
                	deviceRepository.save(device);
                	List<Device> devices = null;
                	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "geofences removed successfully",devices);
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

	public List<DeviceSelect> getDeviceSelect(Long userId) {

		return deviceRepository.getDeviceSelect(userId);
	

	}

	@Override
	public ResponseEntity<?> getDeviceDriver(Long deviceId) {
		// TODO Auto-generated method stub
		logger.info("************************ getDeviceToDriver STARTED ***************************");
		if(deviceId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",devices);
			logger.info("************************ getDeviceToDriver ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			Device device = findById(deviceId);
			if(device == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this device is not found",devices);
				logger.info("************************ getDeviceToDriver ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else
			{
				Set<Driver> drivers=new HashSet<>() ;
				drivers = device.getDriver();
				if(drivers.isEmpty()) {
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "no drivers assigned to this device",devices);
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
	public ResponseEntity<?> getDeviceGeofences(Long deviceId) {
		// TODO Auto-generated method stub
		logger.info("************************ getDeviceGeofences STARTED ***************************");
		if(deviceId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",devices);
			logger.info("************************ getDeviceGeofences ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			Device device = findById(deviceId);
			if(device == null) {
				List<Device> devices = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this device is not found",devices);
				logger.info("************************ getDeviceGeofences ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else
			{
				Set<Geofence> geofences=new HashSet<>() ;
				geofences = device.getGeofence();
				if(geofences.isEmpty()) {
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "no geofences assigned to this device",devices);
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
	public ResponseEntity<?> getDeviceStatus(Long userId) {
		logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
		if(userId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		Integer onlineDevices = deviceRepository.getNumberOfOnlineDevices(userId);
		Integer outOfNetworkDevices = deviceRepository.getNumberOfOutOfNetworkDevices(userId);
		Integer totalDevices = deviceRepository.getTotalNumberOfUserDevices(userId);
		Integer offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
		Integer drivers = driverService.getTotalNumberOfUserDrivers(userId);
		
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
   


}
