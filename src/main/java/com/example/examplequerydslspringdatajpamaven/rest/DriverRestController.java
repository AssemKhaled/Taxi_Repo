package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;



@RestController
@RequestMapping(path = "/drivers")
@CrossOrigin
public class DriverRestController {
	
	@Autowired
	DriverServiceImpl driverServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@RequestMapping(value = "/getAllDrivers", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDrivers(@RequestParam (value = "userId", defaultValue = "0") Long id,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "search", defaultValue = "") String search) {
		
		offset=offset-1;
		if(offset <0) {
			offset=0;
		}

		
    	return  ResponseEntity.ok(driverServiceImpl.getAllDrivers(id,offset,search).getBody());

	}
	
	@RequestMapping(value = "/getDriverById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverById(@RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		
		return ResponseEntity.ok(driverServiceImpl.findById(driverId).getBody());

	}
	
	
	@RequestMapping(value = "/deleteDriver", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		
		
		return ResponseEntity.ok(driverServiceImpl.deleteDriver(driverId).getBody());

	}
	
	@RequestMapping(value = "/addDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody(required = false) Driver driver,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return ResponseEntity.ok(driverServiceImpl.addDriver(driver,id).getBody());

		
	}	
	@RequestMapping(value = "/editDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editDriver(@RequestBody(required = false) Driver driver,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		
		return ResponseEntity.ok(driverServiceImpl.editDriver(driver,id).getBody());


	}	
	
	// added by Maryam
	@GetMapping(path = "/getUnassignedDrivers")
	public ResponseEntity<?> getUnassignedDrivers(@RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return driverServiceImpl.getUnassignedDrivers(userId);
	}

	
	

}
