package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	UserServiceImpl userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PermissionService permissionService;
	
	GetObjectResponse getObjectResponse;
	@Override
	public ResponseEntity<?> createRole(UserRole role) {
		// TODO Auto-generated method stub
		
		if(role.getId()!=null || role.getName() == null || role. getName() == ""
				||role.getPermissions() == null || role.getPermissions() == "") {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Only name and permissions are required to add Role ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		List<UserRole> roles = userRoleRepository.findByName(role.getName());
		if(!roles.isEmpty()) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this role name was added before you can edit or delte it only",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		userRoleRepository.save(role);
		
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "role added successfully",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}
	@Override
	public ResponseEntity<?> editRole(UserRole role) {
		// TODO Auto-generated method stub
		if(role.getId() == null || role.getId() == 0 || role.getName() == null || role.getName() == ""
				||role.getPermissions() == null || role.getPermissions() == "") {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "id, name and permissions are required to add Role ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			
		}
		UserRole checkRole = findById(role.getId());
		if(checkRole == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Role isn't found to edit ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> roles = userRoleRepository.findByName(role.getName());
		if(!roles.isEmpty()) {
			if(roles.get(0).getId() != role.getId()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this role  was added in another role ",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
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
	public ResponseEntity<?> deleteRole(Long roleId) {
		// TODO Auto-generated method stub
		if(roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No roleId  to delete ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
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
	public ResponseEntity<?> getRoleById(Long roleId) {
		// TODO Auto-generated method stub
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
	public ResponseEntity<?> assignRoleToUser(Long roleId, Long userId) {
		// TODO Auto-generated method stub
		if(userId == 0 || roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " roleId  and userId are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = findById(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		user.setRoleId(roleId);
		userRepository.save(user);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "assigned successfully",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	@Override
	public ResponseEntity<?> getAllRolesCreatedByUser(Long userId) {
		// TODO Auto-generated method stub
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> roles = userRoleRepository.getAllRolesCreatedByUser(userId);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles);
		
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
	@Override
	public ResponseEntity<?> getRolePageContent(Long userId) {
		// TODO Auto-generated method stub
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userService.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Permission> permissions = permissionService.getPermissionsList();
		if(permissions.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "no permissions to add ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<UserRole> role = userRoleRepository.getUserRole(userId);
		Map content = new HashMap();
		content.put("permissions", permissions);
		content.put("role" ,role);
		List<Map> pageContent = new ArrayList<>();
		pageContent.add(content);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",pageContent);
		
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
		}
		
		UserRole role = roles.get(0);
		
		JSONObject permissions = new JSONObject(role.getPermissions());
		 
		 if(permissions.has(module)) {
			
			 JSONObject serviceFunctionalities = permissions.getJSONObject("device"); 
			
			 if(serviceFunctionalities.has(functionality)) {
				 
				 if(serviceFunctionalities.getBoolean(functionality)) {
					 
					 return true;
				 }else {
					 return false;
				 }
			 }
			 else {
				 return false;
			 }
		 }
		 else {
			 return false;
		 }
		
	}
	
	

}