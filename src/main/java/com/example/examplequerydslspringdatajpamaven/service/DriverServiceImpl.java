package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
@Component
public class DriverServiceImpl implements DriverService{

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public List<Driver> getAllDrivers(Long id,int offset,String search) {
		
		//User user=userRepository.getUserData(id);
		//Set<Driver> drivers = user.getDrivers();
		List<Driver> drivers = driverRepository.getAllDrivers(id,offset,search);
		return drivers;
	
	}

	@Override
	public List<Driver> checkDublicateDriverInAdd(Long id, String name, String uniqueId, String mobileNum) {
		
		return driverRepository.checkDublicateDriverInAdd(id,name,uniqueId,mobileNum);

	}
	
	@Override
	public String addDriver(Driver driver,Long id) {
		User userData = userRepository.getUserData(id);
		if(userData != null) {
			Set<User> userDriver = new HashSet<>();
			userDriver.add(userData);
			driver.setUserDriver(userDriver);
			driverRepository.save(driver);
			return "Add successfully";
		}
		else {
			return "no user by this id";
		}
		
		
	}
	
	@Override
	public List<Driver> checkDublicateDriverInEdit(Long driverId, Long userId, String name, String uniqueId,
			String mobileNum) {

		return driverRepository.checkDublicateDriverInEdit(driverId, userId, name, uniqueId, mobileNum);

	}
	
	@Override
	public void editDriver(Driver driver) {
		
		driverRepository.save(driver);
		
	}


	@Override
	public Driver getDriverById(Long driverId) {
		
		return driverRepository.findOne(driverId);
	}
	
	@Override
	public void deleteDriver(Long driverId) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		driverRepository.deleteDriver(driverId,currentDate);
		driverRepository.deleteDriverId(driverId);

		
	}

	
	
	

}
