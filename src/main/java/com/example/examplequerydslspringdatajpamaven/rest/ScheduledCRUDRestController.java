package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.examplequerydslspringdatajpamaven.entity.Schedule;
import com.example.examplequerydslspringdatajpamaven.service.ScheduledServiceImpl;

/**
 * Service of schedule component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/scheduled")
public class ScheduledCRUDRestController {

	@Autowired
	private ScheduledServiceImpl scheduledServiceImpl;
	

	@PostMapping(path ="/createScheduled")
	public ResponseEntity<?> createScheduled(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Schedule schedule) {
		
			 return scheduledServiceImpl.createScheduled(TOKEN,schedule,userId);				
	}

	@GetMapping("/getScheduledList")
	public ResponseEntity<?> getScheduledList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
								              @RequestParam (value = "exportData", defaultValue = "") String exportData,                             
											  @RequestParam (value = "userId",defaultValue = "0") Long userId,
											  @RequestParam(value = "offset", defaultValue = "0") int offset,
								              @RequestParam(value = "search", defaultValue = "") String search) {
	  
		return scheduledServiceImpl.getScheduledList(TOKEN,userId,offset,search,exportData);
		
	}
	
	@RequestMapping(value = "/getScheduledById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getScheduledById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "scheduledId", defaultValue = "0") Long scheduledId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  scheduledServiceImpl.getScheduledById(TOKEN,scheduledId,userId);

	}
	
	@RequestMapping(value = "/deleteScheduled", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteScheduled(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "scheduledId", defaultValue = "0") Long scheduledId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  scheduledServiceImpl.deleteScheduled(TOKEN,scheduledId,userId);


	}
	
	@RequestMapping(value = "/editScheduled", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editScheduled(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Schedule schedule,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return scheduledServiceImpl.editScheduled(TOKEN,schedule,id);

	}
	
	@RequestMapping(value = "/getScheduledSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getScheduledSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  scheduledServiceImpl.getScheduledSelect(TOKEN,userId);

		
	}
	
}
