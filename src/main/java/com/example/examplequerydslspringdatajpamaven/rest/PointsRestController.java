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
import com.example.examplequerydslspringdatajpamaven.entity.Points;
import com.example.examplequerydslspringdatajpamaven.service.PointsServiceImpl;

/**
 * service of point component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/points")
public class PointsRestController {

	
	@Autowired
	PointsServiceImpl pointsServiceImpl;

	@GetMapping("/getPointsList")
	public ResponseEntity<?> getPointsList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
								            @RequestParam (value = "exportData", defaultValue = "") String exportData,	                           
											@RequestParam (value = "userId",defaultValue = "0") Long userId,
										   @RequestParam(value = "offset", defaultValue = "0") int offset,
								           @RequestParam(value = "search", defaultValue = "") String search) {
	 
		return pointsServiceImpl.getPointsList(TOKEN,userId,offset,search,exportData);
		
	}
	
	@GetMapping("/getPointsMap")
	public ResponseEntity<?> getPointsMap(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
				                           @RequestParam (value = "userId",defaultValue = "0") Long userId) {
	 
		return pointsServiceImpl.getPointsMap(TOKEN,userId);
		
	}
	
	
	@RequestMapping(value = "/getPointsById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getPointsById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "PointId", defaultValue = "0") Long PointId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  pointsServiceImpl.getPointsById(TOKEN,PointId,userId);

	}
	
	@RequestMapping(value = "/deletePoints", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deletePoints(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "PointId", defaultValue = "0") Long PointId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  pointsServiceImpl.deletePoints(TOKEN,PointId,userId);


	}
	
	@PostMapping(path ="/createPoints")
	public ResponseEntity<?> createPoints(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Points points) {
		
			 return pointsServiceImpl.createPoints(TOKEN,points,userId);				
	}
	
	@RequestMapping(value = "/editPoints", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editPoints(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Points points,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		return pointsServiceImpl.editPoints(TOKEN,points,id);

	}
	
	@RequestMapping(value = "/getPointSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getPointSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                          @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  pointsServiceImpl.getPointSelect(TOKEN,loggedUserId,userId);

	}
	
	@RequestMapping(value = "/getPointUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getPointUnSelectOfClient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																	@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                             
																	@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  pointsServiceImpl.getPointUnSelectOfClient(TOKEN,loggedUserId,userId);

	}
	
	@GetMapping("/assignClientPoints")
	public ResponseEntity<?> assignClientPoints(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "pointIds", defaultValue = "0") Long [] pointIds) {
		return pointsServiceImpl.assignClientPoints(TOKEN,loggedUserId,userId,pointIds);
	}
	
	@GetMapping("/getClientPoints")
	public ResponseEntity<?> getClientPoints(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return pointsServiceImpl.getClientPoints(TOKEN,loggedUserId,userId);
	}
}
