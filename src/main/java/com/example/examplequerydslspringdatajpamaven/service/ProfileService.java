package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface ProfileService {
	
	public ResponseEntity<?> getUserInfo(Long userId);
	
	public User getUserInfoObj(Long userId);

	public ResponseEntity<?>  updateProfileInfo(User user,Long userId);
	
	public ResponseEntity<?> updateProfilePassword(Map<String, String> data,Long userId);

	public ResponseEntity<?> updateProfilePhoto(Map<String, String> data,Long userId);

	
	



}
