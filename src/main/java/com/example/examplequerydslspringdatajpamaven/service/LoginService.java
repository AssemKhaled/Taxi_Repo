package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface LoginService {

	public ResponseEntity<?> login(String authorization);
	public ResponseEntity<?> logout(String token);
	
	public ResponseEntity<?> getBilling(String Token,Long loggedId,Long userId,String start,String end,int offset,String search);

	
	
}
