package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
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
		 if(userId == 0) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser Id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		 User loggedUser = userService.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "ROLE", "create")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 System.out.println("fdvdfvfdvfdv: "+role.getName());
		if(role.getId()!=null || role.getName() == null || role.getName() == ""
				||role.getPermissions() == null || role.getPermissions() == "") {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Only name and permissions are required to add Role ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		List<UserRole> roles = userRoleRepository.findByName(role.getName());
		if(!roles.isEmpty()) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This Role Name was added before you can Edit or Delete it only",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		role.setUserId(userId);
		userRoleRepository.save(role);
		
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "role added successfully",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}
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
		if(userId==0) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Logged User ID is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
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
		Long createdByUserId=userRole.getUserId();
		if(createdByUserId == 4) {
			 if(loggedUser.getAccountType()==4) {
				 if(createdByUserId!=userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 
		}
		if(createdByUserId == 3) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
		}
		if(createdByUserId == 2) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			
	 
		}
		if(createdByUserId == 1) {	 
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to edit this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==1) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
		}
		 
		 
		
		
		UserRole checkRole = findById(role.getId());
		if(checkRole == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Role isn't found to edit ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> roles = userRoleRepository.findByName(role.getName());
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
		if(createdByUserId == 4) {
			 if(loggedUser.getAccountType()==4) {
				 if(createdByUserId!=userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to delete this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 
		}
		if(createdByUserId == 3) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to delete this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
		}
		if(createdByUserId == 2) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(createdByUserId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to delete this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(userId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			
	 
		}
		if(createdByUserId == 1) {	 
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to delete this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==1) {
				 if(createdByUserId != userId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to delete this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
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
	
	@Override
	public ResponseEntity<?> getRoleById(String TOKEN,Long roleId) {
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
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> roles = new ArrayList<>();
		roles.add(role);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	
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
		if(userId == 0 || roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " roleId  and userId are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		Long createdByUserId=role.getUserId();
		User createrType = userService.findById(createdByUserId);
		if(createrType == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Create of role is not found maybe deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		}
		if(createrType.getAccountType() == 4) {
			 if(loggedUser.getAccountType()==4) {
				 if(createdByUserId!=loggedId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 User child=userService.findById(userId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow assign this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(loggedId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			 
		}
		if(createrType.getAccountType()  == 3) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				 if(createdByUserId != loggedId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(userId);
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit assign role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(loggedId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
		}
		if(createrType.getAccountType()  == 2) {
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				 if(createdByUserId != loggedId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
			 User child=userService.findById(userId);
			 if(child == null) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Create of role is not found maybe deleted",null);
				 return  ResponseEntity.status(404).body(getObjectResponse); 
			 }
			 List<User> parents=userService.getAllParentsOfuser(child,child.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to assign this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 boolean isParent = false; 
				 User parentClient = new User() ;
				 for(User object : parents) {
					 parentClient = object;
					 if(loggedId == parentClient.getId()) {
						isParent =true;
						break;
					 }
				 }
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to edit this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			 }
			
	 
		}
		if(createrType.getAccountType()  == 1) {	 
			 if(loggedUser.getAccountType()==4) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==3) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==2) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "your not the creater or parent to assign this role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(loggedUser.getAccountType()==1) {
				 if(createdByUserId != loggedId) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the creater of the role you cannot allow to assign this role.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
			 }
		}
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		 List<User> parentsOfUser=userService.getAllParentsOfuser(user,user.getAccountType());

		 boolean isParentOfUser = false; 
		 User parentClientOfUser = new User() ;
		 for(User object : parentsOfUser) {
			 parentClientOfUser = object;
			 if(createdByUserId == parentClientOfUser.getId()) {
				 isParentOfUser =true;
				break;
			 }
		 }
		 if(isParentOfUser == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as this user not child of creater userId its'nt allow to assign",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		
		
		
		
		Set<User> userParents = user.getUsersOfUser();
		User parent = null;
		for(User parentClient : userParents) {
			 parent = parentClient;
		}
		UserRole parentRole=findById(parent.getRoleId());	
		
		if(parentRole== null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "As no Role assigned to your direct parent yet not allow to assign to this user else",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
    	JSONObject ParentRolePerm = new JSONObject(parentRole.getPermissions());
    	JSONObject ChildRolePerm = new JSONObject(role.getPermissions());
    	if(ChildRolePerm.getJSONArray("permissions").length() > ParentRolePerm.getJSONArray("permissions").length()) {
    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign functionality to user not assigned to direct parent",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
    	}
    	Boolean check=true;
    	for(int i=0;i<ChildRolePerm.getJSONArray("permissions").length();i++) {
    		for(int j=0;j<ParentRolePerm.getJSONArray("permissions").length();j++) {
    			if(ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("name").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("name"))
    					&&
    					ChildRolePerm.getJSONArray("permissions").getJSONObject(i).get("id").equals(ParentRolePerm.getJSONArray("permissions").getJSONObject(j).get("id"))) {
    				check=true;
    				if(ChildRolePerm.getJSONArray("permissions").getJSONObject(i).getJSONObject("functionality").length() > ParentRolePerm.getJSONArray("permissions").getJSONObject(j).getJSONObject("functionality").length()) {
    		    		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign functionality to user not assigned to direct parent",null);
    					return  ResponseEntity.badRequest().body(getObjectResponse);
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
    								   getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign functionality to user not assigned to direct parent",null);
    								   return  ResponseEntity.badRequest().body(getObjectResponse);
    							   }    							  
    						   }
    						   
    					   }    							       						
    					   else {
    						   getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign functionality to user not assigned to direct parent",null);
							   return  ResponseEntity.badRequest().body(getObjectResponse);
						   }

    					 }
    				}
    				break;
    			}
    			else {
					check=false;
    				
    			}
    			
    		}
    		if(check == false) {
    			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to assign functionality to user not assigned to direct parent",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
    		}
    	}

		
		
		user.setRoleId(roleId);
		userRepository.save(user);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "assigned successfully",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	@Override
	public ResponseEntity<?> getAllRolesCreatedByUser(String TOKEN,Long userId) {
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
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get list role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		userService.resetChildernArray();
		List<User> childernUsers=userService.getActiveAndInactiveChildern(userId);
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
		List<UserRole> roles = userRoleRepository.getAllRolesCreatedByUser(usersIds);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
		
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
//	@Override
//	public ResponseEntity<?> getRolePageContent(Long userId) {
//		// TODO Auto-generated method stub
//		if(userId == 0) {
//			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
//			 return  ResponseEntity.badRequest().body(getObjectResponse);
//		}
//		User user = userService.findById(userId);
//		if(user == null) {
//			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
//			 return  ResponseEntity.status(404).body(getObjectResponse);
//		}
//		List<Permission> permissions = permissionService.getPermissionsList();
//		if(permissions.isEmpty()) {
//			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no permissions to add ",null);
//			 return  ResponseEntity.status(404).body(getObjectResponse);
//		}
//		List<UserRole> role = userRoleRepository.getUserRole(userId);
//		Map content = new HashMap();
//		content.put("permissions", permissions);
//		content.put("role" ,role);
//		List<Map> pageContent = new ArrayList<>();
//		pageContent.add(content);
//		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",pageContent);
//		
//		return ResponseEntity.ok().body(getObjectResponse);
//	}
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
					System.out.println("myJson"+the_json_array);
					
					
					for(Object object : the_json_array) {	
						System.out.println("myJson"+object);
						JSONObject permissionObject = new JSONObject(object.toString());
						

						
						if( permissionObject.has("name")) {
							if(permissionObject.getString("name").equals(module)) {
								System.out.println("get here");
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
//				 
			 }else {
				 return false;
			 }
		}
		
		
		
		
	}
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
				//getAllPermissions
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
					System.out.println("myJson"+the_json_array);
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
		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
		 return  ResponseEntity.badRequest().body(getObjectResponse);
	}else {
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			Set<User> userParents = user.getUsersOfUser();
			User parent = null;
			for(User parentClient : userParents) {
				 parent = parentClient;
			}
			
			List<Long>usersIds= new ArrayList<>();
			
			usersIds.add(parent.getId());
			
			List<UserRole> roles = userRoleRepository.getAllRolesCreatedByUser(usersIds);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
			
			return ResponseEntity.ok().body(getObjectResponse);
		}
	}
	
}
	
	

}
