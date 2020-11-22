package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

	public ResponseEntity<?> login(String authorization);
	public ResponseEntity<?> logout(String token);
		
}
