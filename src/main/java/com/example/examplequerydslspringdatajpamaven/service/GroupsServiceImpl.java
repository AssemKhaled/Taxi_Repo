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

import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientGroup;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.NotificationRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientComputedRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to groups
 * @author fuinco
 *
 */
@Component
@Service
public class GroupsServiceImpl extends RestServiceController implements GroupsService{

	private static final Log logger = LogFactory.getLog(GroupsServiceImpl.class);

	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	UserClientDriverRepository userClientDriverRepository;
	
	@Autowired
	UserClientGeofenceRepository userClientGeofenceRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	UserClientComputedRepository userClientComputedRepository;
	
	@Autowired
	UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	private DriverServiceImpl driverService;
	
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@Autowired
	UserClientGroupRepository userClientGroupRepository;
	
	@Autowired
	private GeofenceRepository geofenceRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired 
	GroupRepository groupRepository;
	
	/**
	 * create group with data in body
	 */
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
		if(loggedUser == null) {
			
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
		
		if( group.getName() == null || group.getName().equals("") ||
				group.getType() == null || group.getType().equals("")) {
			
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
				
										
			
				Set<User> user=new HashSet<>() ;
				User userCreater ;
				userCreater=userService.findById(userId);
				if(userCreater == null)
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
				  	
			        
			        List<Group> groupCheck=groupRepository.checkDublicateGroupInAdd(parent.getId(),group.getName());
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
			        
			    	groupRepository.save(group);
			    	List<Group> groups = null;
			    	
			    	if(userCreater.getAccountType().equals(4)) {
			    		userClientGroup saveData = new userClientGroup();
			    		Long GroupId = groupRepository.getGroupIdByName(parent.getId(),group.getName());
			    		if(GroupId != null) {
				    		saveData.setUserid(userId);
				    		saveData.setGroupid(GroupId);
					        userClientGroupRepository.save(saveData);
			    		}
			    		
			    	}
			    	
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

	/**
	 * get group list with limit 10
	 */
	@Override
	public ResponseEntity<?> getAllGroups(String TOKEN, Long id, int offset, String search,String exportData) {
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
						 

				    	
				    	List<Long> groupIds = userClientGroupRepository.getGroupsIds(id);
						Integer size=0;
						List<Map> data = new ArrayList<>();

						 if(groupIds.size()>0) {
							 if(exportData.equals("exportData")) {
								groups = groupRepository.getAllGroupsByIdsExport(groupIds,search);
								 
							 }
							 else {
								groups = groupRepository.getAllGroupsByIds(groupIds,offset,search);
								
								if(groups.size()>0) {
									size=groupRepository.getAllGroupsSizeByIds(groupIds,search);
										 
								} 
							 }

							    
							for(Group group:groups) {
							     Map PointsList= new HashMap();
							     
								 PointsList.put("id", group.getId());
								 PointsList.put("name", group.getName());
								 PointsList.put("attributes", group.getAttributes());
								 PointsList.put("groupid", group.getGroupid());
								 PointsList.put("is_deleted", group.getIs_deleted());
								 PointsList.put("type", group.getType());
								 PointsList.put("companyName",null);
								 PointsList.put("companyId",null);

								 
								 
						    	Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									

								}else {
									for(User parentObject : groupParents) {
										 PointsList.put("companyId",parentObject.getId());
										 User us = userRepository.findOne(parentObject.getId());
										 if(us != null) {
											 PointsList.put("companyName", us.getName());

										 }
										 break;
										
									}
								}
								data.add(PointsList);

							}


						 }
						 
						 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
						 logger.info("************************ getAllUserGeofences ENDED ***************************");
						 return  ResponseEntity.ok().body(getObjectResponse);
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

					 Integer size=0;
					 List<Map> data = new ArrayList<>();
					 if(exportData.equals("exportData")) {
						groups = groupRepository.getAllGroupsExport(usersIds,search);
						
					 }
					 else {
						groups = groupRepository.getAllGroups(usersIds,offset,search);
							
						if(groups.size()>0) {
							size=groupRepository.getAllGroupsSize(usersIds,search);	
						}
					 }
					 for(Group group:groups) {
					     Map PointsList= new HashMap();
					     
						 PointsList.put("id", group.getId());
						 PointsList.put("name", group.getName());
						 PointsList.put("attributes", group.getAttributes());
						 PointsList.put("groupid", group.getGroupid());
						 PointsList.put("is_deleted", group.getIs_deleted());
						 PointsList.put("type", group.getType());
						 PointsList.put("companyName",null);
						 PointsList.put("companyId",null);

						 
						 
				     		Set<User>groupParents = group.getUserGroup();
							if(groupParents.isEmpty()) {
								

							}else {
								for(User parentObject : groupParents) {
									 PointsList.put("companyId",parentObject.getId());
									 User us = userRepository.findOne(parentObject.getId());
									 if(us != null) {
										 PointsList.put("companyName", us.getName());

									 }
									break;
									
								}
							}
							data.add(PointsList);

					}

				    
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
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

	/**
	 * get group by id
	 */
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
       if(loggedUser == null) {
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
						List<Long> groupData = userClientGroupRepository.getGroup(userId,groupId);
						if(groupData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
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

	/**
	 * edit group by id in body mandatory
	 */
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
								if(groupCheck.getType() != null) {
									if(groupCheck.getType() != "") {
										if(group.getType().equals(groupCheck.getType())) {
											
										}
										else {
											
											getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "not allow to edit type of group",null);
											logger.info("************************ deleteGeo ENDED ***************************");
											return  ResponseEntity.badRequest().body(getObjectResponse);
											
											
										}
									}
								}
								
								
								boolean isParent = false;
								User parent = null;
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit group",groups);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
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
									List<Long> groupData = userClientGroupRepository.getGroup(id,group.getId());
									if(groupData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
									
									
									
								}
								else {
									parent = user;
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
									
									List<Group> checkDublicateInEdit= groupRepository.checkDublicateGroupInEdit(group.getId(),parent.getId(),group.getName());
								    List<Integer> duplictionList =new ArrayList<Integer>();
									if(!checkDublicateInEdit.isEmpty()) {
				    					for(int i=0;i<checkDublicateInEdit.size();i++) {
				    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(group.getName())) {
												duplictionList.add(1);						
			
				    						}
				    						
				    						
				    					}
								    	getObjectResponse = new GetObjectResponse( 401, "This group was found before",duplictionList);
										return ResponseEntity.badRequest().body(getObjectResponse);

				    				}
									

									Set<User> userCreater=new HashSet<>();
			    					userCreater = groupCheck.getUserGroup();			    					
			    					group.setUserGroup(userCreater);

									Set<Attribute> attributeGroup=new HashSet<>();
									attributeGroup = groupCheck.getAttributeGroup();
			    					group.setAttributeGroup(attributeGroup);
			    					
									Set<Device> deviceGroup=new HashSet<>();
									deviceGroup = groupCheck.getDeviceGroup();
			    					group.setDeviceGroup(deviceGroup);
			    					
									Set<Driver> driverGroup=new HashSet<>();
									driverGroup = groupCheck.getDriverGroup();
			    					group.setDriverGroup(driverGroup);
			    					
									Set<Geofence> geofenceGroup=new HashSet<>();
									geofenceGroup = groupCheck.getGeofenceGroup();
			    					group.setGeofenceGroup(geofenceGroup);
			    					
									Set<Notification> notificationGroup=new HashSet<>();
									notificationGroup = groupCheck.getNotificationGroup();
			    					group.setNotificationGroup(notificationGroup);

			    					groupRepository.save(group);
									groups.add(group);
									
									getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",null);
									logger.info("************************ editGeofence ENDED ***************************");
									return ResponseEntity.ok().body(getObjectResponse);

									
			    					
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

	/**
	 * delete group by id
	 */
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
						List<Long> groupData = userClientGroupRepository.getGroup(userId,groupId);
						if(groupData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					 }
					 if(!checkIfParent(group , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this group ",groups);
							logger.info("************************ deleteGroup ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						groupRepository.deleteGroup(groupId);
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

	/**
	 * assign driver to group
	 */
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
		if(loggedUser == null) {
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
			if(group == null) {
				
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
							List<Long> groupData = userClientGroupRepository.getGroup(userId,groupId);
							if(groupData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this group ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("drivers") == null || data.get("drivers").size() == 0) {
						Set<Driver> drivers=new HashSet<>() ;
						drivers= group.getDriverGroup();
				        if(drivers.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No driver to assign or remove",groups);
							logger.info("************************ assignDeviceToDriver ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {

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

	/**
	 * assign geofences to group
	 */
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
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ groupAssignGeofence ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "assignGroupToGeofence")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignGroupToGeofence",null);
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
			if(group == null) {
				
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
							List<Long> groupData = userClientGroupRepository.getGroup(userId,groupId);
							if(groupData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign Geofence to this group ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("geofences") == null || data.get("geofences").size() == 0) {
						Set<Geofence> geofences=new HashSet<>() ;
						geofences= group.getGeofenceGroup();
				        if(geofences.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No Geofence to assign or remove",groups);
							logger.info("************************ groupAssignGeofence ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {

				        	Set<Geofence> oldGeofences =new HashSet<>() ;
				        	oldGeofences= geofences;
				        	geofences.removeAll(oldGeofences);
			        	    group.setGeofenceGroup(geofences);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Geofence removed successfully",groups);
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

	/**
	 * assign devices to group
	 */
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
		if(loggedUser == null) {
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
			if(group == null) {
				
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
							List<Long> groupData = userClientGroupRepository.getGroup(userId,groupId);
							if(groupData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
					   }
					   if(!checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign driver to this group ",null);
							logger.info("************************ groupAssignDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					   
					if(data.get("devices") == null || data.get("devices").size() == 0) {
						Set<Device> devices=new HashSet<>() ;
						devices= group.getDeviceGroup();
				        if(devices.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No device to assign or remove",groups);
							logger.info("************************ groupAssignDevice ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {

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

	/**
	 * get selected items for group
	 */
	@Override
	public ResponseEntity<?> getGroupDevices(String TOKEN, Long groupId,String type) {
		// TODO Auto-generated method stub
		logger.info("************************ getGroupDevices STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(groupId.equals(0)) {
			 List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",devices);
			logger.info("************************ getGroupDevices ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group == null) {
				List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",devices);
				logger.info("************************ getGroupDevices ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else
			{
				if(type == null) {
					List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "type is Required",devices);
					logger.info("************************ getGroupDevices ENDED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					if(type.equals("")) {
						List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "type is Required",devices);
						logger.info("************************ getGroupDevices ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}
					else {
						List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
						if(type.equals("devices")) {
							devices = groupRepository.getGroupDevicesSelect(groupId);
						}
						if(type.equals("drivers")) {
							devices = groupRepository.getGroupDriverSelect(groupId);
						}
						if(type.equals("geofences")) {
							devices = groupRepository.getGroupGeofencesSelect(groupId);
						}
						if(type.equals("notifications")) {
							devices = groupRepository.getGroupNotificationsSelect(groupId);
						}
						if(type.equals("attributes")) {
							devices = groupRepository.getGroupAttrbuitesSelect(groupId);
						}
						
						
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
						logger.info("************************ getGroupDevices ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
					
				}
				 
					
				
			}
		}
		
	}

	/**
	 * get group list selection
	 */
	@Override
	public ResponseEntity<?> getGroupSelect(String TOKEN,Long loggedUserId, Long userId,List<String> type) {
		logger.info("************************ getDriverSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(loggedUserId != 0) {
	    	User loggedUser = userService.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long> groupIds = userClientGroupRepository.getGroupsIds(loggedUserId);

						 if(groupIds.size()>0) {
							 if(type.size() > 0) {
						    	drivers = groupRepository.getGroupSelectByIdsByType(groupIds,type);

 
							 }
							 else {
						    	drivers = groupRepository.getGroupSelectByIds(groupIds);

							 }

						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
						logger.info("************************ getDriverSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
		
	    if(userId != 0) {
	    	User user = userService.findById(userId);
	    	userService.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 


				    	List<Long> groupIds = userClientGroupRepository.getGroupsIds(userId);

				    	if(groupIds.size()>0) {
				    		if(type.size() > 0) {
						    	drivers = groupRepository.getGroupSelectByIdsByType(groupIds,type);

							}
							else {
					    		drivers = groupRepository.getGroupSelectByIds(groupIds);

							}

						}
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
						logger.info("************************ getDriverSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	   			 }
	    			 List<User>childernUsers = userService.getAllChildernOfUser(userId);
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
		   			 
		   			 if(type.size() > 0) {
			    			drivers = groupRepository.getGroupSelectByType(usersIds,type);		   				 

		   			 }
		   			 else {
			    			drivers = groupRepository.getGroupSelect(usersIds);		   				 
		   			 }
	    			
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getDriverSelect ENDED ***************************");
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
	 * get unassigned groups to any user type 4
	 */
	@Override
	public ResponseEntity<?> getGroupUnSelectOfCient(String TOKEN,Long loggedUserId, Long userId) {
		logger.info("************************ getDriverSelect STARTED ***************************");
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
		User client = userService.findById(loggedUserId);
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
		User user = userService.findById(userId);
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
		

		drivers = groupRepository.getGroupUnSelectOfClient(loggedUserId,userId);
		List<DriverSelect> selectedGroups = userClientGroupRepository.getGroupsOfUserList(userId);
		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedGroups", selectedGroups);
	    obj.put("groups", drivers);

	    data.add(obj);
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getDriverSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
		

	
	}
	
	
	/**
	 * assign group to user type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientGroups(String TOKEN, Long loggedUserId, Long userId, Long[] groupIds) {
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
		User client = userService.findById(loggedUserId);
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
		User user = userService.findById(userId);
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
		
        if(groupIds.length > 0 && groupIds[0] != 0) {
			
        	List<userClientGroup> checkData = userClientGroupRepository.getGroupByGroIds(groupIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is group assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
        	
        	
			for(Long id:groupIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Group assignedGroup = groupRepository.findOne(id);
				if(assignedGroup == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Group is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					if(assignedGroup.getType() != null) {
						if(assignedGroup.getType().equals("device")) {
							 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
							 List<Long> list = groupRepository.getDevicesFromGroup(id);

							 if(!deviceIds.containsAll(list)) {

								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "group of devices you want to assign is contain ids not share with this user",null);
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
							 
							
						}
						if(assignedGroup.getType().equals("driver")) {
							 List<Long> driverIds = userClientDriverRepository.getDriverIds(userId);
							 List<Long> list = groupRepository.getDriversFromGroup(id);

							 if(!driverIds.containsAll(list)) {

								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "group of drivers you want to assign is contain ids not share with this user",null);
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
							 
							
						}
						if(assignedGroup.getType().equals("geofence")) {
							 List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(userId);
							 List<Long> list = groupRepository.getGeofneceFromGroup(id);

							 if(!geofenceIds.containsAll(list)) {

								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "group of geofences you want to assign is contain ids not share with this user",null);
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
							 
							
						}
						
						if(assignedGroup.getType().equals("attribute")) {
							 List<Long> attributeIds = userClientComputedRepository.getComputedsIds(userId);
							 List<Long> list = groupRepository.getAttrbuiteFromGroup(id);

							 if(!attributeIds.containsAll(list)) {

								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "group of attribute you want to assign is contain ids not share with this user",null);
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
							 
							
						}
						
						if(assignedGroup.getType().equals("notification")) {
							 List<Long> notificationIds = notificationRepository.getNotificationIds(userId);
							 List<Long> list = groupRepository.getNotifcationFromGroup(id);

							 if(!notificationIds.containsAll(list)) {

								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "group of notification you want to assign is contain ids not share with this user",null);
								return ResponseEntity.status(404).body(getObjectResponse);
							 }
							 
						}
					}
				}
		        
				
			}
			
			userClientGroupRepository.deleteGroupsByUserId(userId);
			for(Long assignedId:groupIds) {
				userClientGroup userGroup = new userClientGroup();
				userGroup.setUserid(userId);
				userGroup.setGroupid(assignedId);
				userClientGroupRepository.save(userGroup);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientGroup> groups = userClientGroupRepository.getGroupsOfUser(userId);
			
			if(groups.size() > 0) {

				userClientGroupRepository.deleteGroupsByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no groups for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get selected group for type 4
	 */
	@Override
	public ResponseEntity<?> getClientGroups(String TOKEN, Long loggedUserId, Long userId) {
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
		User client = userService.findById(loggedUserId);
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
		User user = userService.findById(userId);
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

		List<DriverSelect> devices = userClientGroupRepository.getGroupsOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",devices);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
	

}
