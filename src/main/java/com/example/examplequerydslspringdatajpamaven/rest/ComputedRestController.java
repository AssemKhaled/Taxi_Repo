package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.service.ComputedServiceImpl;

/**
 * Services of Computed attributes component 
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/computed")
public class ComputedRestController {

	@Autowired
	ComputedServiceImpl computedServiceImpl;
	
	@PostMapping(path ="/createComputed")
	public ResponseEntity<?> createComputed(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestParam (value = "userId",defaultValue = "0") Long userId,
            @RequestBody(required = false) Attribute attrbuite) {
			 return computedServiceImpl.createComputed(TOKEN, attrbuite,userId);				
	}
	
	@RequestMapping(value = "/getAllComputed", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllComputed(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											            @RequestParam (value = "exportData", defaultValue = "") String exportData,          
														@RequestParam (value = "userId", defaultValue = "0") Long id,
														@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  computedServiceImpl.getAllComputed(TOKEN,id,offset,search,exportData);

	}
	
	@RequestMapping(value = "/getComputedById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getComputedById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "attributeId", defaultValue = "0") Long attributeId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  computedServiceImpl.getComputedById(TOKEN,attributeId,userId);

	}
	
	@RequestMapping(value = "/editComputed", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editComputed(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Attribute attribute,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return computedServiceImpl.editComputed(TOKEN,attribute,id);

	}
	
	@RequestMapping(value = "/deleteComputed", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteComputed(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "attributeId", defaultValue = "0") Long attributeId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  computedServiceImpl.deleteComputed(TOKEN,attributeId,userId);


	}
	
	@RequestMapping(value = "/assignComputedToGroup", method = RequestMethod.POST)
	public ResponseEntity<?> assignComputedToGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return computedServiceImpl.assignComputedToGroup(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignComputedToDevice", method = RequestMethod.POST)
	public ResponseEntity<?> assignComputedToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return computedServiceImpl.assignComputedToDevice(TOKEN,deviceId,data,userId);	
		
	}
	
	@RequestMapping(value = "/getComputedSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getComputedSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                             @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
                                                             @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
                                                             @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                                 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		

    	return  computedServiceImpl.getComputedSelect(TOKEN,loggedUserId,userId,deviceId,groupId);
		
	}
	
	@GetMapping("/assignClientComputeds")
	public ResponseEntity<?> assignClientComputeds(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "computedIds", defaultValue = "0") Long [] computedIds) {
		return computedServiceImpl.assignClientComputeds(TOKEN,loggedUserId,userId,computedIds);
	}
	
	@GetMapping("/getClientComputeds")
	public ResponseEntity<?> getClientComputeds(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return computedServiceImpl.getClientComputeds(TOKEN,loggedUserId,userId);
	}
	
	@RequestMapping(value = "/getComputedUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getComputedUnSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                                                       @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                                   @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		

    	return  computedServiceImpl.getComputedUnSelect(TOKEN,loggedUserId,userId);
		
	}
}
