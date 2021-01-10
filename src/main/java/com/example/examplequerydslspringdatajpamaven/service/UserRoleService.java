package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.UserRole;

@Service
public interface UserRoleService {

	public ResponseEntity<?>createRole(String TOKEN,UserRole role,Long userId);
	
	public ResponseEntity<?>editRole(String TOKEN,UserRole role,Long userId);
	
	public UserRole findById(Long Id);
	
	public ResponseEntity<?>deleteRole(String TOKEN,Long roleId,Long userId);
	
	public ResponseEntity<?>getRoleById(String TOKEN,Long roleId,Long loggedId);
	
	public ResponseEntity<?>assignRoleToUser(String TOKEN,Long roleId,Long userId,Long loggedId);
	
	public ResponseEntity<?>getAllRolesCreatedByUser(String TOKEN,Long userId,int offset,String search, String exportData);
	
	public ResponseEntity<?> getRolePageContent(String TOKEN,Long userId);
	
	public Boolean checkUserHasPermission(Long userId,String module,String functionality); 
	
	public ResponseEntity<?>getUserParentRoles( String TOKEN, Long userId);
	
	public ResponseEntity<?>removeRoleFromUser(String TOKEN,Long roleId,Long userId,Long loggedId);
	
	
}
