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
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Points;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientPoint;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.PointsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientPointRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to points
 * @author fuinco
 *
 */

@Component
@Service
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
	
	@Autowired
    UserClientPointRepository userClientPointRepository;
	
	
	/**
	 * get list of points limit 10
	 */
	@Override
	public ResponseEntity<?> getPointsList(String TOKEN, Long id, int offset, String search,String exportData) {
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
						 
				    	 List<Long> pointIds = userClientPointRepository.getPointIds(id);
						 Integer size = 0;
						 List<Map> data = new ArrayList<>();
						 if(pointIds.size()>0) {
							 
							 if(exportData.equals("exportData")) {
								 points = pointsRepository.getAllPointsByIdsExport(pointIds,search);

							 }
							 else {
								 points = pointsRepository.getAllPointsByIds(pointIds,offset,search);

								 if(points.size() >0) {
									 size = pointsRepository.getAllPointsSizeByIds(pointIds,search);
								 }
							 }

					    	
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
						
						 Integer size = 0;
						 List<Map> data = new ArrayList<>();
						 if(exportData.equals("exportData")) {
							 points = pointsRepository.getAllPointsExport(usersIds,search);

						 }
						 else {
							 points = pointsRepository.getAllPoints(usersIds,offset,search);
							 
							 if(points.size() >0) {
								 size = pointsRepository.getAllPointsSize(usersIds,search);

							 } 
						 }

						
						
						 
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
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
						logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					}

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

	/**
	 * get points by id
	 */
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
		
		if(loggedUser.getAccountType().equals(4)) {
			List<Long> points = userClientPointRepository.getPoint(userId,PointId);
			if(points.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
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

	/**
	 * delete points by id 
	 */
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
		
		if(loggedUser.getAccountType().equals(4)) {
			List<Long> points = userClientPointRepository.getPoint(userId,PointId);
			if(points.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
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

	/**
	 * create points in body
	 */
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
						 User parent = null;
						if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User object : parentClients) {
									 parent = object;
								 }
								 point.setUserId(parent.getId());


							 }
						 }
						 else {
							 point.setUserId(userId);

						 }
						
						
						

						
						
						DecodePhoto decodePhoto=new DecodePhoto();
				    	if(image !=null) {
					    	if(image !="") {
					    		if(image.startsWith("data:image")) {
						    		point.setPhoto(decodePhoto.Base64_Image(image,"point"));				

					    		}
					    	}
						}

						pointsRepository.save(point);

						points.add(point);
						
						if(user.getAccountType().equals(4)) {
				    		userClientPoint saveData = new userClientPoint();

					        Long pointId = pointsRepository.getPointIdByName(parent.getId(),point.getName(),point.getLatitude(),point.getLongitude());
				    		if(pointId != null) {
					    		saveData.setUserid(userId);
					    		saveData.setPointid(pointId);
						        userClientPointRepository.save(saveData);
				    		}
				    		
				    	}
						
						
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

	/**
	 * edit point in body id mandatory
	 */
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
		
		if(loggedUser.getAccountType().equals(4)) {
			List<Long> pointsData = userClientPointRepository.getPoint(userId,point.getId());
			if(pointsData.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
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

	/**
	 * get points on map
	 */
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
						
						List<Map> data = new ArrayList<>();
				    	List<Long> pointIds = userClientPointRepository.getPointIds(id);

				    	if(pointIds.size() > 0) {
				    		points = pointsRepository.getAllPointsMapByIds(pointIds);

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
				    	}
						
				    	getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data);
						logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
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

	/**
	 * get points select list
	 */
	@Override
	public ResponseEntity<?> getPointSelect(String TOKEN,Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub

		logger.info("************************ getNotificationSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(loggedUserId != 0) {
	    	User loggedUser = userServiceImpl.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				 List<Long> pointIds = userClientPointRepository.getPointIds(loggedUserId);
						 if(pointIds.size()>0) {
							 drivers = pointsRepository.getPointSelectByIds(pointIds);
							 
						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
						logger.info("************************ getNotificationSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
		
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 
	    				 List<Long> pointIds = userClientPointRepository.getPointIds(userId);
						 if(pointIds.size()>0) {
							 drivers = pointsRepository.getPointSelectByIds(pointIds);
							 
						 }
						 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
							logger.info("************************ getNotificationSelect ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
	   			 }
	    			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
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
	    			
	    			drivers = pointsRepository.getPointSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getNotificationSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	}
	    	else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	
	}
	
	/**
	 * get unassigned points for type 3
	 */
	@Override
	public ResponseEntity<?> getPointUnSelectOfClient(String TOKEN,Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub

		logger.info("************************ getNotificationSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		

		drivers = pointsRepository.getPointUnSelectOfClient(loggedUserId,userId);
		List<DriverSelect> selectedPoints = userClientPointRepository.getPointsOfUserList(userId);

		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedPoints", selectedPoints);
	    obj.put("points", drivers);

	    data.add(obj);
	    
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getNotificationSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
		
		
	
	
	}
	
	
	/**
	 * assign points to type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientPoints(String TOKEN, Long loggedUserId, Long userId, Long[] pointIds) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		
        if(pointIds.length > 0 && pointIds[0] != 0) {
			
        	List<userClientPoint> checkData = userClientPointRepository.getPointByPoiIds(pointIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is point assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
			for(Long id:pointIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Points assignedPoint = pointsRepository.findOne(id);
				if(assignedPoint == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Point is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientPointRepository.deletePointsByUserId(userId);
			for(Long assignedId:pointIds) {
				userClientPoint userPoint = new userClientPoint();
				userPoint.setUserid(userId);
				userPoint.setPointid(assignedId);
				userClientPointRepository.save(userPoint);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientPoint> computeds = userClientPointRepository.getPointsOfUser(userId);
			
			if(computeds.size() > 0) {

				userClientPointRepository.deletePointsByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no computeds for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get points of user type 4
	 */
	@Override
	public ResponseEntity<?> getClientPoints(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}

		List<DriverSelect> computeds = userClientPointRepository.getPointsOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",computeds);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}


}
