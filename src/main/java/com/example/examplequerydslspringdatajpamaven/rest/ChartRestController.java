package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.examplequerydslspringdatajpamaven.service.ChartServiceImpl;

/**
 * Services related to charts component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/charts")
public class ChartRestController {

	
	
	@Autowired
	ChartServiceImpl chartServiceImpl;

	
	
	@GetMapping(path ="/getStatus")
	public ResponseEntity<?> getStatus(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return chartServiceImpl.getStatus(TOKEN,userId);
	}
	
	
	@GetMapping(path ="/getDistanceFuelEngine")
	public ResponseEntity<?> getDistanceFuelEngine(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return chartServiceImpl.getDistanceFuelEngine(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getNotificationsChart", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationsChart(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  chartServiceImpl.getNotificationsChart(TOKEN,userId);

	}
	@RequestMapping(value = "/getMergeHoursIgnition", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getMergeHoursIgnition(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  chartServiceImpl.getMergeHoursIgnition(TOKEN,userId);

	}
	
	

}
