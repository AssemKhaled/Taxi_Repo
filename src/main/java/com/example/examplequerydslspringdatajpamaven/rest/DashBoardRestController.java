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
import org.springframework.web.bind.annotation.RequestHeader;
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
	public ResponseEntity<?> devicesStatuesAndAllDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return deviceService.getDeviceStatus(TOKEN,userId);
	}
	
	@GetMapping(path = "/getAllDevicesLastInfo")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
												   @RequestParam (value = "offset", defaultValue = "0")int offset,
												   @RequestParam (value = "search", defaultValue = "") String search ){
		
		return deviceService.getAllDeviceLiveData(TOKEN,userId, offset, search);
	}
	
	@GetMapping(path = "/getAllDevicesLastInfoMap")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId
			                                       ){		
		return deviceService.getAllDeviceLiveDataMap(TOKEN,userId);
	}

	@GetMapping(path = "/getDeviceLiveData")
	public ResponseEntity<?> getDevicesLiveData(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                    @RequestParam(value = "userId",defaultValue = "0") Long userId){
		
		return deviceService.getDeviceLiveData(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path = "/getDeviceLiveDataMap")
	public ResponseEntity<?> getDevicesLiveDataMap(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                    @RequestParam(value = "userId",defaultValue = "0")Long userId){
		
		return deviceService.getDeviceLiveDataMap(TOKEN,deviceId,userId);
	}
	

	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId,
															@RequestParam (value = "offset", defaultValue = "0") int offset,
															@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  reportServiceImpl.getNotifications(TOKEN,userId, offset,search);

	}
	@RequestMapping(value = "/vehicleInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                           @RequestParam(value = "userId",defaultValue = "0")Long userId){
		

		
    	return  deviceService.vehicleInfo(TOKEN,deviceId,userId);

	}

	
}
