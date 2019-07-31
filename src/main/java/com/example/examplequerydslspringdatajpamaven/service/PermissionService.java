package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Permission;

public interface PermissionService {
 
	public ResponseEntity<?>addPermission(Permission permission);
	
	public Permission findById(Long Id);
	
	public ResponseEntity<?>editPermission(Permission permission);
	
	public ResponseEntity<?>deletePermission(Long PermissionId);
	
	public List<Permission>getPermissionsList();
}
