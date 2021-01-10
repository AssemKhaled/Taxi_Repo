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
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientComputed;
import com.example.examplequerydslspringdatajpamaven.repository.ComputedRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientComputedRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * service functionality related to computeds
 * @author fuinco
 *
 */
@Component
@Service
public class ComputedServiceImpl extends RestServiceController implements ComputedService{
	private static final Log logger = LogFactory.getLog(ComputedServiceImpl.class);

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
	
	@Autowired
	UserClientDeviceRepository userClientDeviceRepository;
	@Autowired
	UserClientComputedRepository userClientComputedRepository;
	
	@Autowired
	UserClientGroupRepository userClientGroupRepository;

	/**
	 * create attributes by body data 
	 */
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
		if(loggedUser == null) {
			
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
		
		if( attribute.getDescription() == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Description is required",attributes);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}
		
		if( attribute.getDescription().equals("")) {
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Description is required",attributes);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
					
		}

		
		
		Set<User> user=new HashSet<>() ;
		User userCreater ;
		userCreater=userService.findById(userId);
		if(userCreater == null)
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
			
			List<Attribute> res1 = computedRepository.checkDuplicationAdd(parent.getId(), attribute.getDescription());
			List<Integer> duplictionList =new ArrayList<Integer>();

			if(!res1.isEmpty()) {
				for(int i=0;i<res1.size();i++) {
					if(res1.get(i).getDescription().equals(attribute.getDescription())) {
						duplictionList.add(1);				
					}
		
				}
			}
			
			if(!res1.isEmpty()) {
				getObjectResponse = new GetObjectResponse( 301, "This Attribute was found before",duplictionList);
				return ResponseEntity.ok().body(getObjectResponse);	
			}
			
			user.add(parent);	
			attribute.setUserAttribute(user);
	        computedRepository.save(attribute);
	        
			if(userCreater.getAccountType().equals(4)) {

		        userClientComputed saveData = new userClientComputed();
		        Long attId = computedRepository.getComputedIdByName(parent.getId(),attribute.getDescription(),attribute.getType());
	    		if(attId != null) {
		    		saveData.setUserid(userId);
		    		saveData.setComputedid(attId);
			        userClientComputedRepository.save(saveData);
	    		}
	    		
			}
	        
	    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success",attributes);
			logger.info("************************ createComputed ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
			
			
			
		
	}

	/**
	 * get attributes list with limit 10
	 */
	@Override
	public ResponseEntity<?> getAllComputed(String TOKEN, Long id,int offset,String search,String exportData) {
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
							 

					    	List<Long> computedIds = userClientComputedRepository.getComputedsIds(id);
					    	Integer size=0;
					    	if(computedIds.size() > 0) {
								 if(exportData.equals("exportData")) {
								    attrbuites = computedRepository.getAllComputedByIdsExport(computedIds,search);
									 
								 }
								 else {
							    	attrbuites = computedRepository.getAllComputedByIds(computedIds,offset,search);
									size=computedRepository.getAllComputedSizeByIds(computedIds);
								 }

								
					    	}
					    	
					    	getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites,size);
							logger.info("************************ getAllComputed ENDED ***************************");
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
						 Integer size = 0;
						 if(exportData.equals("exportData")) {
							 attrbuites = computedRepository.getAllComputedExport(usersIds,search);

						 }
						 else {

							 attrbuites = computedRepository.getAllComputed(usersIds,offset,search);
							 size=computedRepository.getAllComputedSize(usersIds);
						 }
						

						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites,size);
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

	/**
	 * get attribute by id 
	 */
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
       if(loggedUser == null) {
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
					
					 List<Long> computeds = userClientComputedRepository.getComputed(userId,attributeId);
					 if(computeds.isEmpty()) {
							isParent = false;
					 }
					 else {
							isParent = true;
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

	/**
	 * edit attribute by id in body mandatory
	 */
	@Override
	public ResponseEntity<?> editComputed(String TOKEN, Attribute attribute, Long id) {
		logger.info("************************ editGeofence STARTED ***************************");

		GetObjectResponse getObjectResponse;
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",null);
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
								User parent = null;
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit attribute",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>attributeParent = attributeCheck.getUserAttribute();
									if(attributeParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit attribute",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : attributeParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
									
									 List<Long> computeds = userClientComputedRepository.getComputed(id,attributeCheck.getId());
									 if(computeds.isEmpty()) {
											isParent = false;
									 }
									 else {
											isParent = true;
									 }
									
								}
								else {
									parent = user;
									
								}
								if(!checkIfParent(attributeCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this attribute ",null);
									logger.info("************************ editGeofnece ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								
								if(attributeCheck.getAttribute()== null ||  attributeCheck.getAttribute()== "" ) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Attribute is Required",null);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								
								if(attributeCheck.getDescription()== null ||  attributeCheck.getDescription()== "" ) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Description is Required",null);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								
									
								List<Attribute> res1 = computedRepository.checkDuplicationEdit(parent.getId(),attribute.getId() ,attribute.getDescription());
								List<Integer> duplictionList =new ArrayList<Integer>();

								if(!res1.isEmpty()) {
									for(int i=0;i<res1.size();i++) {
										if(res1.get(i).getDescription().equals(attribute.getDescription())) {
											duplictionList.add(1);				
										}
							
									}
								}
								
								if(!res1.isEmpty()) {
									getObjectResponse = new GetObjectResponse( 301, "This Attribute was found before",duplictionList);
									return ResponseEntity.ok().body(getObjectResponse);	
								}
			    					
								
									 
								Set<User> userCreater=new HashSet<>();
		    					userCreater = attributeCheck.getUserAttribute();			    					
								attribute.setUserAttribute(userCreater);
								
								Set<Device> devices=new HashSet<>();
								devices = attributeCheck.getDevices();	
								attribute.setDevices(devices);
								
								Set<Group> groups=new HashSet<>();
								groups = attributeCheck.getGroups();	
								attribute.setGroups(groups);
																
								computedRepository.save(attribute);

								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",null);
								logger.info("************************ editGeofence ENDED ***************************");
								return ResponseEntity.ok().body(getObjectResponse);

									
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attribute ID is not Found",null);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
					 }
					 else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "attribute ID is Required",null);
							return ResponseEntity.status(404).body(getObjectResponse);

					 }
					 
				 }
				 else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",null);
						return ResponseEntity.status(404).body(getObjectResponse);

				 }
				
			}
		   
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	/**
	 * delete attribute by id 
	 */
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
						 List<Long> computeds = userClientComputedRepository.getComputed(userId,attributeId);
						 if(computeds.isEmpty()) {
								isParent = false;
						 }
						 else {
								isParent = true;
						 }
					 }
					 if(!checkIfParent(attribute , user) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this attribute ",attributes);
							logger.info("************************ deleteComputed ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
					 
					    attribute.setDelete_date(currentDate);

					    computedRepository.save(attribute);
					    
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

	/**
	 * assign attributes to group
	 */
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
		if(loggedUser == null) {
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
			if(group == null) {
				
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
							 List<Long> groups = userClientGroupRepository.getGroup(userId,groupId);
							 if(groups.isEmpty()) {
									isParent = false;
							 }
							 else {
									isParent = true;
							 }
					   }
					   if(!groupsServiceImpl.checkIfParent(group , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign attributes to this group ",null);
							logger.info("************************ assignComputedToGroup ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("attributes") == null || data.get("attributes").size() == 0) {
						Set<Attribute> attributes=new HashSet<>() ;
						attributes= group.getAttributeGroup();
				        if(attributes.isEmpty()) {
				        	List<Group> groups = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No attributes to assign or remove",groups);
							logger.info("************************ assignComputedToGroup ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
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

	/**
	 * assign attributes to device
	 */
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
		if(loggedUser == null) {
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

			if(device == null) {
				
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
							List<Long> devices = userClientDeviceRepository.getDevice(userId,deviceId);
							 if(devices.isEmpty()) {
									isParent = false;
							 }
							 else {
									isParent = true;
							 }
					   }
					   if(!deviceServiceImpl.checkIfParent(device , loggedUser)&& ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to assign attribute to this device ",null);
							logger.info("************************ assignComputedToDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
					   }
					if(data.get("attributes") == null || data.get("attributes").size() == 0) {
						Set<Attribute> attributes=new HashSet<>() ;
						attributes= device.getAttributeDevice();
				        if(attributes.isEmpty()) {
				        	List<Device> devices = null;
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No attributes to assign or remove",devices);
							logger.info("************************ assignComputedToDevice ENDED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
				        }
				        else {
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

	/**
	 * select list of attributes
	 */
	@Override
	public ResponseEntity<?> getComputedSelect(String TOKEN,Long loggedUserId, Long userId ,Long deviceId, Long groupId) {
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
		List<DeviceSelect> devices = new ArrayList<DeviceSelect>();
		List<DeviceSelect> groups = new ArrayList<DeviceSelect>();
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();

		if(deviceId != 0) {
			devices = deviceRepository.getAttributesDeviceSelect(deviceId);

		}
		if(groupId != 0) {
			groups = groupRepository.getGroupAttrbuitesSelect(groupId);

		}
		obj.put("selectedDevices", devices);
		obj.put("selectedGroups", groups);

		
		if(loggedUserId != 0) {
	    	User loggedUser = userService.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long> computedIds = userClientComputedRepository.getComputedsIds(loggedUserId);
				    	if(computedIds.size() > 0) {

   							drivers = computedRepository.getComputedSelectByIds(computedIds);
   							
				    	}
						obj.put("computeds", drivers);
						data.add(obj);

				    	getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getComputedSelect ENDED ***************************");
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
	   				    
	    				List<Long> computedIds = userClientComputedRepository.getComputedsIds(userId);
				    	Integer size=0;
				    	if(computedIds.size() > 0) {

   							drivers = computedRepository.getComputedSelectByIds(computedIds);
   								
				    	}
				    	obj.put("computeds", drivers);
						data.add(obj);
				    	getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getComputedSelect ENDED ***************************");
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
	    			
	    			drivers = computedRepository.getComputedSelect(usersIds);
			    	obj.put("computeds", drivers);
					data.add(obj);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
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
	
	/**
	 * assign attributes to user type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientComputeds(String TOKEN, Long loggedUserId, Long userId, Long[] computedIds) {
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
		
        if(computedIds.length > 0 && computedIds[0] != 0) {
			
			List<userClientComputed> checkComputeds = userClientComputedRepository.getComputedsByCompIds(computedIds,userId);
        	if(checkComputeds.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is computed assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
			for(Long id:computedIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Attribute assignedComputed = computedRepository.findOne(id);
				if(assignedComputed == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Computed is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientComputedRepository.deleteComputedsByUserId(userId);
			for(Long assignedId:computedIds) {
				userClientComputed userComputed = new userClientComputed();
				userComputed.setUserid(userId);
				userComputed.setComputedid(assignedId);
				userClientComputedRepository.save(userComputed);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientComputed> computeds = userClientComputedRepository.getComputedsOfUser(userId);
			
			if(computeds.size() > 0) {

				userClientComputedRepository.deleteComputedsByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no computeds for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get attributes related to type 4
	 */
	@Override
	public ResponseEntity<?> getClientComputeds(String TOKEN, Long loggedUserId, Long userId) {
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

		List<DriverSelect> computeds = userClientComputedRepository.getComputedsOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",computeds);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get unselect attributes to chose from them 
	 */
	@Override
	public ResponseEntity<?> getComputedUnSelect(String TOKEN,Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
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
		
		List<DriverSelect> computeds = userClientComputedRepository.getComputedsOfUserList(userId);
		drivers = computedRepository.getComputedUnSelectOfClient(loggedUserId,userId);
		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedComputeds", computeds);
	    obj.put("computeds", drivers);

	    data.add(obj);
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getComputedSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	
	}


}
