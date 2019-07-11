package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface ProfileService {
	
	public User getUserInfo(Long userId);
			
	public String updateProfile(User user);



}
