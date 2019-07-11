package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


@RestController
@RequestMapping(path = "/geofences")
@CrossOrigin
public class GeofenceRestController {
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@RequestMapping(value = "/getAllGeofences", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofences(@RequestParam (value = "userId", defaultValue = "0") Long id,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "search", defaultValue = "") String search) {
		offset=offset-1;
		if(offset <0) {
			offset=0;
		}
		GetObjectResponse getObjectResponse ;
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
			}
			else {
				if(user.getDelete_date() == null) {
					geofences =geofenceServiceImpl.getAllGeofences(id,offset,search);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
		
		}
		
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/getGeofenceById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceById(@RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId) {
		
		GetObjectResponse getObjectResponse;
		List<Geofence> geofences = new ArrayList<Geofence>();

		if(geofenceId != 0) {
			
			Geofence geofence=geofenceServiceImpl.getGeofenceById(geofenceId);
			if(geofence != null) {
				if(geofence.getDelete_date() == null) {
					
					geofences.add(geofence);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);

		}
		return ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/deleteGeofence", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId) {
		
		GetObjectResponse getObjectResponse;
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(geofenceId != 0) {
			Geofence geofence= geofenceServiceImpl.getGeofenceById(geofenceId);
			if(geofence != null) {
				
				if(geofence.getDelete_date()==null) {
					geofenceServiceImpl.deleteGeofence(geofenceId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",geofences);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence was Deleted Before",geofences);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);

		}
		return ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/addGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody(required = false) Geofence geofence,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		GetObjectResponse getObjectResponse;
		List<Geofence> geofences= new ArrayList<Geofence>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
			}
			else {
				if(user.getDelete_date()==null) {
					if(geofence.getName()== null || geofence.getType()== null
							   || geofence.getArea() == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);

					}
					else {
						List<Geofence> geofenceCheck=geofenceServiceImpl.checkDublicateGeofenceInAdd(id,geofence.getName());
					    List<Integer> duplictionList =new ArrayList<Integer>();
						if(!geofenceCheck.isEmpty()) {
							for(int i=0;i<geofenceCheck.size();i++) {
								if(geofenceCheck.get(i).getName().equalsIgnoreCase(geofence.getName())) {
									duplictionList.add(1);						
								}
							}
					    	getObjectResponse = new GetObjectResponse( 401, "This Geofence was found before",duplictionList);

						}
						else {
							Set<User> userDriver = new HashSet<>();
							userDriver.add(user);
							geofence.setUserGeofence(userDriver);
							String resut=geofenceServiceImpl.addGeofence(geofence);
							geofences.add(geofence);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resut,geofences);


						}
					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
				}

			}
           
			
		
			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);

			
		}
		return ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/editGeofence", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGeofence(@RequestBody(required = false) Geofence geofence,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		GetObjectResponse getObjectResponse;
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
			}
			else {
				 if(user.getDelete_date()==null) {
					 if(geofence.getId() != null) {
						 Geofence geofneceCheck = geofenceServiceImpl.getGeofenceById(geofence.getId());
						if(geofneceCheck != null) {
							if(geofneceCheck.getDelete_date() == null) {
								if(geofence.getName()== null || geofence.getType()== null
										   || geofence.getArea() == null) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);

								}
								else {
									List<Geofence> checkDublicateInEdit=geofenceServiceImpl.checkDublicateGeofenceInEdit(geofence.getId(),id,geofence.getName());
								    List<Integer> duplictionList =new ArrayList<Integer>();
									if(!checkDublicateInEdit.isEmpty()) {
				    					for(int i=0;i<checkDublicateInEdit.size();i++) {
				    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(geofence.getName())) {
				    														
				    						}
				    						
				    						
				    					}
								    	getObjectResponse = new GetObjectResponse( 401, "This Geofence was found before",duplictionList);
				    					
				    				}
				    				else {
				    					Set<User> userDriver = new HashSet<>();
										userDriver.add(user);
										geofence.setUserGeofence(userDriver);
										if(geofneceCheck.getUserGeofence().equals(geofence.getUserGeofence())) {
											geofenceServiceImpl.editGeofence(geofence);
											geofences.add(geofence);
											getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",geofences);
				
										}
										else {
											getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this geofence it belongs to another user",geofences);

										}
				    					
				    				}	
								}
								

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);

							}

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Geofence ID is Required",geofences);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);

				 }
				
			}
		   
    			
    				
    				
    			

			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);

			
		}
		return ResponseEntity.ok(getObjectResponse);

	}	

}
