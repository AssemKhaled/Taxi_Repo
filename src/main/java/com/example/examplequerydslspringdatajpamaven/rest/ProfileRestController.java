package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
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
	
	@RequestMapping(value = "/updateProfile/{userId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updateProfile(@RequestBody(required = false) User user ,@PathVariable (value = "userId") Long userId) {

		User Data= profileServiceImpl.getUserInfo(userId);
		
		String password=Data.getPassword();
		user.setId(userId);
		user.setPassword(password);
		user.setUsersOfUser(Data.getUsersOfUser());
		 Long id = user.getId();
		 String email = user.getEmail();
		 String identityNum = user.getIdentity_num();
		 String commercialNum = user.getCommercial_num();
		 String	companyPhone = user.getCompany_phone();
		 String managerPhone = user.getManager_phone();
		 String managerMobile = user.getManager_mobile();
		 String phone = user.getPhone();
		
		List<User> checkDublicate=profileServiceImpl.checkDublicate(id,email, identityNum, commercialNum, companyPhone, managerPhone, managerMobile,phone);
		if(!checkDublicate.isEmpty()) {
			List<Integer>duplicationCodes = new ArrayList<Integer>();
			for(int i=0;i<checkDublicate.size();i++) {
				if(checkDublicate.get(i).getEmail().equalsIgnoreCase(email)) {
					duplicationCodes.add(1);
		
				}
				else if(checkDublicate.get(i).getIdentity_num().equalsIgnoreCase(identityNum)) {
					duplicationCodes.add(2);
	
				}
				else if(checkDublicate.get(i).getCommercial_num().equalsIgnoreCase(commercialNum)) {
					duplicationCodes.add(3);

				}
				else if(checkDublicate.get(i).getCompany_phone().equalsIgnoreCase(companyPhone)) {
					duplicationCodes.add(4);

				}
				else if(checkDublicate.get(i).getManager_phone().equalsIgnoreCase(managerPhone)) {
					duplicationCodes.add(5);

				}
				else if(checkDublicate.get(i).getManager_mobile().equalsIgnoreCase(managerMobile)) {
					duplicationCodes.add(6);

				}
				else if(checkDublicate.get(i).getPhone().equalsIgnoreCase(phone)) {
					duplicationCodes.add(7);

				}
				
			}
			return ResponseEntity.ok(duplicationCodes);


		}
		else {
			
			return ResponseEntity.ok(profileServiceImpl.updateProfile(user));

		}
	
		
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
