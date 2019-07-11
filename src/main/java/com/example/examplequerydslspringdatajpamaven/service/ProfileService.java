package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface ProfileService {
	
	public User getUserInfo(Long userId);
		
	public List<User> checkDublicate(Long id,String email,String identityNum,String commercialNum,String companyPhone,String managerPhone,String managerMobile,String phone);
	
	public String updateProfile(User user);



}
