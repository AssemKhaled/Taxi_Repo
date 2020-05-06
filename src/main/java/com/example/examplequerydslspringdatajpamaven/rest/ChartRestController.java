package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.examplequerydslspringdatajpamaven.service.ChartServiceImpl;

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
	
	@GetMapping(path ="/getIgnitionMotion")
	public ResponseEntity<?> getIgnitionMotion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return chartServiceImpl.getIgnitionMotion(TOKEN,userId);
	}
	
	@GetMapping(path ="/getDriverHours")
	public ResponseEntity<?> getDriverHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return chartServiceImpl.getDriverHours(TOKEN,userId);
	}
	
	@GetMapping(path ="/getDistanceFuelEngine")
	public ResponseEntity<?> getDistanceFuelEngine(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return chartServiceImpl.getDistanceFuelEngine(TOKEN,userId);
	}
	
	

}
