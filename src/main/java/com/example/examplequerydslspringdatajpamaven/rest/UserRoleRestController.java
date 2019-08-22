package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;

@CrossOrigin
@Component
@RequestMapping(path = "/roles")
public class UserRoleRestController {

	@Autowired
	UserRoleService userRoleService;
	
	@PostMapping("/createRole")
	public ResponseEntity<?> createRole(@RequestParam (value = "userId",defaultValue = "0") Long userId,@RequestBody(required = false) UserRole role){
		
		return userRoleService.createRole(role,userId);
	}
	
	@PostMapping("/editRole")
	public ResponseEntity<?> editRole(@RequestBody(required = false) UserRole role){
		return userRoleService.editRole(role);
	}
	
	@GetMapping("/deleteRole")
	public ResponseEntity<?>deleteRole(@RequestParam (value = "roleId",defaultValue = "0") Long roleId){
		
		
		return userRoleService.deleteRole(roleId);
	}
	
	@GetMapping("/getRoleById")
	public ResponseEntity<?>getRoleByTd(@RequestParam (value = "roleId",defaultValue = "0") Long roleId){
		
		return userRoleService.getRoleById(roleId);
	}
	
	@GetMapping("/assignRoleToUser")
	public ResponseEntity<?>assignRoleToUser(@RequestParam (value = "roleId",defaultValue = "0") Long roleId,@RequestParam (value = "userId",defaultValue = "0") Long userId){
	
		return userRoleService.assignRoleToUser(roleId,userId);
	}
	
	@GetMapping("/getAllRolesCreatedByUser")
	public ResponseEntity<?> getAllRolesCreatedByUser(@RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		
		return userRoleService.getAllRolesCreatedByUser(userId);
	}
	
	@GetMapping("/getRolePageContent")
	public ResponseEntity<?> getRolePageContent(@RequestParam (value = "userId",defaultValue = "0") Long userId){
		return userRoleService.getRolePageContent(userId);
	}
	
	
}
