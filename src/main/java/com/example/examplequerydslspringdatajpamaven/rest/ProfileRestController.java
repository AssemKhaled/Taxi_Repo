package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.service.ProfileServiceImpl;
import com.example.service.UserServiceImpl;

@CrossOrigin
@RestController
@RequestMapping(path = "/profile")
public class ProfileRestController {

	@Autowired
	ProfileServiceImpl profileServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@RequestMapping(value = "/getProfileInfo/{userId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfo(@PathVariable (value = "userId") Long userId) {
		
		if(userId != 0) {
			
			return  ResponseEntity.ok(profileServiceImpl.getUserInfo(userId));
						
		}
		else {
			
			return ResponseEntity.ok("no user selected");

		}
		
	}
	
	@RequestMapping(value = "/changePassowrd/{userId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrd(@RequestBody Map<String, String> data ,@PathVariable (value = "userId") Long userId) {

		User user= new User();
		String hashedPassword =null;
		String newPassword=null;
		String oldPassword=null;

		if(data.get("oldPassword")!=null) {
			hashedPassword = userServiceImpl.getMd5(data.get("oldPassword").toString());
			user = profileServiceImpl.getUserInfo(userId);
			oldPassword = user.getPassword();
			
			if(hashedPassword.equalsIgnoreCase(oldPassword)) {
				
				if(data.get("newPassword")!=null) {
					newPassword = userServiceImpl.getMd5(data.get("newPassword").toString());
					user.setId(userId);
					user.setPassword(newPassword);
					return  ResponseEntity.ok(profileServiceImpl.updatePassword(user));


				}
				else {
					return  ResponseEntity.ok("no new password to update");
	
				}


			}
			else {
				return  ResponseEntity.ok("wrong old password");

			}
		}
		else {
			
			return  ResponseEntity.ok("no old password to check");
		
		}
		
		
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
	
	@RequestMapping(value = "/updatePhoto/{userId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhoto(@RequestBody Map<String, String> data ,@PathVariable (value = "userId") Long userId) {
		User user=profileServiceImpl.getUserInfo(userId);
		if(data.get("photo")!=null) {
			String photo =data.get("photo");
			if(photo.equals("")) {
				user.setPhoto("Not-available.png");				
			}
			else {
				//base64_Image
				DecodePhoto decodePhoto=new DecodePhoto();
				user.setPhoto(decodePhoto.Base64_Image(photo));
			}
			return ResponseEntity.ok(profileServiceImpl.updateProfile(user));

			
		}
		else {
			return ResponseEntity.ok("no photo selected");
		}
	}
	
}
