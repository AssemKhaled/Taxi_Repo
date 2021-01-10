package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;


/**
 * Service of role component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/roles")
public class UserRoleRestController {

	@Autowired
	UserRoleService userRoleService;
	
	@PostMapping("/createRole")
	public ResponseEntity<?> createRole(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestParam (value = "userId",defaultValue = "0") Long userId,@RequestBody(required = false) UserRole role){
		
		return userRoleService.createRole(TOKEN,role,userId);
	}
	
	@PostMapping("/editRole")
	public ResponseEntity<?> editRole(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestBody(required = false) UserRole role,@RequestParam (value = "userId",defaultValue = "0") Long userId){
		return userRoleService.editRole(TOKEN,role,userId);
	}
	
	@GetMapping("/deleteRole")
	public ResponseEntity<?>deleteRole(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestParam (value = "roleId",defaultValue = "0") Long roleId,@RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		
		return userRoleService.deleteRole(TOKEN,roleId,userId);
	}
	
	@GetMapping("/getRoleById")
	public ResponseEntity<?>getRoleByTd(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestParam (value = "roleId",defaultValue = "0") Long roleId
			,@RequestParam (value = "loggedId",defaultValue = "0") Long loggedId){
		
		return userRoleService.getRoleById(TOKEN,roleId,loggedId);
	}
	
	@GetMapping("/assignRoleToUser")
	public ResponseEntity<?>assignRoleToUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestParam (value = "roleId",defaultValue = "0") Long roleId,@RequestParam (value = "userId",defaultValue = "0") Long userId,@RequestParam (value = "loggedId",defaultValue = "0") Long loggedId){
	
		if(roleId == 0) {
			return userRoleService.removeRoleFromUser(TOKEN,roleId,userId,loggedId);
		}else {
			return userRoleService.assignRoleToUser(TOKEN,roleId,userId,loggedId);
		}
		
	}
	
	@GetMapping("/getAllRolesCreatedByUser")
	public ResponseEntity<?> getAllRolesCreatedByUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
            @RequestParam (value = "exportData", defaultValue = "") String exportData,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
	        @RequestParam(value = "search", defaultValue = "") String search,
			@RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		
		return userRoleService.getAllRolesCreatedByUser(TOKEN,userId,offset,search,exportData);
	}
	
	@GetMapping("/getRolePageContent")
	public ResponseEntity<?> getRolePageContent(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,@RequestParam (value = "userId",defaultValue = "0") Long userId){
		return userRoleService.getRolePageContent(TOKEN,userId);
	}
	
	@GetMapping("/getUserParentRoles")
	public ResponseEntity<?>getUserParentRoles(@RequestHeader(value = "TOKEN",defaultValue = "")String TOKEN,
			                                   @RequestParam(value ="selectedUserId",defaultValue= "0")Long userId){
		return userRoleService.getUserParentRoles(TOKEN,userId);
	}
	

}
