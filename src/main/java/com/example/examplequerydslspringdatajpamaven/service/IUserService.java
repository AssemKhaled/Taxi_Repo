package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface IUserService {

	public User getName();
	public User findById(Long userId);
	public ResponseEntity<?> findUserById(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> usersOfUser(String TOKEN,Long userId,Long loggedUserId,int offset,String search,int active,String exportData);
	public ResponseEntity<?> createUser(String TOKEN,User user,Long userId);
	public ResponseEntity<?> editUser(String TOKEN,User user,Long userId);
	public List<Integer> checkUserDuplication(User user);
	public ResponseEntity<?> deleteUser(String TOKEN,Long userId , Long deleteUserId);
	public ResponseEntity<?> activeUser(String TOKEN,Long userId , Long activeUserId);

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

	public ResponseEntity<?> getUserSelectWithChild(String TOKEN,Long userId);



}
