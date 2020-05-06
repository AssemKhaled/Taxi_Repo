package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.NotificationRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
@Service
public class NotificationServiceImpl extends RestServiceController implements NotificationService{
	private static final Log logger = LogFactory.getLog(GroupsServiceImpl.class);

	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired 
	GroupRepository groupRepository;
	
	@Autowired
	GroupsServiceImpl groupsServiceImpl;
	
	@Autowired 
	DeviceRepository deviceRepository;
	
	@Autowired 
	DeviceServiceImpl deviceServiceImpl;
	
	@Override
	public ResponseEntity<?> createNotification(String TOKEN, Notification notification, Long userId) {
		logger.info("************************ createNotification STARTED ***************************");
	

		List<Notification> notifications = null;
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ createNotification ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ createNotification ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATION", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create notification",null);
				 logger.info("************************ createNotification ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if( (notification.getId() != null && notification.getId() != 0) ) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "notification Id not allowed in create new notification",notifications);
			logger.info("************************ createNotification ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if( notification.getAttributes() == null || notification.getAttributes().equals("") ) {
			notification.setAttributes("{}");
		}


		if( notification.getType() == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "type is required",notifications);
			logger.info("************************ createNotification ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		
		if( notification.getType().equals("")) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "type is required",notifications);
			logger.info("************************ createNotification ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		else
		{
			Set<User> user=new HashSet<>() ;
			User userCreater ;
			userCreater=userService.findById(userId);
			if(userCreater.equals(null))
			{

				getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "Assigning to not found user",null);
				logger.info("************************ createNotification ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				User parent = null;
				if(userCreater.getAccountType().equals(4)) {
					Set<User>parentClient = userCreater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this user cannot add user",null);
						logger.info("************************ createNotification ENDED ***************************");
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
		        notification.setUserNotification(user);
		        notificationRepository.save(notification);
		        
		    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",notifications);
				logger.info("************************ createNotification ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
			
		}
	}

	@Override
	public ResponseEntity<?> getAllNotifications(String TOKEN, Long id) {
       logger.info("************************ getAllNotifications STARTED ***************************");
		
		List<Notification> notifications = new ArrayList<Notification>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "NOTIFICATION", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get notifications list",null);
						 logger.info("************************ getAllNotifications ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					
					userService.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							
							 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get notifications of this user",null);
							 logger.info("************************ getAllNotifications ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						 }else {
							 User parentClient = new User() ;
							 for(User object : parentClients) {
								 parentClient = object;
							 }
							 List<Long>usersIds= new ArrayList<>();
							 usersIds.add(parentClient.getId());
							 notifications = notificationRepository.getAllNotifications(usersIds);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",notifications);
							logger.info("************************ getAllNotifications ENDED ***************************");
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

					
					
					notifications = notificationRepository.getAllNotifications(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",notifications);
					logger.info("************************ getAllNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getNotificationById(String TOKEN, Long notificationId, Long userId) {
		logger.info("************************ getNotificationById STARTED ***************************");

		List<Notification> notifications= new ArrayList<Notification>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
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
       	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",notifications);
			return  ResponseEntity.status(404).body(getObjectResponse);
       }
		if(!notificationId.equals(0)) {
			
			Notification notification=notificationRepository.findOne(notificationId);

			if(notification != null) {
				boolean isParent = false;
				if(loggedUser.getAccountType().equals(4)) {
					Set<User> clientParents = loggedUser.getUsersOfUser();
					if(clientParents.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this notification",null);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					}else {
						User parent = null;
						for(User object : clientParents) {
							parent = object ;
						}
						Set<User>notificationParents = notification.getUserNotification();
						if(notificationParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this notification",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							for(User parentObject : notificationParents) {
								if(parentObject.getId().equals(parent.getId())) {
									isParent = true;
									break;
								}
							}
						}
					}
				}
					if(!checkIfParent(notification , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this notification ",null);
						logger.info("************************ getNotificationById ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					notifications.add(notification);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",notifications);
					logger.info("************************ getNotificationById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This notification ID is  not Found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "notification ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}
	public Boolean checkIfParent(Notification notification, User loggedUser) {
		   Set<User> notificationParent = notification.getUserNotification();
		   if(notificationParent.isEmpty()) {
			  
			   return false;
		   }else {
			   User parent = null;
			   for (User object : notificationParent) {
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
	public ResponseEntity<?> editNotification(String TOKEN, Notification notification, Long id) {
		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		List<Notification> notifications = new ArrayList<Notification>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",notifications);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "NOTIFICATION", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				 if(user.getDelete_date()==null) {
					 if(notification.getId() != null) {
						Notification notificationCheck = notificationRepository.findOne(notification.getId());
						

						if(notificationCheck != null) {
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit notification",notifications);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>notificationParent = notificationCheck.getUserNotification();
									if(notificationParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit notification",notifications);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : notificationParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!checkIfParent(notificationCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this notification ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(notification.getType()== null ||  notification.getType()== "" ) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Type is Required",notifications);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									

			    					Set<User> userDriver = new HashSet<>();
									 if(user.getAccountType().equals(4)) {
										 Set<User> parentClients = user.getUsersOfUser();
										 if(parentClients.isEmpty()) {
											 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this notification",notifications);
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
			    					
			    					
									notification.setUserNotification(userDriver);
									if(notificationCheck.getUserNotification().equals(notification.getUserNotification())) {
										notificationRepository.save(notification);
										notifications.add(notification);
										getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",notifications);
										logger.info("************************ editGeofence ENDED ***************************");
										return ResponseEntity.ok().body(getObjectResponse);

									}
									else {
										getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this notification it belongs to another user",notifications);
										return ResponseEntity.status(404).body(getObjectResponse);

									}
			    					
			    				}	
								
								

							

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This notification ID is not Found",notifications);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "notification ID is Required",notifications);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",notifications);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	@Override
	public ResponseEntity<?> deleteNotification(String TOKEN, Long notificationId, Long userId) {
		logger.info("************************ deleteNotification STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Notification> notifications = new ArrayList<Notification>();
		User user = userService.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATION", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete",null);
				 logger.info("************************ deleteNotification ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(notificationId != 0) {
			Notification notification = notificationRepository.findOne(notificationId);
			if(notification != null) {
				
				if(notification.getDelete_date()==null) {
					 boolean isParent = false;
					 if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this notification",notifications);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 User parent = null;
							 for(User object : parentClients) {
								 parent = object;
							 }
							 Set<User>notificationParent = notification.getUserNotification();
							 if(notificationParent.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this notification",notifications);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User parentObject : notificationParent) {
									 if(parentObject.getId().equals(parent.getId())) {
										 isParent = true;
										 break;
									 }
								 }
							 }
						 }
					 }
					 if(!checkIfParent(notification , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this notification ",notifications);
							logger.info("************************ deleteNotification ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
					 
					    notification.setDelete_date(currentDate);

					    notificationRepository.deleteNotificationId(notificationId);
					    notificationRepository.deleteNotificationDeviceId(notificationId);
					    notificationRepository.deleteNotificationGroupId(notificationId);
					    
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",notifications);
						logger.info("************************ deleteNotification ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					
					
					

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This notification ID was Deleted before",notifications);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This notification ID was not found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "notification ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> assignNotificationToGroup(String TOKEN, Long groupId, Map<String, List> data,
			Long userId) {
		logger.info("************************ assignNotificationToGroup STARTED ***************************");
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
			logger.info("************************ assignNotificationToGroup ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignNotificationToGroup ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATION", "assignGroupToNotification")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ assignNotificationToGroup ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(groupId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",null);
			logger.info("************************ assignNotificationToGroup ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",null);
				logger.info("************************ assignNotificationToGroup ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
			
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ assignNotificationToGroup ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> groupParent = group.getUserGroup();
								if(groupParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this group is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ assignNotificationToGroup ENDED ***************************");
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
					   if(!groupsServiceImpl.checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign notification to this group ",null);
							logger.info("************************ assignNotificationToGroup ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("notifications").equals(null) || data.get("notifications").size() == 0) {
						Set<Notification> notifications=new HashSet<>() ;
						notifications= group.getNotificationGroup();
				        if(notifications.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No notification to assign or remove",groups);
							logger.info("************************ assignNotificationToGroup ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Notification> oldNotifications=new HashSet<>() ;
				        	oldNotifications= notifications;
				        	notifications.removeAll(oldNotifications);
			        	    group.setNotificationGroup(notifications);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "notification removed successfully",groups);
							logger.info("************************ assignNotificationToGroup ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>notificationIds= new ArrayList<>();
					notificationIds = data.get("notifications");
					Set<Notification> notifications=new HashSet<>() ;
					for(Object notificationId : notificationIds) {
	
				        String stringToConvert = String.valueOf(notificationId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long notificationIdToAssign = convertedLong;
						Notification notification =null;
						notification = notificationRepository.findOne(notificationIdToAssign);
						if(notification != null) {
							if(notification.getDelete_date() == null) {
								
								notifications.add(notification);
						        
							}
							
						}
	
	
					}
	
					group.setNotificationGroup(notifications);
					groupRepository.save(group);
					List<Group> groups = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
					logger.info("************************ assignNotificationToDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    
				
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignNotificationToDevice(String TOKEN, Long deviceId, Map<String, List> data,
			Long userId) {
		logger.info("************************ assignNotificationToDevice STARTED ***************************");
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
			logger.info("************************ assignNotificationToDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignNotificationToDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATION", "assignDeviceToNotification")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ assignNotificationToDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(deviceId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "device ID is Required",null);
			logger.info("************************ assignNotificationToDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Device device = deviceRepository.findOne(deviceId);

			if(device.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
				logger.info("************************ assignNotificationToDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
			else {
			
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ assignNotificationToDevice ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> deviceParent = device.getUser();
								if(deviceParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ assignNotificationToDevice ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									
									for(User deviceUser : deviceParent) {
										if(deviceUser.getId().equals(parent.getId())) {
											
											isParent = true;
											break;
										}
									}
								}
							}
					   }
					   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign notification to this device ",null);
							logger.info("************************ assignNotificationToDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("notifications").equals(null) || data.get("notifications").size() == 0) {
						Set<Notification> notifications=new HashSet<>() ;
						notifications= device.getNotificationDevice();
				        if(notifications.isEmpty()) {
				        	List<Device> devices = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No notification to assign or remove",devices);
							logger.info("************************ assignNotificationToGroup ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Notification> oldNotifications=new HashSet<>() ;
				        	oldNotifications= notifications;
				        	notifications.removeAll(oldNotifications);
			        	    device.setNotificationDevice(notifications);
						    deviceRepository.save(device);
				        	List<Device> devices = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "notification removed successfully",devices);
							logger.info("************************ assignNotificationToDevice ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>notificationIds= new ArrayList<>();
					notificationIds = data.get("notifications");
					Set<Notification> notifications=new HashSet<>() ;
					for(Object notificationId : notificationIds) {
	
				        String stringToConvert = String.valueOf(notificationId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long notificationIdToAssign = convertedLong;
						Notification notification =null;
						notification = notificationRepository.findOne(notificationIdToAssign);
						if(notification != null) {
							if(notification.getDelete_date() == null) {
								
								notifications.add(notification);
						        
							}
							
						}
	
	
					}
	
					device.setNotificationDevice(notifications);
					deviceRepository.save(device);
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
					logger.info("************************ assignNotificationToDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    
				
			}
			
		}
	}
		
}
