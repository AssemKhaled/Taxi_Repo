package com.example.examplequerydslspringdatajpamaven.rest;


import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.examplequerydslspringdatajpamaven.service.LoginService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Services Login and Logout Component
 * @author fuinco
 *
 */
@CrossOrigin
@RestController
public class LoginRestController {
	
	@Autowired
	private LoginService loginService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DriverRepository driverRepository;

//	@RequestMapping(value = "/activityToAttributes", method = RequestMethod.GET)
//	public void activityToAttributes() throws JsonProcessingException {
//		List<User> allUsers = userRepository.findAll();
//		for(User user : allUsers){
//			String activity = user.getActivity();
//			HashMap<String,String> attributes = new HashMap<>();
//			attributes.put("activity",activity);
//			ObjectMapper objectMapper = new ObjectMapper();
//			String json = objectMapper.writeValueAsString(attributes);
//			user.setAttributes(json);
//			userRepository.save(user);
//		}
//	}

	@RequestMapping(value = "/activityToAttributes/devices", method = RequestMethod.GET)
	public void activityToAttributesDevices() throws JsonProcessingException{
		List<Device> allDevices = deviceRepository.findAll();
		for(Device device : allDevices){
			String activity = device.getActivity();
			HashMap<String,String> attributes = new HashMap<>();
			attributes.put("activity",activity);
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(attributes);
			device.setAttributes(json);
			deviceRepository.save(device);
		}
	}


	@RequestMapping(value = "/vendorToParents", method = RequestMethod.GET)
	public void vendorToParents() throws JsonProcessingException{
		List<User> allUsers = userRepository.findAll();
		for(User user : allUsers){
			Integer vendor = 34;
			HashMap<String,Integer> parents = new HashMap<>();
			parents.put("vendorId",vendor);
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(parents);
			user.setParents(json);
			userRepository.save(user);
		}
	}

	public void deviceDriverRelation(){
		List<User> allUsers = userRepository.findAll();
		for(User user : allUsers){

		}
	}

	@GetMapping(path = "/login")
	public 	ResponseEntity<?> login(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

		return loginService.login(authtorization);
	}
	
	@GetMapping(path = "/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
		
		return loginService.logout(TOKEN);
	}
	
	
	
		

		
}
