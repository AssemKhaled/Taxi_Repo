package com.example.examplequerydslspringdatajpamaven.service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface LoginService {

	public User login(String authorization);
	
}
