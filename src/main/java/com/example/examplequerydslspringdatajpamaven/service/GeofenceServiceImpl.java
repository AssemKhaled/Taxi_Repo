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

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
public class GeofenceServiceImpl extends RestServiceController implements GeofenceService {
	
	private static final Log logger = LogFactory.getLog(GeofenceServiceImpl.class);

	GetObjectResponse getObjectResponse;

	@Autowired
	GeofenceRepository geofenceRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Override
	public ResponseEntity<?> getAllGeofences(String TOKEN,Long id,int offset,String search) {

		//User user=userRepository.getUserData(id);
		//Set<Geofence> geofences = user.getGeofences();
		logger.info("************************ getAllUserGeofences STARTED ***************************");
		
		List<Geofence> geofences = new ArrayList<Geofence>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					
				    userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							
							 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
							 logger.info("************************ getAllUserDevices ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						 }else {
							 User parentClient = new User() ;
							 for(User object : parentClients) {
								 parentClient = object;
							 }
							 List<Long>usersIds= new ArrayList<>();
							 usersIds.add(parentClient.getId());
							 geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
							 Integer size=geofenceRepository.getAllGeofencesSize(usersIds);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
							logger.info("************************ getAllUserGeofences ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						 }
					 }
				    List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }

					
					
				    geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
					Integer size=geofenceRepository.getAllGeofencesSize(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

	
	}

	@Override
	public ResponseEntity<?> getGeofenceById(String TOKEN,Long geofenceId,Long userId) {
		logger.info("************************ getGeofenceById STARTED ***************************");

		List<Geofence> geofences = new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
       	 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
       }
       User loggedUser = userServiceImpl.findById(userId);
       if(loggedUser == null) {
       	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",geofences);
			return  ResponseEntity.status(404).body(getObjectResponse);
       }
		if(!geofenceId.equals(0)) {
			
			Geofence geofence=geofenceRepository.findOne(geofenceId);

			if(geofence != null) {
				if(geofence.getDelete_date() == null) {
					boolean isParent = false;
					if(loggedUser.getAccountType().equals(4)) {
						Set<User> clientParents = loggedUser.getUsersOfUser();
						if(clientParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : clientParents) {
								parent = object ;
							}
							Set<User>geofneceParents = geofence.getUserGeofence();
							if(geofneceParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User parentObject : geofneceParents) {
									if(parentObject.getId().equals(parent.getId())) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!checkIfParent(geofence , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					geofences.add(geofence);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);
					logger.info("************************ getDriverById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		


	}

	@Override
	public ResponseEntity<?> deleteGeofence(String TOKEN,Long geofenceId,Long userId) {

		logger.info("************************ deleteGeofence STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Geofence> geofences = new ArrayList<Geofence>();
		User user = userServiceImpl.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "GEOFENCE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete",null);
				 logger.info("************************ deleteGeo ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(geofenceId != 0) {
			Geofence geofence= getById(geofenceId);
			if(geofence != null) {
				
				if(geofence.getDelete_date()==null) {
					 boolean isParent = false;
					 if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 User parent = null;
							 for(User object : parentClients) {
								 parent = object;
							 }
							 Set<User>geofneceParent = geofence.getUserGeofence();
							 if(geofneceParent.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User parentObject : geofneceParent) {
									 if(parentObject.getId().equals(parent.getId())) {
										 isParent = true;
										 break;
									 }
								 }
							 }
						 }
					 }
					 if(!checkIfParent(geofence , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece ",geofences);
							logger.info("************************ deleteGeofence ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						geofenceRepository.deleteGeofence(geofenceId,currentDate);
						geofenceRepository.deleteGeofenceId(geofenceId);
						geofenceRepository.deleteGeofenceDeviceId(geofenceId);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",geofences);
						logger.info("************************ deleteGeofence ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					
					
					

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was Deleted before",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was not found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}



	}

	@Override
	public List<Geofence> checkDublicateGeofenceInAdd(Long id, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInAdd(id, name);
		
	}
	
	@Override
	public ResponseEntity<?> addGeofence(String TOKEN,Geofence geofence,Long id) {
		logger.info("************************ addGeofence STARTED ***************************");

		List<Geofence> geofences= new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date()==null) {
					if(geofence.getName()== null || geofence.getType()== null
							   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
							   || geofence.getArea() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);
						return ResponseEntity.badRequest().body(getObjectResponse);

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
							return ResponseEntity.ok().body(getObjectResponse);

						}
						else {
							Set<User> userDriver = new HashSet<>();
							if(geofence.getId()==null || geofence.getId()==0) {
								boolean isParent = false;
								 if(user.getAccountType().equals(4)) {
									 Set<User> parentClients = user.getUsersOfUser();
									 if(parentClients.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									 }else {
										 User parent = null;
										 for(User object : parentClients) {
											 parent = object;
										 }
										userDriver.add(parent);


									 }
								 }
								 else {
									userDriver.add(user);

								 }
								
								
								geofence.setUserGeofence(userDriver);
								geofenceRepository.save(geofence);
								geofences.add(geofence);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",geofences);
								logger.info("************************ addGeofence ENDED ***************************");

								return ResponseEntity.ok().body(getObjectResponse);

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update Geofence ID",geofences);
								return ResponseEntity.badRequest().body(getObjectResponse);

							}
						}
					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}
           			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}	
		
		
	}
	
	@Override
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId, Long userId, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInEdit(geofenceId,userId,name);
		
	}

	@Override
	public ResponseEntity<?> editGeofence(String TOKEN,Geofence geofence,Long id) {

		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		List<Geofence> geofences = new ArrayList<Geofence>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				 if(user.getDelete_date()==null) {
					 if(geofence.getId() != null) {
						 Geofence geofneceCheck = getById(geofence.getId());
						if(geofneceCheck != null) {
							if(geofneceCheck.getDelete_date() == null) {
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>geofenceParent = geofneceCheck.getUserGeofence();
									if(geofenceParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : geofenceParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!checkIfParent(geofneceCheck , user) && ! isParent) {

									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this geofence ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(geofence.getName()== null || geofence.getType()== null
										   || geofence.getArea() == null || geofence.getName()== "" || geofence.getType()== ""
										   || geofence.getArea() == "") {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",geofences);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									List<Geofence> checkDublicateInEdit=checkDublicateGeofenceInEdit(geofence.getId(),id,geofence.getName());
								    List<Integer> duplictionList =new ArrayList<Integer>();
									if(!checkDublicateInEdit.isEmpty()) {
				    					for(int i=0;i<checkDublicateInEdit.size();i++) {
				    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(geofence.getName())) {
												duplictionList.add(1);						
			
				    						}
				    						
				    						
				    					}
								    	getObjectResponse = new GetObjectResponse( 401, "This Geofence was found before",duplictionList);
										return ResponseEntity.ok().body(getObjectResponse);

				    				}
				    				else {
				    					

				    					Set<User> userCreater=new HashSet<>();
				    					userCreater = geofneceCheck.getUserGeofence();
										geofence.setUserGeofence(userCreater);
										
										geofenceRepository.save(geofence);
										geofences.add(geofence);
										getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",geofences);
										logger.info("************************ editGeofence ENDED ***************************");
										return ResponseEntity.ok().body(getObjectResponse);

										
										
				    					
				    				}	
								}
								

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
								return ResponseEntity.status(404).body(getObjectResponse);

							}

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",geofences);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Geofence ID is Required",geofences);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",geofences);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
		

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

	@Override
	public ResponseEntity<?> getAllGeo(String TOKEN, Long id) {
logger.info("************************ getAllUserGeofences STARTED ***************************");
		
		List<Geofence> geofences = new ArrayList<Geofence>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to list",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					geofences = geofenceRepository.getAllGeos(id);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}
	
	 public Boolean checkIfParent(Geofence geofnece , User loggedUser) {

		   Set<User> geofenceParent = geofnece.getUserGeofence();

		   if(geofenceParent.isEmpty()) {

			   return false;
		   }else {
			   User parent = null;
			   for (User object : geofenceParent) {
				   parent = object;
			   }
			   if(parent.getId() == loggedUser.getId()) {

				   return true;
			   }
			   if(parent.getAccountType() == 1) {
				   if(parent.getId() == loggedUser.getId()) {

					   return true;
				   }
			   }else {
				   List<User> parents = userServiceImpl.getAllParentsOfuser(parent, parent.getAccountType());
				   if(parents.isEmpty()) {

					   return false;
				   }else {
					   for(User object :parents) {
						   if(object.getId() == loggedUser.getId()) {

							   return true;
						   }
					   }
				   }
			   }
			  
		   }

		   return false;
	   }

	@Override
	public ResponseEntity<?> getGeofenceSelect(String TOKEN, Long userId) {
		logger.info("************************ getGeofenceSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 Set<User>parentClient = user.getUsersOfUser();
	   					if(parentClient.isEmpty()) {
	   						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
	   						logger.info("************************ getGeofenceSelect ENDED ***************************");
	   						return ResponseEntity.badRequest().body(getObjectResponse);
	   					}else {
	   					  
	   						User parent =null;
	   						for(User object : parentClient) {
	   							parent = object;
	   						}
	   						if(parent != null) {

					   			List<Long>usersIds= new ArrayList<>();
			   					usersIds.add(parent.getId());
	   							drivers = geofenceRepository.getGeofenceSelect(usersIds);
	   							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	   							logger.info("************************ getGeofenceSelect ENDED ***************************");
	   							return ResponseEntity.ok().body(getObjectResponse);
	   						}
	   						else {
	   							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
	   							return ResponseEntity.badRequest().body(getObjectResponse);
	   						}
	   						
	   					}
	   			 }
	    			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
		   			 List<Long>usersIds= new ArrayList<>();
		   			 if(childernUsers.isEmpty()) {
		   				 usersIds.add(userId);
		   			 }
		   			 else {
		   				 usersIds.add(userId);
		   				 for(User object : childernUsers) {
		   					 usersIds.add(object.getId());
		   				 }
		   			 }
	    			
	    			drivers = geofenceRepository.getGeofenceSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getGeofenceSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	}

}
