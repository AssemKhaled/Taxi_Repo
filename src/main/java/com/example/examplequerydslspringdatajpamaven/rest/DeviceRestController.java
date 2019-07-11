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
import org.springframework.web.bind.annotation.RequestParam;
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
	private DriverServiceImpl driverService;
	//selection of all devices from user controller not device controller
	@Autowired 
	GeofenceServiceImpl geofenceService;
	
	
	@GetMapping("/getUserDevices")
	public ResponseEntity<?> devicesList(@RequestParam (value = "userId") Long userId,@RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "search", defaultValue = "") String search) {
 
		return deviceService.getAllUserDevices(userId,offset,search);
		
	}
	
	@PostMapping(path ="/createDevice/{userId}")
	public ResponseEntity<?> createDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
			 return deviceService.createDevice(device,userId);				
	}
	
	@PostMapping(path ="/editDevice/{userId}")
	public ResponseEntity<?> editDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
		
			 return deviceService.editDevice(device,userId);	
	}
	
	@GetMapping(path ="/deleteDevice")
	public ResponseEntity<?> deleteDevice(@RequestParam  (value = "userId") Long userId,@RequestParam (value = "deviceId") Long deviceId ) {
			
			 return deviceService.deleteDevice(userId,deviceId);			
	}
	
	@GetMapping(path ="/getDevicebyId")
	public ResponseEntity<?> getDevicebyId(@RequestParam (value = "deviceId") Long deviceId) {

			 return  deviceService.findDeviceById(deviceId);
	}
	
	@GetMapping(path = "/assignDeviceToDriver")
	public ResponseEntity<?> assignDeviceToDriver(@RequestParam (value = "driverId", defaultValue = "0") Long driverId,@RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId) {
		
//		if(device.getId() == null) {
//			return new	ResponseEntity<>("bad request",  HttpStatus.BAD_REQUEST);
//		}
//		Set<Driver> driver=new HashSet<>() ;
//		driver.add(driverService.getDriverById(driverId));
////		Set<User> user= userService.findById(userId);
//        device.setDriver(driver);
////		Device device = deviceService.findById(deviceId);
//	            
//		/*	if(device == null) {
//				//throw bad request
////				return "bad request";
//				return new ResponseEntity<>(device, HttpStatus.BAD_REQUEST);
//			}
//			else
//			{*/
//				String assignDevice = deviceService.assignDeviceToDriver(device);
//	      
//				 return new ResponseEntity<>(assignDevice, HttpStatus.OK);
//			//}	 
		return deviceService.assignDeviceToDriver(deviceId,driverId);		
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
