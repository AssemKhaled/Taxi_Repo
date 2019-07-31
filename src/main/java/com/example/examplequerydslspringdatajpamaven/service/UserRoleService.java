package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.UserRole;

public interface UserRoleService {

	public ResponseEntity<?>createRole(UserRole role);
	
	public ResponseEntity<?>editRole(UserRole role);
	
	public UserRole findById(Long Id);
	
	public ResponseEntity<?>deleteRole(Long roleId);
	
	public ResponseEntity<?>getRoleById(Long roleId);
	
	public ResponseEntity<?>assignRoleToUser(Long roleId,Long userId);
	
	public ResponseEntity<?>getAllRolesCreatedByUser(Long userId);
	
	public ResponseEntity<?> getRolePageContent(Long userId);
	
	public Boolean checkUserHasPermission(Long userId,String module,String functionality); 
	
	
}
