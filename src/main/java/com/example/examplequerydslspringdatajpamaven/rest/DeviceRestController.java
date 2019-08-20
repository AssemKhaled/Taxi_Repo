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
import org.springframework.web.bind.annotation.RequestHeader;
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
	public ResponseEntity<?> devicesList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                             @RequestParam (value = "userId",defaultValue = "0") Long userId,
										 @RequestParam(value = "offset", defaultValue = "0") int offset,
							             @RequestParam(value = "search", defaultValue = "") String search) {
 
		return deviceService.getAllUserDevices(TOKEN,userId,offset,search);
		
	}
	
	@PostMapping(path ="/createDevice")
	public ResponseEntity<?> createDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Device device) {
			 return deviceService.createDevice(TOKEN,device,userId);				
	}
	
	@PostMapping(path ="/editDevice")
	public ResponseEntity<?> editDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                            @RequestBody(required = false) Device device) {
		
			 return deviceService.editDevice(TOKEN,device,userId);	
	}
	
	@GetMapping(path ="/deleteDevice")
	public ResponseEntity<?> deleteDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
			 return deviceService.deleteDevice(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path ="/getDevicebyId")
	public ResponseEntity<?> getDevicebyId(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                               @RequestParam(value = "userId",defaultValue = "0") Long userId) {

			 return  deviceService.findDeviceById(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path = "/assignDeviceToDriver")
	public ResponseEntity<?> assignDeviceToDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												  @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
												  @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
												  @RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		

		return deviceService.assignDeviceToDriver(TOKEN,deviceId,driverId,userId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDevice")
	public ResponseEntity<?> assignGeofencesToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
			                                         @RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds) {
	
				return deviceService.assignDeviceToGeofences(TOKEN,deviceId,geoIds);	
				
	}
	@GetMapping(path = "/testResponse")
	public ResponseEntity<?> testResponse(){
		 return deviceService.testgetDeviceById();
	 }
	
	@GetMapping(value = "/getDeviceDriver")
	public @ResponseBody ResponseEntity<?> getDeviceDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return deviceService.getDeviceDriver(TOKEN,deviceId);
	}
	
	@GetMapping(value = "/getDeviceGeofences")
	public @ResponseBody ResponseEntity<?> getDeviceGeofences(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
			return deviceService.getDeviceGeofences(TOKEN,deviceId);

	}
	@RequestMapping(value = "/getDeviceSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  deviceService.getDeviceSelect(TOKEN,userId);

		
	}
	
	@GetMapping(value = "/assignDeviceToUser")
	public ResponseEntity<?> assignDeviceToUser( @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return deviceService.assignDeviceToUser(userId,deviceId,toUserId);
	}

}
