package com.example.examplequerydslspringdatajpamaven.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

/**
 * services functionality related to users
 * @author fuinco
 *
 */
@Component
@Service
public class UserServiceImpl extends RestServiceController implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;

	@Autowired
	private UserRoleService userRoleService;
	
	
	private static final Log logger = LogFactory.getLog(UserServiceImpl.class);
	
	GetObjectResponse getObjectResponse;

	@Override
	public User getName() {
		Long id =(long) 1;
		User x = userRepository.findOne(id);


		return x;
	}


	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
		if(user == null) {
			return null;
		}
		if(user.getDelete_date() != null) {

			return null;
		}
		else
		{
			return user;
		}
		
	}
	
	/**
	 * get user by id
	 */
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

	
	/**
	 * get user list with limit 10 of userId and active or not
	 */
	@Override
	public ResponseEntity<?> usersOfUser(String TOKEN,Long userId,Long loggedUserId,int offset,String search,int active,String exportData) {
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
		
		if(userId.equals(0)) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			 logger.info("************************ getAllUsersOfUser ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User Loggeduser = findById(loggedUserId);
			if(Loggeduser.equals(null)) {
				 List<User> users = null;
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value() ,"This logged user ID is not found",users);
				 logger.info("************************ getAllUsersOfUser ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			User user=userRepository.findOne(userId);
			if(user.equals(null)) {
				 List<User> users = null;
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value() ,"This user is not found",users);
				 logger.info("************************ getAllUsersOfUser ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if(Loggeduser.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get user list",null);
						 logger.info("************************ getAllUses ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getAccountType().equals(4)) {
					 Set<User> parentClients = user.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						
						 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get users of this user",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.status(404).body(getObjectResponse);
					 }else {
						 User parentClient = new User() ;
						 for(User object : parentClients) {
							 parentClient = object;
						 }
						 userId=parentClient.getId();
						 
					 }
				}
				else {
					 List<User> parents=getAllParentsOfuser(user,user.getAccountType());
					 boolean isParent = false; 
					 User parentClient = new User();
					 for(User object : parents) {
						 parentClient = object;
						 if(loggedUserId.equals(parentClient.getId())) {
							isParent =true;
							break;
						 }
					 }
					 if(userId.equals(loggedUserId)) {
						 isParent =true;
					 }
					 if(isParent == false) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to edit this role.",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);
					 }
				}
				
				List<User> users = new ArrayList<User>();
				Integer size = 0;
				if(active  == 0) {
					
					if(exportData.equals("exportData")) {
						
						users = userRepository.getInactiveUsersOfUserExport(loggedUserId,userId,search);
						size=userRepository.getInactiveUsersOfUserSize(userId);
					}
					else {
						users = userRepository.getInactiveUsersOfUser(loggedUserId,userId,offset,search);
						size=userRepository.getInactiveUsersOfUserSize(userId);
					}
					
				}
				else if(active == 2) {
					if(exportData.equals("exportData")) {
						 
						users = userRepository.getAllUsersOfUserExport(loggedUserId,userId,search);
					}
					else {
						users = userRepository.getAllUsersOfUser(loggedUserId,userId,offset,search);
						size=userRepository.getAllUsersOfUserSize(userId); 
					}
					
				}
				else {
					if(exportData.equals("exportData")) {
						
						users = userRepository.getUsersOfUserExport(loggedUserId,userId,search);

					}
					else {
						users = userRepository.getUsersOfUser(loggedUserId,userId,offset,search);
						size=userRepository.getUsersOfUserSize(userId);
					}
				    
					
				}
				
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users,size);
				logger.info("************************ getAllUsersOfUser ENDED ***************************");

				return  ResponseEntity.ok().body(getObjectResponse);
			}
			
		}
		 
	}


	
	/**
	 * create user with data in body
	 */
	@Override
	public ResponseEntity<?> createUser(String TOKEN,User user,Long userId) {
		
		
		logger.info("************************createUser STARTED ***************************");
		
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);
		
		user.setCreate_date(nowTime);
		
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
			

	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User creater = findById(userId);
		if(creater == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This  creater user is not Found",null);
    	logger.info("************************createUser ENDED ***************************");

	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(creater.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "USER", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create user",null);
				 logger.info("************************ getAllUses ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(user.getId() != null && user.getId() != 0) {
			
			String message= "create doesn't accept id";

	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,null);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.ok().body(getObjectResponse);
		}
		if(user.getIsCompany() == 1) {
			if(user.getEmail() == null || user.getEmail() == "" || user.getPassword() == null
					|| user.getPassword() == "" || user.getName() == null || user.getName() == "" 
					|| user.getIdentity_num() == null || user.getIdentity_num() == ""
					|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
					|| user.getCompany_phone() == null || user.getCompany_phone() == ""
					|| user.getManager_phone() == null || user.getManager_phone() == ""
					|| user.getManager_mobile() == null || user.getManager_mobile() == "" 
					|| user.getAccountType() == null || user.getAccountType() == 0 ) {
					List<User> users = null;
					String message= "attributes [email , password, name, identityNumber ,commercialNumber,"
							+ "companyPhone ,Managerphone, ManagerMobile ,accountType] are required";

			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				  }
			
		}
		if(user.getIsCompany() == 0) {
			if(user.getEmail() == null || user.getEmail() == "" || user.getPassword() == null
					|| user.getPassword() == "" || user.getName() == null || user.getName() == "" 
					|| user.getIdentity_num() == null || user.getIdentity_num() == ""
					|| user.getDateOfBirth() == null ||user.getDateOfBirth() == ""
					|| user.getCompany_phone() == null || user.getCompany_phone() == ""
					|| user.getAccountType() == null || user.getAccountType() == 0 ) {
					List<User> users = null;
					String message= "attributes [email , password, name, identityNumber ,"
							+ "companyPhone ,accountType] are required";

			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				  }
			
		}
		
		if(user.getAccountType() == 1) {
		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Can't Create account type 1",null);
    	logger.info("************************createUser ENDED ***************************");
    	return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(creater.getAccountType() == 4 && user.getAccountType() != 4) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this account can't create account of type admin , vendor or client",null);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(creater.getAccountType() == 3 && user.getAccountType() != 4) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this account can't create account of type admin , vendor or client",null);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(creater.getAccountType() ==2 && (user.getAccountType() ==2 || user.getAccountType() == 1)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this account can't create account of type admin or vendor",null);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(creater.getAccountType() == 4) {
			Set<User> parentClients = creater.getUsersOfUser();
			if(parentClients.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user account cannot create user as it has no parent",null);
		    	logger.info("************************createUser ENDED ***************************");
		    	return ResponseEntity.badRequest().body(getObjectResponse);
			}
			User parentClient = null;
			
			for(User object : parentClients) {
				parentClient = object;
			}
			return saveUser(parentClient.getId(),user);
			
		}
		if(creater.getAccountType() == 3) {
		
			return saveUser(creater.getId(), user);
		 
		}
		if(creater.getAccountType() == 2) {
			
			if(user.getAccountType() == 3) {
				return saveUser(creater.getId(), user);
			}
			if(user.getAccountType() == 4){
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				JSONObject parentUsers = new JSONObject(user.getParents());
				
		    	if( !parentUsers.has("clientId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select client to be parent of this user",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
		    	if(parentUsers.get("clientId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "client ID is Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
		    	if(!checkIfParentOrNot(userId,parentUsers.getLong("clientId"),creater.getAccountType(),3)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user cannot assign user to this client",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
		    	return saveUser(parentUsers.getLong("clientId"), user);
			}
		}
		if(creater.getAccountType() == 1) {
			if(user.getAccountType() == 2) {
				return saveUser(creater.getId(), user);
			}
			if(user.getAccountType() == 3) {
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				JSONObject parentUsers = new JSONObject(user.getParents());
				
		    	if( !parentUsers.has("vendorId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select vendor to be parent of this user",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
		    	if(parentUsers.get("vendorId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "vendor ID is Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
		    	if(!checkIfParentOrNot(userId,parentUsers.getLong("vendorId"),creater.getAccountType(),2)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this client cannot assign user to this vendor",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
		    	return saveUser(parentUsers.getLong("vendorId"), user);
			}
			if(user.getAccountType() == 4) {
				
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				JSONObject parentUsers = new JSONObject(user.getParents());
				if( !parentUsers.has("vendorId") || !parentUsers.has("clientId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select vendor  and client to be parent of this user",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
				if(parentUsers.get("vendorId").equals(null) || parentUsers.get("clientId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "vendor ID and client ID are Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
				if(!checkIfParentOrNot(userId,parentUsers.getLong("vendorId"),creater.getAccountType(),2)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this admin cannot assign user to this vendor",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
				if(!checkIfParentOrNot(parentUsers.getLong("vendorId"),parentUsers.getLong("clientId"),2,3)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this vendor cannot assign user to this client",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
				return saveUser(parentUsers.getLong("clientId"), user);
			}
		}
		
		return null;
	}
	
	/**
	 * edit user with data in body and id is mandatory
	 */
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
    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
    		logger.info("************************editUser ENDED ***************************");
    		return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser =  findById(userId);
		if(loggedUser == null) {
			 
		   	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user ID is not Found",null);
		   	logger.info("************************editUser ENDED ***************************");
		   	return ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "USER", "edit")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit user",null);
					 logger.info("************************ editUSER ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
		}
		 
			if(user.getIsCompany() == 1) {
				if(user.getId() == null || user.getId() == 0 || user.getEmail() == null || user.getEmail() == "" 
						|| user.getName() == null || user.getName() == "" 
						|| user.getIdentity_num() == null || user.getIdentity_num() == ""
						|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
						|| user.getCompany_phone() == null || user.getCompany_phone() == ""
						|| user.getManager_phone() == null || user.getManager_phone() == ""
						|| user.getManager_mobile() == null || user.getManager_mobile() == "" 
						|| user.getAccountType() == null || user.getAccountType() == 0 ) {
						List<User> users = null;
						String message= "attributes [id , email , password, name, identityNumber ,commercialNumber,"
								+ "companyPhone ,Managerphone, ManagerMobile ,accountType] are required";

				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.badRequest().body(getObjectResponse);
					  }
				
			}
			if(user.getIsCompany() == 0) {
				if(user.getId() == null || user.getId() == 0 || user.getEmail() == null || user.getEmail() == ""
						|| user.getName() == null || user.getName() == "" 
						|| user.getIdentity_num() == null || user.getIdentity_num() == ""
						|| user.getDateOfBirth() == null ||user.getDateOfBirth() == ""
						|| user.getCompany_phone() == null || user.getCompany_phone() == ""
						|| user.getAccountType() == null || user.getAccountType() == 0 ) {
						List<User> users = null;
						String message= "attributes [id ,email , password, name, identityNumber ,"
								+ "companyPhone, dateOfBirth ,accountType] are required";

				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.badRequest().body(getObjectResponse);
					  }
				
			}
		 
		if(user.getPassword()!= null) {
		
			String message= "you are not allowed to edit password";

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,null);
			logger.info("************************createUser ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User oldUser = findById(user.getId());
		if(oldUser == null) {
			

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not found",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(oldUser.getAccountType() ==1 ) {

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "admin acouunt is not editable",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType() == 4 && oldUser.getAccountType() !=4) {

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User can't edit admin,vendor or client users",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
			
		}
		if(loggedUser.getAccountType() == 3 && oldUser.getAccountType() !=4) {

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User can't edit admin,vendor or client users",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType() == 2 && (oldUser.getAccountType() !=4 && oldUser.getAccountType() !=3)) {
			

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User can't edit admin or vendor users",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!user.getAccountType().equals(oldUser.getAccountType())) {

	    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "You cannot edit user account type ",null);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType() == 4) {
			
			Set<User> loggedParents = loggedUser.getUsersOfUser();
			if(loggedParents.isEmpty()) {

		    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you and this user not belong to the same client ",null);
		    	return ResponseEntity.status(404).body(getObjectResponse);
			}
			User loggedParent = null;
			
			for(User object:loggedParents) {
				loggedParent = object;
			}
			if(!checkIfParentOrNot(loggedParent.getId(),user.getId(),3,4)){

		    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you and this user donot belong to the same client ",null);
		    	return ResponseEntity.status(404).body(getObjectResponse);
			}
			String password = oldUser.getPassword();
			user.setPassword(password);
	    	return saveUser(loggedParent.getId(),user);
			
		}
		if(loggedUser.getAccountType() == 3) {
			if(!checkIfParentOrNot(loggedUser.getId(),user.getId(),3,4)){

		    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent client of this user ",null);
		    	return ResponseEntity.status(404).body(getObjectResponse);
			}
			String password = oldUser.getPassword();
			user.setPassword(password);
	    	return saveUser(loggedUser.getId(),user);
			
		}
		if(loggedUser.getAccountType() == 2) {
			if(oldUser.getAccountType() == 3) {
				if(!checkIfParentOrNot(loggedUser.getId(),user.getId(),2,3)){

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent vendor of this client ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				String password = oldUser.getPassword();
				user.setPassword(password);
		    	return saveUser(loggedUser.getId(),user);
			}
			if(oldUser.getAccountType() == 4) {
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				JSONObject parentUsers = new JSONObject(user.getParents());
                if( !parentUsers.has("clientId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select  client to be parent of this user",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
				if( parentUsers.get("clientId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " client ID is Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
				if(!checkIfParentOrNot(loggedUser.getId(),parentUsers.getLong("clientId"),2,3)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this vendor cannot assign user to this client",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}

				List<User>  parents = getAllParentsOfuser(oldUser,4);
				if(parents.isEmpty()) {

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				boolean isParent = false;
				for(User object: parents) {
					if(loggedUser.getId().equals(object.getId())) {
						isParent = true;
					}
				}
				if(isParent) {
					String password = oldUser.getPassword();
					user.setPassword(password);
			    	return saveUser(parentUsers.getLong("clientId"),user);
				}else {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
							
			}
		}
		if(loggedUser.getAccountType() == 1) {
			if(oldUser.getAccountType() == 2) {
				if(!checkIfParentOrNot(loggedUser.getId(),user.getId(),1,2)){

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this vendor ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				String password = oldUser.getPassword();
				user.setPassword(password);
		    	return saveUser(loggedUser.getId(),user);
				
			}
			if(oldUser.getAccountType() == 3) {
				
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				JSONObject parentUsers = new JSONObject(user.getParents());
                if( !parentUsers.has("vendorId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select  vendor to be parent of this client",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
				if( parentUsers.get("vendorId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " vendor ID is Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
				if(!checkIfParentOrNot(loggedUser.getId(),parentUsers.getLong("vendorId"),1,2)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not the parent of this vendor ",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
				List<User>  parents = getAllParentsOfuser(oldUser,3);
				if(parents.isEmpty()) {

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				boolean isParent = false;
				for(User object: parents) {
					if(loggedUser.getId().equals(object.getId())) {
						isParent = true;
					}
				}

				if(isParent) {
					String password = oldUser.getPassword();
					user.setPassword(password);
			    	return saveUser(parentUsers.getLong("vendorId"),user);
				}else {

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				
			}
			if(oldUser.getAccountType() == 4) {
				if(user.getParents() == null || user.getParents() == "") {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "parents is required in this case",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
				}
				
				JSONObject parentUsers = new JSONObject(user.getParents());
				if( !parentUsers.has("vendorId") || !parentUsers.has("clientId")) {
		    		
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you must select vendor  and client to be parent of this user",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
			    	
		    	}
				if(parentUsers.get("vendorId").equals(null) || parentUsers.get("clientId").equals(null)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "vendor ID and client ID are Required",null);
				    logger.info("************************createUser ENDED ***************************");
				    return ResponseEntity.badRequest().body(getObjectResponse);
		    	}
				if(!checkIfParentOrNot(loggedUser.getId(),parentUsers.getLong("vendorId"),1,2)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this admin cannot assign user to this vendor",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}
				if(!checkIfParentOrNot(parentUsers.getLong("vendorId"),parentUsers.getLong("clientId"),2,3)) {
		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this vendor cannot assign user to this client",null);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.badRequest().body(getObjectResponse);
	    		}

				List<User>  parents = getAllParentsOfuser(oldUser,4);
				if(parents.isEmpty()) {

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
				boolean isParent = false;
				for(User object: parents) {
					if(loggedUser.getId().equals(object.getId())) {
						isParent = true;
					}
				}
				if(isParent) {
					String password = oldUser.getPassword();
					user.setPassword(password);
			    	return saveUser(parentUsers.getLong("clientId"),user);
				}else {

			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not the parent admin of this user ",null);
			    	return ResponseEntity.status(404).body(getObjectResponse);
				}
			}
		}
		return null;
	}

	
	
	 public static String getMd5(String input) 
	    { 
	        try { 
	  

	            MessageDigest md = MessageDigest.getInstance("MD5"); 
	  

	            byte[] messageDigest = md.digest(input.getBytes()); 
	  

	            BigInteger no = new BigInteger(1, messageDigest); 
	  

	            String hashtext = no.toString(16); 
	            while (hashtext.length() < 32) { 
	                hashtext = "0" + hashtext; 
	            } 
	            return hashtext; 
	        }  
	  

	        catch (NoSuchAlgorithmException e) { 
	            throw new RuntimeException(e); 
	        } 
	    }

	@Override
	public List<Integer> checkUserDuplication(User user) {
		 // TODO Auto-generated method stub
		 String email = user.getEmail();
		 String identityNum = user.getIdentity_num();
		 String commercialNum = user.getCommercial_num();
		 String	companyPhone = user.getCompany_phone();
		 String managerPhone = user.getManager_phone();
		 String managerMobile = user.getManager_mobile();
		 String phone = user.getPhone();
		 List<User>userDuolicationList = new ArrayList<User>();
		 if(user.getIsCompany() == 1) {
			 userDuolicationList = userRepository.checkUserDuplication(email, identityNum, commercialNum, companyPhone, managerPhone, managerMobile,phone);
		 }
		 if(user.getIsCompany() == 0) {
			 userDuolicationList = userRepository.checkUserDuplicationIndvidual(email, identityNum, companyPhone,phone);

		 }
		 List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (User matchedUser : userDuolicationList) 
		    { 
		    	
		    	if(matchedUser.getId() != user.getId() ) {
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



	/**
	 * get user roles to view
	 */
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
						if(parentOfChild.getId().equals(parentId)) {
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
			

			getObjectResponse = new GetObjectResponse(101, "Duplication Erorr",duplictionList);
			logger.info("************************createUser ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else
		{
			user.setIs_deleted(null);
			user.setDelete_date(null);
			
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

	/**
	 * delete user by deleteUserId
	 */
	@Override
	public ResponseEntity<?> deleteUser(String TOKEN, Long userId, Long deleteUserId) {
		// TODO Auto-generated method stub
		

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

		User loggedUser = findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "USER", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete user",null);
				 logger.info("************************ editUSER ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		

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

 					Calendar cal = Calendar.getInstance();
	    			int day = cal.get(Calendar.DATE);
	    			int month = cal.get(Calendar.MONTH) + 1;
	    			int year = cal.get(Calendar.YEAR);
	    			String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	   				deletedUser.setDelete_date(date);
	   				userRepository.save(deletedUser);

	   				

	   				 if(deletedUser.getAccountType() == 4) {
	   				   TokenSecurity.getInstance().removeActiveUserById(deleteUserId);
	   				 }else {
	   				  TokenSecurity.getInstance().removeActiveUserById(deleteUserId);

	   				 resetChildernArray();
	   				  List<User>children = getAllChildernOfUser(deleteUserId);
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

	/**
	 * active user with activeUserId 
	 */
	@Override
	public ResponseEntity<?> activeUser(String TOKEN, Long userId, Long activeUserId) {
		// TODO Auto-generated method stub
		
		logger.info("************************activeUser STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0 || activeUserId == 0) {
			
		    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
		    logger.info("************************activeUser ENDED ***************************");
		    return ResponseEntity.badRequest().body(getObjectResponse);
		}

		User loggedUser = findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "USER", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete user",null);
				 logger.info("************************ activeUser ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		User checkdDeleted = findById(activeUserId);
		if(checkdDeleted != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to active is already activated",null);
		    return ResponseEntity.status(404).body(getObjectResponse);
		}else {
			User deletedUser = userRepository.getDeletedUser(activeUserId);


			List<User>parents = getAllParentsOfuser(deletedUser,deletedUser.getAccountType());

			if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this user",null);
			    logger.info("************************activeUser ENDED ***************************");
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
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this user",null);
					    logger.info("************************activeUser ENDED ***************************");
					    return ResponseEntity.badRequest().body(getObjectResponse);
				 }else {
					
	   				deletedUser.setDelete_date(null);
	   				userRepository.save(deletedUser);	   				
	    			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(),"success",null);
	    			logger.info("************************activeUser ENDED ***************************");
	    			return ResponseEntity.ok().body(getObjectResponse);
				 }
				 
			} 
		}
		
	}
	@Override
	public List<User> getAllChildernOfUser(Long userId) {
		// TODO Auto-generated method stub
		List<User> childernUsers = new ArrayList<>();
		
		
		User user = userRepository.findOne(userId);
		if(user != null) {
			if(user.getAccountType() == 1) {
				List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
						
						if(object1.getAccountType() == 2) {
							List<User>childrenReturned2 = userRepository.getChildrenOfUser(object1.getId());
							if(!childrenReturned2.isEmpty()) {
								for(User object2 : childrenReturned2) {
									childernUsers.add(object2);
									
									
									
									if(object2.getAccountType() == 3) {
										List<User>childrenReturned3 = userRepository.getChildrenOfUser(object2.getId());
										if(!childrenReturned3.isEmpty()) {
											for(User object3 : childrenReturned3) {
												childernUsers.add(object3);
											
											}

										}

									}
									

								}
							}

						
						}
					}
				}
			}
			if(user.getAccountType() == 2) {
				List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
						
						if(object1.getAccountType() == 3) {
							List<User>childrenReturned2 = userRepository.getChildrenOfUser(object1.getId());
							if(!childrenReturned2.isEmpty()) {
								for(User object2 : childrenReturned2) {
									childernUsers.add(object2);
									
									
									

								}
							}

						
						}
					}
				}
			}
			if(user.getAccountType() == 3) {
				List<User>childrenReturned1 = userRepository.getChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
					}
				}
			}
		}
		
		return childernUsers;
	}

	@Override
	public void resetChildernArray() {
		// TODO Auto-generated method stub
		List<User> childernUsers = new ArrayList<>();
		childernUsers = new ArrayList<>();
		
	}
	

	@Override	
	public List<User> getActiveAndInactiveChildern(Long userId) {

		// TODO Auto-generated method stub
       List<User> childernUsers = new ArrayList<>();
		
		
		User user = userRepository.findOne(userId);
		if(user != null) {
			if(user.getAccountType() == 1) {
				List<User>childrenReturned1 = userRepository.getActiveAndInactiveChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
						
						if(object1.getAccountType() == 2) {
							List<User>childrenReturned2 = userRepository.getActiveAndInactiveChildrenOfUser(object1.getId());
							if(!childrenReturned2.isEmpty()) {
								for(User object2 : childrenReturned2) {
									childernUsers.add(object2);
									
									
									
									if(object2.getAccountType() == 3) {
										List<User>childrenReturned3 = userRepository.getActiveAndInactiveChildrenOfUser(object2.getId());
										if(!childrenReturned3.isEmpty()) {
											for(User object3 : childrenReturned3) {
												childernUsers.add(object3);
											
											}

										}

									}
									

								}
							}

						
						}
					}
				}
			}
			if(user.getAccountType() == 2) {
				List<User>childrenReturned1 = userRepository.getActiveAndInactiveChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
						
						if(object1.getAccountType() == 3) {
							List<User>childrenReturned2 = userRepository.getActiveAndInactiveChildrenOfUser(object1.getId());
							if(!childrenReturned2.isEmpty()) {
								for(User object2 : childrenReturned2) {
									childernUsers.add(object2);
									
									
									

								}
							}

						
						}
					}
				}
			}
			if(user.getAccountType() == 3) {
				List<User>childrenReturned1 = userRepository.getActiveAndInactiveChildrenOfUser(userId);
				if(!childrenReturned1.isEmpty()) {
					for(User object1 : childrenReturned1) {
						childernUsers.add(object1);
						
					}
				}
			}
		}
		
		return childernUsers;
	}
	
	/**
	 * get childs of user to select in reports or another
	 */
	@Override
	public  ResponseEntity<?> getUserSelectWithChild(String TOKEN,Long userId) {

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
	    		if(user.getAccountType().equals(4)) {
		   			 Set<User> parentClients = user.getUsersOfUser();
		   			 if(parentClients.isEmpty()) {
		   				
		   				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get users of this user",null);
		   				 logger.info("************************ getAllUserDevices ENDED ***************************");
		   				return  ResponseEntity.status(404).body(getObjectResponse);
		   			 }else {
		   				 User parentClient = new User() ;
		   				 for(User object : parentClients) {
		   					 parentClient = object;
		   				 }
		   				 
		   				userId = parentClient.getId();
		   				user = findById(parentClient.getId());
		   			 }
	   		    }
	    		if(user.getDelete_date() == null) {
	    			List<User>childernUsers = getActiveAndInactiveChildern(userId);
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
	    			
	    			
	    			users = userRepository.getUserSelectWithChild(usersIds);
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
	
	/**
	 * get vendor select to chose 
	 */
	@Override
	public  ResponseEntity<?> getVendorSelect(String TOKEN,Long userId) {

		logger.info("************************ getVendorSelect STARTED ***************************");
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
	    			if(user.getAccountType() == 1) {
	    				users = userRepository.getVendorSelect(userId);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",users);
						logger.info("************************ getDeviceSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    			}
	    			else {
	    				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is not Account type 1 to get his own vendors",users);
	    				 return  ResponseEntity.badRequest().body(getObjectResponse);
	    			}
	    			

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
	/**
	 * get client list to chose
	 */
	@Override
	public  ResponseEntity<?> getClientSelect(String TOKEN,Long vendorId) {

		logger.info("************************ getClientSelect STARTED ***************************");
		List<UserSelect> users = new ArrayList<UserSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(vendorId != 0) {
	    	User user = findById(vendorId);
	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			if(user.getAccountType()==2) {
	    				users = userRepository.getClientSelect(vendorId);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",users);
						logger.info("************************ getClientSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    			}
	    			else {
	    				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Vendor ID is not Account type 2 to get his own clients",users);
	    				return  ResponseEntity.badRequest().body(getObjectResponse);
	    			}

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Vendor ID is not found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Vendor ID is not found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Vendor ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
		

	}

	/**
	 * get data of user select
	 */
	@Override
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
