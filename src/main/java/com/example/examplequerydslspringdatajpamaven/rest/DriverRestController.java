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


/**
 * Service of driver component 
 * @author fuinco
 *
 */
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
											          @RequestParam (value = "exportData", defaultValue = "") String exportData,                                          
													  @RequestParam (value = "userId", defaultValue = "0") Long id,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  driverServiceImpl.getAllDrivers(TOKEN,id,offset,search,exportData);

	}
	
	@RequestMapping(value = "/getDriverById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                             @RequestParam(value = "userId",defaultValue = "0")Long userId) {
		
		
		return driverServiceImpl.findById(TOKEN,driverId,userId);

	}
	
	
	@RequestMapping(value = "/deleteDriver", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                            @RequestParam(value = "userId",defaultValue = "0") Long userId) {
		
		
		
		return driverServiceImpl.deleteDriver(TOKEN,driverId,userId);

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
	

	@GetMapping(path = "/getUnassignedDrivers")
	public ResponseEntity<?> getUnassignedDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                  @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,                                              
	                                              @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                              
			                                      @RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return driverServiceImpl.getUnassignedDrivers(TOKEN,loggedUserId,userId,deviceId);
	}
	
	@RequestMapping(value = "/getDriverSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												           @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                              
														   @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  driverServiceImpl.getDriverSelect(TOKEN,loggedUserId,userId);

		
	}
	
	@RequestMapping(value = "/getDriverSelectGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverSelectGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                           @RequestParam (value = "groupId", defaultValue = "0") Long groupId,                                               
												           @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                              
														   @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  driverServiceImpl.getDriverSelectGroup(TOKEN,loggedUserId,userId,groupId);

		
	}
	
	@RequestMapping(value = "/getDriverUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverUnSelectOfClient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															         @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                           
																     @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		 
	
    	return  driverServiceImpl.getDriverUnSelectOfClient(TOKEN,loggedUserId,userId);

		
	}

	@GetMapping(value = "/assignDriverToUser")
	public ResponseEntity<?> assignDeviceToUser( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return driverServiceImpl.assignDriverToUser(TOKEN,userId,driverId,toUserId);
	}
	
	@GetMapping("/assignClientDrivers")
	public ResponseEntity<?> assignClientDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "driverIds", defaultValue = "0") Long [] driverIds) {
		return driverServiceImpl.assignClientDrivers(TOKEN,loggedUserId,userId,driverIds);
	}
	
	@GetMapping("/getClientDrivers")
	public ResponseEntity<?> getClientDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return driverServiceImpl.getClientDrivers(TOKEN,loggedUserId,userId);
	}
	

}
