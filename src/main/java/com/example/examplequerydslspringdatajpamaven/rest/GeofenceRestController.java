package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


/**
 * Services of geofence component
 * @author fuinco
 *
 */
@RestController
@RequestMapping(path = "/geofences")
@CrossOrigin
public class GeofenceRestController {
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@RequestMapping(value = "/getAllGeofences", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofences(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                        @RequestParam (value = "exportData", defaultValue = "") String exportData,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id,
														@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  geofenceServiceImpl.getAllGeofences(TOKEN,id,offset,search,exportData);

	}
	
	@RequestMapping(value = "/getGeofenceById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  geofenceServiceImpl.getGeofenceById(TOKEN,geofenceId,userId);

	}
	
	@RequestMapping(value = "/deleteGeofence", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  geofenceServiceImpl.deleteGeofence(TOKEN,geofenceId,userId);


	}
	
	@RequestMapping(value = "/addGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addGeofence(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Geofence geofence,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		

		return geofenceServiceImpl.addGeofence(TOKEN,geofence,id);

	}
	
	@RequestMapping(value = "/editGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGeofence(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Geofence geofence,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return geofenceServiceImpl.editGeofence(TOKEN,geofence,id);

	}	
	@RequestMapping(value = "/getAllGeo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllGeo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return geofenceServiceImpl.getAllGeo(TOKEN,id);

	}	
	
	@RequestMapping(value = "/getGeofenceSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												             @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												             @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
                                                             @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                                 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  geofenceServiceImpl.getGeofenceSelect(TOKEN,loggedUserId,userId,deviceId,groupId);

		
	}
	
	@RequestMapping(value = "/getGeofenceUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceUnSelectOfClient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															            @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                                 
																		@RequestParam (value = "userId", defaultValue = "0") Long userId) {
																	
	
    	return  geofenceServiceImpl.getGeofenceUnSelectOfClient(TOKEN,loggedUserId,userId);

		
	}
	
	@GetMapping("/assignClientGeofence")
	public ResponseEntity<?> assignClientGeofence(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "geofenceIds", defaultValue = "0") Long [] geofenceIds) {
		return geofenceServiceImpl.assignClientGeofences(TOKEN,loggedUserId,userId,geofenceIds);
	}
	
	@GetMapping("/getClientGeofences")
	public ResponseEntity<?> getClientGeofences(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return geofenceServiceImpl.getClientGeofences(TOKEN,loggedUserId,userId);
	}

}
