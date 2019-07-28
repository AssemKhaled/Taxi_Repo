package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface ProfileService {
	
	public ResponseEntity<?> getUserInfo(String TOKEN,Long userId);
	
	public User getUserInfoObj(Long userId);

	public ResponseEntity<?>  updateProfileInfo(String TOKEN,User user,Long userId);
	
	public ResponseEntity<?> updateProfilePassword(String TOKEN,Map<String, String> data,Long userId);

	public ResponseEntity<?> updateProfilePhoto(String TOKEN,Map<String, String> data,Long userId);

	
	



}
