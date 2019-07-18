package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class GeofenceServiceImpl implements GeofenceService {
	
	private static final Log logger = LogFactory.getLog(GeofenceServiceImpl.class);

	GetObjectResponse getObjectResponse;

	@Autowired
	GeofenceRepository geofenceRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Override
	public ResponseEntity<?> getAllGeofences(Long id,int offset,String search) {

		//User user=userRepository.getUserData(id);
		//Set<Geofence> geofences = user.getGeofences();
		logger.info("************************ getAllUserGeofences STARTED ***************************");
		
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
			}
			else {
				if(user.getDelete_date() == null) {
					geofences = geofenceRepository.getAllGeofences(id,offset,search);
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

		
		logger.info("************************ getAllUserGeofences ENDED ***************************");

		return  ResponseEntity.ok(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getGeofenceById(Long geofenceId) {
		logger.info("************************ getGeofenceById STARTED ***************************");

		List<Geofence> geofences = new ArrayList<Geofence>();

		if(geofenceId != 0) {
			
			Geofence geofence=geofenceRepository.findOne(geofenceId);

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
		
		logger.info("************************ getGeofenceById ENDED ***************************");

		return  ResponseEntity.ok(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> deleteGeofence(Long geofenceId) {

		logger.info("************************ deleteGeofence STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(geofenceId != 0) {
			Geofence geofence= getById(geofenceId);
			if(geofence != null) {
				
				if(geofence.getDelete_date()==null) {
					
					geofenceRepository.deleteGeofence(geofenceId,currentDate);
					geofenceRepository.deleteGeofenceId(geofenceId);
					geofenceRepository.deleteGeofenceDeviceId(geofenceId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",geofences);

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was Deleted before",geofences);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was not found",geofences);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);

		}

		logger.info("************************ deleteGeofence ENDED ***************************");
		return  ResponseEntity.ok(getObjectResponse);


	}

	@Override
	public List<Geofence> checkDublicateGeofenceInAdd(Long id, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInAdd(id, name);
		
	}
	
	@Override
	public ResponseEntity<?> addGeofence(Geofence geofence,Long id) {
		logger.info("************************ addGeofence STARTED ***************************");

		List<Geofence> geofences= new ArrayList<Geofence>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
			}
			else {
				if(user.getDelete_date()==null) {
					if(geofence.getName()== null || geofence.getType()== null
							   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
							   || geofence.getArea() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);

					}
					else {
						List<Geofence> geofenceCheck=checkDublicateGeofenceInAdd(id,geofence.getName());
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
							if(geofence.getId()==null) {
								Set<User> userDriver = new HashSet<>();
								userDriver.add(user);
								geofence.setUserGeofence(userDriver);
								geofenceRepository.save(geofence);
								geofences.add(geofence);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",geofences);


							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update Geofence ID",geofences);

							}
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
		

		logger.info("************************ addGeofence ENDED ***************************");

		return ResponseEntity.ok(getObjectResponse);
	
		
		
	}
	
	@Override
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId, Long userId, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInEdit(geofenceId,userId,name);
		
	}

	@Override
	public ResponseEntity<?> editGeofence(Geofence geofence,Long id) {

		logger.info("************************ editGeofence STARTED ***************************");

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
						 Geofence geofneceCheck = getById(geofence.getId());
						if(geofneceCheck != null) {
							if(geofneceCheck.getDelete_date() == null) {
								if(geofence.getName()== null || geofence.getType()== null
										   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
										   || geofence.getArea() == "") {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);

								}
								else {
									List<Geofence> checkDublicateInEdit=checkDublicateGeofenceInEdit(geofence.getId(),id,geofence.getName());
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
											geofenceRepository.save(geofence);
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
		
		logger.info("************************ editGeofence ENDED ***************************");

		return ResponseEntity.ok(getObjectResponse);


	}

	@Override
	public Set<Geofence> getMultipleGeofencesById(Long [] ids) {
		// TODO Auto-generated method stub
		Set<Geofence> geofences = new HashSet<>();
		List<Geofence> geos = geofenceRepository.getMultipleGeofencesById(ids);
		for( Geofence geo : geos) {
			geofences.add(geo);
		}
		
		return geofences;
	}

	@Override
	public Geofence getById(Long geofenceId) {
		
		Geofence geofence = geofenceRepository.findOne(geofenceId);
		if(geofence == null) {
			return null;
		}
		else
		{
			return geofence;
		}
	}
	

}
