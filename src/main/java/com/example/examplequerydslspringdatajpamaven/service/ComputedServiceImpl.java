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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ComputedRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
@Service
public class ComputedServiceImpl extends RestServiceController implements ComputedService{
	private static final Log logger = LogFactory.getLog(GroupsServiceImpl.class);

	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private ComputedRepository computedRepository;
	
	@Autowired 
	GroupRepository groupRepository;
	
	@Autowired
	GroupsServiceImpl groupsServiceImpl;
	
	@Autowired 
	DeviceRepository deviceRepository;
	
	@Autowired 
	DeviceServiceImpl deviceServiceImpl;
	
	@Override
	public ResponseEntity<?> createComputed(String TOKEN, Attribute attribute, Long userId) {
		logger.info("************************ createComputed STARTED ***************************");
		

		List<Attribute> attributes = null;
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attributes);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "COMPUTED", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create attribute",null);
				 logger.info("************************ createComputed ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if( (attribute.getId() != null && attribute.getId() != 0) ) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "attribute Id not allowed in create new attribute",attributes);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		

		if( attribute.getAttribute() == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "attribute is required",attributes);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		
		if( attribute.getAttribute().equals("")) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "attribute is required",attributes);
			logger.info("************************ createComputed ENDED ***************************");
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
				logger.info("************************ createComputed ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				User parent = null;
				if(userCreater.getAccountType().equals(4)) {
					Set<User>parentClient = userCreater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "this user cannot add user",null);
						logger.info("************************ createComputed ENDED ***************************");
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
				attribute.setUserAttribute(user);
		        computedRepository.save(attribute);
		        
		    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",attributes);
				logger.info("************************ createComputed ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
			
		}
	}

