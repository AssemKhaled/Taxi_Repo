package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;


/**
 * services functionality related to roles
 * @author fuinco
 *
 */
@Component
@Service
public class UserRoleServiceImpl extends RestServiceController implements UserRoleService {

	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	UserServiceImpl userService;
	
	
	@Autowired
	private UserRoleService userRoleService;
	
	
	@Autowired
	UserRepository userRepository;
	
	
	@Autowired
	PermissionService permissionService;
	
	GetObjectResponse getObjectResponse;
	
	
	/**
	 * create role with data in body
	 */
	@Override
	public ResponseEntity<?> createRole(String TOKEN,UserRole role,Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		 if(userId.equals(0)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser Id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		 User loggedUser = userService.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "ROLE", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(role.getId()!=null || role.getName() == null || role.getName() == ""
				||role.getPermissions() == null || role.getPermissions() == "") {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Only name and permissions are required to create Role ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		 User parentClient = new User();
		
		if(loggedUser.getAccountType().equals(4)) {
			 Long uId=(long) 0;
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to create this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 for(User object : parents) {
					 parentClient = object;
					 uId=parentClient.getId();
					 break;
				 }
				 
			 }
			role.setUserId(uId);

		}
		else {
			role.setUserId(userId);
			parentClient = loggedUser;
		}
		
		List<UserRole> roles = userRoleRepository.checkDublicateAdd(parentClient.getId(),role.getName());
		if(!roles.isEmpty()) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This Role Name was added before you can Edit or Delete it only",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		String ParentRole="";
		String childRole="";

		if(!loggedUser.getAccountType().equals(1)) {
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to create this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
			 }
			 if(!parentClient.getAccountType().equals(1)) {
				 UserRole parentRoles = userRoleRepository.findOne(parentClient.getRoleId());
				 if(parentRoles == null) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to create role  as your direct parent not have roles yet.",null);
					 return  ResponseEntity.badRequest().body(getObjectResponse);
					 
				 }

				 ParentRole=parentRoles.getPermissions();
				 childRole=role.getPermissions();
				 boolean check= compareRoles(ParentRole, childRole);

				 if(!check) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to create role has permission more than your direct parent.",null);
					 return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			
			 
		}
		
		userRoleRepository.save(role);
		
	 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "role added successfully",null);
	 return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * edit role in body with id is mandatory
	 */
	@Override
	public ResponseEntity<?> editRole(String TOKEN,UserRole role,Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "ROLE", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(role.getId() == null || role.getId() == 0 || role.getName() == null || role.getName() == ""
				||role.getPermissions() == null || role.getPermissions() == "") {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "id, name and permissions are required to add Role ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			
		}
		
		UserRole userRole = userRoleRepository.findOne(role.getId());
		if(userRole == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "role is not found ",null);
		    return  ResponseEntity.status(404).body(getObjectResponse); 
		}
		else {
			if(userRole.getDelete_date() != null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "role is not found ",null);
			    return  ResponseEntity.status(404).body(getObjectResponse); 
			}
		}
		Long createdByUserId=userRole.getUserId();
		User createdByUser = userService.findById(createdByUserId);
		 User parentClient = new User();

		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false;

				 for(User object : parents) {
					 parentClient = object;
					 break;
					 
				 }
				 if(createdByUserId.equals(parentClient.getId())) {
				 		isParent =true;
				 }
				if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				 
			 }
			 
		}
		else {
			
			parentClient = loggedUser;
		}
		if(loggedUser.getAccountType().equals(3)) {
			if(!userId.equals(createdByUserId)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 3 and this role not created by yourself not allow to edit.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			 
		}
		if(loggedUser.getAccountType().equals(2)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(userId.equals(createdByUserId)) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(userId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;

					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 2 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(loggedUser.getAccountType().equals(1)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(userId.equals(createdByUserId)) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(userId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;
					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 1 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		UserRole checkRole = findById(role.getId());
		if(checkRole == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Role isn't found to edit ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
	    List<User>usersAssignedByRoleId = userRepository.getUsersAssignedByRoleId(role.getId());
	    if(usersAssignedByRoleId != null) {
	    	for(int i=0;i<usersAssignedByRoleId.size();i++) {
	    		List<UserRole> ownRole = userRoleRepository.getAllRolesCreatedByUserToCompare(usersAssignedByRoleId.get(i).getId());
	    		if(!ownRole.isEmpty()) {
		    		UserRole child= new UserRole();
		    		for(UserRole object : ownRole) {
		    			child = object;
		    			String ParentRole="";
			    		String childRole="";
			    		ParentRole=role.getPermissions();
						childRole=child.getPermissions();
						String check= editCompareRoles(ParentRole, childRole);
						if(check != null) {
							Boolean removedUSER = TokenSecurity.getInstance().removeActiveUserById(usersAssignedByRoleId.get(i).getId());
							child.setPermissions(check);
							userRoleRepository.save(child);
						}
					}

	    		}
	    		
	    		userService.resetChildernArray();
			    List<User>childs = userService.getAllChildernOfUser(usersAssignedByRoleId.get(i).getId());
				if(!childs.isEmpty()) {
		    		User childUser= new User();
					for(User object : childs) {
						childUser = object;
						List<UserRole> ownRoleOf = userRoleRepository.getAllRolesCreatedByUserToCompare(childUser.getId());
			    		if(!ownRole.isEmpty()) {
				    		UserRole childComp= new UserRole();
				    		for(UserRole object1 : ownRoleOf) {
				    			childComp = object1;
				    			String ParentRole="";
					    		String childRole="";
					    		ParentRole=role.getPermissions();
								childRole=childComp.getPermissions();
								String check= editCompareRoles(ParentRole, childRole);
								if(check != null) {
									Boolean removedCHILD = TokenSecurity.getInstance().removeActiveUserById(childUser.getId());
									childComp.setPermissions(check);
									userRoleRepository.save(childComp);
								}
							}

			    		}
					}
				}
	    		
	    	}
	    	
	    }
		
		
		
		List<UserRole> roles = userRoleRepository.checkDublicateAdd(role.getUserId(),role.getName());
		if(!roles.isEmpty()) {
			if(roles.get(0).getId() != role.getId()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This Role Name was added before you can Edit or Delete it only",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		role.setUserId(createdByUserId);
		userRoleRepository.save(role);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "role updated successfully",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	@Override
	public UserRole findById(Long Id) {
		// TODO Auto-generated method stub
		 UserRole role = userRoleRepository.findOne(Id);
		 if(role == null) {
			 return null;
		 }
		 else {
			 if(role.getDelete_date() != null) {
				 return null;
			 }else {
				 return role;
			 }
		 }	
	}
	
	/**
	 * delete role by id
	 */
	@Override
	public ResponseEntity<?> deleteRole(String TOKEN,Long roleId,Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userService.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "ROLE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No roleId  to delete ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		Long createdByUserId=role.getUserId();
		User createdByUser = userService.findById(createdByUserId);
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;
				 boolean isParent = false;

				 for(User object : parents) {
					 parentClient = object;
					 break;
					 
				 }
				 if(createdByUserId.equals(parentClient.getId())) {
				 		isParent =true;
				 }
				if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				 
			 }
			 
		}
		if(loggedUser.getAccountType().equals(3)) {
			if(!userId.equals(createdByUserId)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 3 and this role not created by yourself not allow to edit.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			 
		}
		if(loggedUser.getAccountType().equals(2)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(userId==createdByUserId) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(userId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;

					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 2 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(loggedUser.getAccountType().equals(1)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(userId.equals(createdByUserId)) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(userId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;
					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 1 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 
		 
		List<User>usersAssignedByRoleId = userRepository.getUsersAssignedByRoleId(role.getId());
	    if(usersAssignedByRoleId != null) {

	    	for(int i=0;i<usersAssignedByRoleId.size();i++) {

	    		userService.resetChildernArray();
			    List<User>childs = userService.getAllChildernOfUser(usersAssignedByRoleId.get(i).getId());
				if(!childs.isEmpty()) {
		    		User childUser= new User();
					for(User object : childs) {
						childUser = object;
						List<UserRole> ownRoleOf = userRoleRepository.getAllRolesCreatedByUserToCompare(childUser.getId());
			    		if(!ownRoleOf.isEmpty()) {

				    		UserRole childComp= new UserRole();
				    		for(UserRole object1 : ownRoleOf) {
				    			childComp = object1;
				    			Calendar cal = Calendar.getInstance();
				    			int day = cal.get(Calendar.DATE);
				    		    int month = cal.get(Calendar.MONTH) + 1;
				    		    int year = cal.get(Calendar.YEAR);
				    		    String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
				    		    
				    		    List<User>usersChildsAssignedByRoleId = userRepository.getUsersAssignedByRoleId(childComp.getId());
				    			User childUserASSigned= new User();
								for(User objectChild : usersChildsAssignedByRoleId) 
								{
									childUserASSigned=objectChild;
									Boolean removedCHILDYY = TokenSecurity.getInstance().removeActiveUserById(childUserASSigned.getId());
									childUserASSigned.setRoleId(null);
						    		userRepository.save(childUserASSigned);
								}
				    		    
				    		    
				    		    childComp.setDelete_date(date);
								Boolean removedCHILD = TokenSecurity.getInstance().removeActiveUserById(childUser.getId());
				    		    userRoleRepository.save(childComp);
				    			
				    		    
				    		    
							}

			    		}
			    		childUser.setRoleId(null);
			    		userRepository.save(childUser);
					}
				}
	    		
	    		
	    		
	    		List<UserRole> ownRole = userRoleRepository.getAllRolesCreatedByUserToCompare(usersAssignedByRoleId.get(i).getId());
	    		if(!ownRole.isEmpty()) {
		    		UserRole child= new UserRole();
		    		for(UserRole object : ownRole) {
		    			child = object;
		    			Calendar cal = Calendar.getInstance();
		    			int day = cal.get(Calendar.DATE);
		    		    int month = cal.get(Calendar.MONTH) + 1;
		    		    int year = cal.get(Calendar.YEAR);
		    		    String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
		    		    
		    		    List<User>usersChildsAssignedByRoleId = userRepository.getUsersAssignedByRoleId(child.getId());
		    			User childUserASSigned= new User();
						for(User objectChild : usersChildsAssignedByRoleId) 
						{
							childUserASSigned=objectChild;
							Boolean removedCHILDYYT = TokenSecurity.getInstance().removeActiveUserById(childUserASSigned.getId());
							childUserASSigned.setRoleId(null);
				    		userRepository.save(childUserASSigned);
						}
						
		    		    child.setDelete_date(date);
						Boolean removedUSER = TokenSecurity.getInstance().removeActiveUserById(usersAssignedByRoleId.get(i).getId());
		    		    userRoleRepository.save(child);
					}
		    		

	    		}
	    		

				usersAssignedByRoleId.get(i).setRoleId(null);
	   		    Boolean removeCretedBy = TokenSecurity.getInstance().removeActiveUserById(usersAssignedByRoleId.get(i).getId());
	    		userRepository.save(usersAssignedByRoleId.get(i));

	    		
	    	}
	    	
	    }
		
		 
		 Calendar cal = Calendar.getInstance();
		 int day = cal.get(Calendar.DATE);
	     int month = cal.get(Calendar.MONTH) + 1;
	     int year = cal.get(Calendar.YEAR);
	     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	     role.setDelete_date(date);

	     userRoleRepository.save(role);
	     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "role deleted successfully",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get role by id
	 */
	@Override
	public ResponseEntity<?> getRoleById(String TOKEN,Long roleId,Long loggedId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No roleId  to return ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(loggedId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		User loggedUser = userService.findById(loggedId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Long createdBy=role.getUserId();
		Boolean isParent=false;

		if(createdBy.equals(loggedId)) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 
				 }
				 
				userService.resetChildernArray();
				childs = userService.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userService.resetChildernArray();
			childs = userService.getAllChildernOfUser(loggedId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().equals(createdBy)) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get role",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
		List<UserRole> roles = new ArrayList<>();
		roles.add(role);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	public boolean compareRoles(String Parent,String Child) {
		JSONObject ParentRolePerm = new JSONObject(Parent);
    	JSONObject ChildRolePerm = new JSONObject(Child);
    	Boolean check=true;
    	if(ChildRolePerm.getJSONArray("permissions").length() > ParentRolePerm.getJSONArray("permissions").length()) {
    		check =false;
    	}
    	for(int i=0;i<ChildRolePerm.getJSONArray("permissions").length();i++) {
    		for(int j=0;j<ParentRolePerm.getJSONArray("permissions").length();j++) {
    			if(ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("name").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("name"))
    					&&
    					ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("id").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("id"))) {
    				check=true;
    				if(ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").length() > ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").length()) {
    		    		check =false;
    		    	}
    				else {
    					Iterator iterChild = ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").keys();
    					Iterator iterParent = ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").keys();
    					 while(iterChild.hasNext()){
    					   String keyChild = (String)iterChild.next();
    					   Boolean valueChild = ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").getBoolean(keyChild);
    					   if(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").has(keyChild)) { 
    						   if(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").getBoolean(keyChild) == false) {
    							   if(valueChild != false ) {
    						    		check =false;
    							   }    							  
    						   }
    						   
    					   }    							       						
    					   else {
    				    		check =false;
						   }

    					 }
    				}
    				break;
    			}
    			else {
    	    		check =false;
    				
    			}
    			
    		}
    		
    	}
    	return check;
		
	}
	public String editCompareRoles(String Parent,String Child) {
		JSONObject ParentRolePerm = new JSONObject(Parent);
    	JSONObject ChildRolePerm = new JSONObject(Child);
    	
    	for(int i=0;i<ChildRolePerm.getJSONArray("permissions").length();i++) {
    		for(int j=0;j<ParentRolePerm.getJSONArray("permissions").length();j++) {
    			if(ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("name").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("name"))
    					&&
    					ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("id").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("id"))) {
    				
    					Iterator iterChild = ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").keys();
    					Iterator iterParent = ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").keys();
    					 while(iterChild.hasNext()){
    					   String keyChild = (String)iterChild.next();
    					   Boolean valueChild = ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").getBoolean(keyChild);
    					   if(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").has(keyChild)) { 
    						   if(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").getBoolean(keyChild) == false) {
    							   if(valueChild != false ) {

    								   ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").put(keyChild, false);

    							   }    							  
    						   }
    						   
    					   }    							       						
    					   
    					 }
    				
    			}
    			
    			
    		}
    		
    	}
    	String returned=""; 
    	String childReturn=ChildRolePerm.toString();
    	if(childReturn != Child) {
    		returned= childReturn;

    	}
    	
    	return returned;
	}
	
	/**
	 * assign role to user from parent roles
	 */
	@Override
	public ResponseEntity<?> assignRoleToUser(String TOKEN,Long roleId, Long userId,Long loggedId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(loggedId)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign to your self",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(loggedId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedId, "ROLE", "assignToUser")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToUser role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(userId.equals(0) || roleId.equals(0)) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " roleId  and userId are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		User userOLDROLE = userService.findById(userId);

		if(roleId.equals(userOLDROLE.getRoleId())) {

			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigned successfully",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		
		
		Long createdByUserId=role.getUserId();
		User createdByUser = userService.findById(createdByUserId);
		if(createdByUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Creater of role is not found maybe deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		}
		
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userService.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;
				 boolean isParent = false;

				 for(User object : parents) {
					 parentClient = object;
					 break;
					 
				 }
				 if(createdByUserId.equals(parentClient.getId())) {
				 		isParent =true;
				 }
				if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				 
			 }
			 
		}
		if(loggedUser.getAccountType().equals(3)) {
			if(!loggedId.equals(createdByUserId)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 3 and this role not created by yourself not allow to edit.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			 
		}
		if(loggedUser.getAccountType().equals(2)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(loggedId.equals(createdByUserId)) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(loggedId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;

					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 2 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			User user = userService.findById(userId);
			if(user.getAccountType().equals(1)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not Allow to assign to Admin",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(user.getAccountType().equals(2)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not Allow to assign to Another Vendor",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(user.getAccountType().equals(3)) {
				if(!loggedId.equals(createdByUserId)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not Allow to assign to Client with role not your own",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
		 		}
			}
			if(user.getAccountType().equals(4)) {
				List<User> parents=userService.getAllParentsOfuser(user,user.getAccountType());
				if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 else {
					 User parentClient = new User() ;
					 boolean isParentt = false;

					 for(User object : parents) {
						 parentClient = object;
						 break;
						 
					 }
					 if(createdByUserId.equals(parentClient.getId())) {
						 isParentt =true;
					 }
					if(isParentt == false) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					 
				 }
			}

			
			
		}
		if(loggedUser.getAccountType().equals(1)) {
			User parentChilds = new User() ;
	 		boolean isParent =false;
	 		if(loggedId.equals(createdByUserId)) {
				isParent=true;
	 		}
	 		userService.resetChildernArray();
		    List<User>childs = userService.getAllChildernOfUser(loggedId);
			if(!childs.isEmpty()) {
				for(User object : childs) {
					parentChilds = object;
					if(parentChilds.getId().equals(createdByUserId)) {
						isParent=true;
					}
				}
			}
			if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are type 1 and not the creater or no created by childs you are not allow to edit",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			User user = userService.findById(userId);
			if(user.getAccountType().equals(1)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not Allow to assign to Another Admin",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(user.getAccountType().equals(2)) {
				if(!loggedId.equals(createdByUserId)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not Allow to assign to Vendor with role not your own",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
		 		}
			}
			if(user.getAccountType().equals(3)) {
				List<User> parents=userService.getAllParentsOfuser(user,user.getAccountType());
				if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 else {
					 User parentClient = new User() ;
					 boolean isParentt = false;

					 for(User object : parents) {
						 parentClient = object;
						 break;
						 
					 }
					 if(createdByUserId.equals(parentClient.getId())) {
						 isParentt =true;
					 }
					if(isParentt == false) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 3 and this role not created by direct parent not allow to edit.",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					 
				 }
			
			}
			if(user.getAccountType().equals(4)) {
				List<User> parents=userService.getAllParentsOfuser(user,user.getAccountType());
				if(parents.isEmpty()) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 else {
					 User parentClient = new User() ;
					 boolean isParentt = false;

					 for(User object : parents) {
						 parentClient = object;
						 break;
					 }

					 if(createdByUserId.equals(parentClient.getId())) {
						 isParentt =true;
					 }
					 
					if(isParentt == false) {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					 
				 }
			}

			
		}
		
		User user = userService.findById(userId);

		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		
		List<UserRole> ownRole = userRoleRepository.getAllRolesCreatedByUserToCompare(userId);
		if(ownRole != null) {
			 UserRole userRoles = new UserRole() ;
			 for(UserRole object : ownRole) {
				 userRoles=object;
				 List<User> childUsers = userRepository.getUsersAssignedByRoleId(userRoles.getId());
				 User userChilds = new User(); 
				 for(User objectR : childUsers) {
					 userChilds=objectR;
					 List<UserRole> ownRoleChild = userRoleRepository.getAllRolesCreatedByUserToCompare(userChilds.getId());
					 UserRole userChildsRo = new UserRole(); 
					 for(UserRole objectRol : ownRoleChild) {
						 userChildsRo=objectRol;
							
						 List<User> childUsersAssi = userRepository.getUsersAssignedByRoleId(userChildsRo.getId());
						 User userChildsAssi = new User(); 
						 for(User objectRAssi : childUsersAssi) {
							 userChildsAssi=objectRAssi;
							 List<UserRole> ownRoleChildChild = userRoleRepository.getAllRolesCreatedByUserToCompare(userChildsAssi.getId());
							 UserRole userChildsRoCh = new UserRole(); 
							 for(UserRole objectRolChil : ownRoleChildChild) {
								 userChildsRoCh=objectRolChil;
								 List<User> childUsersAssiCh = userRepository.getUsersAssignedByRoleId(userChildsRoCh.getId());
								 User userChildsAssiCh1 = new User(); 
								 for(User objectRAssiCh1: childUsersAssiCh) {
									 userChildsAssiCh1=objectRAssiCh1;
									 userChildsAssiCh1.setRoleId(null);
									 Boolean removedCHILD = TokenSecurity.getInstance().removeActiveUserById(userChildsAssiCh1.getId());
									 userRepository.save(userChildsAssiCh1);
								 }	 
								 
								 Calendar cal = Calendar.getInstance();
								 int day = cal.get(Calendar.DATE);
							     int month = cal.get(Calendar.MONTH) + 1;
							     int year = cal.get(Calendar.YEAR);
							     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
							     userChildsRoCh.setDelete_date(date);
							     userRoleRepository.save(userChildsRoCh);
								 
							 }	 
							 
							 userChildsAssi.setRoleId(null);
							 Boolean removedCHILD = TokenSecurity.getInstance().removeActiveUserById(userChildsAssi.getId());
							 userRepository.save(userChildsAssi);


						 }
						 
						 Calendar cal = Calendar.getInstance();
						 int day = cal.get(Calendar.DATE);
					     int month = cal.get(Calendar.MONTH) + 1;
					     int year = cal.get(Calendar.YEAR);
					     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
					     userChildsRo.setDelete_date(date);
					     userRoleRepository.save(userChildsRo);
					
					 }	 
					 userChilds.setRoleId(null);
					 Boolean removedCHILD = TokenSecurity.getInstance().removeActiveUserById(userChilds.getId());
					 userRepository.save(userChilds);
				 }
				 
				 Calendar cal = Calendar.getInstance();
				 int day = cal.get(Calendar.DATE);
			     int month = cal.get(Calendar.MONTH) + 1;
			     int year = cal.get(Calendar.YEAR);
			     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
			     userRoles.setDelete_date(date);
			     userRoleRepository.save(userRoles);
			 }	
		}
	
	
		
		

		
		
		 
		
		user.setRoleId(roleId);
		userRepository.save(user);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "assigned successfully",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get roles created by user limit 10
	 */
	@Override
	public ResponseEntity<?> getAllRolesCreatedByUser(String TOKEN,Long userId,int offset,String search, String exportData) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "ROLE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this loggedId doesnot has permission to get list role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		userService.resetChildernArray();
		List<User> childernUsers= new ArrayList<User>();
		 List<Long>usersIds= new ArrayList<>();

		if(user.getAccountType().equals(4)) {
			 Set<User> parents = user.getUsersOfUser();
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;
				 for(User object : parents) {

					 parentClient = object;
					 break;
				 }
				 userService.resetChildernArray();
				 childernUsers=userService.getActiveAndInactiveChildern(parentClient.getId());
				 usersIds.add(parentClient.getId());
				 
				 
			 }
			 
		}
		else {

			userService.resetChildernArray();
			childernUsers=userService.getActiveAndInactiveChildern(userId);
		}
		
		
		
		 if(childernUsers.isEmpty()) {

			 usersIds.add(userId);
		 }
		 else {

			 usersIds.add(userId);
			 for(User object : childernUsers) {

				 usersIds.add(object.getId());

			 }
		 }
		 List<UserRole> roles = new ArrayList<UserRole>();
		 Integer size = 0;
	     if(exportData.equals("exportData")) {
	    	 roles = userRoleRepository.getAllRolesCreatedByUserOffsetExport(usersIds,search);
	    	 
	     }
	     else{
	    	 roles = userRoleRepository.getAllRolesCreatedByUserOffset(usersIds,offset,search);
	    	 if(roles.size()>0) {
	 	 		size=userRoleRepository.getRolesSize(usersIds);

	    	 }
	     }

		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles,size);
		
		return ResponseEntity.ok().body(getObjectResponse);
	}


	@Override
	public Boolean checkUserHasPermission(Long userId, String module, String functionality) {
		
		
		// TODO Auto-generated method stub
		if(userId == 0 || module == "" || module == null || functionality == "" || functionality == null) {
			return false;
		}
		List<UserRole> roles = userRoleRepository.getUserRole(userId);
		if(roles.isEmpty()) {
			return false;
		}else {
			UserRole role = roles.get(0);
			
			JSONObject permissions = new JSONObject(role.getPermissions());
			 if(permissions.has("permissions")) {
				 JSONArray the_json_array = permissions.getJSONArray("permissions");

					for(Object object : the_json_array) {	

						JSONObject permissionObject = new JSONObject(object.toString());
						

						if( permissionObject.has("name")) {
							if(permissionObject.getString("name").equals(module)) {

								JSONObject serviceFunctionalities= permissionObject.getJSONObject("functionality");
								
								
								 if(serviceFunctionalities.has(functionality)) {
									 
									 if(serviceFunctionalities.getBoolean(functionality)) {
										 
										 return true;
									 }
								 }
							}
							
							 
						 }
						 
					}
				 return false;
			 
			 }else {
				 return false;
			 }
		}
		
		
		
		
	}
	
	/**
	 * get only role that has permission to this user to create one from it
	 */
	@Override
	public ResponseEntity<?> getRolePageContent(String TOKEN,Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}else {
			User loggedUser = userService.findById(userId);
			if(loggedUser == null) {
				
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
				 return  ResponseEntity.status(404).body(getObjectResponse);	
			}else {
				if(loggedUser.getAccountType() == 1) {
	
					List<Permission> permissions = permissionService.getPermissionsList();
					if(permissions.isEmpty()) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no permissions to add",null);
						 return  ResponseEntity.status(404).body(getObjectResponse);
					}else {
						Map permissionsList   = new HashMap<>();
						permissionsList.put("permissions", permissions);
						Map responseData = new HashMap<>();
						responseData.put("id",null);
						responseData.put("name",null);
						responseData.put("permissions", permissionsList);
						List<Map> response = new ArrayList<Map>();
						response.add(responseData);
						
						getObjectResponse =  new GetObjectResponse(HttpStatus.OK.value(), "sucecss",response);
						 return  ResponseEntity.ok().body(getObjectResponse);
					}
				}else {
					UserRole userRole = findById(loggedUser.getRoleId());
					if(userRole == null) { 
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user not has role to createnew one",null);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					}else {
						
						JSONObject myjson = new JSONObject(userRole.getPermissions());
						
						JSONArray the_json_array = myjson.getJSONArray("permissions");
						List<Permission> list = new ArrayList<Permission>();
						
						for(Object object : the_json_array) {	
							JSONObject permissionObject = new JSONObject(object.toString());
							Permission permission = new Permission();
							permission.setId((long) permissionObject.getInt("id"));
							permission.setName(permissionObject.getString("name"));
							
							permission.setFunctionality(permissionObject.getJSONObject("functionality").toString());
							
							list.add(permission);
						}
						Map permissions   = new HashMap<>();
						permissions.put("permissions", list);
						Map responseData = new HashMap<>();
						responseData.put("id",null);
						responseData.put("name",null);
						responseData.put("permissions", permissions);
						List<Map> response = new ArrayList<Map>();
						response.add(responseData);
						
						getObjectResponse =  new GetObjectResponse(HttpStatus.OK.value(), "sucecss",response);
						 return  ResponseEntity.ok().body(getObjectResponse);
						
					}
				}
			}
			
		}
	}
	
	/**
	 * get roles of parent to assign
	 */
	@Override
	public ResponseEntity<?> getUserParentRoles(String TOKEN, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}else {
			User user = userService.findById(userId);
			if(user == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user not found ",null);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if(user.getAccountType() == 1) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No parent for admin",null);
					 return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				Set<User> userParents = user.getUsersOfUser();
				User parent = null;
				for(User parentClient : userParents) {
					 parent = parentClient;
				}
				
				List<Long>usersIds= new ArrayList<>();
				
				usersIds.add(parent.getId());
				
				List<UserRole> roles = userRoleRepository.getAllRolesCreatedByUser(usersIds);
				List<UserRole> selectedRoles = userRoleRepository.getUserRole(userId);
	
				List<Map> data = new ArrayList<>();
			    Map obj = new HashMap();
			    
			    obj.put("selectedRoles", selectedRoles);
			    obj.put("roles", roles);
	
			    data.add(obj);
				
				
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				
				return ResponseEntity.ok().body(getObjectResponse);
			}
		}
		
	}
	@Override
	public ResponseEntity<?> removeRoleFromUser(String TOKEN, Long roleId, Long userId, Long loggedId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == loggedId) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign to your self",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(loggedId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedId, "ROLE", "assignToUser")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToUser role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 if(userId == 0 ) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " userId is required",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		 User assignedToUser  = userService.findById(userId);
		 if(assignedToUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned to user is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);  
		 }
		 Boolean isParent = false;
		 if(loggedUser.getAccountType() == 4) {
			 Set<User> loggedParent = loggedUser.getUsersOfUser();
			 if(loggedParent.isEmpty()) {
				 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to remove role from this user ",null);
	
					return ResponseEntity.badRequest().body(getObjectResponse);
			 }else {
				if(assignedToUser.getAccountType() != 4) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to remove role from this user ",null);
	
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				 User parent =null;
					for(User object : loggedParent) {
						parent = object;
					}
				Set<User> assignedToParents = assignedToUser.getUsersOfUser();
				if(assignedToParents.isEmpty()) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to remove role from this user ",null);
	
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				for(User object :assignedToParents ) {
					if(object.getId() == parent.getId() ) {
						isParent = true;
						
						break;
					}
				}			
			 }
		 }else {
			 List<User> parents = userService.getAllParentsOfuser(assignedToUser, assignedToUser.getAccountType());
			   if(parents.isEmpty()) {
				   isParent =  false;
			   }else {
				   for(User object :parents) {
					   if(object.getId() == loggedUser.getId()) {
						   isParent = true;
						   break;
					   }
				   }
			   }
		 }
		 if(isParent) {
			 
			 Long role = assignedToUser.getRoleId();
			 if(role == null) {
				 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user does not have role to remove  ",null);
	
					return ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 
				 	userRepository.removeRoleFromUser(userId);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "removed successfully",null);
					return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }else {
			 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to remove role from this user ",null);
	
				return ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 
					 
		 
		 
		 
		 
		 
	}
		
	

}
