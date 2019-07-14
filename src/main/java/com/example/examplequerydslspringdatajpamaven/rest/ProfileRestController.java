package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

@CrossOrigin
@RestController
@RequestMapping(path = "/profile")
public class ProfileRestController {

	@Autowired
	ProfileServiceImpl profileServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@RequestMapping(value = "/getProfileInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfo(@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User user =profileServiceImpl.getUserInfo(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

			}
			else {
				if(user.getDelete_date() == null) {
					
					users.add(user);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",users);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

				}
			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);

		}
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/changePassowrd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrd(@RequestBody Map<String, String> data ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User user =profileServiceImpl.getUserInfo(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

			}
			else {
				if(user.getDelete_date() == null) {
					
					if(data.get("oldPassword") == null || data.get("newPassword") == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "newPassword and oldPassword is Required",users);

					}
					else {
						String hashedPassword = userServiceImpl.getMd5(data.get("oldPassword").toString());
						String newPassword= userServiceImpl.getMd5(data.get("newPassword").toString());
						String oldPassword= user.getPassword();
						
						if(hashedPassword.equals(oldPassword)){
							user.setPassword(newPassword);
							String result = profileServiceImpl.updateProfile(user);
							users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), result ,users);

						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Wrong oldPassword",users);

						}
						

					}
					

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

				}
			}
		
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
		}
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updateProfile(@RequestBody(required = false) User user ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId != 0) {
			User Data =profileServiceImpl.getUserInfo(userId);
			if(Data == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

			}
			else {
				if(Data.getDelete_date() == null) {
					if(user.getEmail() == null || user.getIdentity_num() == null || user.getCommercial_num() == null 
							|| user.getCompany_phone() == null || user.getManager_phone() == null
							|| user.getManager_mobile() ==null ||user.getPhone() == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User {Email , Identity Number , commercial Numebr , Company Phone , Phone Manager , Manager Mobile , Phone } is Required",users);

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
					    }
					    else {
					    	String result = profileServiceImpl.updateProfile(user);
					    	users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), result ,users);

					    }


					}
					

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

				}
		    }
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
		}
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
	@RequestMapping(value = "/updatePhoto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhoto(@RequestBody Map<String, String> data ,@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(userId !=0) {
			User user =profileServiceImpl.getUserInfo(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

			}
			else {
				if(user.getDelete_date() == null) {
					if(data.get("photo") ==null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "photo is Required",users);

					}
					else {
						String photo = data.get("photo").toString();
						if(photo.equals("")) {
							user.setPhoto("Not-available.png");				
						}
						else {
							//base64_Image
							DecodePhoto decodePhoto=new DecodePhoto();
							user.setPhoto(decodePhoto.Base64_Image(photo));
					    }
						String result = profileServiceImpl.updateProfile(user);
						users.add(user);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), result ,users);

					}
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);

				}
			}
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",users);
		}
    	return  ResponseEntity.ok(getObjectResponse);

	}
	
}
