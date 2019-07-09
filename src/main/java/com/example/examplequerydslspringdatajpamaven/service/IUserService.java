package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface IUserService {

	public User getName();
//	public ResponseEntity<?> UserDevice(Long userId,int offset, String search);
	public User findById(Long userId);
	public ResponseEntity<?> findUserById(Long userId);
	public ResponseEntity<?> usersOfUser(Long userId,int offset,String search);
	public ResponseEntity<?> createUser(User user,Long userId);
	public ResponseEntity<?> editUser(User user,Long userId);
	public List<Integer> checkUserDuplication(User user);
	public ResponseEntity<?> deleteUser(User user);


}
