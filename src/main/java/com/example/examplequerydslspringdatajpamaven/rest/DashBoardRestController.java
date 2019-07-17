package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;

@CrossOrigin
@Component
@RequestMapping(path = "/home")
public class DashBoardRestController {
	
	@Autowired
	DeviceServiceImpl deviceService;
	
 
	@GetMapping(path ="/getDevicesStatuesAndAllDrivers")
	public ResponseEntity<?> devicesStatuesAndAllDrivers(@RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return deviceService.getDeviceStatus(userId);
	}
	
	@GetMapping(path = "/getAllDevicesLastInfo")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestParam (value = "userId", defaultValue = "0") Long userId,
													@RequestParam (value = "offset", defaultValue = "0")int offset,
													@RequestParam (value = "search", defaultValue = "0") String search ){
		
		return deviceService.getAllDeviceLiveData(userId, offset, search);
	}
	@GetMapping(path = "/getDevicesLiveData")
	public ResponseEntity<?> getDevicesLiveData(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		
		return deviceService.getDeviceLiveData(deviceId);
	}
	
	
}
