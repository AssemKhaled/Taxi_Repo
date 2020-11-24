package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.repository.PermissionRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

/**
 * services functionality related to permissions
 * @author fuinco
 *
 */
@Component
@Service
public class PermissionServiceImpl  implements PermissionService{

	@Autowired
	PermissionRepository permissionRepositoty;
	
	GetObjectResponse getObjectResponse;
	
	/**
	 * add permission in body data
	 */
	@Override
	public ResponseEntity<?> addPermission(Permission permission) {
		// TODO Auto-generated method stub
		 if(permission.getId()!= null || permission.getName() == null ||
			permission.getName() == ""|| permission.getFunctionality()== ""	 || permission.getFunctionality() == null ) {
			 List<Permission> permissions = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Only name and functionality are required to add permission ",permissions);
			 return  ResponseEntity.badRequest().body(getObjectResponse);

		 }
		 List<Permission> duplicatePermission= permissionRepositoty.findByName(permission.getName());
		 if(!duplicatePermission.isEmpty()) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This Permission Name was added before you can Edit or Delete it only",duplicatePermission);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 permissionRepositoty.save(permission);
		 List<Permission> permissions = null;
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "permission added successfully",permissions);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get pemission  by id 
	 */
	@Override
	public Permission findById(Long Id) {
		// TODO Auto-generated method stub
		Permission permission = permissionRepositoty.findOne(Id);
		if(permission == null)
		{
			return null;
		}
		if(permission.getDelete_date() != null) {
			return null;
		}
		return permission;
	}

	/**
	 * edit permission by id in body mandatory
	 */
	@Override
	public ResponseEntity<?> editPermission(Permission permission) {
		// TODO Auto-generated method stub
		if(permission.getId() == null || permission.getId() == 0 
		   || permission.getName() == "" || permission.getName() == null
		   || permission.getFunctionality() == "" || permission.getFunctionality() == null) {
			
			List<Permission> permissions = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "id , name and functionality are required to edit permission ",permissions);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Permission checkPermission = findById(permission.getId());
		if(checkPermission == null) {
			List<Permission> permissions = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this permission isn't found to edit ",permissions);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Permission> duplicatePermission= permissionRepositoty.findByName(permission.getName());
		 if(!duplicatePermission.isEmpty()) {
			  if(duplicatePermission.get(0).getId() != permission.getId()) {
				  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "This Permission Name was added before you can Edit or Delete it only",null);
					 return  ResponseEntity.badRequest().body(getObjectResponse);
			  }
			
		 }
		 permissionRepositoty.save(permission);
		 List<Permission> permissions = null;
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "permission updated successfully",permissions);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * delete permission by id
	 */
	@Override
	public ResponseEntity<?> deletePermission(Long PermissionId) {
		// TODO Auto-generated method stub
		if(PermissionId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No permissionId  to delete ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Permission permission = findById(PermissionId);
		if(permission == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this permission not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		Calendar cal = Calendar.getInstance();
		 int day = cal.get(Calendar.DATE);
	     int month = cal.get(Calendar.MONTH) + 1;
	     int year = cal.get(Calendar.YEAR);
	     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	     permission.setDelete_date(date);
	     permissionRepositoty.save(permission);
	     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "permission deleted successfully",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get list of all permissions
	 */
	@Override
	public List<Permission> getPermissionsList() {
		// TODO Auto-generated method stub
		List<Permission>permissions = permissionRepositoty.getAllPermissions();
		return permissions;
	}
	
	

}
