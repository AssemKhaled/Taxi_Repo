package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;


@Component
@Service
public class GroupsServiceImpl extends RestServiceController implements GroupsService{

	private static final Log logger = LogFactory.getLog(GroupsServiceImpl.class);

	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private DriverServiceImpl driverService;
	
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@Autowired
	private GeofenceRepository geofenceRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired 
	GroupRepository groupRepository;
	
	@Override
	public ResponseEntity<?> createGroup(String TOKEN, Group group,Long userId) {
		
		logger.info("************************ createGroups STARTED ***************************");

		if(TOKEN.equals("")) {
			 List<Group> groups = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create group",null);
				 logger.info("************************ createDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if( (group.getId() != null && group.getId() != 0) ) {
            List<Group> groups = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "group Id not allowed in create new group",groups);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if( group.getName().equals(null) || group.getName().equals("") ||
				group.getType().equals(null) || group.getType().equals("")) {
			
			List<Group> groups = null;
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "name and type is required",groups);
			logger.info("************************ createDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		else
		{
			
			if( group.getType().equals("driver") || group.getType().equals("device")
					|| group.getType().equals("geofence") || group.getType().equals("attribute")
					|| group.getType().equals("command") || group.getType().equals("maintenance")
					|| group.getType().equals("notification") ) {
				
				List<Group> groupCheck=groupRepository.checkDublicateGroupInAdd(userId,group.getName());
			    List<Integer> duplictionList =new ArrayList<Integer>();
				if(!groupCheck.isEmpty()) {
					for(int i=0;i<groupCheck.size();i++) {
						if(groupCheck.get(i).getName().equalsIgnoreCase(group.getName())) {
							duplictionList.add(1);						
						}
					}
			    	getObjectResponse = new GetObjectResponse( 401, "This group was found before",duplictionList);
					return ResponseEntity.ok().body(getObjectResponse);

				}
				
				
						
			
				Set<User> user=new HashSet<>() ;
				User userCreater ;
				userCreater=userService.findById(userId);
				if(userCreater.equals(null))
				{
	
					getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "Assigning to not found user",null);
					logger.info("************************ createDevice ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					User parent = null;
					if(userCreater.getAccountType().equals(4)) {
						Set<User>parentClient = userCreater.getUsersOfUser();
						if(parentClient.isEmpty()) {
							getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this user cannot add user",null);
							logger.info("************************ createDevice ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
						}else {
						
						 for(User object : parentClient) {
							 parent = object ;
						 }
						 
						}
					}else {
						parent = userCreater;
					}
					
					user.add(parent);	
			        group.setUserGroup(user);
				  	
			    	groupRepository.save(group);
			    	List<Group> groups = null;
			    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",groups);
					logger.info("************************ createDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				}
			
			}
			else {
				List<Group> groups = null;
				
				getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Type should be from (driver,device"
						+ ",geofence,attribute,command,maintenance,notification)",groups);
				logger.info("************************ createDevice ENDED ***************************");
				return ResponseEntity.badRequest().body(getObjectResponse);
		        
			}	
		}
	}

	@Override
	public ResponseEntity<?> getAllGroups(String TOKEN, Long id, int offset, String search) {
		logger.info("************************ getAllUserGroups STARTED ***************************");
		
		List<Group> groups = new ArrayList<Group>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GROUP", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					
					userService.resetChildernArray();
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
							 groups = groupRepository.getAllGroups(usersIds,offset,search);
							 Integer size=groupRepository.getAllGroupsSize(usersIds);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",groups,size);
							logger.info("************************ getAllUserGeofences ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						 }
					 }
				    List<User>childernUsers = userService.getActiveAndInactiveChildern(id);
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

					
					
				    groups = groupRepository.getAllGroups(usersIds,offset,search);
					Integer size=groupRepository.getAllGroupsSize(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",groups,size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",groups);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

	}

	@Override
	public ResponseEntity<?> getGroupById(String TOKEN, Long groupId, Long userId) {
		
		logger.info("************************ getgroupById STARTED ***************************");

		List<Group> groups = new ArrayList<Group>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
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
       User loggedUser = userService.findById(userId);
       if(loggedUser.equals(null)) {
       	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",groups);
			return  ResponseEntity.status(404).body(getObjectResponse);
       }
		if(!groupId.equals(0)) {
			
			Group group=groupRepository.findOne(groupId);

			if(group != null) {
				if(group.getIs_deleted() == null) {
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
							Set<User>groupParents = group.getUserGroup();
							if(groupParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User parentObject : groupParents) {
									if(parentObject.getId().equals(parent.getId())) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!checkIfParent(group , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver ",null);
						logger.info("************************ getgroupById ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					groups.add(group);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",groups);
					logger.info("************************ getgroupById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group ID is not Found",groups);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group ID is not Found",groups);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",groups);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}
	public Boolean checkIfParent(Group group , User loggedUser) {
		   Set<User> groupParent = group.getUserGroup();
		   if(groupParent.isEmpty()) {
			  
			   return false;
		   }else {
			   User parent = null;
			   for (User object : groupParent) {
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
				   List<User> parents = userService.getAllParentsOfuser(parent, parent.getAccountType());
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
	public ResponseEntity<?> editGroup(String TOKEN, Group group, Long id) {
		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		List<Group> groups = new ArrayList<Group>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",groups);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GROUP", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				 if(user.getDelete_date()==null) {
					 if(group.getId() != null) {
						Group groupCheck = groupRepository.findOne(group.getId());
						

						if(groupCheck != null) {
							if(groupCheck.getIs_deleted() == null) {
								
								if(group.getType() != groupCheck.getType()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "not allow to edit type of group",null);
									logger.info("************************ deleteGeo ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								List<Group> checkDublicateInEdit= groupRepository.checkDublicateGroupInEdit(group.getId(),id,group.getName());
							    List<Integer> duplictionList =new ArrayList<Integer>();
								if(!checkDublicateInEdit.isEmpty()) {
			    					for(int i=0;i<checkDublicateInEdit.size();i++) {
			    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(group.getName())) {
											duplictionList.add(1);						
		
			    						}
			    						
			    						
			    					}
							    	getObjectResponse = new GetObjectResponse( 401, "This group was found before",duplictionList);
									return ResponseEntity.ok().body(getObjectResponse);

			    				}
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit group",groups);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>groupParent = groupCheck.getUserGroup();
									if(groupParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit group",groups);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : groupParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!checkIfParent(groupCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this group ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(group.getName()== null ||  group.getName()== "" ) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence name , type and area is Required",groups);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									

			    					Set<User> userDriver = new HashSet<>();
									 if(user.getAccountType().equals(4)) {
										 Set<User> parentClients = user.getUsersOfUser();
										 if(parentClients.isEmpty()) {
											 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this group",groups);
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
			    					
			    					
									group.setUserGroup(userDriver);
									if(groupCheck.getUserGroup().equals(group.getUserGroup())) {
										groupRepository.save(group);
										groups.add(group);
										getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",groups);
										logger.info("************************ editGeofence ENDED ***************************");
										return ResponseEntity.ok().body(getObjectResponse);

									}
									else {
										getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this geofence it belongs to another user",groups);
										return ResponseEntity.status(404).body(getObjectResponse);

									}
			    					
			    				}	
								
								

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",groups);
								return ResponseEntity.status(404).body(getObjectResponse);

							}

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID is not Found",groups);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Geofence ID is Required",groups);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",groups);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",groups);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	@Override
	public ResponseEntity<?> deleteGroup(String TOKEN, Long groupId, Long userId) {
		logger.info("************************ deleteGroup STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Group> groups = new ArrayList<Group>();
		User user = userService.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete",null);
				 logger.info("************************ deleteGeo ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(groupId != 0) {
			Group group = groupRepository.findOne(groupId);
			if(group != null) {
				
				if(group.getIs_deleted()==null) {
					 boolean isParent = false;
					 if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this group",groups);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 User parent = null;
							 for(User object : parentClients) {
								 parent = object;
							 }
							 Set<User>groupParent = group.getUserGroup();
							 if(groupParent.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this group",groups);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User parentObject : groupParent) {
									 if(parentObject.getId().equals(parent.getId())) {
										 isParent = true;
										 break;
									 }
								 }
							 }
						 }
					 }
					 if(!checkIfParent(group , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this group ",groups);
							logger.info("************************ deleteGroup ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						groupRepository.deleteGroup(groupId);
						groupRepository.deleteGroupId(groupId);
						groupRepository.deleteGroupdriverId(groupId);
						groupRepository.deleteGroupDeviceId(groupId);
						groupRepository.deleteGroupgeoId(groupId);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",groups);
						logger.info("************************ deleteGroup ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					
					
					

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group ID was Deleted before",groups);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group ID was not found",groups);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",groups);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> assignGroupToDriver(String TOKEN, Long groupId,Map<String, List> data, Long userId) {
		logger.info("************************ groupAssignDriver STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is Required",null);
			logger.info("************************ groupAssignDriver ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ groupAssignDriver ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "assignGroupToDriver")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(groupId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",null);
			logger.info("************************ assignDeviceToDriver ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",null);
				logger.info("************************ assignDeviceToDriver ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
			
				if( group.getType().equals("driver")) {
					
				
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ groupAssignDriver ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> groupParent = group.getUserGroup();
								if(groupParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ groupAssignDriver ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									
									for(User deviceUser : groupParent) {
										if(deviceUser.getId().equals(parent.getId())) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this group ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("drivers").equals(null) || data.get("drivers").size() == 0) {
						Set<Driver> drivers=new HashSet<>() ;
						drivers= group.getDriverGroup();
				        if(drivers.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",groups);
							logger.info("************************ assignDeviceToDriver ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Driver> oldDrivers =new HashSet<>() ;
				        	oldDrivers= drivers;
				        	drivers.removeAll(oldDrivers);
			        	    group.setDriverGroup(drivers);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Driver removed successfully",groups);
							logger.info("************************ assignDeviceToDriver ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>driverIds= new ArrayList<>();
					driverIds = data.get("drivers");
					Set<Driver> drivers=new HashSet<>() ;
					for(Object driverId : driverIds) {
	
				        String stringToConvert = String.valueOf(driverId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long driverIdToAssign = convertedLong;
						Driver driver =null;
						driver = driverService.getDriverById(driverIdToAssign);
						if(driver != null) {
							if(driver.getDelete_date() == null) {
								
								drivers.add(driver);
						        
							}
							
						}
	
	
					}
	
					group.setDriverGroup(drivers);
					groupRepository.save(group);
					List<Group> groups = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
					logger.info("************************ assignDeviceToDriver ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    }
				else {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "You should assign driver to group of type driver",null);
					logger.info("************************ groupAssignDriver ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
					
				}
				
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignGroupToGeofence(String TOKEN, Long groupId, Map<String, List> data, Long userId) {
		logger.info("************************ groupAssignGeofence STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is Required",null);
			logger.info("************************ groupAssignGeofence ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ groupAssignGeofence ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "assignGroupToGeofence")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(groupId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",null);
			logger.info("************************ groupAssignGeofence ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",null);
				logger.info("************************ groupAssignGeofence ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if( group.getType().equals("geofence")) {

					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ groupAssignDriver ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> groupParent = group.getUserGroup();
								if(groupParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ groupAssignGeofence ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									
									for(User deviceUser : groupParent) {
										if(deviceUser.getId().equals(parent.getId())) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this group ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("geofences").equals(null) || data.get("geofences").size() == 0) {
						Set<Geofence> geofences=new HashSet<>() ;
						geofences= group.getGeofenceGroup();
				        if(geofences.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",groups);
							logger.info("************************ groupAssignGeofence ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Geofence> oldGeofences =new HashSet<>() ;
				        	oldGeofences= geofences;
				        	geofences.removeAll(oldGeofences);
			        	    group.setGeofenceGroup(geofences);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Driver removed successfully",groups);
							logger.info("************************ groupAssignGeofence ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>geofencesIds= new ArrayList<>();
					geofencesIds = data.get("geofences");
					Set<Geofence> geofences=new HashSet<>() ;
					for(Object driverId : geofencesIds) {
	
				        String stringToConvert = String.valueOf(driverId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long geofenceIdToAssign = convertedLong;
						Geofence geofence =null;
						geofence=geofenceRepository.findOne(geofenceIdToAssign);
	
						if(geofence != null) {
							if(geofence.getDelete_date() == null) {
								
								geofences.add(geofence);
						        
							}
							
						}
	
	
					}
	
					group.setGeofenceGroup(geofences);
					groupRepository.save(group);
					List<Group> groups = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
					logger.info("************************ groupAssignGeofence ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
					
				}
				else {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "You should assign geofence to group of type geofence",null);
					logger.info("************************ groupAssignDriver ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
						
				
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignGroupToDevice(String TOKEN, Long groupId, Map<String, List> data, Long userId) {
		logger.info("************************ groupAssignDevice STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is Required",null);
			logger.info("************************ groupAssignDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ groupAssignDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "assignGroupToDevice")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ deleteDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(groupId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",null);
			logger.info("************************ groupAssignDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",null);
				logger.info("************************ groupAssignDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
			
				if( group.getType().equals("device")) {
					
				
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ groupAssignDevice ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> groupParent = group.getUserGroup();
								if(groupParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ groupAssignDriver ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									
									for(User deviceUser : groupParent) {
										if(deviceUser.getId().equals(parent.getId())) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this group ",null);
							logger.info("************************ groupAssignDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					   
					if(data.get("devices").equals(null) || data.get("devices").size() == 0) {
						Set<Device> devices=new HashSet<>() ;
						devices= group.getDeviceGroup();
				        if(devices.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No device to assign or remove",groups);
							logger.info("************************ groupAssignDevice ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Device> oldDevices =new HashSet<>() ;
				        	oldDevices= devices;
				        	devices.removeAll(oldDevices);
			        	    group.setDeviceGroup(devices);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "device removed successfully",groups);
							logger.info("************************ groupAssignDevice ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>deviceIds= new ArrayList<>();
					deviceIds = data.get("devices");
					Set<Device> devices=new HashSet<>() ;
					for(Object deviceId : deviceIds) {
	
				        String stringToConvert = String.valueOf(deviceId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long deviceIdToAssign = convertedLong;
						Device device =null;
						device = deviceService.findById(deviceIdToAssign);
						if(device != null) {
							if(device.getDelete_date() == null) {
								
								devices.add(device);
						        
							}
							
						}
	
	
					}
	
					group.setDeviceGroup(devices);
					groupRepository.save(group);
					List<Group> groups = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
					logger.info("************************ groupAssignDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    }
				else {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "You should assign device to group of type device",null);
					logger.info("************************ groupAssignDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
					
				}
				
			}
			
		}
	}
	
	

}
