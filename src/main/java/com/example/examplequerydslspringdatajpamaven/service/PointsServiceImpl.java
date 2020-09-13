package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Points;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.PointsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
public class PointsServiceImpl extends RestServiceController implements PointsService{

	private static final Log logger = LogFactory.getLog(PointsServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private PointsRepository pointsRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public ResponseEntity<?> getPointsList(String TOKEN, Long id, int offset, String search) {
		 logger.info("************************ getPointsList STARTED ***************************");
			
		List<Points> points = new ArrayList<Points>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "POINTS", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get POINTS list",null);
						 logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							
							 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get pointD of this user",null);
							 logger.info("************************ getPointsList ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						 }else {
							 User parentClient = new User() ;
							 for(User object : parentClients) {
								 parentClient = object;
							 }
							 usersIds.add(parentClient.getId());
						 }
					}
					else {
						List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						 if(childernUsers.isEmpty()) {
							 usersIds.add(id);
						 }
						 else {
							 usersIds.add(id);
							 for(User object : childernUsers) {
								 usersIds.add(object.getId());
							 }
						 }
					}
				    

					
					
					 points = pointsRepository.getAllPoints(usersIds,offset,search);
					 Integer size = 0;
					 List<Map> data = new ArrayList<>();
					 if(points.size() >0) {
						 size = pointsRepository.getAllPointsSize(usersIds);

						 for(Points point:points) {
						     Map PointsList= new HashMap();

						     
							 PointsList.put("id", point.getId());
							 PointsList.put("name", point.getName());
							 PointsList.put("latitude", point.getLatitude());
							 PointsList.put("longitude", point.getLongitude());
							 PointsList.put("delete_date", point.getDelete_date());
							 PointsList.put("photo", point.getPhoto());
							 PointsList.put("userId", point.getUserId());
							 PointsList.put("userName", null);

							 User us = userRepository.findOne(point.getUserId());
							 if(us != null) {
								 PointsList.put("userName", us.getName());

							 }
							 data.add(PointsList);

						 }
						

					 }
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getPointsList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",points);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getPointsById(String TOKEN, Long PointId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Points> Points = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",Points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(PointId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No PointId  to return",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Points point= pointsRepository.findOne(PointId);
		if(point == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Points not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(point.getDelete_date() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Points not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Long createdBy=point.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this point.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get Points",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
		List<Points> points = new ArrayList<>();
		points.add(point);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",points);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> deletePoints(String TOKEN, Long PointId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Points> points= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "POINTS", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete point",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(PointId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No PointId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Points point= pointsRepository.findOne(PointId);
		if(point == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this PointId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(point.getDelete_date() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this point not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=point.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this point.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get point",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int year = cal.get(Calendar.YEAR);
	    String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	    point.setDelete_date(date);
	     
	    pointsRepository.save(point);
	    
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> createPoints(String TOKEN, Points point, Long userId) {
		logger.info("************************ createPoints STARTED ***************************");

		String image = point.getPhoto();
		point.setPhoto("not_available.png");
		
		List<Points> points= new ArrayList<Points>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",points);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "POINTS", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ createPoints ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date()==null) {
					if(point.getName()== null || point.getLatitude()== null
							   || point.getLongitude() == null || point.getName()== "" || point.getLatitude()== 0.0
							   || point.getLongitude() == 0.0) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Point Name, Latitude and Longitude are Required",null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}

					
					if(point.getId()==null || point.getId()==0) {
						if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 User parent = null;
								 for(User object : parentClients) {
									 parent = object;
								 }
								 point.setUserId(parent.getId());


							 }
						 }
						 else {
							 point.setUserId(userId);

						 }
						
						
						
						pointsRepository.save(point);
						
						
						DecodePhoto decodePhoto=new DecodePhoto();
				    	if(image !=null) {
					    	if(image !="") {
					    		if(image.startsWith("data:image")) {
						    		point.setPhoto(decodePhoto.Base64_Image(image,"point"));				

					    		}
					    	}
						}
						
						points.add(point);

						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",points);
						logger.info("************************ createPoints ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update Point Id",points);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",points);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}
           			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",points);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}	
	}

	@Override
	public ResponseEntity<?> editPoints(String TOKEN, Points point, Long userId) {
    	String newPhoto= point.getPhoto();
    	point.setPhoto("not_available.png");
    	
		// TODO Auto-generated method stub
		List<Points> points = new ArrayList<Points>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "POINTS", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit point",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 
		 if(point.getId() == null) {
			 getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "point ID is Required",points);
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 Points pointsCheck = pointsRepository.findOne(point.getId());
			if(pointsCheck == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "point is not found",points);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			if(pointsCheck.getDelete_date() != null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "point is not found or deleted",points);
				return ResponseEntity.status(404).body(getObjectResponse);
			}

		point.setUserId(pointsCheck.getUserId());
		Long createdBy=point.getUserId();
		Boolean isParent=false;

		if(createdBy == userId) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this point.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
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
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get point",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(point.getName()== null || point.getLatitude()== null
				   || point.getLongitude() == null || point.getName()== "" || point.getLatitude()== 0.0
				   || point.getLongitude() == 0.0) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Point Name, Latitude and Longitude are Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			
			
			DecodePhoto decodePhoto=new DecodePhoto();
        	String oldPhoto=pointsCheck.getPhoto();

			if(oldPhoto != null) {
	        	if(!oldPhoto.equals("")) {
					if(!oldPhoto.equals("not_available.png")) {
						decodePhoto.deletePhoto(oldPhoto, "point");
					}
				}
			}

			
			if(newPhoto.equals("")) {
				
				point.setPhoto("not_available.png");				
			}
			else {
				if(newPhoto.equals(oldPhoto)) {
					point.setPhoto(oldPhoto);				
				}
				else{
		    		if(newPhoto.startsWith("data:image")) {

		    			point.setPhoto(decodePhoto.Base64_Image(newPhoto,"point"));
		    		}
				}

		    }
			
			pointsRepository.save(point);
			

			points.add(point);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",points);
			logger.info("************************ editPoints ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

				
				
			
		
		}
		
				
			    
	}

	@Override
	public ResponseEntity<?> getPointsMap(String TOKEN, Long id) {
	 logger.info("************************ getPointsList STARTED ***************************");
		
		List<Points> points = new ArrayList<Points>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "POINTS", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get POINTS list",null);
						 logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 Set<User> parentClients = user.getUsersOfUser();
						 if(parentClients.isEmpty()) {
							
							 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get pointD of this user",null);
							 logger.info("************************ getPointsList ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						 }else {
							 User parentClient = new User() ;
							 for(User object : parentClients) {
								 parentClient = object;
							 }
							 usersIds.add(parentClient.getId());
						 }
					}
					else {
						List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						 if(childernUsers.isEmpty()) {
							 usersIds.add(id);
						 }
						 else {
							 usersIds.add(id);
							 for(User object : childernUsers) {
								 usersIds.add(object.getId());
							 }
						 }
					}
				    

					
					
					 points = pointsRepository.getAllPointsMap(usersIds);
					 List<Map> data = new ArrayList<>();
					 if(points.size() >0) {
						 for(Points point:points) {
						     Map PointsList= new HashMap();
						     
							 PointsList.put("name", point.getName());
							 PointsList.put("latitude", point.getLatitude());
							 PointsList.put("longitude", point.getLongitude());
							 PointsList.put("photo", point.getPhoto());

							 data.add(PointsList);

						 }
						

					 }
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data);
					logger.info("************************ getPointsList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",points);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}
}