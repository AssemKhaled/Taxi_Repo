package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

@CrossOrigin
@Component
@RequestMapping(path = "/home")
public class DashBoardRestController {
	
	@Autowired
	DeviceServiceImpl deviceService;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	ReportServiceImpl reportServiceImpl;
	
 
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
	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotifications(@RequestParam (value = "userId", defaultValue = "0") Long userId,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "search", defaultValue = "") String search) {
		

		
    	return  ResponseEntity.ok(reportServiceImpl.getNotifications(userId, offset,search).getBody());

	}
	@RequestMapping(value = "/vehicleInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfo(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		

		
    	return  ResponseEntity.ok(deviceService.vehicleInfo(deviceId).getBody());

	}
	
}
