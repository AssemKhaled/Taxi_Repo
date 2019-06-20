package com.example.service;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getName() {
		User x = userRepository.getName();
		//System.out.println(userRepository);

		return x;
	}

	@Override
	public Set<Device> UserDevice() {
		// TODO Auto-generated method stub
		User x=userRepository.getAll();
	    Set<Device> devices = x.getDevices();
		return devices ;
	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
		return user;
	}

	@Override
	public Set<User> getAllUsers(int id) {
		User user=userRepository.getUserData(id);
		Set<User> users = user.getUsers();
		
		return users;
	}
	

}