	@Override
	public ResponseEntity<?> getAllComputed(String TOKEN, Long id) {
		  logger.info("************************ getAllComputed STARTED ***************************");
			
			List<Attribute> attrbuites = new ArrayList<Attribute>();
			
			if(TOKEN.equals("")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attrbuites);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
			if(id != 0) {
				
				User user = userService.findById(id);
				if(user == null ) {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attrbuites);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				else {
					if(user.getAccountType()!= 1) {
						if(!userRoleService.checkUserHasPermission(id, "COMPUTED", "list")) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get attrbuites list",null);
							 logger.info("************************ getAllattributes ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
					}
					if(user.getDelete_date() == null) {
						
						userService.resetChildernArray();
					    if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								
								 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get attrbuites of this user",null);
								 logger.info("************************ getAllComputed ENDED ***************************");
								return  ResponseEntity.status(404).body(getObjectResponse);
							 }else {
								 User parentClient = new User() ;
								 for(User object : parentClients) {
									 parentClient = object;
								 }
								 List<Long>usersIds= new ArrayList<>();
								 usersIds.add(parentClient.getId());
								 attrbuites = computedRepository.getAllComputed(usersIds);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites);
								logger.info("************************ getAllComputed ENDED ***************************");
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

						
						
						 attrbuites = computedRepository.getAllComputed(usersIds);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites);
						logger.info("************************ getAllComputed ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attrbuites);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
					
				}

			}
			else{
				
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",attrbuites);
				return  ResponseEntity.badRequest().body(getObjectResponse);

			}
	}

	@Override
	public ResponseEntity<?> getComputedById(String TOKEN, Long attributeId, Long userId) {
		logger.info("************************ getComputedById STARTED ***************************");

		List<Attribute> attributes= new ArrayList<Attribute>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attributes);
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
       	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",attributes);
			return  ResponseEntity.status(404).body(getObjectResponse);
       }
		if(!attributeId.equals(0)) {
			
			Attribute attrbuite=computedRepository.findOne(attributeId);

			if(attrbuite != null) {
				boolean isParent = false;
				if(loggedUser.getAccountType().equals(4)) {
					Set<User> clientParents = loggedUser.getUsersOfUser();
					if(clientParents.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this attributes",null);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					}else {
						User parent = null;
						for(User object : clientParents) {
							parent = object ;
						}
						Set<User>attrbuiteParents = attrbuite.getUserAttribute();
						if(attrbuiteParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this attributes",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							for(User parentObject : attrbuiteParents) {
								if(parentObject.getId().equals(parent.getId())) {
									isParent = true;
									break;
								}
							}
						}
					}
				}
					if(!checkIfParent(attrbuite , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this attributes ",null);
						logger.info("************************ getComputedById ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					attributes.add(attrbuite);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attributes);
					logger.info("************************ getComputedById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
					
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attributes ID is  not Found",attributes);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "attributes ID is Required",attributes);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}
	public Boolean checkIfParent(Attribute attribute, User loggedUser) {
		   Set<User> attributeParent = attribute.getUserAttribute();
		   if(attributeParent.isEmpty()) {
			  
			   return false;
		   }else {
			   User parent = null;
			   for (User object : attributeParent) {
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
	public ResponseEntity<?> editComputed(String TOKEN, Attribute attribute, Long id) {
		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		List<Attribute> attributes = new ArrayList<Attribute>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attributes);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",attributes);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "COMPUTED", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit",null);
						 logger.info("************************ deleteGeo ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				 if(user.getDelete_date()==null) {
					 if(attribute.getId() != null) {
						 Attribute attributeCheck = computedRepository.findOne(attribute.getId());
						

						if(attributeCheck != null) {
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit attribute",attributes);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>attributeParent = attributeCheck.getUserAttribute();
									if(attributeParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit attribute",attributes);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : attributeParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!checkIfParent(attributeCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this attribute ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(attributeCheck.getAttribute()== null ||  attributeCheck.getAttribute()== "" ) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Attributea is Required",attributes);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									

			    					Set<User> userDriver = new HashSet<>();
									 if(user.getAccountType().equals(4)) {
										 Set<User> parentClients = user.getUsersOfUser();
										 if(parentClients.isEmpty()) {
											 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this attribute",attributes);
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
			    					
			    					
									attribute.setUserAttribute(userDriver);
									if(attributeCheck.getUserAttribute().equals(attribute.getUserAttribute())) {
										computedRepository.save(attribute);
										attributes.add(attribute);
										getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",attributes);
										logger.info("************************ editGeofence ENDED ***************************");
										return ResponseEntity.ok().body(getObjectResponse);

									}
									else {
										getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this attribute it belongs to another user",attributes);
										return ResponseEntity.status(404).body(getObjectResponse);

									}
			    					
			    				}	
								
								

							

							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attribute ID is not Found",attributes);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "attribute ID is Required",attributes);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",attributes);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",attributes);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	@Override
	public ResponseEntity<?> deleteComputed(String TOKEN, Long attributeId, Long userId) {
		logger.info("************************ deleteComputed STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);

		
		List<Attribute> attributes = new ArrayList<Attribute>();
		User user = userService.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attributes);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "COMPUTED", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete",null);
				 logger.info("************************ deleteComputed ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attributes);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(attributeId != 0) {
			Attribute attribute = computedRepository.findOne(attributeId);
			if(attribute != null) {
				
				if(attribute.getDelete_date()==null) {
					 boolean isParent = false;
					 if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this attribute",attributes);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 User parent = null;
							 for(User object : parentClients) {
								 parent = object;
							 }
							 Set<User>attributeParent = attribute.getUserAttribute();
							 if(attributeParent.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this attribute",attributes);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User parentObject : attributeParent) {
									 if(parentObject.getId().equals(parent.getId())) {
										 isParent = true;
										 break;
									 }
								 }
							 }
						 }
					 }
					 if(!checkIfParent(attribute , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this attribute ",attributes);
							logger.info("************************ deleteComputed ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
					 
					    attribute.setDelete_date(currentDate);

					    computedRepository.deleteAttributeId(attributeId);
					    computedRepository.deleteAttributeDeviceId(attributeId);
					    computedRepository.deleteAttributeGroupId(attributeId);
					    
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",attributes);
						logger.info("************************ deleteComputed ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					
					
					

				}
				else {
					
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attribute ID was Deleted before",attributes);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attribute ID was not found",attributes);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "attributes ID is Required",attributes);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> assignComputedToGroup(String TOKEN, Long groupId, Map<String, List> data, Long userId) {
		logger.info("************************ assignComputedToGroup STARTED ***************************");
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
			logger.info("************************ assignComputedToGroup ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignComputedToGroup ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "COMPUTED", "assignGroupToComputed")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ assignComputedToGroup ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(groupId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",null);
			logger.info("************************ assignComputedToGroup ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Group group = groupRepository.findOne(groupId);
			if(group.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group is not found",null);
				logger.info("************************ assignComputedToGroup ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
			
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ assignComputedToGroup ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> groupParent = group.getUserGroup();
								if(groupParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this group is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ assignComputedToGroup ENDED ***************************");
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
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign attributes to this group ",null);
							logger.info("************************ assignComputedToGroup ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("attributes").equals(null) || data.get("attributes").size() == 0) {
						Set<Attribute> attributes=new HashSet<>() ;
						attributes= group.getAttributeGroup();
				        if(attributes.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No attributes to assign or remove",groups);
							logger.info("************************ assignComputedToGroup ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Attribute> oldAttributes=new HashSet<>() ;
				        	oldAttributes= attributes;
				        	attributes.removeAll(oldAttributes);
			        	    group.setAttributeGroup(attributes);
						    groupRepository.save(group);
				        	List<Group> groups = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "attributes removed successfully",groups);
							logger.info("************************ assignComputedToGroup ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>attributeIds= new ArrayList<>();
					attributeIds = data.get("attributes");
					Set<Attribute> attributes=new HashSet<>() ;
					for(Object attributeId : attributeIds) {
	
				        String stringToConvert = String.valueOf(attributeId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long attributeIdToAssign = convertedLong;
						Attribute attribute =null;
						attribute = computedRepository.findOne(attributeIdToAssign);
						if(attribute != null) {
							if(attribute.getDelete_date() == null) {
								
								attributes.add(attribute);
						        
							}
							
						}
	
	
					}
	
					group.setAttributeGroup(attributes);
					groupRepository.save(group);
					List<Group> groups = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
					logger.info("************************ assignComputedToGroup ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    
				
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignComputedToDevice(String TOKEN, Long deviceId, Map<String, List> data, Long userId) {
		logger.info("************************ assignComputedToDevice STARTED ***************************");
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
			logger.info("************************ assignComputedToDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser is not found",null);
			logger.info("************************ assignComputedToDevice ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "COMPUTED", "assignDeviceToComputed")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignDeviceToDriver",null);
				 logger.info("************************ assignComputedToDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		

		if(deviceId.equals(0) ) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "device ID is Required",null);
			logger.info("************************ assignComputedToDevice ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			Device device = deviceRepository.findOne(deviceId);

			if(device.equals(null)) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This device is not found",null);
				logger.info("************************ assignComputedToDevice ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
			else {
			
					boolean isParent = false;
					   if(loggedUser.getAccountType().equals(4)) {
						   Set<User>parentClient = loggedUser.getUsersOfUser();
							if(parentClient.isEmpty()) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
								logger.info("************************ assignComputedToDevice ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}else {
							  
								User parent =null;
								for(User object : parentClient) {
									parent = object;
								}
								Set<User> deviceParent = device.getUser();
								if(deviceParent.isEmpty()) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
									logger.info("************************ assignComputedToDevice ENDED ***************************");
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
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign attribute to this device ",null);
							logger.info("************************ assignComputedToDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("attributes").equals(null) || data.get("attributes").size() == 0) {
						Set<Attribute> attributes=new HashSet<>() ;
						attributes= device.getAttributeDevice();
				        if(attributes.isEmpty()) {
				        	List<Device> devices = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No attributes to assign or remove",devices);
							logger.info("************************ assignComputedToDevice ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
				        	//check if parent in drivers
				        	Set<Attribute> oldAttribute=new HashSet<>() ;
				        	oldAttribute= attributes;
				        	attributes.removeAll(oldAttribute);
			        	    device.setAttributeDevice(attributes);
						    deviceRepository.save(device);
				        	List<Device> devices = null;
				        	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "attributes removed successfully",devices);
							logger.info("************************ assignComputedToDevice ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
				        }
					}
					List<?>attributeIds= new ArrayList<>();
					attributeIds = data.get("attributes");
					Set<Attribute> attributes=new HashSet<>() ;
					for(Object attributeId : attributeIds) {
	
				        String stringToConvert = String.valueOf(attributeId);
				        Long convertedLong = Long.parseLong(stringToConvert);
						Long attribuiteIdToAssign = convertedLong;
						Attribute attribute =null;
						attribute = computedRepository.findOne(attribuiteIdToAssign);
						if(attribute != null) {
							if(attribute.getDelete_date() == null) {
								
								attributes.add(attribute);
						        
							}
							
						}
	
	
					}
	
					device.setAttributeDevice(attributes);
					deviceRepository.save(device);
					List<Device> devices = null;
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices);
					logger.info("************************ assignComputedToDevice ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
					
				
					
			    
				
			}
			
		}
	}

	@Override
	public ResponseEntity<?> getComputedSelect(String TOKEN, Long userId) {
		logger.info("************************ getComputedSelect STARTED ***************************");
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
	    	User user = userService.findById(userId);
	    	userService.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 Set<User>parentClient = user.getUsersOfUser();
	   					if(parentClient.isEmpty()) {
	   						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
	   						logger.info("************************ getComputedSelect ENDED ***************************");
	   						return ResponseEntity.badRequest().body(getObjectResponse);
	   					}else {
	   					  
	   						User parent =null;
	   						for(User object : parentClient) {
	   							parent = object;
	   						}
	   						if(!parent.equals(null)) {

					   			List<Long>usersIds= new ArrayList<>();
			   					usersIds.add(parent.getId());
	   							drivers = computedRepository.getComputedSelect(usersIds);
	   							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	   							logger.info("************************ getComputedSelect ENDED ***************************");
	   							return ResponseEntity.ok().body(getObjectResponse);
	   						}
	   						else {
	   							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
	   							return ResponseEntity.badRequest().body(getObjectResponse);
	   						}
	   						
	   					}
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
	    			
	    			drivers = computedRepository.getComputedSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getComputedSelect ENDED ***************************");
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
