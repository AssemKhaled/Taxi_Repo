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
	public ResponseEntity<?> findUserById(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> usersOfUser(String TOKEN,Long userId,int offset,String search,int active);
	public ResponseEntity<?> createUser(String TOKEN,User user,Long userId);
	public ResponseEntity<?> editUser(String TOKEN,User user,Long userId);
	public List<Integer> checkUserDuplication(User user);
	public ResponseEntity<?> deleteUser(String TOKEN,Long userId , Long deleteUserId);
	public ResponseEntity<?> getUserRole(Long userId);
	public Boolean checkIfParentOrNot(Long parentId,Long childId,Integer parentType ,Integer childTye);
	public ResponseEntity<?> saveUser(Long parentId , User user);
	public List<User> getAllParentsOfuser(User user,Integer accountType);

	
	public ResponseEntity<?> getUserSelect(String TOKEN,Long userId);
	public ResponseEntity<?> getVendorSelect(String TOKEN,Long userId);
	public ResponseEntity<?> getClientSelect(String TOKEN,Long vendorId);

	
	public List<User>getAllChildernOfUser(Long userId);
	public List<User>getActiveAndInactiveChildern(Long userId);
	public void resetChildernArray();



}
