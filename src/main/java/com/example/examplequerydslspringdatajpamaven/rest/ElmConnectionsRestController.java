package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Map;

import com.example.examplequerydslspringdatajpamaven.entity.ElmReturn;
import com.example.examplequerydslspringdatajpamaven.responses.ElmRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.example.examplequerydslspringdatajpamaven.service.ElmServiceImpl;


/**
 * Service of elm connections
 * @author fuinco
 *
 */
//@Component
@RequestMapping(path = "/elm")
@CrossOrigin
@RestController
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
	boolean start = true;
	
	@GetMapping(path ="/lastLocations")
//	@Scheduled(fixedRate = 10000)
	public void lastLocations(){
		if(start){
			start = false;
			elmServiceImpl.lastLocations();
		}
//		return null;

	}

//	@Scheduled(fixedRate = 6000)
//	public void lastLocations2(){
//		elmServiceImpl.lastLocationsHelper1();
//	}
	@GetMapping(path ="/lastLocations/test")
	public ResponseEntity<?> lastLocationTest(@RequestParam(value = "size", defaultValue = "0") int size){
		return elmServiceImpl.lastLocationsTest(size);
	}

	@GetMapping(path ="/lastLocations/tow")
//	@Scheduled(fixedRate = 10000)
	public ResponseEntity<?> lastLocationsForTowCar(){
		return elmServiceImpl.lastLocationsForTowCar();
	}

	@GetMapping(path ="/lastLocations/tow/pythonCalls")
	public ResponseEntity<?> lastLocationsForTowCarForPython(@RequestParam(value = "size", defaultValue = "0") int size){
		return elmServiceImpl.lastLocationsForTowCarForPythonCall(size);
	}

	@PostMapping("/elmReturnTest")
	public ResponseEntity<?> elmReturnTest(@RequestBody ElmRequest elmReturn){
		return elmServiceImpl.lastLocationsSaveResponseTeElm(elmReturn);
	}
	@PostMapping("/elmReturn/tow")
	public ResponseEntity<?> elmReturnForTowCars(@RequestBody ElmRequest elmReturn){
		return elmServiceImpl.towCarsLastLocationsSaveResponseTeElm(elmReturn);
	}
	
	@GetMapping(path ="/getExpiredVehicles")
//	@Scheduled(cron = "0 59 23 ? * *")
	public ResponseEntity<?> getExpiredVehicles(){
		
		return elmServiceImpl.getExpiredVehicles();
	}
	
	@GetMapping(path ="/getRemoveOldLogs")
	public ResponseEntity<?> getRemoveOldLogs(){
		
		return elmServiceImpl.getRemoveOldLogs();
	}
	
	@GetMapping(path ="/getRemoveOldPositions")
	public ResponseEntity<?> getRemoveOldPositions(){
		
		return elmServiceImpl.getRemoveOldPositions();
	}
	
	@GetMapping(path ="/getRemoveOldEvents")
	public ResponseEntity<?> getRemoveOldEvents(){
		
		return elmServiceImpl.getRemoveOldEvents();
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
	
	@GetMapping(path ="/deleteOldExpiredData")
	public ResponseEntity<?> deleteOldExpiredData(){
		
		return elmServiceImpl.deleteOldExpiredData();
	}


	@GetMapping(path ="/scripts/deviceInquery/issue")
	public int deviceInqueryIssue(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
										   @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
										   @RequestParam (value = "userId", defaultValue = "0") Long userId){

		return elmServiceImpl.deviceInqueryIssue(TOKEN,userId);
	}

	@GetMapping(path = "/findLastPositionsSequenceNumberSpeedZero")
	public ResponseEntity<?> findLastPositionsSequenceNumberSpeedZero(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findLastPositionsSequenceNumberSpeedZero(sequenceNumber);
	}

	@GetMapping(path = "/findLastPositionsSequenceNumberNoneSpeedZero")
	public ResponseEntity<?> findLastPositionsSequenceNumberNoneSpeedZero(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findLastPositionsSequenceNumberNoneSpeedZero(sequenceNumber);
	}


	@GetMapping(path = "/findLastNoneZeroVelocityPositionsBySequenceNumber")
	public ResponseEntity<?> findLastNoneZeroVelocityPositionsBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findLastNoneZeroVelocityPositionsBySequenceNumber(sequenceNumber);
	}

	@GetMapping(path = "/findLastZeroVelocityPositionsBySequenceNumber")
	public ResponseEntity<?> findLastZeroVelocityPositionsBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findLastZeroVelocityPositionsBySequenceNumber(sequenceNumber);
	}


	@GetMapping(path ="/findDeviceData")
	public ResponseEntity<?> findDeviceData(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findDeviceData(sequenceNumber);
	}
	@GetMapping(path ="/findDeviceLastPosition")
	public ResponseEntity<?> findDeviceLastPosition(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImpl.findDeviceLastPosition(sequenceNumber);
	}

}
