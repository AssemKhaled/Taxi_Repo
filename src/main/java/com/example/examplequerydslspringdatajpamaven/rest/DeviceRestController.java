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
		

		return deviceService.assignDeviceToDriver(deviceId,driverId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDevice")
	public ResponseEntity<?> assignGeofencesToDevice(@RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds,@RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId) {
	
				return deviceService.assignDeviceToGeofences(deviceId,geoIds);	
				
		}
	 @GetMapping(path = "/testResponse")
	  public ResponseEntity<?> testResponse(){
		 return deviceService.testgetDeviceById();
	 }
	
	@GetMapping(value = "/getDeviceDriver")
	public @ResponseBody ResponseEntity<?> getDeviceDriver(@RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return deviceService.getDeviceDriver(deviceId);
	}
	
	@	GetMapping(value = "/getDeviceGeofences")
	public @ResponseBody ResponseEntity<?> getDeviceGeofences(@RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
			return deviceService.getDeviceGeofences(deviceId);

		
		
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
