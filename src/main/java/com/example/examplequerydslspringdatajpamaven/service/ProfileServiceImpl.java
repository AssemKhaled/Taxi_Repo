package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ProfileRepository;

public class ProfileServiceImpl implements ProfileService{

	@Autowired
	ProfileRepository profileRepository;
	
	
	@Override
	public User getUserInfo(Long userId) {
		
		return profileRepository.findOne(userId);
	}

	@Override
	public String updatePassword(User user) {
		
		profileRepository.save(user);
		return "updated successfully";
	}

	@Override
	public String updateProfile(User user) {
		
		
		profileRepository.save(user);
		return "updated successfully";
		
	}

	@Override
	public List<User> checkDublicate(Long id,String email, String identityNum, String commercialNum, String companyPhone,
			String managerPhone, String managerMobile, String phone) {
		return profileRepository.checkUserDuplication(id, email, identityNum, commercialNum, companyPhone, managerPhone, managerMobile, phone);
	}

}
