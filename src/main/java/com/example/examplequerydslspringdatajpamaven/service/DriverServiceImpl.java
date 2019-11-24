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

import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
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
	private UserRoleService userRoleService;
	
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
	    List<CustomDriverList> customDrivers = new ArrayList<CustomDriverList>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user.equals(null)) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver list",null);
						 logger.info("************************ getAllUserDrivers ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				   userServiceImpl.resetChildernArray();
					if(user.getAccountType().equals(4)) {
						Set<User>parentClients = user.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get drivers of this user",null);
							 logger.info("************************ getAllUserDrivers ENDED ***************************");
							return  ResponseEntity.status(404).body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object;
							}
							List<Long>usersIds= new ArrayList<>();
						    usersIds.add(parent.getId());
						     
							//drivers = driverRepository.getAllDrivers(usersIds,offset,search);
						    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);
						    Integer size= driverRepository.getAllDriversSize(usersIds);
							
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
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
					//drivers = driverRepository.getAllDrivers(usersIds,offset,search);
				    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);

					Integer size= driverRepository.getAllDriversSize(usersIds);
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
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
	public List<Driver> checkDublicateDriverInAddName(Long id, String name) {
		
		return driverRepository.checkDublicateDriverInAddName(id,name);

	}
	@Override
	public List<Driver> checkDublicateDriverInAddUniqueMobile(String uniqueId, String mobileNum) {
		
		return driverRepository.checkDublicateDriverInAddUniqueMobile(uniqueId,mobileNum);

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
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user.equals(null)) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create driver",null);
						 logger.info("************************ createDriver ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
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
							driver.setPhoto("Not-available.png");
						}
						
						List<Driver> res1=checkDublicateDriverInAddName(id,driver.getName());					    
					    List<Driver> res2=checkDublicateDriverInAddUniqueMobile(driver.getUniqueid(),driver.getMobile_num());
					    List<Integer> duplictionList =new ArrayList<Integer>();

						if(!res1.isEmpty()) {
							for(int i=0;i<res1.size();i++) {
								if(res1.get(i).getName().equalsIgnoreCase(driver.getName())) {
									duplictionList.add(1);				
								}
					
							}
					    	

						}
						
						if(!res2.isEmpty()) {
							for(int i=0;i<res2.size();i++) {
								
								if(res2.get(i).getUniqueid().equalsIgnoreCase(driver.getUniqueid())) {
									duplictionList.add(2);				
				
								}
								if(res2.get(i).getMobile_num().equalsIgnoreCase(driver.getMobile_num())) {
									duplictionList.add(3);				

								}
								
							}
					    	

						}
						
						if(!res1.isEmpty() || !res2.isEmpty()) {
							getObjectResponse = new GetObjectResponse( 301, "This Driver was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);	
						}
						
						else {
							if(driver.getId() == null || driver.getId() == 0) {
								User driverParent = new User();
								if(user.getAccountType().equals(4)) {
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
	public List<Driver> checkDublicateDriverInEditName(Long driverId, Long userId, String name) {

		return driverRepository.checkDublicateDriverInEditName(driverId, userId, name);

	}
	@Override
	public List<Driver> checkDublicateDriverInEditMobileUnique(Long driverId, String uniqueId, String mobileNum) {

		return driverRepository.checkDublicateDriverInEditUniqueMobile(driverId, uniqueId, mobileNum);

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
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user.equals(null)) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "edit")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit driver",null);
						 logger.info("************************ editDriver ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
               
                	if(!driver.getId().equals(null)) {
                		   	
						Driver driverCheck = driverRepository.findOne(driver.getId());

						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								boolean isParent = false;
								
								if(user.getAccountType().equals(4)) {
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
										if(parentObject.getId().equals(parent.getId())) {
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
									
									List<Driver> res1=checkDublicateDriverInEditName(driver.getId(),id,driver.getName());
									List<Driver> res2=checkDublicateDriverInEditMobileUnique(driver.getId(),driver.getUniqueid(),driver.getMobile_num());

									List<Integer> duplictionList =new ArrayList<Integer>();
									
									if(!res1.isEmpty()) {
										for(int i=0;i<res1.size();i++) {
											if(res1.get(i).getName().equalsIgnoreCase(driver.getName())) {
												duplictionList.add(1);				
											}
											
											
										}
								    	

									}
									
									
									if(!res2.isEmpty()) {
										for(int i=0;i<res2.size();i++) {
											
											if(res2.get(i).getUniqueid().equalsIgnoreCase(driver.getUniqueid())) {
												duplictionList.add(2);				
							
											}
											if(res2.get(i).getMobile_num().equalsIgnoreCase(driver.getMobile_num())) {
												duplictionList.add(3);				
			
											}
											
										}
								    	

									}
									if(!res1.isEmpty() || !res2.isEmpty()) {
										
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
        if(userId.equals(0)) {
        	 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
        }
        User loggedUser = userServiceImpl.findById(userId);
        if(loggedUser.equals(null)) {
        	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
        }
		if(!driverId.equals(0)) {
			
			Driver driver= driverRepository.findOne(driverId);

			if(!driver.equals(null)) {
				if(driver.getDelete_date() == null) {
					boolean isParent = false;
					if(loggedUser.getAccountType().equals(4)) {
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
									if(parentObject.getId().equals(parent.getId())) {
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
		if(userId.equals(0)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser.equals(null)) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser  is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete driver",null);
				 logger.info("************************ deleteDriver ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(!driverId.equals(0)) {
			Driver driver= driverRepository.findOne(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
				 boolean isParent = false;
				 if(loggedUser.getAccountType().equals(4)) {
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
								 if(parentObject.getId().equals(parent.getId())) {
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
		if(userId.equals(0)) {
			List<Driver> unAssignedDrivers = new ArrayList<>();
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			 if(user.getAccountType().equals(4)) {
				 Set<User>parentClient = user.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
					  
						User parent =null;
						for(User object : parentClient) {
							parent = object;
						}
						if(!parent.equals(null)) {
							List<Driver> unAssignedDrivers = driverRepository.getUnassignedDrivers(parent.getId());
							
							getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
							logger.info("************************ getUnassignedDrivers ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);
						}
						else {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						
					}
			 }
			
			
			
			
			

			if(user.equals(null)) {
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
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 Set<User>parentClient = user.getUsersOfUser();
	   					if(parentClient.isEmpty()) {
	   						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
	   						logger.info("************************ editDevice ENDED ***************************");
	   						return ResponseEntity.badRequest().body(getObjectResponse);
	   					}else {
	   					  
	   						User parent =null;
	   						for(User object : parentClient) {
	   							parent = object;
	   						}
	   						if(!parent.equals(null)) {
	   							drivers = driverRepository.getDriverSelect(parent.getId());
	   							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	   							logger.info("************************ getDeviceSelect ENDED ***************************");
	   							return ResponseEntity.ok().body(getObjectResponse);
	   						}
	   						else {
	   							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "No parent for this type 4",null);
	   							return ResponseEntity.badRequest().body(getObjectResponse);
	   						}
	   						
	   					}
	   			 }
	    			
	    			
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

	@Override
	public ResponseEntity<?> assignDriverToUser(String TOKEN,Long userId, Long driverId, Long toUserId) {
		// TODO Auto-generated method stub
		
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId == 0 || driverId == 0 || toUserId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId , driverId and toUserId  are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser.equals(null)) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}else {
				if(!loggedUser.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "assignToUser")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToUser",null);
						 logger.info("************************ assignToUser ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggedUser.getAccountType().equals(3) || loggedUser.getAccountType().equals(4)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign driver to any user",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				Driver driver = getDriverById(driverId);
				if(driver == null) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "driver is not found",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}else {
					if(checkIfParent( driver ,  loggedUser)) {
					     User toUser = userServiceImpl.findById(toUserId);
					     if(toUser == null) {
					    	 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to assign to  is not found",null);
								
								return ResponseEntity.status(404).body(getObjectResponse);
					     }else {
					    	  if(toUser.getAccountType().equals(4)) {
					    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign driver to this user",null);
									
									return ResponseEntity.status(404).body(getObjectResponse);
					    	  }
					    	  
					    	  //to make user assign driver to himself
					    	  else if(loggedUser.getAccountType().equals(toUser.getAccountType())) {
					    		  if(loggedUser.getId() == toUser.getId()) {
					    			  Set<User> driverOldUser = driver.getUserDriver();
						    			 Set<User> temp = driverOldUser;
						    			 driverOldUser.removeAll(temp);
						    			 driver.setUserDriver(driverOldUser);
						    			 driverOldUser.add(toUser);
						    			 driver.setUserDriver(driverOldUser);
						    		     driverRepository.save(driver);
						    		     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "driver assigned successfully",null);
											
										return ResponseEntity.ok().body(getObjectResponse);
					    		  }else {
					    			  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign driver to this user",null);
										
										return ResponseEntity.status(404).body(getObjectResponse);
					    		  }
					    	  }
					    	 List<User>toUserParents = userServiceImpl.getAllParentsOfuser(toUser, toUser.getAccountType());
					    	 if(toUserParents.isEmpty()) {
					    		 
					    		 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign device to this user",null);
									
									return ResponseEntity.status(404).body(getObjectResponse);
					    	 }else {
					    		
					    		 boolean isParent = false;
					    		 for(User object : toUserParents) {
					    			 if(loggedUser.getId().equals(object.getId())) {
					    				 isParent = true;
					    				 break;
					    			 }
					    		 }
					    		 if(isParent) {
					    			 
					    			// assign user to another user
					    			 Set<User> driverOldUser = driver.getUserDriver();
					    			 Set<User> temp = driverOldUser;
					    			 driverOldUser.removeAll(temp);
					    			 driver.setUserDriver(driverOldUser);
					    		     driverOldUser.add(toUser);
					    		     driver.setUserDriver(driverOldUser);;
					    		     driverRepository.save(driver);
					    		     getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "driver assigned successfully",null);
										
									return ResponseEntity.ok().body(getObjectResponse);
					    		     
					    		 }else {
					    			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign driver to this user",null);
										
									 return ResponseEntity.status(404).body(getObjectResponse);
					    		 }
					    	 }
					     }
						
						
					}else {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not allowed  to assign this device",null);
						
						return ResponseEntity.status(404).body(getObjectResponse);
					}
				}
			}
		}
		
	}
	

	
	
	

}
