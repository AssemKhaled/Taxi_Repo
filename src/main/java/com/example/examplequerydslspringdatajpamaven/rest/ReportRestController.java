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
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;


@RestController
@RequestMapping(path = "/reports")
@CrossOrigin
public class ReportRestController {
	
	@Autowired
	ReportServiceImpl reportServiceImpl;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	

	@RequestMapping(value = "/getEventsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search) {
		
		
		
    	return  reportServiceImpl.getEventsReport(TOKEN,deviceId, offset, start, end,search);

	}
	
	@RequestMapping(value = "/getStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStops(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit) {
		

		
    	return reportServiceImpl.getStopsReport(TOKEN,deviceId, type, from, to, page, start, limit);
		
		
	}
	
	
	@RequestMapping(value = "/getTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTrips(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit) {
												

    	return  reportServiceImpl.getTripsReport(TOKEN,deviceId, type, from, to, page, start, limit);
		 
		
	}
	

}
