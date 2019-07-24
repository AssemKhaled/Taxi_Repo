package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface LoginService {

	public ResponseEntity<?> login(String authorization);
	public ResponseEntity<?> logout(String token);
	
	
}
