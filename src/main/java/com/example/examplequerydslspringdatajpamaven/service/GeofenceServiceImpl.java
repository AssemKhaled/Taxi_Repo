package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientGeofence;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to geofences
 * @author fuinco
 *
 */
@Component
@Service
public class GeofenceServiceImpl extends RestServiceController implements GeofenceService {
	
	private static final Log logger = LogFactory.getLog(GeofenceServiceImpl.class);

	GetObjectResponse getObjectResponse;

	@Autowired
	GeofenceRepository geofenceRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserClientGeofenceRepository userClientGeofenceRepository;	
	
	/**
	 * get list of geofences with limit 10
	 */
	@Override
	public ResponseEntity<?> getAllGeofences(String TOKEN,Long id,int offset,String search,String exportData) {


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
						 

				    	
				    	List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(id);
				    	Integer size = 0;
						 if(geofenceIds.size()>0) {

							 if(exportData.equals("exportData")) {
								 geofences = geofenceRepository.getAllGeofencesByIdsExport(geofenceIds,search);
								
							 }
							 else {
								 geofences = geofenceRepository.getAllGeofencesByIds(geofenceIds,offset,search);
								 size=geofenceRepository.getAllGeofencesSizeByIds(geofenceIds,search);
							 }

						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
						logger.info("************************ getAllUserGeofences ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
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

					Integer size = 0; 
					if(exportData.equals("exportData")) {
					    geofences = geofenceRepository.getAllGeofencesExport(usersIds,search);

					}
					else {

					    geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
						size=geofenceRepository.getAllGeofencesSize(usersIds,search);
					}
					
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

	/**
	 * get geofence by id
	 * 
	 */
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
						List<Long> geofencesData = userClientGeofenceRepository.getGeofence(userId,geofenceId);
						if(geofencesData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
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

	/**
	 * delete geofence by id
	 */
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
						List<Long> geofencesData = userClientGeofenceRepository.getGeofence(userId,geofenceId);
						if(geofencesData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					 }
					 if(!checkIfParent(geofence , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece ",geofences);
							logger.info("************************ deleteGeofence ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						geofenceRepository.deleteGeofence(geofenceId,currentDate);
						geofenceRepository.deleteGeofenceDeviceId(geofenceId);
						geofenceRepository.deleteGeofenceGroupId(geofenceId);

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
	
	
	/**
	 * add geofence by data in body 
	 */
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
						Set<User> userDriver = new HashSet<>();
						 User parent = null;
						 if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this geofnece",geofences);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User object : parentClients) {
									 parent = object;
								 }
								userDriver.add(parent);


							 }
						 }
						 else {
							userDriver.add(user);
							parent = user;

						 }
						 
						List<Geofence> geofenceCheck=checkDublicateGeofenceInAdd(parent.getId(),geofence.getName());
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
							if(geofence.getId()==null || geofence.getId()==0) {
								 
															
								geofence.setUserGeofence(userDriver);
								geofenceRepository.save(geofence);
								geofences = null;
								
								if(user.getAccountType().equals(4)) {
						    		userClientGeofence saveData = new userClientGeofence();
							        
							        Long GeoId = geofenceRepository.getGeofenceIdByName(parent.getId(),geofence.getName());
						    		if(GeoId != null) {
							    		saveData.setUserid(id);
							    		saveData.setGeofenceid(GeoId);
								        userClientGeofenceRepository.save(saveData);
						    		}
						    		
						    	}
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

	/**
	 * edit geoefnece by id in body mandatory
	 */
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
								User parent = null;
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit geofnece",geofences);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
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
									List<Long> geofencesData = userClientGeofenceRepository.getGeofence(id,geofence.getId());
									if(geofencesData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								}
								else {
									parent = user;
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
									List<Geofence> checkDublicateInEdit=checkDublicateGeofenceInEdit(geofence.getId(),parent.getId(),geofence.getName());
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
										
				    					Set<Device> device=new HashSet<>();
				    					device = geofneceCheck.getDevice();
										geofence.setDevice(device);

				    					Set<Group> groups=new HashSet<>();
				    					groups = geofneceCheck.getGroups();
										geofence.setGroups(groups);

										geofenceRepository.save(geofence);
										geofences = null;
										
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

	/**
	 * get all geo list
	 */
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

	 /**
	  * get geofence select list
	  */
	@Override
	public ResponseEntity<?> getGeofenceSelect(String TOKEN,Long loggedUserId, Long userId,Long deviceId, Long groupId) {
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
		
		List<Geofence> deviceGeofences = new ArrayList<Geofence>();
		List<DeviceSelect> groups = new ArrayList<DeviceSelect>();
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();

		if(deviceId != 0) {
			Device device = deviceServiceImpl.findById(deviceId);
			if(device != null) {
				
				Set<Geofence> geofences=new HashSet<>() ;
				geofences = device.getGeofence();
				if(!geofences.isEmpty()) {
					for(Geofence geofence : geofences ) {

						deviceGeofences.add(geofence);
					}
				}


			}
			

		}
		if(groupId != 0) {
			groups = groupRepository.getGroupGeofencesSelect(groupId);

		}
		obj.put("selectedDevices", deviceGeofences);
		obj.put("selectedGroups", groups);
		
		if(loggedUserId != 0) {
	    	User loggedUser = userServiceImpl.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(loggedUserId);

						if(geofenceIds.size()>0) {

							drivers = geofenceRepository.getGeofenceSelectByIds(geofenceIds);

						 }
						obj.put("geofences", drivers);
						data.add(obj);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getGeofenceSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
		
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				

	    				List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(userId);

						if(geofenceIds.size()>0) {

							drivers = geofenceRepository.getGeofenceSelectByIds(geofenceIds);

						 }
						
						obj.put("geofences", drivers);
						data.add(obj);
						
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getGeofenceSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
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
	    			
					
					obj.put("geofences", drivers);
					data.add(obj);
	    			
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
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
	
	/**
	 * get unassigned geofence to any user type 4
	 */
	@Override
	public ResponseEntity<?> getGeofenceUnSelectOfClient(String TOKEN,Long loggedUserId, Long userId) {
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
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}

		
		drivers = geofenceRepository.getGeofenceUnSelectOfClient(loggedUserId,userId);
		
		List<DriverSelect> geofences = userClientGeofenceRepository.getGeofencesOfUserList(userId);

		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedGeofences", geofences);
	    obj.put("geofences", drivers);

	    data.add(obj);
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getGeofenceSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	    

	
	}
	
	

	/**
	 * assign geofences to user type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientGeofences(String TOKEN, Long loggedUserId, Long userId, Long[] geofenceIds) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		
        if(geofenceIds.length > 0 && geofenceIds[0] != 0) {
			
        	List<userClientGeofence> checkData = userClientGeofenceRepository.getGeofeneceByGeoIds(geofenceIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is geofenece assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
			for(Long id:geofenceIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Geofence assignedGeofence = getById(id);
				if(assignedGeofence == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Geofence is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientGeofenceRepository.deleteGeofencesByUserId(userId);
			for(Long assignedId:geofenceIds) {
				userClientGeofence userGeofnece= new userClientGeofence();
				userGeofnece.setUserid(userId);
				userGeofnece.setGeofenceid(assignedId);
				userClientGeofenceRepository.save(userGeofnece);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientGeofence> geofences = userClientGeofenceRepository.getGeofnecesOfUser(userId);
			
			if(geofences.size() > 0) {

				userClientGeofenceRepository.deleteGeofencesByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no geofences for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get geofneces assigned to type 4
	 */
	@Override
	public ResponseEntity<?> getClientGeofences(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}

		List<DriverSelect> geofences = userClientGeofenceRepository.getGeofencesOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

}
