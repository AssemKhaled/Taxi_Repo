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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class DriverServiceImpl implements DriverService{

	private static final Log logger = LogFactory.getLog(DriverServiceImpl.class);

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserRepository userRepository;
	
	GetObjectResponse getObjectResponse;
	
	@Override
	public ResponseEntity<?> getAllDrivers(Long id,int offset,String search) {
		
		//User user=userRepository.getUserData(id);
		//Set<Driver> drivers = user.getDrivers();
		
		logger.info("************************ getAllDrivers STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();

		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					drivers = driverRepository.getAllDrivers(id,offset,search);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
					logger.info("************************ getAllDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
				
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
	public ResponseEntity<?> addDriver(Driver driver,Long id) {

		logger.info("************************ addDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date()==null) {
					
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
							if(driver.getId() == null) {
								Set<User> userDriver = new HashSet<>();
								userDriver.add(user);
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
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

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
	public ResponseEntity<?> editDriver(Driver driver,Long id) {
		logger.info("************************ editDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
                if(user.getDelete_date()==null) {
                	if(driver.getId() != null) {
                		   	
						Driver driverCheck = driverRepository.findOne(driver.getId());

						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								
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
										userDriver.add(user);
										driver.setUserDriver(userDriver);
										if(driverCheck.getUserDriver().equals(driver.getUserDriver())) {
											driverRepository.save(driver);
											drivers.add(driver);
											getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",drivers);
											logger.info("************************ editDriver ENDED ***************************");
											return ResponseEntity.ok().body(getObjectResponse);

										}
										else {
											getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this driver it belongs to another user",drivers);
											return ResponseEntity.status(404).body(getObjectResponse);

										}
										
										
									
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
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
				
				
			}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
		

		
	}


	@Override
	public ResponseEntity<?> findById(Long driverId) {
		logger.info("************************ getDriverById STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();

		if(driverId != 0) {
			
			Driver driver= driverRepository.findOne(driverId);

			if(driver != null) {
				if(driver.getDelete_date() == null) {
					
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
	public ResponseEntity<?> deleteDriver(Long driverId) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		logger.info("************************ deleteDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(driverId != 0) {
			Driver driver= driverRepository.findOne(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
				
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
	public ResponseEntity<?> getUnassignedDrivers(Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
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
	public Integer getTotalNumberOfUserDrivers(Long userId) {
		// TODO Auto-generated method stub
		 Integer totalNumberOfUserDrivers = driverRepository.getTotalNumberOfUserDrivers(userId);
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

	
	
	

}
