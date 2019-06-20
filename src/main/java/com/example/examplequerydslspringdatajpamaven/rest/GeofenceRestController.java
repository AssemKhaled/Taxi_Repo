package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.service.GeofenceServiceImpl;

@RestController
@RequestMapping(path = "/geofences")
public class GeofenceRestController {
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;

	@RequestMapping(value = "/")
	public ResponseEntity<?> noService1() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "")
	public ResponseEntity<?> noService2() {
		return ResponseEntity.ok("no service available");
		
	}
	
	@RequestMapping(value = "/get_all_geofences/{userId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getGeofences(@PathVariable (value = "userId") String id) {
		
		if(Integer.parseInt(id) != 0) {
			
			return ResponseEntity.ok(geofenceServiceImpl.getAllGeofences(Integer.parseInt(id)));

		}
		else{
			
			return ResponseEntity.ok("no user selected to get his own geofences");			
		
		}
		
		
	}
	
	@RequestMapping(value = "/get_geofence_by_id/{geofenceId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getGeofenceById(@PathVariable (value = "geofenceId") String geofenceId) {
		
		if(Integer.parseInt(geofenceId) != 0) {
			
			return ResponseEntity.ok(geofenceServiceImpl.getGeofenceById(Integer.parseInt(geofenceId)));
						
		}
		else {
			
			return ResponseEntity.ok("no geofence selected");

		}
		
	}
	
	@RequestMapping(value = "/delete_geofence/{geofenceId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> deleteDriver(@PathVariable (value = "geofenceId") String geofenceId) {
		
		if(Integer.parseInt(geofenceId) != 0) {
			Geofence res= geofenceServiceImpl.getGeofenceById(Integer.parseInt(geofenceId));
			if(res != null) {
				
				geofenceServiceImpl.deleteGeofence(Integer.parseInt(geofenceId));
				return ResponseEntity.ok("Deleted successfully.");
				
			}
			else {

				return ResponseEntity.ok("not allow to delete this geofence.");

			}
						
		}
		else {
			
			return ResponseEntity.ok("no geofence selected");

		}
		
	}
	
	@RequestMapping(value = "/add_geofence/{userId}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody Map<String, Object> geofence,@PathVariable (value = "userId") String id) {
		if(Integer.parseInt(id) != 0) {
			
			
			return null;
		}
		else {
			
			return ResponseEntity.ok("no user selected to add his own geofence");

			
		}
		
	}	

}
