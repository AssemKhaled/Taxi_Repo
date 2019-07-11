package com.example.examplequerydslspringdatajpamaven.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ProfileRepository;

public class ProfileServiceImpl implements ProfileService{

	@Autowired
	ProfileRepository profileRepository;
	
	private static final Log logger = LogFactory.getLog(ProfileServiceImpl.class);

	@Override
	public User getUserInfo(Long userId) {
		
		logger.info("************************ getUserInfo STARTED ***************************");

		User user = profileRepository.findOne(userId);
		
		logger.info("************************ getUserInfo ENDED ***************************");

		return user;
	}


	@Override
	public String updateProfile(User user) {
		
		logger.info("************************ updateProfile STARTED ***************************");

		profileRepository.save(user);
		
		logger.info("************************ updateProfile ENDED ***************************");
		return "updated successfully";

	}


}
