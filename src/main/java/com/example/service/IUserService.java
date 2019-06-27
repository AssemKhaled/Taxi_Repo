package com.example.service;

import java.util.List;
import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface IUserService {

	public User getName();
	public Set<Device> UserDevice(Long userId);
	public User findById(Long userId);
	public Set<User> usersOfUser(Long userId);
	public User createUser(User user);
	public List<Integer> checkUserDuplication(User user);
	public String deleteUser(User user);


}
