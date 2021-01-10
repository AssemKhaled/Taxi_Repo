package com.example.examplequerydslspringdatajpamaven.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;

/**
 * Service of reports component
 * @author fuinco
 *
 */
@RestController
@RequestMapping(path = "/reports")
@CrossOrigin
public class ReportRestController {
	
	@Autowired
	private ReportServiceImpl reportServiceImpl;

	@RequestMapping(value = "/getEventsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "type", defaultValue = "") String type,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  reportServiceImpl.getEventsReport(TOKEN, deviceId,groupId, offset, start, end, type, search, userId ,exportData);

	}
	

	@RequestMapping(value = "/getDeviceWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getDeviceWorkingHours(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	
	
	@RequestMapping(value = "/getCustomReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCustomReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId,
													 @RequestParam (value = "custom", defaultValue = "") String custom,
													 @RequestParam (value = "value", defaultValue = "") String value) {
		
		
		
    	return  reportServiceImpl.getCustomReport(TOKEN,deviceId,groupId, offset, start, end,search,userId,custom,value,exportData);

	}
	

	@RequestMapping(value = "/getDriverWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getDriverWorkingHours(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}

	@RequestMapping(value = "/getStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStops(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return reportServiceImpl.getStopsReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	
	@RequestMapping(value = "/getTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTrips(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  reportServiceImpl.getTripsReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
	}
	

	@RequestMapping(value = "/getDriveMoreThanReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriveMoreThanReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.getDriveMoreThanReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	
	@RequestMapping(value = "/getEventsReportByType", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsType(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  reportServiceImpl.getEventsReportByType(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	@RequestMapping(value = "/getSummaryReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSummary(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  reportServiceImpl.getSummaryReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getSensorsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSensors(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getSensorsReport(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	

	@RequestMapping(value = "/getNumTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumTripsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.getNumTripsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getNumStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumStopsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
		

		
    	return reportServiceImpl.getNumStopsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	
	@RequestMapping(value = "/geTotalTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> geTotalTripsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.geTotalTripsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	@RequestMapping(value = "/getTotalStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTotalStopsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
		

		
    	return reportServiceImpl.getTotalStopsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	

	@RequestMapping(value = "/getNumberDriverWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumberDriverWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getNumberDriverWorkingHours(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}
	
	@RequestMapping(value = "/viewTrip", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> viewTrip(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													 @RequestParam (value = "startTime", defaultValue = "0") String startTime,
													 @RequestParam (value = "endTime", defaultValue = "0") String endTime) {	
    	return  reportServiceImpl.getviewTrip(TOKEN, deviceId,startTime, endTime);

	}


}
