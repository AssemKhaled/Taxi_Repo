package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

@Component
public class DriverServiceImpl extends RestServiceController implements DriverService{

	private static final Log logger = LogFactory.getLog(DriverServiceImpl.class);

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserRepository userRepository;
	
	GetObjectResponse getObjectResponse;
	
	@Override
	public ResponseEntity<?> getAllDrivers(String TOKEN,Long id,int offset,String search) {
		
		//User user=userRepository.getUserData(id);
		//Set<Driver> drivers = user.getDrivers();
		
		logger.info("************************ getAllDrivers STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				   userServiceImpl.resetChildernArray();
					if(user.getAccountType() == 4 ) {
						Set<User>parentClients = user.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get drivers of this user",null);
							 logger.info("************************ getAllUserDtivers ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object;
							}
							List<Long>usersIds= new ArrayList<>();
						   usersIds.add(parent.getId());
							drivers = driverRepository.getAllDrivers(usersIds,offset,search);
							Integer size= driverRepository.getAllDriversSize(parent.getId());
							
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers,size);
							logger.info("************************ getAllDrivers ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
						}
					}
					List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }
					 System.out.println("Ids"+usersIds.toString());
					drivers = driverRepository.getAllDrivers(usersIds,offset,search);
					Integer size= driverRepository.getAllDriversSize(id);
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers,size);
					logger.info("************************ getAllDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				
				
			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	
	}

	@Override
	public List<Driver> checkDublicateDriverInAdd(Long id, String name, String uniqueId, String mobileNum) {
		
		return driverRepository.checkDublicateDriverInAdd(id,name,uniqueId,mobileNum);

	}
	
	@Override
	public ResponseEntity<?> addDriver(String TOKEN,Driver driver,Long id) {

		logger.info("************************ addDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
					
					if(driver.getName()== null || driver.getUniqueid()== null
							   || driver.getMobile_num() == null || driver.getName()== "" || driver.getUniqueid()== ""
							   || driver.getMobile_num() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						if(driver.getPhoto() != null) {
							
							//base64_Image
							DecodePhoto decodePhoto=new DecodePhoto();
							String photo=driver.getPhoto().toString();
							driver.setPhoto(decodePhoto.Base64_Image(photo));				
							
						}
						else {
							driver.setPhoto("Not-available.png");
						}
						
						List<Driver> res=checkDublicateDriverInAdd(id,driver.getName(),driver.getUniqueid(),driver.getMobile_num());
					    List<Integer> duplictionList =new ArrayList<Integer>();
						if(!res.isEmpty()) {
							for(int i=0;i<res.size();i++) {
								if(res.get(i).getName().equalsIgnoreCase(driver.getName())) {
									duplictionList.add(1);				
								}
								if(res.get(i).getUniqueid().equalsIgnoreCase(driver.getUniqueid())) {
									duplictionList.add(2);				
				
								}
								if(res.get(i).getMobile_num().equalsIgnoreCase(driver.getMobile_num())) {
									duplictionList.add(3);				

								}
								
							}
					    	getObjectResponse = new GetObjectResponse( 301, "This Driver was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}
						else {
							if(driver.getId() == null || driver.getId() == 0) {
								User driverParent = new User();
								if(user.getAccountType() == 4) {
									Set<User> parentClients = user.getUsersOfUser();
									if(parentClients.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to add driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User object : parentClients) {
										driverParent = object;
									}
								}else {
									driverParent = user;
								}
								Set<User> userDriver = new HashSet<>();
								userDriver.add(driverParent);
								driver.setUserDriver(userDriver);
								driverRepository.save(driver);
								drivers.add(driver);
								getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",drivers);
								logger.info("************************ addDriver ENDED ***************************");

								return ResponseEntity.ok().body(getObjectResponse);

							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update this Driver ID",drivers);
								return ResponseEntity.badRequest().body(getObjectResponse);

							}
							
						
						}
						
					}
				
			}
			
			

		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

		
		
	}
	
	@Override
	public List<Driver> checkDublicateDriverInEdit(Long driverId, Long userId, String name, String uniqueId,
			String mobileNum) {

		return driverRepository.checkDublicateDriverInEdit(driverId, userId, name, uniqueId, mobileNum);

	}
	
	@Override
	public ResponseEntity<?> editDriver(String TOKEN,Driver driver,Long id) {
		logger.info("************************ editDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
               
                	if(driver.getId() != null) {
                		   	
						Driver driverCheck = driverRepository.findOne(driver.getId());

						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								boolean isParent = false;
								
								if(user.getAccountType() == 4) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									User parent = null;
									for(User object : parentClient) {
										parent = object ;
									}
									Set<User>driverParent = driverCheck.getUserDriver();
									if(driverParent.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									for(User parentObject : driverParent) {
										if(parentObject.getId() == parent.getId()) {
											isParent = true;
											break;
										}
									}
								}
								if(!checkIfParent(driverCheck , user) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this driver ",null);
									logger.info("************************ editDevice ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(driver.getName()== null || driver.getUniqueid()== null
										   || driver.getMobile_num() == null || driver.getName()== "" || driver.getUniqueid()== ""
										   || driver.getMobile_num() == "") {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);
									return ResponseEntity.badRequest().body(getObjectResponse);

								}
								else {
									if(driver.getPhoto() != null) {
										
										//base64_Image
										DecodePhoto decodePhoto=new DecodePhoto();
										String photo=driver.getPhoto().toString();
										driver.setPhoto(decodePhoto.Base64_Image(photo));				
										
									}
									else {
										driver.setPhoto(driverCheck.getPhoto());
									}
									
									List<Driver> res=checkDublicateDriverInEdit(driver.getId(),id,driver.getName(),driver.getUniqueid(),driver.getMobile_num());
								    List<Integer> duplictionList =new ArrayList<Integer>();
									if(!res.isEmpty()) {
										for(int i=0;i<res.size();i++) {
											if(res.get(i).getName().equalsIgnoreCase(driver.getName())) {
												duplictionList.add(1);				
											}
											if(res.get(i).getUniqueid().equalsIgnoreCase(driver.getUniqueid())) {
												duplictionList.add(2);				
							
											}
											if(res.get(i).getMobile_num().equalsIgnoreCase(driver.getMobile_num())) {
												duplictionList.add(3);				
			
											}
											
										}
								    	getObjectResponse = new GetObjectResponse( 301, "This Driver was found before",duplictionList);
										return ResponseEntity.ok().body(getObjectResponse);

									}
									else {
										
										   Set<User> userDriver = new HashSet<>();
										
									    	userDriver = driverCheck.getUserDriver();
										
										   driver.setUserDriver(userDriver);
										
											driverRepository.save(driver);
											drivers.add(driver);
											getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",drivers);
											logger.info("************************ editDriver ENDED ***************************");
											return ResponseEntity.ok().body(getObjectResponse);

										
										
										
										
									
									}
								}
							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
								return ResponseEntity.status(404).body(getObjectResponse);

							}
							
							
							
						}
						else{
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
                	}
                	else {
            			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
            			return ResponseEntity.badRequest().body(getObjectResponse);

                	}
					
				
				
				
			}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
		

		
	}


	@Override
	public ResponseEntity<?> findById(String TOKEN,Long driverId,Long userId) {
		logger.info("************************ getDriverById STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
        if(userId == 0) {
        	 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
        }
        User loggedUser = userServiceImpl.findById(userId);
        if(loggedUser == null) {
        	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
        }
		if(driverId != 0) {
			
			Driver driver= driverRepository.findOne(driverId);

			if(driver != null) {
				if(driver.getDelete_date() == null) {
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User> clientParents = loggedUser.getUsersOfUser();
						if(clientParents.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this user",null);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : clientParents) {
								parent = object ;
							}
							Set<User>driverParents = driver.getUserDriver();
							if(driverParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User parentObject : driverParents) {
									if(parentObject.getId() == parent.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					drivers.add(driver);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
					logger.info("************************ getDriverById ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
						
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
			return  ResponseEntity.badRequest().body(getObjectResponse);


		}


	}
	
	@Override
	public ResponseEntity<?> deleteDriver(String TOKEN,Long driverId,Long userId) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		logger.info("************************ deleteDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN  is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser  is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(driverId != 0) {
			Driver driver= driverRepository.findOne(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
				 boolean isParent = false;
				 if(loggedUser.getAccountType() == 4) {
					 Set<User> parentClients = loggedUser.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver",drivers);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>driverParent = driver.getUserDriver();
						 if(driverParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver",drivers);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : driverParent) {
								 if(parentObject.getId() == parent.getId()) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }
				 }
				 if(!checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					driverRepository.deleteDriver(driverId,currentDate);
					driverRepository.deleteDriverId(driverId);
					driverRepository.deleteDriverDeviceId(driverId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",drivers);
					logger.info("************************ deleteDriver ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver was Deleted Before",drivers);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

		

	}

	@Override
	public ResponseEntity<?> getUnassignedDrivers(String TOKEN,Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
		if(TOKEN.equals("")) {
			List<Driver> unAssignedDrivers = new ArrayList<>();

			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",unAssignedDrivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			List<Driver> unAssignedDrivers = new ArrayList<>();
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			if(user == null) {
				List<Driver> unAssignedDrivers = new ArrayList<>();
				
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",unAssignedDrivers);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				List<Driver> unAssignedDrivers = driverRepository.getUnassignedDrivers(userId);
				
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
				logger.info("************************ getUnassignedDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
		}
		
	}

	@Override
	public Integer getTotalNumberOfUserDrivers(List<Long> usersIds) {
		// TODO Auto-generated method stub
		 Integer totalNumberOfUserDrivers = driverRepository.getTotalNumberOfUserDrivers(usersIds);
		return totalNumberOfUserDrivers;
	}

	@Override
	public Driver getDriverById(Long driverId) {
		Driver driver = driverRepository.findOne(driverId);
		if(driver == null) {
			return null;
		}
		if(driver.getDelete_date() != null) {
			//throw not found 
			return null;
		}
		else
		{
			return driver;
		}
	}

	public  ResponseEntity<?> getDriverSelect(String TOKEN,Long userId) {

		logger.info("************************ getDeviceSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			drivers = driverRepository.getDriverSelect(userId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getDeviceSelect ENDED ***************************");
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
	
	 public Boolean checkIfParent(Driver driver , User loggedUser) {
		   Set<User> driverParent = driver.getUserDriver();
		   if(driverParent.isEmpty()) {
			  
			   return false;
		   }else {
			   User parent = null;
			   for (User object : driverParent) {
				   parent = object;
			   }
			   if(parent.getId() == loggedUser.getId()) {
				   return true;
			   }
			   if(parent.getAccountType() == 1) {
				   if(parent.getId() == loggedUser.getId()) {
					   return true;
				   }
			   }else {
				   List<User> parents = userServiceImpl.getAllParentsOfuser(parent, parent.getAccountType());
				   if(parents.isEmpty()) {
					   
					   return false;
				   }else {
					   for(User object :parents) {
						   if(object.getId() == loggedUser.getId()) {
							   return true;
						   }
					   }
				   }
			   }
			  
		   }
		   return false;
	   }
	

	
	
	

}
