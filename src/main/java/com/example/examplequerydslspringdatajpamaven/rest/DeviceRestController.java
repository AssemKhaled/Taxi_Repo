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
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

/**
 * Services of Device Component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/devices")
public class DeviceRestController {
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@GetMapping("/getUserDevices")
	public ResponseEntity<?> devicesList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                             @RequestParam (value = "exportData", defaultValue = "") String exportData,
			                             @RequestParam (value = "userId",defaultValue = "0") Long userId,
										 @RequestParam(value = "offset", defaultValue = "0") int offset,
							             @RequestParam(value = "search", defaultValue = "") String search) {
 
		return deviceService.getAllUserDevices(TOKEN,userId,offset,search,exportData);
		
	}
	
	@PostMapping(path ="/createDevice")
	public ResponseEntity<?> createDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Device device) {
			 return deviceService.createDevice(TOKEN,device,userId);				
	}
	
	@PostMapping(path ="/editDevice")
	public ResponseEntity<?> editDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                            @RequestBody(required = false) Device device) {
		
			 return deviceService.editDevice(TOKEN,device,userId);	
	}
	
	@GetMapping(path ="/deleteDevice")
	public ResponseEntity<?> deleteDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
			 return deviceService.deleteDevice(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path ="/getDevicebyId")
	public ResponseEntity<?> getDevicebyId(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                               @RequestParam(value = "userId",defaultValue = "0") Long userId) {

			 return  deviceService.findDeviceById(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path = "/assignDeviceToDriver")
	public ResponseEntity<?> assignDeviceToDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												  @RequestParam (value = "driverId", defaultValue = "0") String driverId,
												  @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
												  @RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		try
	    {
			Long.parseLong(driverId);
	    }
	    catch(NumberFormatException ex)
	    {
	    	GetObjectResponse getObjectResponse;

	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " Input is not Number or Can't Assign more than 2  Drivers to the Device",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
	    }

		return deviceService.assignDeviceToDriver(TOKEN,deviceId,Long.parseLong(driverId),userId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDevice")
	public ResponseEntity<?> assignGeofencesToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
			                                         @RequestParam(value = "userId" , defaultValue = "0")Long userId,
			                                         @RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds) {
	
	     return deviceService.assignDeviceToGeofences(TOKEN,deviceId,geoIds,userId);	
				
	}

	
	@GetMapping(value = "/getDeviceDriver")
	public @ResponseBody ResponseEntity<?> getDeviceDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return deviceService.getDeviceDriver(TOKEN,deviceId);
	}
	
	@GetMapping(value = "/getDeviceGeofences")
	public @ResponseBody ResponseEntity<?> getDeviceGeofences(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
			return deviceService.getDeviceGeofences(TOKEN,deviceId);

	}
	
	@RequestMapping(value = "/getDeviceSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												           @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                               
														   @RequestParam (value = "userId", defaultValue = "0") Long userId) {
														
	
    	return  deviceService.getDeviceSelect(TOKEN,loggedUserId,userId);

		
	}
	@RequestMapping(value = "/getDeviceSelectGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelectGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                    @RequestParam (value = "groupId", defaultValue = "0") Long groupId,                                               
														        @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                               
																@RequestParam (value = "userId", defaultValue = "0") Long userId) {
																
		
    	return  deviceService.getDeviceSelectGroup(TOKEN,loggedUserId,userId,groupId);

		
	}
	
	
	@RequestMapping(value = "/getDeviceUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceUnSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                             @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                                 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  deviceService.getDeviceUnSelect(TOKEN,loggedUserId,userId);

		
	}
	
	@GetMapping(value = "/assignDeviceToUser")
	public ResponseEntity<?> assignDeviceToUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return deviceService.assignDeviceToUser(TOKEN,userId,deviceId,toUserId);
	}

	@GetMapping(value = "/getCalibrationData")
	public ResponseEntity<?> getCalibrationData( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceService.getCalibrationData(TOKEN,userId,deviceId);
	}
	@PostMapping(value = "/addDataToCaliberation")
	public ResponseEntity<?> addDataToCaliberation( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, List> data){
		return deviceService.addDataToCaliberation(TOKEN,userId,deviceId,data);
	}
	@GetMapping(value = "/getFuelData")
	public ResponseEntity<?> getFuelData( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceService.getFuelData(TOKEN,userId,deviceId);
	}
	@PostMapping(value = "/addDataToFuel")
	public ResponseEntity<?> addDataToFuel(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                               @RequestParam (value = "userId", defaultValue = "0") Long userId,
										   @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
										   @RequestBody Map<String, Object> data){
		return deviceService.addDataToFuel(TOKEN,userId,deviceId,data);
	}
	
	@GetMapping(value = "/getSensorSettings")
	public ResponseEntity<?> getSensorSettings( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceService.getSensorSettings(TOKEN,userId,deviceId);
	}

	@PostMapping(value = "/sendCommand")
	public ResponseEntity<?> sendCommand( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
									    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
									    @RequestBody Map<String, Object> data){
		return deviceService.sendCommand(TOKEN,userId,deviceId,data);
	}
	
	@PostMapping(value = "/addSensorSettings")
	public ResponseEntity<?> addSensorSettings( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, Object> data){
		return deviceService.addSensorSettings(TOKEN,userId,deviceId,data);
	}
	
	@PostMapping(value = "/addIcon")
	public ResponseEntity<?> addIcon( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, Object> data){
		return deviceService.addIcon(TOKEN,userId,deviceId,data);
	}
	
	@GetMapping(value = "/getIcon")
	public ResponseEntity<?> getIcon( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceService.getIcon(TOKEN,userId,deviceId);
	}
	
	@GetMapping(value = "/getDeviceDataSelected")
	public @ResponseBody ResponseEntity<?> getDeviceDataSelected(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                                               @RequestParam (value = "type", defaultValue = "") String type) {
		
			
			return deviceService.getDeviceDataSelected(TOKEN,deviceId,type);

	}
	
	@GetMapping("/assignClientDevices")
	public ResponseEntity<?> assignClientDevices(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "deviceIds", defaultValue = "0") Long [] deviceIds) {
		return deviceService.assignClientDevices(TOKEN,loggedUserId,userId,deviceIds);
	}
	
	@GetMapping("/getClientDevices")
	public ResponseEntity<?> getClientDevices(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return deviceService.getClientDevices(TOKEN,loggedUserId,userId);
	}
	
	@GetMapping("/updateLineData")
	public ResponseEntity<?> updateLineData() {
		return deviceService.updateLineData();
	}
	
	@GetMapping("/updatePositionData")
	public ResponseEntity<?> updatePositionData() {
		return deviceService.updatePositionData();
	}
	
	
	
}
