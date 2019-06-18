package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.service.DeviceServiceImpl;
import com.example.service.UserServiceImpl;

@RestController
@RequestMapping(path = "/devices")
public class DeviceRestController {
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@Autowired
	private UserServiceImpl userService;
	//selection of all devices from user controller not device controller
	@GetMapping(path = "/getAllUserDevices")
	public List<Device> getAllUserDevices() {
		
		return deviceService.getAllUserDevices();
	}
	
	@PostMapping(path ="/createDevice/{userId}")
	public ResponseEntity<Device> createDevice(@PathVariable (value = "userId") Long userId,@RequestBody(required = false) Device device) {
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
			 Device newDevice= deviceService.createDevice(device);
			 return new ResponseEntity<>(newDevice, HttpStatus.OK);
		//}
			
		
		
		
		
	}

}
