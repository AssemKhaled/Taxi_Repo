package com.example.service;

import java.util.List;
import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;

public interface DriverService {
	
	public Set<Driver> getAllDrivers(int id);
	public Driver getDriverById(int driverId);
	public void deleteDriver(int driverId);
	public List<Driver> checkDublicateDriverInAdd(int userId,String name,String uniqueId,String mobileNum);
	public String addDriver(Driver driver,int id);
	public List<Driver> checkDublicateDriverInEdit(int driverId,int userId,String name,String uniqueId,String mobileNum);
	public void editDriver(Driver driver);



}
