package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class DriverServiceImpl implements DriverService{

	private static final Log logger = LogFactory.getLog(DriverServiceImpl.class);

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	UserRepository userRepository;
	
	GetObjectResponse getObjectResponse;
	
	@Override
	public List<Driver> getAllDrivers(Long id,int offset,String search) {
		
		//User user=userRepository.getUserData(id);
		//Set<Driver> drivers = user.getDrivers();
		logger.info("************************ getAllDrivers STARTED ***************************");

		List<Driver> drivers = driverRepository.getAllDrivers(id,offset,search);
		
		logger.info("************************ getAllDrivers ENDED ***************************");
		
		return drivers;
	
	}

	@Override
	public List<Driver> checkDublicateDriverInAdd(Long id, String name, String uniqueId, String mobileNum) {
		
		return driverRepository.checkDublicateDriverInAdd(id,name,uniqueId,mobileNum);

	}
	
	@Override
	public String addDriver(Driver driver) {
		
		logger.info("************************ addDriver STARTED ***************************");

		driverRepository.save(driver);
		
		logger.info("************************ addDriver ENDED ***************************");

		return "Added successfully";
		
		
	}
	
	@Override
	public List<Driver> checkDublicateDriverInEdit(Long driverId, Long userId, String name, String uniqueId,
			String mobileNum) {

		return driverRepository.checkDublicateDriverInEdit(driverId, userId, name, uniqueId, mobileNum);

	}
	
	@Override
	public void editDriver(Driver driver) {
		logger.info("************************ editDriver STARTED ***************************");

		driverRepository.save(driver);
		
		logger.info("************************ editDriver ENDED ***************************");

		
	}


	@Override
	public Driver getDriverById(Long driverId) {
		logger.info("************************ getAllDrivers STARTED ***************************");
		
		Driver driver= driverRepository.findOne(driverId);

		logger.info("************************ getAllDrivers ENDED ***************************");

		return driver;
	}
	
	@Override
	public void deleteDriver(Long driverId) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		logger.info("************************ deleteDriver STARTED ***************************");

		driverRepository.deleteDriver(driverId,currentDate);
		driverRepository.deleteDriverId(driverId);
		driverRepository.deleteDriverDeviceId(driverId);

		logger.info("************************ deleteDriver ENDED ***************************");

	}

	@Override
	public ResponseEntity<?> getUnassignedDrivers(Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
		if(userId == 0) {
			List<Driver> unAssignedDrivers = new ArrayList<>();
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",unAssignedDrivers);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		List<Driver> unAssignedDrivers = driverRepository.getUnassignedDrivers(userId);
		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",unAssignedDrivers);
		logger.info("************************ getUnassignedDrivers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
		
	}

	
	
	

}
