package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.ProfileRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

public class ProfileServiceImpl implements ProfileService{

	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	GetObjectResponse getObjectResponse;

	private static final Log logger = LogFactory.getLog(ProfileServiceImpl.class);

	@Override
	public ResponseEntity<?> getUserInfo(Long userId) {
		
		logger.info("************************ getUserInfo STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User user = profileRepository.findOne(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					
					users.add(user);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",users);
					logger.info("************************ getUserInfo ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}


	@Override
	public ResponseEntity<?>  updateProfileInfo(User user,Long userId) {
		
		logger.info("************************ updateProfile STARTED ***************************");

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User Data =getUserInfoObj(userId);
			if(Data == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(Data.getDelete_date() == null) {
					if(user.getEmail() == null || user.getIdentity_num() == null || user.getCommercial_num() == null 
							|| user.getCompany_phone() == null || user.getManager_phone() == null
							|| user.getManager_mobile() ==null ||user.getPhone() == null
							|| user.getEmail() == "" || user.getIdentity_num() == "" || user.getCommercial_num() == "" 
							|| user.getCompany_phone() == "" || user.getManager_phone() == ""
							|| user.getManager_mobile() =="" ||user.getPhone() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User {Email , Identity Number , commercial Numebr , Company Phone , Phone Manager , Manager Mobile , Phone } is Required",users);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						user.setId(userId);
						user.setPassword(Data.getPassword());
						user.setUsersOfUser(Data.getUsersOfUser());
						user.setDevices(Data.getDevices());
						user.setDrivers(Data.getDrivers());
						user.setGeofences(Data.getGeofences());
					    List<Integer> duplictionList = userServiceImpl.checkUserDuplication(user);
					    if(duplictionList.size()>0) {
					    	getObjectResponse= new GetObjectResponse(501, "was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

					    }
					    else {
							profileRepository.save(user);
					    	users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
							logger.info("************************ updateProfile ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);

					    }


					}
					

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}

	@Override
	public ResponseEntity<?> updateProfilePassword(Map<String, String> data,Long userId) {
		
		logger.info("************************ updateProfilePassword STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User user =getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					
					if(data.get("oldPassword") == null || data.get("newPassword") == null ||
							data.get("oldPassword") == "" || data.get("newPassword") == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "newPassword and oldPassword is Required",users);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						String hashedPassword = userServiceImpl.getMd5(data.get("oldPassword").toString());
						String newPassword= userServiceImpl.getMd5(data.get("newPassword").toString());
						String oldPassword= user.getPassword();
						
						if(hashedPassword.equals(oldPassword)){
							user.setPassword(newPassword);
							profileRepository.save(user);
							users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
							logger.info("************************ updateProfilePassword ENDED ***************************");
							return ResponseEntity.ok().body(getObjectResponse);

						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Wrong oldPassword",users);
							return ResponseEntity.status(404).body(getObjectResponse);

						}
						

					}
					

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
		
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	}
	
	@Override
	public ResponseEntity<?> updateProfilePhoto(Map<String, String> data,Long userId){
		
		logger.info("************************ updateProfile STARTED ***************************");

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId !=0) {
			User user = getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					if(data.get("photo") ==null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Photo is Required",users);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						String photo = data.get("photo").toString();
						if(photo.equals("")) {
							user.setPhoto("Not-available.jpg");				
						}
						else {
							//base64_Image
							DecodePhoto decodePhoto=new DecodePhoto();
							user.setPhoto(decodePhoto.Base64_Image(photo));
					    }
						profileRepository.save(user);
						users.add(user);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
						logger.info("************************ updateProfile ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);

					}
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
					return ResponseEntity.status(404).body(getObjectResponse);

				}
			}
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	@Override
	public User getUserInfoObj(Long userId) {
		
		User user = profileRepository.findOne(userId);
		if(user == null) {
			return null;
		}
		if(user.getDelete_date() != null) {
			//throw not found 
			return null;
		}
		else
		{
			return user;
		}
		
	}




}
