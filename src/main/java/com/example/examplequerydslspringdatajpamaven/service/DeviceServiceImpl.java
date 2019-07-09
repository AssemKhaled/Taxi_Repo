package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceEntity;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceProjection;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.CustomDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;



@Component
public class DeviceServiceImpl implements DeviceService {

	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	@Autowired 
	DeviceRepository deviceRepository;
	
	
	GetObjectResponse getObjectResponse;
	
	@Autowired
	CustomDeviceRepository customDeviceRepository;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Override
	public List<?> getAllUserDevices(Long userId , int offset, String search) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getAllUserDevices STARTED ***************************");
		
	    List<CustomDeviceProjection> data=customDeviceRepository.getUserDevices(userId, offset);
//	    List< CustomDeviceProjection> data2= null;
//	     Set<Driver> drivers = new HashSet<>();
//	     drivers =  data.get(0).getDriver();
//		List<String> data= new ArrayList<>();
//		data.add("mariam");
	     
		logger.info("************************ getAllUserDevices ENDED ***************************");
		return data;
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
		     deviceRepository.save(device);
		     deviceRepository.deleteUserDevice(device.getId());
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
	public String assignDeviceToDriver(Device device) {
		// TODO Auto-generated method stub
		deviceRepository.save(device);
		
		return "ok";
	}

	@Override
	public String assignDeviceToGeofences(Device device) {
		// TODO Auto-generated method stub
		deviceRepository.save(device);
		return "ok";
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



}
