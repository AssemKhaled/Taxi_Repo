package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;



@RestController
@RequestMapping(path = "/drivers")
@CrossOrigin
public class DriverRestController {
	
	@Autowired
	DriverServiceImpl driverServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@RequestMapping(value = "/getAllDrivers", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDrivers(@RequestParam (value = "userId", defaultValue = "0") Long id,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "search", defaultValue = "") String search) {
		
		offset=offset-1;
		if(offset <0) {
			offset=0;
		}
		GetObjectResponse getObjectResponse ;
		List<Driver> drivers = new ArrayList<Driver>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
			}
			else {
				if(user.getDelete_date() == null) {
					drivers =driverServiceImpl.getAllDrivers(id,offset,search);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				}
				
			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);

		}
		
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/getDriverById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverById(@RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		GetObjectResponse getObjectResponse;
		List<Driver> drivers = new ArrayList<Driver>();

		if(driverId != 0) {
			
			Driver driver=driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
					
					drivers.add(driver);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",drivers);
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);

				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
			}
			
						
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);


		}
		return ResponseEntity.ok(getObjectResponse);

	}
	
	
	@RequestMapping(value = "/deleteDriver", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriver(@RequestParam (value = "driverId", defaultValue = "0") Long driverId) {
		
		GetObjectResponse getObjectResponse;
		List<Driver> drivers = new ArrayList<Driver>();
		if(driverId != 0) {
			Driver driver= driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
				
					driverServiceImpl.deleteDriver(driverId);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Deleted Successfully",drivers);
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver was Deleted Before",drivers);

				}
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);

		}
		
		return ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/addDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody(required = false) Driver driver,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		GetObjectResponse getObjectResponse;
		List<Driver> drivers = new ArrayList<Driver>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
			}
			else {
				if(user.getDelete_date()==null) {
					
					if(driver.getName()== null || driver.getUniqueid()== null
							   || driver.getMobile_num() == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);

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
						
						List<Driver> res=driverServiceImpl.checkDublicateDriverInAdd(id,driver.getName(),driver.getUniqueid(),driver.getMobile_num());
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

						}
						else {
							Set<User> userDriver = new HashSet<>();
							userDriver.add(user);
							driver.setUserDriver(userDriver);
							String resut=driverServiceImpl.addDriver(driver);
							drivers.add(driver);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resut,drivers);

							
						
						}
						
					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				}
			}
			
			

		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			
		}
		return ResponseEntity.ok(getObjectResponse);

		
	}	
	@RequestMapping(value = "/editDriver", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editDriver(@RequestBody(required = false) Driver driver,@RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		GetObjectResponse getObjectResponse;
		List<Driver> drivers = new ArrayList<Driver>();
		if(id != 0) {
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
			}
			else {
                if(user.getDelete_date()==null) {
                	if(driver.getId() != null) {
                		   	
						Driver driverCheck = driverServiceImpl.getDriverById(driver.getId());
						if(driverCheck != null) {
							if(driverCheck.getDelete_date() == null) {
								
								if(driver.getName()== null || driver.getUniqueid()== null
										   || driver.getMobile_num() == null) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver name , uniqueid and mobile number is Required",drivers);
			
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
									
									List<Driver> res=driverServiceImpl.checkDublicateDriverInEdit(driver.getId(),id,driver.getName(),driver.getUniqueid(),driver.getMobile_num());
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
			
									}
									else {
										
										Set<User> userDriver = new HashSet<>();
										userDriver.add(user);
										driver.setUserDriver(userDriver);
										if(driverCheck.getUserDriver().equals(driver.getUserDriver())) {
											driverServiceImpl.editDriver(driver);
											drivers.add(driver);
											getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",drivers);
				
										}
										else {
											getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),"Not allow to edit this driver it belongs to another user",drivers);

										}
										
										
									
									}
								}
							}
							else {
								getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);

							}
							
							
							
						}
						else{
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
	
						}
                	}
                	else {
            			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);

                	}
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",drivers);
				}
				
				
			}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);

			
		}
		
		return ResponseEntity.ok(getObjectResponse);

	}	
	
	// added by Maryam
	@GetMapping(path = "/getUnassignedDrivers")
	public ResponseEntity<?> getUnassignedDrivers(@RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return driverServiceImpl.getUnassignedDrivers(userId);
	}

	
	

}
