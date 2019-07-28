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
	public @ResponseBody ResponseEntity<?> getDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "search", defaultValue = "") String search) {
		
		offset=offset-1;
		if(offset <0) {
			offset=0;
		}

		
    	return  driverServiceImpl.getAllDrivers(TOKEN,id,offset,search);

	}
	
	@RequestMapping(value = "/getDriverById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		
		return driverServiceImpl.findById(TOKEN,driverId);

	}
	
	
	@RequestMapping(value = "/deleteDriver", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		
		
		return driverServiceImpl.deleteDriver(TOKEN,driverId);

	}
	
	@RequestMapping(value = "/addDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Driver driver,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return driverServiceImpl.addDriver(TOKEN,driver,id);

		
	}	
	@RequestMapping(value = "/editDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Driver driver,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		
		return driverServiceImpl.editDriver(TOKEN,driver,id);


	}	
	
	// added by Maryam
	@GetMapping(path = "/getUnassignedDrivers")
	public ResponseEntity<?> getUnassignedDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                      @RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return driverServiceImpl.getUnassignedDrivers(TOKEN,userId);
	}

	
	

}
