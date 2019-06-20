package com.example.service;

import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface IUserService {

	public User getName();
	public Set<Device> UserDevice();
	public  User findById(Long userId);
	
	public Set<User> getAllUsers(int id);


}
