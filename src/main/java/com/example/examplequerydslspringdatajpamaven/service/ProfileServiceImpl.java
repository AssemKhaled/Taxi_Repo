package com.example.examplequerydslspringdatajpamaven.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.examplequerydslspringdatajpamaven.repository.ProfileRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * services functionality related to profile
 * @author fuinco
 *
 */
@Component
@Service
public class ProfileServiceImpl extends RestServiceController implements ProfileService{

	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	GetObjectResponse getObjectResponse;

	private static final Log logger = LogFactory.getLog(ProfileServiceImpl.class);

	
	/**
	 * get user profile info to edit
	 */
	@Override
	public ResponseEntity<?> getUserInfo(String TOKEN,Long userId) {
		
		logger.info("************************ getUserInfo STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
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


	/**
	 * update profile of user by data in body and userId
	 */
	@Override
	public ResponseEntity<?>  updateProfileInfo(String TOKEN,User user,Long userId) {
		
		logger.info("************************ updateProfile STARTED ***************************");

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User Data =getUserInfoObj(userId);
			if(Data == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(Data.getDelete_date() == null) {
					if( (user.getId() != null && user.getId() != 0) ) {
						
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Id not allow in request body of profile",users);
						logger.info("************************ createDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
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
						user.setAttributes(Data.getAttributes());
						user.setGroups(Data.getGroups());
						user.setNotifications(Data.getNotifications());

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

	/**
	 * change password with data in body (oldPassword,newPassword) and check if in edit user or in profile by value check ""
	 */
	@Override
	public ResponseEntity<?> updateProfilePassword(String TOKEN,Map<String, String> data,String check,Long userId) {
		
		logger.info("************************ updateProfilePassword STARTED ***************************");

		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user =getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					if(check.equals("")) {
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


						if(data.get("newPassword") == null || data.get("newPassword") == "") {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "newPassword is Required",users);
							return ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							String newPassword= userServiceImpl.getMd5(data.get("newPassword").toString());
							
							user.setPassword(newPassword);
							profileRepository.save(user);
							users.add(user);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,users);
							logger.info("************************ updateProfilePassword ENDED ***************************");
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
	
	/**
	 * update photo by data in body (photo) if "" remove and put not_available.png if valid add it and return name
	 */
	@Override
	public ResponseEntity<?> updateProfilePhoto(String TOKEN,Map<String, String> data,Long userId){
		
		logger.info("************************ updateProfile STARTED ***************************");

		GetObjectResponse getObjectResponse ;
		List<User> users = new ArrayList<User>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",users);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User user = getUserInfoObj(userId);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",users);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getDelete_date() == null) {
					if(data.get("photo") == null) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Photo is Required",users);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					else {
						
						
						DecodePhoto decodePhoto=new DecodePhoto();
						String photo = data.get("photo").toString();
						if(user.getPhoto() != null) {
							if(!user.getPhoto().equals("")) {
								if(!user.getPhoto().equals("not_available.png")) {
									decodePhoto.deletePhoto(user.getPhoto(), "user");
								}
							}
						}
						
						if(photo == "") {
							
							user.setPhoto("not_available.png");				
						}
						else {
							if(photo.startsWith("data:image")) {
								user.setPhoto(decodePhoto.Base64_Image(photo,"user"));
							}
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

			return null;
		}
		else
		{
			return user;
		}
		
	}


	/**
	 * reset password only for admin as loggedUserId with data in body (password)
	 */
	@Override
	public ResponseEntity<?> restPassword(String TOKEN, Long loggedUserId, Long userId, Map<String, String> data) {
		// TODO Auto-generated method stub
		
		logger.info("************************ restPassword STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User admin = userServiceImpl.findById(loggedUserId);
		if(admin == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(admin.getAccountType() != 1) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type admin to reset passwords",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		if(data.get("password") == null || data.get("password") == "") {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "password is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			String password= userServiceImpl.getMd5(data.get("password").toString());
			
			user.setPassword(password);
			profileRepository.save(user);

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success" ,null);
			logger.info("************************ restPassword ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

			
		
		}
		
	}




}
