package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.userClientDriver;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to drivers
 * @author fuinco
 *
 */
@Component
public class DriverServiceImpl extends RestServiceController implements DriverService{

	private static final Log logger = LogFactory.getLog(DriverServiceImpl.class);

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;

	@Autowired
	GroupRepository groupRepository;
	
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserRepository userRepository;
	
	GetObjectResponse getObjectResponse;
	
	@Autowired
	UserClientDriverRepository userClientDriverRepository;
	
	
	/**
	 * get drivers list with limit 10
	 */
	@Override
	public ResponseEntity<?> getAllDrivers(String TOKEN,Long id,int offset,String search,String exportData) {
		
		
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
			if(user == null) {
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
						
						List<Long> driverIds = userClientDriverRepository.getDriverIds(id);
						Integer size = 0;
						if(driverIds.size()>0) {
							if(exportData.equals("exportData")) {
								customDrivers= driverRepository.getAllDriversCustomByIdsExport(driverIds,search);

							}
							else {

								customDrivers= driverRepository.getAllDriversCustomByIds(driverIds,offset,search);
							    size= driverRepository.getAllDriversSizeByIds(driverIds,search);
							}

						}
						 
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
						logger.info("************************ getAllDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
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
					Integer size = 0;
					
					if(exportData.equals("exportData")) {
					    customDrivers= driverRepository.getAllDriversCustomExport(usersIds,search);

					}
					else {
					    customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);
						size= driverRepository.getAllDriversSize(usersIds,search);
					}
					
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
	public List<Driver> checkDublicateDriverInAddEmail(Long id, String name) {
		
		return driverRepository.checkDublicateDriverInAddEmail(id,name);

	}
	@Override
	public List<Driver> checkDublicateDriverInAddUniqueMobile(String uniqueId, String mobileNum, String email) {
		
		return driverRepository.checkDublicateDriverInAddUniqueMobile(uniqueId,mobileNum,email);

	}

	/**
	 * add driver by data in body
	 */
	@Override
	public ResponseEntity<?> addDriver(String TOKEN,Driver driver,Long id) {

		logger.info("************************ addDriver STARTED ***************************");

		String image = driver.getPhoto();
		driver.setPhoto("not_available.png");
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
			if(user == null) {
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
						DecodePhoto decodePhoto=new DecodePhoto();
				    	if(image !=null) {
					    	if(image !="") {
					    		if(image.startsWith("data:image")) {
						        	driver.setPhoto(decodePhoto.Base64_Image(image,"driver"));				
					    		
					    		}
					    	}
						}
							
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
						
						List<Driver> res1=checkDublicateDriverInAddEmail(driverParent.getId(),driver.getName());					    
					    List<Driver> res2=checkDublicateDriverInAddUniqueMobile(driver.getUniqueid(),driver.getMobile_num(),driver.getEmail());
					    List<Integer> duplictionList =new ArrayList<Integer>();

						if(!res1.isEmpty()) {
							for(int i=0;i<res1.size();i++) {
								
								if(res1.get(i).getName().equals(driver.getName())) {
									duplictionList.add(4);				
								}
					
							}
					    	

						}
						
						if(!res2.isEmpty()) {
							for(int i=0;i<res2.size();i++) {
								
								if(res2.get(i).getEmail().equals(driver.getEmail())) {
									duplictionList.add(1);				
								}
								if(res2.get(i).getUniqueid().equals(driver.getUniqueid())) {
									duplictionList.add(2);				
				
								}
								if(res2.get(i).getMobile_num().equals(driver.getMobile_num())) {
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
								
								Set<User> userDriver = new HashSet<>();
								userDriver.add(driverParent);
								driver.setUserDriver(userDriver);
								
								driver.setIs_deleted(null);
								driver.setDelete_date(null);
								
								driverRepository.save(driver);
								drivers.add(driver);
								
								if(user.getAccountType().equals(4)) {
						    		userClientDriver saveData = new userClientDriver();

							        
							        Long DriId = driverRepository.getDriverIdByName(driverParent.getId(),driver.getName(),driver.getUniqueid());
						    		if(DriId != null) {
							    		saveData.setUserid(id);
							    		saveData.setDriverid(DriId);
								        userClientDriverRepository.save(saveData);
						    		}
						    		
						    	}
								
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
	public List<Driver> checkDublicateDriverInEditEmail(Long driverId, Long userId,String name) {

		return driverRepository.checkDublicateDriverInEditEmail(driverId, userId,name);

	}
	@Override
	public List<Driver> checkDublicateDriverInEditMobileUnique(Long driverId, String uniqueId, String mobileNum,String email) {

		return driverRepository.checkDublicateDriverInEditUniqueMobile(driverId, uniqueId, mobileNum, email);

	}
	
	/**
	 * edit driver by id in body mandatory
	 */
	@Override
	public ResponseEntity<?> editDriver(String TOKEN,Driver driver,Long id) {
		logger.info("************************ editDriver STARTED ***************************");
    	String newPhoto= driver.getPhoto();
		
		driver.setPhoto("not_available.png");
			
		
		
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
			if(user == null) {
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
               
                	if(driver.getId() != null) {
                		   	
						Driver driverCheck = driverRepository.findOne(driver.getId());

						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								boolean isParent = false;
								User parent = null;
								if(user.getAccountType().equals(4)) {
									Set<User>parentClient = user.getUsersOfUser();
									if(parentClient.isEmpty()) {
										 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allowed to edit driver",drivers);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}
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
									
									List<Long> driversData = userClientDriverRepository.getDriver(id,driver.getId());
									if(driversData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								}
								else {
									parent = user;
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
									DecodePhoto decodePhoto=new DecodePhoto();
						        	String oldPhoto=driverCheck.getPhoto();
									if(oldPhoto != null) {
							        	if(!oldPhoto.equals("")) {
											if(!oldPhoto.equals("not_available.png")) {
												decodePhoto.deletePhoto(oldPhoto, "driver");
											}
										}
									}

									if(newPhoto == null) {
										driver.setPhoto("not_available.png");
									}
									else {
										if(newPhoto.equals("")) {
											
											driver.setPhoto("not_available.png");				
										}
										else {
											if(newPhoto.equals(oldPhoto)) {
												driver.setPhoto(oldPhoto);				
											}
											else{
												if(newPhoto.startsWith("data:image")) {
										        	driver.setPhoto(decodePhoto.Base64_Image(newPhoto,"driver"));				
									    		
									    		}

											}

									    }
									}
									
									
										
									
									List<Driver> res1=checkDublicateDriverInEditEmail(driver.getId(),parent.getId(),driver.getName());
									List<Driver> res2=checkDublicateDriverInEditMobileUnique(driver.getId(),driver.getUniqueid(),driver.getMobile_num(),driver.getEmail());

									List<Integer> duplictionList =new ArrayList<Integer>();
									
									if(!res1.isEmpty()) {
										for(int i=0;i<res1.size();i++) {
											
											if(res1.get(i).getName().equals(driver.getName())) {
												duplictionList.add(4);				
											}
											
											
										}
								    	

									}
									
									
									if(!res2.isEmpty()) {
										for(int i=0;i<res2.size();i++) {
											if(res2.get(i).getEmail().equals(driver.getEmail())) {
												duplictionList.add(1);				
											}
											if(res2.get(i).getUniqueid().equals(driver.getUniqueid())) {
												duplictionList.add(2);				
							
											}
											if(res2.get(i).getMobile_num().equals(driver.getMobile_num())) {
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
									
									    Set<Device> device = new HashSet<>();
									    device = driverCheck.getDevice();
									    driver.setDevice(device);
									    
									    Set<Group> groups = new HashSet<>();
									    groups = driverCheck.getGroups();
									    driver.setGroups(groups);
									    									    
									    
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


	/**
	 * get driver by id
	 */
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
        if(loggedUser == null) {
        	getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
        }
		if(!driverId.equals(0)) {
			
			Driver driver= driverRepository.findOne(driverId);

			if(driver != null) {
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
						List<Long> driversData = userClientDriverRepository.getDriver(userId,driverId);
						if(driversData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
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
	
	/**
	 * delete driver by id
	 */
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
		if(loggedUser == null) {
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
					List<Long> driversData = userClientDriverRepository.getDriver(userId,driverId);
					if(driversData.isEmpty()) {
							isParent = false;
					}
					else {
							isParent = true;
					}
				 }
				 if(!checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to delete this driver ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					driverRepository.deleteDriver(driverId,currentDate);
					driverRepository.deleteDriverDeviceId(driverId);
					driverRepository.deleteDriverGroupId(driverId);
					
					
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

	/**
	 * get unassigned drivers to any device
	 */
	@Override
	public ResponseEntity<?> getUnassignedDrivers(String TOKEN,Long loggedUserId,Long userId,Long deviceId) {
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
		List<Map> data = new ArrayList<>();
		List<Driver> deviceDriver = new ArrayList<>();
	    Map obj = new HashMap();
	    
		if(deviceId != 0) {
			Device device = deviceServiceImpl.findById(deviceId);
			if(device != null) {
				Set<Driver> drivers=new HashSet<>() ;
				drivers = device.getDriver();
				if(!drivers.isEmpty()) {
					for(Driver driver : drivers ) {
						deviceDriver.add(driver);
					}

				}
				

			}

		}
		obj.put("selectedDrivers", deviceDriver);
		
		List<Driver> unAssignedDrivers = new ArrayList<>();

		if(loggedUserId != 0) {
	    	User loggedUser = userServiceImpl.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long>usersIds= new ArrayList<>();
					    usersIds.add(loggedUserId);
	                    unAssignedDrivers = driverRepository.getUnassignedDriversByIds(usersIds);
							
	            		obj.put("drivers", unAssignedDrivers);
	            		data.add(obj);

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getUnassignedDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			if(user == null) {
				
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",unAssignedDrivers);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if(user.getAccountType().equals(4)) {
					 

					List<Long>usersIds= new ArrayList<>();
				    usersIds.add(userId);
                    unAssignedDrivers = driverRepository.getUnassignedDriversByIds(usersIds);
            		obj.put("drivers", unAssignedDrivers);
            		data.add(obj);				
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
					logger.info("************************ getUnassignedDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				 }
				
				 List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
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
				 
				unAssignedDrivers = driverRepository.getUnassignedDrivers(usersIds);
        		obj.put("drivers", unAssignedDrivers);
        		data.add(obj);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
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

			return null;
		}
		else
		{
			return driver;
		}
	}

	/**
	 * get driver select list
	 */
	@Override
	public  ResponseEntity<?> getDriverSelect(String TOKEN,Long loggedUserId,Long userId) {

		logger.info("************************ getDriverSelect STARTED ***************************");
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
	    				 List<Long> driverIds = userClientDriverRepository.getDriverIds(loggedUserId);

	    				 if(driverIds.size()>0) {
	    					 drivers = driverRepository.getDriverSelectByIds(driverIds);

	    				 }
	    				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	    				 logger.info("************************ getDriverSelect ENDED ***************************");
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
	   				 

	    				
	    				 List<Long> driverIds = userClientDriverRepository.getDriverIds(userId);

	    				 if(driverIds.size()>0) {
	    					 drivers = driverRepository.getDriverSelectByIds(driverIds);

	    				 }
	    				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
	    				 logger.info("************************ getDriverSelect ENDED ***************************");
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
	    			
	    			drivers = driverRepository.getDriverSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getDriverSelect ENDED ***************************");
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
	 * get selected driver of group
	 */
	@Override
	public  ResponseEntity<?> getDriverSelectGroup(String TOKEN,Long loggedUserId,Long userId,Long groupId) {

		logger.info("************************ getDriverSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
		List<DeviceSelect> selectedDrivers = new ArrayList<DeviceSelect>();

	    
		if(groupId != 0) {
			selectedDrivers = groupRepository.getGroupDriverSelect(groupId);

		}
		obj.put("selectedDrivers", selectedDrivers);
		
		if(loggedUserId != 0) {
	    	User loggedUser = userServiceImpl.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDelete_date() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				 List<Long> driverIds = userClientDriverRepository.getDriverIds(loggedUserId);

	    				 if(driverIds.size()>0) {
	    					 drivers = driverRepository.getDriverSelectByIds(driverIds);

	    				 }
	    				 
	    				 obj.put("drivers", drivers);
	    				 data.add(obj);

	    				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
	    				 logger.info("************************ getDriverSelect ENDED ***************************");
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
	   				 

	    				 List<Long> driverIds = userClientDriverRepository.getDriverIds(userId);

	    				 if(driverIds.size()>0) {
	    					 drivers = driverRepository.getDriverSelectByIds(driverIds);

	    				 }
	    				 obj.put("drivers", drivers);
	    				 data.add(obj);
	    				 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
	    				 logger.info("************************ getDriverSelect ENDED ***************************");
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

	    			drivers = driverRepository.getDriverSelect(usersIds);
	    			
   				 obj.put("drivers", drivers);
   				 data.add(obj);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
					logger.info("************************ getDriverSelect ENDED ***************************");
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
	 
	 
    /**
     * assign driver to user used by type admin or vendor only
     */
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
			if(loggedUser == null) {
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
					    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign driver to this user type 4 assign to his parents",null);
								  return ResponseEntity.status(404).body(getObjectResponse);
					    	  }
					    	  

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
	

	/**
	 * assign driver to type 4 from type 3
	 */
	@Override
	public ResponseEntity<?> assignClientDrivers(String TOKEN, Long loggedUserId, Long userId, Long[] driverIds) {
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
		
        if(driverIds.length > 0 && driverIds[0] != 0) {
			
        	List<userClientDriver> checkData = userClientDriverRepository.getDriversByDriIds(driverIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is driver assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
			
			for(Long id:driverIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Driver assignedDriver = getDriverById(id);
				if(assignedDriver == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned Driver is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientDriverRepository.deleteDriversByUserId(userId);
			for(Long assignedId:driverIds) {
				userClientDriver userDriver = new userClientDriver();
				userDriver.setUserid(userId);
				userDriver.setDriverid(assignedId);
				userClientDriverRepository.save(userDriver);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientDriver> drivers = userClientDriverRepository.getDriversOfUser(userId);
			
			if(drivers.size() > 0) {

				userClientDriverRepository.deleteDriversByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no drivers for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	/**
	 * get drivers who selected to user type 4
	 */
	@Override
	public ResponseEntity<?> getClientDrivers(String TOKEN, Long loggedUserId, Long userId) {
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

		List<DriverSelect> drivers = userClientDriverRepository.getDriversOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	/**
	 * get unselect driver of type 3 to any type 4
	 */
	@Override
	public ResponseEntity<?> getDriverUnSelectOfClient(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getDriverSelect STARTED ***************************");
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
		
		drivers = driverRepository.getDriverUnSelectOfClient(loggedUserId,userId);
		List<DriverSelect> selectedDrivers = userClientDriverRepository.getDriversOfUserList(userId);
		
		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedDrivers", selectedDrivers);
	    obj.put("drivers", drivers);

	    data.add(obj);
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getDriverSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	    


	}
	
	

}
