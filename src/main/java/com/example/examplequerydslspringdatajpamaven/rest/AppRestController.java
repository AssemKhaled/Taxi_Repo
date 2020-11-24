package com.example.examplequerydslspringdatajpamaven.rest;

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
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.AppServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;;

/**
 * Services related to app
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/app")
public class AppRestController {
	
	
	@Autowired
	AppServiceImpl appService;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	
	@GetMapping(path = "/loginApp")
	public 	ResponseEntity<?> loginApp(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

		return appService.loginApp(authtorization);
	}
	
	@GetMapping(path = "/logoutApp")
	public ResponseEntity<?> logoutApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
		
		return appService.logoutApp(TOKEN);
	}
	
	@GetMapping(path = "/getAllDevicesMapApp")
	public ResponseEntity<?> getAllDevicesLastInfoMapApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId
			                                       ){		
		return appService.getAllDeviceLiveDataMapApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/vehicleInfoApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                           @RequestParam(value = "userId",defaultValue = "0")Long userId){
				
    	return  appService.vehicleInfoApp(TOKEN,deviceId,userId);

	}
	
	@GetMapping("/getDevicesListApp")
	public ResponseEntity<?> getDevicesListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                             @RequestParam (value = "userId",defaultValue = "0") Long userId,
										 @RequestParam(value = "offset", defaultValue = "0") int offset,
							             @RequestParam(value = "search", defaultValue = "") String search) {
 
		return appService.getDevicesListApp(TOKEN,userId,offset,search);
		
	}
	
	@PostMapping(path ="/createDeviceApp")
	public ResponseEntity<?> createDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Device device) {
			 return appService.createDeviceApp(TOKEN,device,userId);				
	}
	
	@PostMapping(path ="/editDeviceApp")
	public ResponseEntity<?> editDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                            @RequestBody(required = false) Device device) {
		
			 return appService.editDeviceApp(TOKEN,device,userId);	
	}
	
	@GetMapping(path ="/deleteDeviceApp")
	public ResponseEntity<?> deleteDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
			 return appService.deleteDeviceApp(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path = "/assignDeviceToDriverApp")
	public ResponseEntity<?> assignDeviceToDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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

		return appService.assignDeviceToDriverApp(TOKEN,deviceId,Long.parseLong(driverId),userId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDeviceApp")
	public ResponseEntity<?> assignGeofencesToDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
			                                         @RequestParam(value = "userId" , defaultValue = "0")Long userId,
			                                         @RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds) {
	
		return appService.assignGeofencesToDeviceApp(TOKEN,deviceId,geoIds,userId);	
				
	}
	
	@GetMapping(path ="/getDevicebyIdApp")
	public ResponseEntity<?> getDevicebyIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                               @RequestParam(value = "userId",defaultValue = "0") Long userId) {

		return  appService.findDeviceByIdApp(TOKEN,deviceId,userId);
	}
	
	@GetMapping(value = "/getDeviceDriverApp")
	public @ResponseBody ResponseEntity<?> getDeviceDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return appService.getDeviceDriverApp(TOKEN,deviceId);
	}
	
	@GetMapping(value = "/getDeviceGeofencesApp")
	public @ResponseBody ResponseEntity<?> getDeviceGeofencesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
		return appService.getDeviceGeofencesApp(TOKEN,deviceId);

	}
	
	
	@RequestMapping(value = "/getDriversListApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllDriversApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  appService.getAllDriversApp(TOKEN,id,offset,search);

	}
	
	@RequestMapping(value = "/getDriverByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                             @RequestParam(value = "userId",defaultValue = "0")Long userId) {
		
		
		return appService.getDriverByIdApp(TOKEN,driverId,userId);

	}
	
	@RequestMapping(value = "/deleteDriverApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                            @RequestParam(value = "userId",defaultValue = "0") Long userId) {
		
		
		
		return appService.deleteDriverApp(TOKEN,driverId,userId);

	}
	
	@RequestMapping(value = "/addDriverApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Driver driver,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appService.addDriverApp(TOKEN,driver,id);

		
	}	
	@RequestMapping(value = "/editDriverApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Driver driver,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		
		return appService.editDriverApp(TOKEN,driver,id);


	}
	@GetMapping(path = "/getUnassignedDriversApp")
	public ResponseEntity<?> getUnassignedDriversApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return appService.getUnassignedDriversApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getDriverSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getDriverSelectApp(TOKEN,userId);

		
	}
	
	
	@RequestMapping(value = "/getStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getStopsReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/getTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getTripsReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getSummaryReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSummaryReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getSummaryReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getEventsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "type", defaultValue = "") String type,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  appService.getEventsReportApp(TOKEN, deviceId,groupId, offset, start, end, type, search, userId);

	}
	
	@RequestMapping(value = "/getDeviceWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getDeviceWorkingHoursApp(TOKEN,deviceId,groupId, offset, start, end,search,userId);

	}
	@RequestMapping(value = "/getCustomReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCustomReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId,
													 @RequestParam (value = "custom", defaultValue = "") String custom,
													 @RequestParam (value = "value", defaultValue = "") String value) {
		
		
		
    	return  appService.getCustomReportApp(TOKEN,deviceId,groupId, offset, start, end,search,userId,custom,value);

	}
	
	@RequestMapping(value = "/getDriverWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getDriverWorkingHoursApp(TOKEN,driverId,groupId, offset,start, end,search,userId);

	}
	@RequestMapping(value = "/getDriveMoreThanReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriveMoreThanReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getDriveMoreThanReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	@RequestMapping(value = "/getEventsReportByTypeApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsTypeApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getEventsReportByTypeApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getSensorsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSensorsApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getSensorsReportApp(TOKEN,deviceId,groupId, offset, start, end,search,userId);

	}
	
	@RequestMapping(value = "/getNumTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getNumTripsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getNumStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getNumStopsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/geTotalTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> geTotalTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0")  Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.geTotalTripsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getTotalStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTotalStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getTotalStopsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/viewTripApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> viewTripApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													 @RequestParam (value = "startTime", defaultValue = "0") String startTime,
													 @RequestParam (value = "endTime", defaultValue = "0") String endTime) {	
    	return  appService.viewTripApp(TOKEN, deviceId,startTime, endTime);

	}
	
	@RequestMapping(value = "/getGeoListApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeoListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id,
														@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  appService.getGeoListApp(TOKEN,id,offset,search);

	}
	
	@RequestMapping(value = "/getGeofenceByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getGeofenceByIdApp(TOKEN,geofenceId,userId);

	}
	
	@RequestMapping(value = "/deleteGeofenceApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appService.deleteGeofenceApp(TOKEN,geofenceId,userId);


	}
	
	@RequestMapping(value = "/addGeofenceApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Geofence geofence,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		

		return appService.addGeofenceApp(TOKEN,geofence,id);

	}
	
	@RequestMapping(value = "/editGeofenceApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Geofence geofence,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appService.editGeofenceApp(TOKEN,geofence,id);

	}	
	
	@RequestMapping(value = "/getGeofenceSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                    @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getGeofenceSelectApp(TOKEN,userId);

		
	}
	
	@RequestMapping(value = "/getDeviceSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getDeviceSelectApp(TOKEN,userId);

		
	}
	
	@RequestMapping(value = "/updatePhotoApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhotoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestBody Map<String, String> data ,
			                                           @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appService.updateProfilePhotoApp(TOKEN,data,userId);

	}
	
	@RequestMapping(value = "/getProfileInfoApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getUserInfoApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/changePassowrdApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestBody Map<String, String> data ,
			                                              @RequestParam (value = "check", defaultValue = "") String check,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {

		
    	return appService.updateProfilePasswordApp(TOKEN,data,check,userId);

	}
	
	@GetMapping(path ="/getStatusApp")
	public ResponseEntity<?> getStatusApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                           @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return appService.getStatusApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getMergeHoursIgnitionApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getMergeHoursIgnitionApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getMergeHoursIgnitionApp(TOKEN,userId);

	}
	
	@GetMapping(path ="/getDistanceFuelEngineApp")
	public ResponseEntity<?> getDistanceFuelEngineApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return appService.getDistanceFuelEngineApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getNotificationsChartApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationsChartApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getNotificationsChartApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/getNumberDriverWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumberDriverWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getNumberDriverWorkingHoursApp(TOKEN,driverId,groupId, offset,start, end,search,userId);

	}
	
}
