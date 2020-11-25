package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

import com.example.examplequerydslspringdatajpamaven.service.ElmServiceImpl;


/**
 * Service of elm connections
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/elm")
public class ElmConnectionsRestController {

	
	@Autowired
	ElmServiceImpl elmServiceImpl;
	
	@GetMapping(path ="/companyRegistrtaion")
	public ResponseEntity<?> companyRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyRegistrtaion(TOKEN,userId,loggedUserId);
	}
	
	@PostMapping(path ="/companyUpdate")
	public ResponseEntity<?> companyUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyUpdate(TOKEN,data,userId,loggedUserId);
	}
	
	@GetMapping(path ="/companyDelete")
	public ResponseEntity<?> companyDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyDelete(TOKEN,userId,loggedUserId);
	}
	
	@GetMapping(path ="/deviceRegistrtaion")
	public ResponseEntity<?> deviceRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceRegistrtaion(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path ="/deviceDelete")
	public ResponseEntity<?> deviceDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceDelete(TOKEN,deviceId,userId);
	}
	
	
	@PostMapping(path ="/deviceUpdate")
	public ResponseEntity<?> deviceUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceUpdate(TOKEN,data,deviceId,userId);
	}
	
	@GetMapping(path ="/driverRegistrtaion")
	public ResponseEntity<?> driverRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverRegistrtaion(TOKEN,driverId,userId);
	}
	
	@PostMapping(path ="/driverUpdate")
	public ResponseEntity<?> driverUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverUpdate(TOKEN,data,driverId,userId);
	}
	
	
	
	@GetMapping(path ="/deviceInquery")
	public ResponseEntity<?> deviceInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceInquery(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path ="/companyInquery")
	public ResponseEntity<?> companyInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyInquery(TOKEN,userId,loggedUserId);
	}
	
	@GetMapping(path ="/driverInquery")
	public ResponseEntity<?> driverInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverInquery(TOKEN,driverId,userId);
	}
	
	@GetMapping(path ="/driverDelete")
	public ResponseEntity<?> driverDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverDelete(TOKEN,driverId,userId);
	}
	
	@RequestMapping(value = "/getAllLogs", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getLogs(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "loggedUserId", defaultValue = "0") Long id,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                       @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                       @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												   @RequestParam (value = "offset", defaultValue = "0") int offset,
												   @RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  elmServiceImpl.getLogs(TOKEN,id,userId,driverId,deviceId,offset,search);

	}
	
	@GetMapping(path ="/lastLocations")
//	@Scheduled(fixedRate = 30000)
	public ResponseEntity<?> lastLocations(){
		
		return elmServiceImpl.lastLocations();
	}
	
	@GetMapping(path ="/getExpiredVehicles")
//	@Scheduled(fixedRate = 30000)
	public ResponseEntity<?> getExpiredVehicles(){
		
		return elmServiceImpl.getExpiredVehicles();
	}
	
	@GetMapping(path ="/checkBySequenceNumber")
	public ResponseEntity<?> checkBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){
		
		return elmServiceImpl.checkBySequenceNumber(sequenceNumber);
	}
	
	@PostMapping(path ="/deleteVehicleFromElm")
	public ResponseEntity<?> deleteVehicleFromElm(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		
		return elmServiceImpl.deleteVehicleFromElm(TOKEN,deviceId,userId,data);
	}
}
