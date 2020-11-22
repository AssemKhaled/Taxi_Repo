package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface ProfileService {
	
	public ResponseEntity<?> getUserInfo(String TOKEN,Long userId);
	
	public User getUserInfoObj(Long userId);

	public ResponseEntity<?>  updateProfileInfo(String TOKEN,User user,Long userId);
	
	public ResponseEntity<?> updateProfilePassword(String TOKEN,Map<String, String> data,String check,Long userId);

	public ResponseEntity<?> updateProfilePhoto(String TOKEN,Map<String, String> data,Long userId);

	public ResponseEntity<?> restPassword(String TOKEN,Long loggedUserId,Long userId,Map<String, String> data);

	



}
