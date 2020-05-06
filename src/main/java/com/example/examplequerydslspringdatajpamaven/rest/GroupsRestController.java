package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.GroupsService;
import com.example.examplequerydslspringdatajpamaven.service.GroupsServiceImpl;

@CrossOrigin
@Component
@RequestMapping(path = "/group")
public class GroupsRestController {
	
	
	@Autowired
	GroupsServiceImpl groupsServiceImpl;

	
	
	@PostMapping(path ="/createGroup")
	public ResponseEntity<?> createGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestParam (value = "userId",defaultValue = "0") Long userId,
            @RequestBody(required = false) Group group) {
			 return groupsServiceImpl.createGroup(TOKEN, group,userId);				
	}
	
	@RequestMapping(value = "/getAllGroups", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGroups(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id,
														@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  groupsServiceImpl.getAllGroups(TOKEN,id,offset,search);

	}
	
	@RequestMapping(value = "/getGroupById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGroupById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  groupsServiceImpl.getGroupById(TOKEN,groupId,userId);

	}
	
	@RequestMapping(value = "/editGroup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Group group,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return groupsServiceImpl.editGroup(TOKEN,group,id);

	}
	
	@RequestMapping(value = "/deleteGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  groupsServiceImpl.deleteGroup(TOKEN,groupId,userId);


	}
	
	@RequestMapping(value = "/assignGroupToDriver", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToDriver(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignGroupToGeofence", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToGeofence(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToGeofence(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignGroupToDevice", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToDevice(TOKEN,groupId,data,userId);	
		
	}

}
