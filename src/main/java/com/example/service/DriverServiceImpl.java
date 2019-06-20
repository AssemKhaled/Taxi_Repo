package com.example.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

public class DriverServiceImpl implements DriverService{

	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public Set<Driver> getAllDrivers(int id) {
		
		User user=userRepository.getUserData(id);
		Set<Driver> drivers = user.getDrivers();		
		return drivers;
	
	}

	@Override
	public List<Driver> checkDublicateDriverInAdd(int id, String name, String uniqueId, String mobileNum) {
		
		return driverRepository.checkDublicateDriverInAdd(id,name,uniqueId,mobileNum);

	}
	
	@Override
	public String addDriver(Driver driver,int id) {
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
	public List<Driver> checkDublicateDriverInEdit(int driverId, int userId, String name, String uniqueId,
			String mobileNum) {

		return driverRepository.checkDublicateDriverInEdit(driverId, userId, name, uniqueId, mobileNum);

	}
	
	@Override
	public void editDriver(Driver driver) {
		
		driverRepository.save(driver);
		
	}


	@Override
	public Driver getDriverById(int driverId) {
		
		return driverRepository.getDriverById(driverId);
	}
	
	@Override
	public void deleteDriver(int driverId) {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		driverRepository.deleteDriver(driverId,currentDate);
		driverRepository.deleteDriverId(driverId);

		
	}

	
	
	

}
