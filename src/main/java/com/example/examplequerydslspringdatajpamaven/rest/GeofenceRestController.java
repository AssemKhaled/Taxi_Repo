package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


@RestController
@RequestMapping(path = "/geofences")
@CrossOrigin
public class GeofenceRestController {
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@RequestMapping(value = "/getAllGeofences", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofences(@RequestParam (value = "userId", defaultValue = "0") Long id,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "search", defaultValue = "") String search) {
		offset=offset-1;
		if(offset <0) {
			offset=0;
		}
		
		
    	return  ResponseEntity.ok(geofenceServiceImpl.getAllGeofences(id,offset,search).getBody());

	}
	
	@RequestMapping(value = "/getGeofenceById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceById(@RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId) {
		
		
    	return  ResponseEntity.ok(geofenceServiceImpl.getGeofenceById(geofenceId).getBody());

	}
	
	@RequestMapping(value = "/deleteGeofence", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId) {

    	return  ResponseEntity.ok(geofenceServiceImpl.deleteGeofence(geofenceId).getBody());


	}
	
	@RequestMapping(value = "/addGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody(required = false) Geofence geofence,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		

		return ResponseEntity.ok(geofenceServiceImpl.addGeofence(geofence,id).getBody());

	}
	
	@RequestMapping(value = "/editGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGeofence(@RequestBody(required = false) Geofence geofence,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return ResponseEntity.ok(geofenceServiceImpl.editGeofence(geofence,id).getBody());

	}	

}
