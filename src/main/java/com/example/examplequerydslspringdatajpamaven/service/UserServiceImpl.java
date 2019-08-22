package com.example.examplequerydslspringdatajpamaven.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

@Component
public class UserServiceImpl extends RestServiceController implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	GetObjectResponse getObjectResponse;

	@Override
	public User getName() {
		Long id =(long) 1;
		User x = userRepository.findOne(id);
		//System.out.println(userRepository);

		return x;
	}

//	@Override
//	public ResponseEntity<?> UserDevice(Long userId,int offset,String search) {
//		// TODO Auto-generated method stub
//		User x=userRepository.getUserData(userId);
//		if(x.getName() == null) {
//			System.out.println("no user");
//			return null;
//		}
//		else
//		{
//			Set<Device> devices = x.getDevices();
//			return devices ;
//		}
//	    
//	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
		if(user == null) {
			return null;
		}
		if(user.getDelete_date() != null) {
			//throw not found 
			return null;
		}
		else
		{
			return user;
		}
		
	}
	
	@Override
	public  ResponseEntity<?> findUserById(String TOKEN,Long userId,Long loggedUserId) {
		// TODO Auto-generated method stub
		logger.info("************************ getUserById STARTED ***************************");
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " logged User ID is Required",null);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = findById(loggedUserId);
		if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user=userRepository.findOne(userId);
		if(user == null)
		{
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else
		{
			if(user.getDelete_date()!= null)
			{
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
				logger.info("************************ getUserById STARTED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			if(user.getAccountType() == 4 && loggedUser.getAccountType()== 4) {
				Set<User> loggedUserParents = loggedUser.getUsersOfUser();
				if(loggedUserParents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not aloowed to get this user",null);
					logger.info("************************ getUserById STARTED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				else {
					User loggedUserParent= null;
					for(User object : loggedUserParents) {
						loggedUserParent = object ;
						break;
					}
					Set<User> userParent = user.getUsersOfUser();
					if(userParent.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not aloowed to get this user",null);
						logger.info("************************ getUserById STARTED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					}else {
					  boolean isParent = false;
					  for(User object : userParent) {
						  if(object.getId() == loggedUserParent.getId()) {
							  isParent = true;
							  break;
						  }
					  }
					  if(!isParent) {
						  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not aloowed to get this user",null);
							logger.info("************************ getUserById STARTED ***************************");
							return ResponseEntity.status(404).body(getObjectResponse);
						}
					  List<User> users= new ArrayList<>();
						users.add(user);
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
						logger.info("************************ getUserById STARTED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
				}
				
			}else {
				List<User>parents = getAllParentsOfuser(user,user.getAccountType());
				if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not one of the parents of this user",null);
					logger.info("************************ getUserById STARTED ***************************");
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				 boolean isParent = false;
				 for(User parent : parents ) {
					 if(parent.getId() == loggedUserId) {
						 isParent = true;
						 break;
					 }
				 }
				 if(!isParent) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not one of the parents of this user",null);
						logger.info("************************ getUserById STARTED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
				 }
				List<User> users= new ArrayList<>();
				users.add(user);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
				logger.info("************************ getUserById STARTED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
		}
		
	}

	@Override
	public ResponseEntity<?> usersOfUser(String TOKEN,Long userId,int offset,String search,int active) {
		logger.info("************************ getAllUsersOfUser STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId == 0) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			 logger.info("************************ getAllUsersOfUser ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = findById(userId);
			if(user == null) {
				 List<User> users = null;
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value() ,"This user is not found",users);
				 logger.info("************************ getAllUsersOfUser ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			else {

				if(active == 0) {
					List<User> users = userRepository.getInactiveUsersOfUser(userId,offset,search);
					Integer size=userRepository.getInactiveUsersOfUserSize(userId);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users,size);
					logger.info("************************ getAllUsersOfUser ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
				}
				List<User> users = userRepository.getUsersOfUser(userId,offset,search);
				Integer size=userRepository.getUsersOfUserSize(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users,size);
				logger.info("************************ getAllUsersOfUser ENDED ***************************");

				return  ResponseEntity.ok().body(getObjectResponse);
			}
			
		}
		 
	}

	@Override
	public ResponseEntity<?> createUser(String TOKEN,User user,Long userId) {
		
		logger.info("************************createUser STARTED ***************************");
		
		if(TOKEN.equals("")) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}else {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
			//userId is the user parent of the account user
			if(userId == 0) {
				List<User> users = null;
		    	//throw duplication exception with duplication list
		    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
		    	logger.info("************************createUser ENDED ***************************");
		    	return ResponseEntity.badRequest().body(getObjectResponse);
			}else {
				User creater = findById(userId);
				if(creater == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This  creater user is not Found",null);
		    	logger.info("************************createUser ENDED ***************************");

			    	return ResponseEntity.status(404).body(getObjectResponse);
				}else {
					if(user.getId() != null && user.getId() != 0) {
						List<User> users = null;
						String message= "create doesn't accept id";
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.ok().body(getObjectResponse);
					}
					if(user.getEmail() == null || user.getEmail() == "" || user.getPassword() == null
						|| user.getPassword() == "" || user.getName() == null || user.getName() == "" 
						|| user.getIdentity_num() == null || user.getIdentity_num() == ""
						|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
						|| user.getCompany_phone() == null || user.getCompany_phone() == ""
						|| user.getManager_phone() == null || user.getManager_phone() == ""
						|| user.getManager_mobile() == null || user.getManager_mobile() == "" 
						|| user.getPhone() == null || user.getPhone() == "" || user.getAccountType() == null || user.getAccountType() == 0 
						|| user.getParents() == null || user.getParents() == "") {
						List<User> users = null;
						String message= "attributes [email , password, name, identityNumber ,commercialNumber,"
								+ "companyPhone ,Managerphone, ManagerMobile ,accountType,userParents] are required";
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.badRequest().body(getObjectResponse);
					}
					//create vendor
					if(user.getAccountType() == 2) {
						if(creater.getAccountType() != 1) {
			    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot create vendor",null);
					    	logger.info("************************createUser ENDED ***************************");
					    	return ResponseEntity.badRequest().body(getObjectResponse);
			    		}
			    		else {
			    			return saveUser(userId,user);
			    		}
					}
					//check if the user is client
					else if(user.getAccountType() == 3) {
					    	JSONObject parentUsers = new JSONObject(user.getParents());
					    	if( !parentUsers.has("vendorId")) {
					    		
					    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "the parent user of this user must be of type vendor",null);
						    	logger.info("************************createUser ENDED ***************************");
						    	return ResponseEntity.badRequest().body(getObjectResponse);
					    	}else {
					    		if(!checkIfParentOrNot(userId,parentUsers.getLong("vendorId"),creater.getAccountType(),2)) {
					    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign client to this vendor",null);
							    	logger.info("************************createUser ENDED ***************************");
							    	return ResponseEntity.badRequest().body(getObjectResponse);
					    		}
					    		else {
					    			return saveUser(parentUsers.getLong("vendorId"),user);
					    		}
					    	}
					    }else if(user.getAccountType() == 4) {
					    	if(creater.getAccountType() == 4) {
				    		Set<User> userParents = creater.getUsersOfUser();
				    		if(userParents.isEmpty()) {
				    			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this  creater user not assigned to any client please assign first to client",null);
						    	logger.info("************************createUser ENDED ***************************");
						    	return ResponseEntity.status(404).body(getObjectResponse);
				    		}
				    		else {
				    			User parent = null;
				    			for(User parentClient : userParents) {
					    			 parent = parentClient;
					    			break;
					    		}
				    			return saveUser(parent.getId(),user);
				    		}
				    		
				    	}else if(creater.getAccountType() == 3) {
				    		return saveUser(creater.getId(),user);
				    	}
				    	else if(creater.getAccountType()== 2) {
				    		JSONObject parentUsers = new JSONObject(user.getParents());
					    	if( !parentUsers.has("clientId")) {
					    		
					    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "the parent user of this user must be of type client",null);
						    	logger.info("************************createUser ENDED ***************************");
						    	return ResponseEntity.badRequest().body(getObjectResponse);
					    	}else {
					    		if(!checkIfParentOrNot(userId,parentUsers.getLong("clientId"),creater.getAccountType(),3)) {
					    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this client",null);
							    	logger.info("************************createUser ENDED ***************************");
							    	return ResponseEntity.badRequest().body(getObjectResponse);
					    		}
					    		else {
					    			return saveUser(parentUsers.getLong("clientId"),user);
					    		}
				    	}
				    	
				    }else if(creater.getAccountType() ==1) {
				    	JSONObject parentUsers = new JSONObject(user.getParents());
				    	if( !parentUsers.has("vendorId")) {
				    		
				    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select vendor to be parent of this user",null);
					    	logger.info("************************createUser ENDED ***************************");
					    	return ResponseEntity.badRequest().body(getObjectResponse);
				    	}else {
				    		if(!checkIfParentOrNot(userId,parentUsers.getLong("vendorId"),creater.getAccountType(),2)) {
				    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this vendor",null);
						    	logger.info("************************createUser ENDED ***************************");
						    	return ResponseEntity.badRequest().body(getObjectResponse);
				    		}
				    		else {
				    			if( !parentUsers.has("clientId")) {
						    		
						    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select client to be parent of this user",null);
							    	logger.info("************************createUser ENDED ***************************");
							    	return ResponseEntity.badRequest().body(getObjectResponse);
						    	}else {
						    		if(!checkIfParentOrNot(parentUsers.getLong("vendorId"),parentUsers.getLong("clientId"),2,3)) {
						    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this client ",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.badRequest().body(getObjectResponse);
						    		}
						    		else {
						    			return saveUser(parentUsers.getLong("clientId"),user);
						    		}
				    		}
			    	}
				   }
				   }
				    else {
				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "creater must has account type",null);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.badRequest().body(getObjectResponse);
				    }
					
					
				}else {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "user must has account type",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
					
				}
			}
		}
	

		
	}
	@Override
	public ResponseEntity<?> editUser(String TOKEN,User user,Long userId) {
		
		logger.info("************************editUser STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			List<User> users = null;
	    	//throw duplication exception with duplication list
	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.badRequest().body(getObjectResponse);
		}else {
			  User loggedUser =  findById(userId);
			  if(loggedUser == null) {
				  List<User> users = null;
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user ID is not Found",users);
			    	logger.info("************************editUser ENDED ***************************");
			    	return ResponseEntity.status(404).body(getObjectResponse); 
			  }
			//to set the users of updateduser
			if( user.getId() == null || user.getId() == 0|| user.getEmail() == null || user.getEmail() == "" ||
					 user.getName() == null || user.getName() == "" 
					|| user.getIdentity_num() == null || user.getIdentity_num() == ""
					|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
					|| user.getCompany_phone() == null || user.getCompany_phone() == ""
					|| user.getManager_phone() == null || user.getManager_phone() == ""
					|| user.getManager_mobile() == null || user.getManager_mobile() == ""
					|| user.getPhone() == null || user.getPhone() == "" || user.getAccountType() ==  0 
					|| user.getAccountType() == null) {
					List<User> users = null;
					String message= "attributes [id,email , name, identityNumber ,commercialNumber,"
							+ "companyPhone ,Managerphone, ManagerMobile, accountType ] are required";
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}else {
					
					
					if(user.getPassword()!= null) {
						List<User> users = null;
						String message= "you are not allowed to edit password";
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.badRequest().body(getObjectResponse);
					}
					User oldOne = findById(user.getId());
					if(oldOne == null) {
						List<User> users = null;
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not found",users);
				    	logger.info("************************editUser ENDED ***************************");
				    	return ResponseEntity.status(404).body(getObjectResponse);
					}else {
						if(oldOne.getAccountType() != user.getAccountType()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you cann't edit account Type",null);
					    	logger.info("************************createUser ENDED ***************************");
					    	return ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							// to edit vendor
							if(user.getAccountType() == 2) {
								Set<User> parentUsers = oldOne.getUsersOfUser();
							    Boolean isParent = false;
							    for( User parent : parentUsers) {
							    	if(parent.getId() == userId) {
							    		isParent = true;
							    		break;
							    	}
							    }
							    if(!isParent) {
							    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowe to edit this user",null);
							    	logger.info("************************createUser ENDED ***************************");
							    	return ResponseEntity.badRequest().body(getObjectResponse);
							    }else {
							    	String password = oldOne.getPassword();
									user.setPassword(password);
							    	return saveUser(userId,user);
							    			
							}
							
						 
						}
						//to edit client
							else if(user.getAccountType() == 3) {
								if(loggedUser.getAccountType() == 1) {
									Set<User> parentUsers = oldOne.getUsersOfUser();
									
									
									if(parentUsers.isEmpty()) {
										System.out.println("no parent vendor");
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user is not assigned to any of your vendors you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										User directParent = null;
										for(User userParent : parentUsers) {
											directParent = userParent;
											break;
										}
										Set<User> vendorParents = directParent.getUsersOfUser();
										if(vendorParents.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user is not assigned to any of your vendors you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}else {
											User vendorParent = null;
											for(User userParent : vendorParents) {
												vendorParent = userParent;
												break;
											}
											if(vendorParent.getId() == userId) {
												String password = oldOne.getPassword();
												user.setPassword(password);
												//check assign
												JSONObject parentUsersToAssign = new JSONObject(user.getParents());
										    	if( !parentUsersToAssign.has("vendorId")) {
										    		
										    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "the parent user of this user must be of type vendor",null);
											    	logger.info("************************createUser ENDED ***************************");
											    	return ResponseEntity.badRequest().body(getObjectResponse);
										    	}else {
										    		if(!checkIfParentOrNot(userId,parentUsersToAssign.getLong("vendorId"),loggedUser.getAccountType(),2)) {
										    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign client to this vendor",null);
												    	logger.info("************************createUser ENDED ***************************");
												    	return ResponseEntity.badRequest().body(getObjectResponse);
										    		}
										    		else {
										    			return saveUser(parentUsersToAssign.getLong("vendorId"),user);
										    		}
										    	}
										    	
											}else {
												getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this user vendor you cannot edit it",null);
										    	logger.info("************************createUser ENDED ***************************");
										    	return ResponseEntity.status(404).body(getObjectResponse);
											}
										}
									}
								}else if(loggedUser.getAccountType() == 2){
									Set<User> vendorParents = oldOne.getUsersOfUser();
									if(vendorParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										User vendor = null;
										for(User userVendor :vendorParents ) {
											vendor = userVendor;
											break;
										}
										if(vendor.getId() == userId) {
											
											String password = oldOne.getPassword();
											user.setPassword(password);
											//check assign
											
									    	return saveUser(userId,user);
										}else {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}
									}
									
								}else {
									getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
							    	logger.info("************************createUser ENDED ***************************");
							    	return ResponseEntity.status(404).body(getObjectResponse);
								}
							}
						     // edit user
							else if(user.getAccountType() == 4) {
								if(loggedUser.getAccountType() == 1) {
									Set <User> userParentClients = oldOne.getUsersOfUser();
									if(userParentClients.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										User parentClient = null;
										for(User client : userParentClients ) {
											parentClient = client;
											break;
										}
										Set<User> userParentVendors = parentClient.getUsersOfUser();
										if(userParentVendors.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}else {
											User vendor = null;
											for(User vendorObject : userParentVendors) {
												 vendor = vendorObject;
												 break;
											}
											Set<User>Admins = vendor.getUsersOfUser();
											if(Admins.isEmpty()) {
												getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
										    	logger.info("************************createUser ENDED ***************************");
										    	return ResponseEntity.status(404).body(getObjectResponse);
											}else {
												User admin = null;
												for(User adminParent :Admins) {
													admin = adminParent;
													break;
												}
												if(admin.getId() == userId) {
													//check assign
													JSONObject parentUsers = new JSONObject(user.getParents());
											    	if( !parentUsers.has("vendorId")) {
											    		
											    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select vendor to be parent of this user",null);
												    	logger.info("************************createUser ENDED ***************************");
												    	return ResponseEntity.badRequest().body(getObjectResponse);
											    	}else {
											    		if(!checkIfParentOrNot(userId,parentUsers.getLong("vendorId"),loggedUser.getAccountType(),2)) {
											    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this vendor",null);
													    	logger.info("************************createUser ENDED ***************************");
													    	return ResponseEntity.badRequest().body(getObjectResponse);
											    		}
											    		else {
											    			if( !parentUsers.has("clientId")) {
													    		
													    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select client to be parent of this user",null);
														    	logger.info("************************createUser ENDED ***************************");
														    	return ResponseEntity.badRequest().body(getObjectResponse);
													    	}else {
													    		if(!checkIfParentOrNot(parentUsers.getLong("vendorId"),parentUsers.getLong("clientId"),2,3)) {
													    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this client ",null);
															    	logger.info("************************createUser ENDED ***************************");
															    	return ResponseEntity.badRequest().body(getObjectResponse);
													    		}
													    		else {
													    			String password = oldOne.getPassword();
																	user.setPassword(password);
													    			return saveUser(parentUsers.getLong("clientId"),user);
													    		}
											    		}
										    	}
											   }
													
											    	
												}else {
													getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
											    	logger.info("************************createUser ENDED ***************************");
											    	return ResponseEntity.status(404).body(getObjectResponse);
												}
											}
										}
									}
								}else if(loggedUser.getAccountType() == 2) {
									Set<User> clientParents = oldOne.getUsersOfUser();
									if(clientParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										User client = null;
										for(User clientObject : clientParents) {
											 client = clientObject;
											 break;
										}
										Set<User> vendorParents = client.getUsersOfUser();
										if(vendorParents.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}else {
											User vendor = null;
											for(User vendorObject : vendorParents) {
												vendor = vendorObject;
												break;
											}
											if(vendor.getId() == userId) {
												String password = oldOne.getPassword();
												user.setPassword(password);
												//check assign
												JSONObject parentUsers = new JSONObject(user.getParents());
										    	if( !parentUsers.has("clientId")) {
										    		
										    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "the parent user of this user must be of type client",null);
											    	logger.info("************************createUser ENDED ***************************");
											    	return ResponseEntity.badRequest().body(getObjectResponse);
										    	}else {
										    		if(!checkIfParentOrNot(userId,parentUsers.getLong("clientId"),loggedUser.getAccountType(),3)) {
										    			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this client",null);
												    	logger.info("************************createUser ENDED ***************************");
												    	return ResponseEntity.badRequest().body(getObjectResponse);
										    		}
										    		else {
										    			return saveUser(parentUsers.getLong("clientId"),user);
										    		}
									    	}
										    	
												
											}else {
												getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
										    	logger.info("************************createUser ENDED ***************************");
										    	return ResponseEntity.status(404).body(getObjectResponse);
											}
										}
									}
								}else if( loggedUser.getAccountType() == 3) {
									Set<User> parentClients = oldOne.getUsersOfUser();
									if(parentClients.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										User client = null ;
										for(User clientObject : parentClients) {
											client = clientObject ;
											break;
										}
										if(client.getId() == userId) {
											String password = oldOne.getPassword();
											user.setPassword(password);
											//check assign
									    	return saveUser(userId,user);
										}else {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}
									}
								}else if(loggedUser.getAccountType() == 4) {
									Set<User> loggedUserParents = loggedUser.getUsersOfUser();
									if(loggedUserParents.isEmpty()) {
										
										getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
								    	logger.info("************************createUser ENDED ***************************");
								    	return ResponseEntity.status(404).body(getObjectResponse);
									}else {
										Set<User>userParents = oldOne.getUsersOfUser();
										if(userParents.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
									    	logger.info("************************createUser ENDED ***************************");
									    	return ResponseEntity.status(404).body(getObjectResponse);
										}else {
											User loggedParent = null;
											for(User loggedParentObject : loggedUserParents ) {
												loggedParent = loggedParentObject;
												break;
											}
											User userParent = null ;
										    for(User parentObject : userParents) {
										    	userParent = parentObject; 
										    	break;
										    }
										    if(userParent.getId() == loggedParent.getId()) {
										    	String password = oldOne.getPassword();
												user.setPassword(password);
												//check assign
										    	return saveUser(userParent.getId(),user);
										    }else {
										    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent of this you cannot edit it",null);
										    	logger.info("************************createUser ENDED ***************************");
										    	return ResponseEntity.status(404).body(getObjectResponse);
										    }
										}
									}
								}
							}
							else {
								return null;
							}
						
					    
					   return null;
					}
				}
			
			
				}	
		}
		
		
	}
	
	
	 public static String getMd5(String input) 
	    { 
	        try { 
	  
	            // Static getInstance method is called with hashing MD5 
	            MessageDigest md = MessageDigest.getInstance("MD5"); 
	  
	            // digest() method is called to calculate message digest 
	            //  of an input digest() return array of byte 
	            byte[] messageDigest = md.digest(input.getBytes()); 
	  
	            // Convert byte array into signum representation 
	            BigInteger no = new BigInteger(1, messageDigest); 
	  
	            // Convert message digest into hex value 
	            String hashtext = no.toString(16); 
	            while (hashtext.length() < 32) { 
	                hashtext = "0" + hashtext; 
	            } 
	            return hashtext; 
	        }  
	  
	        // For specifying wrong message digest algorithms 
	        catch (NoSuchAlgorithmException e) { 
	            throw new RuntimeException(e); 
	        } 
	    }

	@Override
	public List<Integer> checkUserDuplication(User user) {
		 // TODO Auto-generated method stubt
		 String email = user.getEmail();
		 String identityNum = user.getIdentity_num();
		 String commercialNum = user.getCommercial_num();
		 String	companyPhone = user.getCompany_phone();
		 String managerPhone = user.getManager_phone();
		 String managerMobile = user.getManager_mobile();
		 String phone = user.getPhone();
		 List<User>userDuolicationList = userRepository.checkUserDuplication(email, identityNum, commercialNum, companyPhone, managerPhone, managerMobile,phone);
		 List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (User matchedUser : userDuolicationList) 
		    { 
		    	
		    	if(matchedUser.getId() != user.getId() ) {
		    		System.out.println("matched User"+matchedUser.getId()+"---user"+user.getId());
		    		if(matchedUser.getEmail() != null) {
		    			if(matchedUser.getEmail().equals(user.getEmail()))
				        {
				        	
				        	duplicationCodes.add(1);
				        }
		    		}
		    		if(matchedUser.getIdentity_num() != null) {
		    			if(matchedUser.getIdentity_num().equals(user.getIdentity_num())) {
		    				duplicationCodes.add(2);
		    			}
		    		}
		    		if(matchedUser.getCommercial_num() != null) {
		    			if(matchedUser.getCommercial_num().equals(user.getCommercial_num())) {
		    				duplicationCodes.add(3);
		    			}
		    		}
		    		if(matchedUser.getCompany_phone() != null) {
		    			if(matchedUser.getCompany_phone().equals(user.getCompany_phone())) {
		    				duplicationCodes.add(4);
		    			}
		    		}
		    		if(matchedUser.getManager_phone() != null) {
		    			if(matchedUser.getManager_phone().equals(user.getManager_phone())) {
		    				duplicationCodes.add(5);
		    			}
		    		}
		    		if(matchedUser.getManager_mobile() != null) {
		    			if(matchedUser.getManager_mobile().equals(user.getManager_mobile())) {
		    				duplicationCodes.add(6);
		    			}
		    		}
		    		if(matchedUser.getPhone() != null) {
		    			if(matchedUser.getPhone().equals(user.getPhone())) {
		    				duplicationCodes.add(7);
		    			}
		    		}
		    		
		    	}
		    }
		 return duplicationCodes;
		
	}

//	@Override
//	public ResponseEntity<?> deleteUser(String TOKEN,Long userId,Long deleteUserId) {
//		logger.info("************************deleteUser STARTED ***************************");
//		if(TOKEN.equals("")) {
//			 List<User> users = null;
//			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
//			 return  ResponseEntity.badRequest().body(getObjectResponse);
//		}
//		
//		if(super.checkActive(TOKEN)!= null)
//		{
//			return super.checkActive(TOKEN);
//		}
//		if(userId == 0 || deleteUserId == 0) {
//			
//		      getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
//		    logger.info("************************deleteUser ENDED ***************************");
//		    return ResponseEntity.badRequest().body(getObjectResponse);
//		}
//		else {
//			 User loggedUser = findById(userId);
//	            if(loggedUser== null) {
//						List<User> users= null;
//					    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",users);
//					    logger.info("************************deleteUser ENDED ***************************");
//					    return ResponseEntity.status(404).body(getObjectResponse);
//	            }else {
//	            	User deletedUser = findById(deleteUserId);
//	    			if(deletedUser == null) {
//	    				logger.info("************************deleteUser STARTED ***************************");
//	    				
//	    					List<User> users= null;
//	    				    getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",users);
//	    				    logger.info("************************deleteUser ENDED ***************************");
//	    				    return ResponseEntity.status(404).body(getObjectResponse);
//	    			}else {
//	    				if(loggedUser.getAccountType() == 4 && deletedUser.getAccountType() == 4 ) {
//	    					Set<User> loggedUserParents = loggedUser.getUsersOfUser();
//	    					if(loggedUserParents.isEmpty()) {
//	    							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this user",null);
//	    						    logger.info("************************deleteUser ENDED ***************************");
//	    						    return ResponseEntity.badRequest().body(getObjectResponse);
//	    					}
//	    					else {
//	    						User loggedUserParent= null;
//	    						for(User object : loggedUserParents) {
//	    							loggedUserParent = object ;
//	    							break;
//	    						}
//	    						Set<User> userParent = deletedUser.getUsersOfUser();
//	    						if(userParent.isEmpty()) {
//	    							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to ]delete this user",null);
//	    							logger.info("************************ getUserById STARTED ***************************");
//	    							return ResponseEntity.status(404).body(getObjectResponse);
//	    						}else {
//	    						  boolean isParent = false;
//	    						  for(User object : userParent) {
//	    							  if(object.getId() == loggedUserParent.getId()) {
//	    								  isParent = true;
//	    								  break;
//	    							  }
//	    						  }
//	    						  if(!isParent) {
//	    							  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowe to delete this user",null);
//	    								logger.info("************************ getUserById STARTED ***************************");
//	    								return ResponseEntity.status(404).body(getObjectResponse);
//	    							}
//	    						
//	    							Calendar cal = Calendar.getInstance();
//	    		    				int day = cal.get(Calendar.DATE);
//	    		    				int month = cal.get(Calendar.MONTH) + 1;
//	    		    				int year = cal.get(Calendar.YEAR);
//	    		    				String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
//	    		    				deletedUser.setDelete_date(date);
//	    		    				userRepository.save(deletedUser);
//	    		    				userRepository.deleteUserOfUser(deletedUser.getId());
//	    		    				
//	    		    				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
//	    		    				logger.info("************************deleteUser ENDED ***************************");
//	    		    				return ResponseEntity.ok().body(getObjectResponse);
//	    						}
//	    					}
//	    					
//	    				}else {
//	    					List<User>parents = getAllParentsOfuser(deletedUser,deletedUser.getAccountType());
//	    					if(parents.isEmpty()) {
//	    						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not one of the parents of this user",null);
//	    						logger.info("************************ getUserById STARTED ***************************");
//	    						return ResponseEntity.status(404).body(getObjectResponse);
//	    					}
//	    					 boolean isParent = false;
//	    					 for(User parent : parents ) {
//	    						 if(parent.getId() == userId) {
//	    							 isParent = true;
//	    							 break;
//	    						 }
//	    					 }
//	    					 if(!isParent) {
//	    						
//	    						 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not one of the parents of this user",null);
//	    							logger.info("************************ getUserById STARTED ***************************");
//	    							return ResponseEntity.status(404).body(getObjectResponse);
//	    					 }
//	    					Calendar cal = Calendar.getInstance();
//	 	    				int day = cal.get(Calendar.DATE);
//	 	    				int month = cal.get(Calendar.MONTH) + 1;
//	 	    				int year = cal.get(Calendar.YEAR);
//	 	    				String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
//	 	    				deletedUser.setDelete_date(date);
//	 	    				userRepository.save(deletedUser);
//	 	    				userRepository.deleteUserOfUser(deletedUser.getId());
//	 	    				
//	 	    				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
//	 	    				logger.info("************************deleteUser ENDED ***************************");
//	 	    				return ResponseEntity.ok().body(getObjectResponse);
//	    				}
//	    				
//	    			}
//	            }
//		}
//		
//	}

	@Override
	public ResponseEntity<?> getUserRole(Long userId) {
		// TODO Auto-generated method stub
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
		    
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		User user = findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> roles = roleRepository.getUserRole(userId);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
		
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public Boolean checkIfParentOrNot(Long parentId, Long childId,Integer parentType , Integer childType) {
		// TODO Auto-generated method stub
		if(parentId == 0 || parentId == null ||  childId == 0 || childId == null) {
			return false;
		}else {			
			User parent = findById(parentId);
			if(parent == null) {
				return false;
			}else {
				if(parent.getAccountType() != parentType ) {
					return false;
				}
				User child = findById(childId);
				if(child == null) {
					return false;
				}else {
					if(child.getAccountType() != childType) {
						return false;
					}
					if(parentType ==childType) {
						return true;
					}
					Set<User>parentsOfChild = child.getUsersOfUser();
					for( User parentOfChild : parentsOfChild) {
						if(parentOfChild.getId() == parentId) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public ResponseEntity<?> saveUser(Long parentId, User user) {
		// TODO Auto-generated method stub
		Set<User> userCreater=new HashSet<>() ;
		userCreater.add(findById(parentId));
		user.setUsersOfUser(userCreater);
		String password = user.getPassword();
		String hashedPassword = getMd5(password);  
		user.setPassword(hashedPassword);
		List<Integer> duplictionList = checkUserDuplication(user);
		if(duplictionList.size()>0)
		{
			
			//throw duplication exception with duplication list
			getObjectResponse = new GetObjectResponse(101, "Duplication Erorr",duplictionList);
			logger.info("************************createUser ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else
		{
			userRepository.save(user);
			List<User> users = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
			logger.info("************************createUser ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		
	}

	@Override
	public List<User> getAllParentsOfuser(User user, Integer accountType) {
		// TODO Auto-generated method stub
		List<User> parents = new ArrayList<>();
		if(accountType == 1) {
			return parents;
		}else if(accountType  == 2) {
		 Set <User> parent = user.getUsersOfUser();
		 if(parent.isEmpty()) {
			 return parents;
		 }else {
			 User vendorParent = null;
			for(User object :parent) {
				vendorParent = object;
				break;
			}
		    parents.add(vendorParent);
		    return parents;
		 }
		}else if(accountType == 3) {
			Set<User>parent = user.getUsersOfUser();
			if(parent.isEmpty()) {
				return parents;
			}else {
				User clientParent = null;
				for(User object :parent) {
					clientParent = object;
					break;
				}
				parents.add(clientParent);
				Set<User>vendorParents = clientParent.getUsersOfUser();
				if(vendorParents.isEmpty()) {
					return parents;
				}else {
					User vendorParent = null;
					for(User object : vendorParents) {
						vendorParent = object;
					}
					parents.add(vendorParent);
					return parents;
				}
			}
		}else if(accountType == 4) {
			Set<User>parent = user.getUsersOfUser();
			if(parent.isEmpty()) {
				return parents;
			}else {
				User userParent = null;
				for(User object: parent) {
					userParent = object;
				}
				parents.add(userParent);
				Set<User>clientParents = userParent.getUsersOfUser();
				if(clientParents.isEmpty()) {
					return parents;
				}else {
					User vendor = null;
					for(User object : clientParents) {
						vendor = object;
					}
					parents.add(vendor);
					Set<User>vendorParents = vendor.getUsersOfUser();
					if(vendorParents.isEmpty()) {
						return parents;
					}else {
						User admin = null;
						for(User object : vendorParents) {
							admin = object ;
						}
						parents.add(admin);
					}
				}
			}
		}
		return parents;
	}

	@Override
	public ResponseEntity<?> deleteUser(String TOKEN, Long userId, Long deleteUserId) {
		// TODO Auto-generated method stub
		
		//check if authorized
		logger.info("************************deleteUser STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0 || deleteUserId == 0) {
			
		    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
		    logger.info("************************deleteUser ENDED ***************************");
		    return ResponseEntity.badRequest().body(getObjectResponse);
		}
		//check if account is found 
		User loggedUser = findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		//check if has permission to delete user
		
		//check if is parent or not to delete 
		User deletedUser = findById(deleteUserId);
		if(deletedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to delete is not found",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}else {
			List<User>parents = getAllParentsOfuser(deletedUser,deletedUser.getAccountType());
			if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this user",null);
			    logger.info("************************deleteUser ENDED ***************************");
			    return ResponseEntity.badRequest().body(getObjectResponse);
			}else {
				boolean isParent = false;
				 for(User parent : parents ) {
					 if(parent.getId() == userId) {
						 isParent = true;
						 break;
					 }
				 }
				 if(!isParent) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this user",null);
					    logger.info("************************deleteUser ENDED ***************************");
					    return ResponseEntity.badRequest().body(getObjectResponse);
				 }else {
					//change delete date to currentDate
 					Calendar cal = Calendar.getInstance();
	    			int day = cal.get(Calendar.DATE);
	    			int month = cal.get(Calendar.MONTH) + 1;
	    			int year = cal.get(Calendar.YEAR);
	    			String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	   				deletedUser.setDelete_date(date);
	   				userRepository.save(deletedUser);
//	   				userRepository.deleteUserOfUser(deletedUser.getId());  
	   				
	   				// kill the session of this account or any of its children
	   				 if(deletedUser.getAccountType() == 4) {
	   				   TokenSecurity.getInstance().removeActiveUserById(deleteUserId);
	   				 }else {
	   				  TokenSecurity.getInstance().removeActiveUserById(deleteUserId);
	   					 //get all children
	   				  List<User>children = getAllChildrenOfUser(deleteUserId);
	   				  if(!children.isEmpty()) {
	   					  for(User object : children ) {
	   						TokenSecurity.getInstance().removeActiveUserById(object.getId());
	   		   				object.setDelete_date(date);
	   		   				userRepository.save(object);
	   					  }
	   				  }
	   				   
	   				 }
	   				
	    			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
	    			logger.info("************************deleteUser ENDED ***************************");
	    			return ResponseEntity.ok().body(getObjectResponse);
				 }
				 
			} 
		}
		
		
		
		
		
	}

	@Override
	public List<User> getAllChildrenOfUser(Long userId) {
		// TODO Auto-generated method stub
		
		return userRepository.getChildrenOfUser(userId);
	}
	
	public  ResponseEntity<?> getUserSelect(String TOKEN,Long userId) {

		logger.info("************************ getDeviceSelect STARTED ***************************");
		List<UserSelect> users = new ArrayList<UserSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = findById(userId);
	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			users = userRepository.getUserSelect(userId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",users);
					logger.info("************************ getDeviceSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
		

	}
}
