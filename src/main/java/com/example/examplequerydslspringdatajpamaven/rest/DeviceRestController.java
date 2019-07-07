package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;

import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;


@CrossOrigin
@Component
@RequestMapping(path = "/devices")
public class DeviceRestController {
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private DriverServiceImpl driverService;
	//selection of all devices from user controller not device controller
	@Autowired 
	GeofenceServiceImpl geofenceService;
	
	
	@GetMapping("/getUserDevices/{userId}")
	public ResponseEntity<?> devicesList(@PathVariable (value = "userId") Long userId) {
		
		//S x = userService.getName();
		return new ResponseEntity<>(userService.UserDevice(userId), HttpStatus.OK);
		//return userService.UserDevice(userId);
	}
	
	@PostMapping(path ="/createDevice/{userId}")
	public ResponseEntity<?> createDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
		if(device.getId() != null || device.getName()== null || device.getUniqueId()== null
		   || device.getSequenceNumber() == null) {
			
			return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
		}
		//System.out.println("bad request"+device.getId());
		Set<User> user=new HashSet<>() ;
		user.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
        device.setUser(user);
            
		//*if(device == null) {
			//throw bad request
//			return "bad request";
//			System.out.println("bad request");
//			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
//		}
		//else
		//{*/
        if(device.getPhoto() !=null) {
			
			//base64_Image
        	String photo=device.getPhoto();
			DecodePhoto decodePhoto=new DecodePhoto();
			device.setPhoto(decodePhoto.Base64_Image(photo));				
			
		}
		else {
			device.setPhoto("Not-available.png");
		}	
        
			 String newDevice= deviceService.createDevice(device);
			 
			 return new ResponseEntity<>(newDevice, HttpStatus.OK);
		//}	 
				
	}
	@PostMapping(path ="/editDevice/{userId}")
	public ResponseEntity<?> editDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
		if(device.getId() == null || device.getName()== null || device.getUniqueId()== null
				   || device.getSequenceNumber() == null) {
					
					return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
		}
		Set<User> user=new HashSet<>() ;
		user.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
        device.setUser(user);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
        if(device.getPhoto() !=null) {
			
			//base64_Image
        	String photo=device.getPhoto();
			DecodePhoto decodePhoto=new DecodePhoto();
			device.setPhoto(decodePhoto.Base64_Image(photo));				
			
		}
		else {
			device.setPhoto("Not-available.png");
		}	
			 String newDevice= deviceService.createDevice(device);
			 return new ResponseEntity<>(newDevice, HttpStatus.OK);
		//}	 
				
	}
	@PostMapping(path ="/deleteDevice/{userId}")
	public ResponseEntity<String> deleteDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
		if(device.getId() == null || device.getName()== null || device.getUniqueId()== null
				   || device.getSequenceNumber() == null) {
					
					return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
		}
		//	Set<User> user=new HashSet<>() ;
		//user.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
      //  device.setUser(user);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			String deleted= deviceService.deleteDevice(device);
			 return new ResponseEntity<>(deleted, HttpStatus.OK);
		//}	 
				
	}
	@PostMapping(path ="/checkDuplication/{userId}")
	public ResponseEntity<List<Integer>> checkDulication(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
		Set<User> user=new HashSet<>() ;
		user.add(userService.findById(userId));
//		Set<User> user= userService.findById(userId);
        device.setUser(user);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			 //Device newDevice= deviceService.createDevice(device);
        List<Integer> newDevice =deviceService.checkDeviceDuplication(device);
			 return new ResponseEntity<>(newDevice, HttpStatus.OK);
		//}	 
				
	}
	@GetMapping(path ="/getDevicebyId/{deviceId}")
	public ResponseEntity<Device> getDevicebyId(@PathVariable (value = "deviceId") Long deviceId) {
      Device device = deviceService.findById(deviceId);
            
	/*	if(device == null) {
			//throw bad request
//			return "bad request";
			return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
		}
		else
		{*/
			 //Device newDevice= deviceService.createDevice(device);
      
			 return new ResponseEntity<>(device, HttpStatus.OK);
		//}	 
				
	}
	
	@PostMapping(path = "/assignDeviceToDriver/{driverId}")
	public ResponseEntity<String> assignDeviceToDriver(@PathVariable (value = "driverId") Long driverId,@RequestBody(required = false) Device device) {
		
		if(device.getId() == null) {
			return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
		}
		Set<Driver> driver=new HashSet<>() ;
		driver.add(driverService.getDriverById(driverId));
//		Set<User> user= userService.findById(userId);
        device.setDriver(driver);
//		Device device = deviceService.findById(deviceId);
	            
		/*	if(device == null) {
				//throw bad request
//				return "bad request";
				return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
			}
			else
			{*/
				String assignDevice = deviceService.assignDeviceToDriver(device);
	      
				 return new ResponseEntity<>(assignDevice, HttpStatus.OK);
			//}	 
					
		}
	@PostMapping(path = "/assignGeofencesToDevice/{geoIds}")
	public ResponseEntity<?> assignGeofencesToDevice(@PathVariable (value = "geoIds")Long [] geoIds,@RequestBody(required = false) Device device) {
	
		
		if(device.getId()==null) {
			return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
		}
		Set<Geofence> geofence=new HashSet<>();
		//selectMultiple geofences
		List<Geofence> geofences = geofenceService.getMultipleGeofencesById(geoIds);
		for ( Geofence geo : geofences) 
		{ 
			geofence.add(geo);
		}
        device.setGeofence(geofence);
//		Device device = deviceService.findById(deviceId);
	            
		/*	if(device == null) {
				//throw bad request
//				return "bad request";
				return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
			}
			else
			{*/
				String assignDevice = deviceService.assignDeviceToGeofences(device);
	      
				 return new ResponseEntity<>(geofences, HttpStatus.OK);
			//}	 
					
		}
	 @GetMapping(path = "/testResponse")
	  public ResponseEntity<?> testResponse(){
		 return deviceService.testgetDeviceById();
	 }
	
	@RequestMapping(value = "/getDeviceDriver/{deviceId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceDriver(@PathVariable (value = "deviceId") Long deviceId) {
		
		if(deviceId != 0) {
			Device device= deviceService.findById(deviceId);
			return ResponseEntity.ok(device.getDriver());	
		
		}
		else {
			
			return ResponseEntity.ok("no device selected");

		}
		
	}
	
	@RequestMapping(value = "/getDeviceGeofences/{deviceId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceGeofences(@PathVariable (value = "deviceId") Long deviceId) {
		
		if(deviceId != 0) {
			Device device= deviceService.findById(deviceId);
			return ResponseEntity.ok(device.getGeofence());	
		
		}
		else {
			
			return ResponseEntity.ok("no device selected");

		}
		
	}
	@RequestMapping(value = "/getDeviceSelect/{userId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelect(@PathVariable (value = "userId") Long userId) {
		
		if(userId != 0) {
			
			return ResponseEntity.ok(deviceService.getDeviceSelect(userId));	
	
		}
		else {
			
			return ResponseEntity.ok("no device selected");

		}
		
	}


}
