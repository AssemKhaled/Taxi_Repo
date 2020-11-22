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
import com.example.examplequerydslspringdatajpamaven.service.PermissionService;

/**
 * Service of permission component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/permissions")
public class PermissionsRestController {

	@Autowired
	PermissionService permissionService;
	@PostMapping(path = "/addPermission")
	public ResponseEntity<?> addPermission(@RequestBody(required = true) Permission permission){
		
		return permissionService.addPermission(permission);
	}
	
	@PostMapping(path ="/editPermission")
	public ResponseEntity<?> editPermission(@RequestBody(required = true) Permission permission){
		

		return permissionService.editPermission(permission);
			
	}
	
	@GetMapping(path = "deletePermission")
	public ResponseEntity<?> deletePermission(@RequestParam (value = "permissionId",defaultValue = "0") Long permissionId){
		
		return permissionService.deletePermission(permissionId);
	}
	
	
}