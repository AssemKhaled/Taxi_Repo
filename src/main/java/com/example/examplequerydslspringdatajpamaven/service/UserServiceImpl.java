package com.example.examplequerydslspringdatajpamaven.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;

@Component
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	GetObjectResponse getObjectResponse;

	@Override
	public User getName() {
		Long id =(long) 1;
		User x = userRepository.findOne(id);
		//System.out.println(userRepository);

		return x;
	}

//	@Override
//	public ResponseEntity<?> UserDevice(Long userId,int offset,String search) {
//		// TODO Auto-generated method stub
//		User x=userRepository.getUserData(userId);
//		if(x.getName() == null) {
//			System.out.println("no user");
//			return null;
//		}
//		else
//		{
//			Set<Device> devices = x.getDevices();
//			return devices ;
//		}
//	    
//	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
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
	
	@Override
	public  ResponseEntity<?> findUserById(Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ getUserById STARTED ***************************");
		if(userId == 0) {
			List<User> users = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request Missing User Id ",users);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		User user=userRepository.findOne(userId);
		if(user == null)
		{
			List<User> users = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user is not found ",users);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else
		{
			if(user.getDelete_date()!= null)
			{
				List<User> users = null;
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user is not found",users);
				logger.info("************************ getUserById STARTED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			List<User> users= new ArrayList<>();
			users.add(user);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
			logger.info("************************ getUserById STARTED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		
	}

	@Override
	public ResponseEntity<?> usersOfUser(Long userId,int offset,String search) {
		logger.info("************************ getAllUsersOfUser STARTED ***************************");
		if(userId == 0) {
			 List<User> users = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "BadRequest Missing user id ",users);
			 logger.info("************************ getAllUsersOfUser ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			User user = findById(userId);
			if(user == null) {
				 List<User> users = null;
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value() ,"this user is not found",users);
				 logger.info("************************ getAllUsersOfUser ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				List<User> users = userRepository.getUsersOfUser(userId,offset,search);
				 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
				 logger.info("************************ getAllUsersOfUser ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}
			
		}
		 
	}

	@Override
	public ResponseEntity<?> createUser(User user,Long userId) {
		
		logger.info("************************createUser STARTED ***************************");
		if(userId == 0) {
			List<User> users = null;
	    	//throw duplication exception with duplication list
	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request Missing user id ",users);
	    	logger.info("************************createUser ENDED ***************************");
	    	return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			User creater = findById(userId);
			if(creater == null) {
				List<User> users = null;
		    	//throw duplication exception with duplication list
		    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), " This user is not Found",users);
		    	logger.info("************************createUser ENDED ***************************");
		    	return ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				if(user.getId() != null) {
					List<User> users = null;
					String message= "create doesn't accept id";
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse);
				}
				if(user.getEmail() == null || user.getEmail() == "" || user.getPassword() == null
					|| user.getPassword() == "" || user.getName() == null || user.getName() == "" 
					|| user.getIdentity_num() == null || user.getIdentity_num() == ""
					|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
					|| user.getCompany_phone() == null || user.getCompany_phone() == ""
					|| user.getManager_phone() == null || user.getManager_phone() == ""
					|| user.getManager_mobile() == null || user.getManager_mobile() == ""
					|| user.getPhone() == null || user.getPhone() == "") {
					List<User> users = null;
					String message= "attributes [email , password, name, identityNumber ,commercialNumber,"
							+ "companyPhone ,Managerphone, ManagerMobile ] are required";
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse);
				}
					Set<User> userCreater=new HashSet<>() ;
					userCreater.add(findById(userId));
					user.setUsersOfUser(userCreater);
					String password = user.getPassword();
					String hashedPassword = getMd5(password);  
					user.setPassword(hashedPassword);
					List<Integer> duplictionList = checkUserDuplication(user);
					if(duplictionList.size()>0)
					{
						System.out.println("duplication" +duplictionList.toString() );
						//throw duplication exception with duplication list
						getObjectResponse = new GetObjectResponse(101, "Duplication Erorr",duplictionList);
						logger.info("************************createUser ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
					else
					{
						userRepository.save(user);
						List<User> users = null;
						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
						logger.info("************************createUser ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
				}		
		}
		
	}
	@Override
	public ResponseEntity<?> editUser(User user,Long userId) {
		
		logger.info("************************editUser STARTED ***************************");
		if(userId == 0) {
			List<User> users = null;
	    	//throw duplication exception with duplication list
	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request Missing user Id",users);
	    	logger.info("************************editUser ENDED ***************************");
	    	return ResponseEntity.ok().body(getObjectResponse);
		}else {
			  User loggedUser =  findById(userId);
			  if(loggedUser == null) {
				  List<User> users = null;
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",users);
			    	logger.info("************************editUser ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse); 
			  }
			//to set the users of updateduser
			if( user.getId() == null || user.getId() == 0|| user.getEmail() == null || user.getEmail() == "" ||
					 user.getName() == null || user.getName() == "" 
					|| user.getIdentity_num() == null || user.getIdentity_num() == ""
					|| user.getCommercial_num() == null ||user.getCommercial_num() == ""
					|| user.getCompany_phone() == null || user.getCompany_phone() == ""
					|| user.getManager_phone() == null || user.getManager_phone() == ""
					|| user.getManager_mobile() == null || user.getManager_mobile() == ""
					|| user.getPhone() == null || user.getPhone() == "") {
					List<User> users = null;
					String message= "attributes [id,email , name, identityNumber ,commercialNumber,"
							+ "companyPhone ,Managerphone, ManagerMobile ] are required";
			    	//throw duplication exception with duplication list
			    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
			    	logger.info("************************createUser ENDED ***************************");
			    	return ResponseEntity.ok().body(getObjectResponse);
				}else {
					if(user.getPassword()!= null) {
						List<User> users = null;
						String message= "you are not allowed to edit password";
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), message,users);
				    	logger.info("************************createUser ENDED ***************************");
				    	return ResponseEntity.ok().body(getObjectResponse);
					}
					User oldOne = findById(user.getId());
					if(oldOne == null) {
						List<User> users = null;
				    	//throw duplication exception with duplication list
				    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this userId is not found",users);
				    	logger.info("************************editUser ENDED ***************************");
				    	return ResponseEntity.ok().body(getObjectResponse);
					}else {
						
						
						Set<User> userCreater=new HashSet<>();
						
						userCreater = oldOne.getUsersOfUser();
						
				        user.setUsersOfUser(userCreater);
				        
						String password = oldOne.getPassword();
						user.setPassword(password);
					    
					    List<Integer> duplictionList = checkUserDuplication(user);
					    
					    if(duplictionList.size()>0)
					    {
					    	System.out.println("duplication" +duplictionList.toString() );
					    	//throw duplication exception with duplication list
					    	getObjectResponse = new GetObjectResponse(101, "Duplication Erorr",duplictionList);
					    	logger.info("************************editUser ENDED ***************************");
					    	return ResponseEntity.ok().body(getObjectResponse);
					    }
					    else
					    {
					    
							
					    
					    	userRepository.save(user);
					    	List<User> users = null;
					    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
					    	logger.info("************************createUser ENDED ***************************");
					    	return ResponseEntity.ok().body(getObjectResponse);
					    }
					}
				}
			
			
			
		}
		
		
	}
	
	
	 public static String getMd5(String input) 
	    { 
	        try { 
	  
	            // Static getInstance method is called with hashing MD5 
	            MessageDigest md = MessageDigest.getInstance("MD5"); 
	  
	            // digest() method is called to calculate message digest 
	            //  of an input digest() return array of byte 
	            byte[] messageDigest = md.digest(input.getBytes()); 
	  
	            // Convert byte array into signum representation 
	            BigInteger no = new BigInteger(1, messageDigest); 
	  
	            // Convert message digest into hex value 
	            String hashtext = no.toString(16); 
	            while (hashtext.length() < 32) { 
	                hashtext = "0" + hashtext; 
	            } 
	            return hashtext; 
	        }  
	  
	        // For specifying wrong message digest algorithms 
	        catch (NoSuchAlgorithmException e) { 
	            throw new RuntimeException(e); 
	        } 
	    }

	@Override
	public List<Integer> checkUserDuplication(User user) {
		 // TODO Auto-generated method stubt
		 String email = user.getEmail();
		 String identityNum = user.getIdentity_num();
		 String commercialNum = user.getCommercial_num();
		 String	companyPhone = user.getCompany_phone();
		 String managerPhone = user.getManager_phone();
		 String managerMobile = user.getManager_mobile();
		 String phone = user.getPhone();
		 List<User>userDuolicationList = userRepository.checkUserDuplication(email, identityNum, commercialNum, companyPhone, managerPhone, managerMobile,phone);
		 List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (User matchedUser : userDuolicationList) 
		    { 
		    	if(matchedUser.getId() != user.getId() ) {
		    		if(matchedUser.getEmail() != null) {
		    			if(matchedUser.getEmail().equals(user.getEmail()))
				        {
				        	
				        	duplicationCodes.add(1);
				        }
		    		}
		    		if(matchedUser.getIdentity_num() != null) {
		    			if(matchedUser.getIdentity_num().equals(user.getIdentity_num())) {
		    				duplicationCodes.add(2);
		    			}
		    		}
		    		if(matchedUser.getCommercial_num() != null) {
		    			if(matchedUser.getCommercial_num().equals(user.getCommercial_num())) {
		    				duplicationCodes.add(3);
		    			}
		    		}
		    		if(matchedUser.getCompany_phone() != null) {
		    			if(matchedUser.getCompany_phone().equals(user.getCompany_phone())) {
		    				duplicationCodes.add(4);
		    			}
		    		}
		    		if(matchedUser.getManager_phone() != null) {
		    			if(matchedUser.getManager_phone().equals(user.getManager_phone())) {
		    				duplicationCodes.add(5);
		    			}
		    		}
		    		if(matchedUser.getManager_mobile() != null) {
		    			if(matchedUser.getManager_mobile().equals(user.getManager_mobile())) {
		    				duplicationCodes.add(6);
		    			}
		    		}
		    		if(matchedUser.getPhone() != null) {
		    			if(matchedUser.getPhone().equals(user.getPhone())) {
		    				duplicationCodes.add(7);
		    			}
		    		}
		    		
		    	}
		    }
		 return duplicationCodes;
		
	}

	@Override
	public ResponseEntity<?> deleteUser(Long userId,Long deleteUserId) {
		System.out.println("userid"+userId+"delete"+deleteUserId);
		logger.info("************************deleteUser STARTED ***************************");
		if(userId == 0 || deleteUserId == 0) {
			 List<User> users= null;
		      getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",users);
		    logger.info("************************deleteUser ENDED ***************************");
		    return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			 User loggedUser = findById(userId);
	            if(loggedUser== null) {
						 List<User> users= null;
					      getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this logged user is not found to delete",users);
					    logger.info("************************deleteUser ENDED ***************************");
					    return ResponseEntity.ok().body(getObjectResponse);
	            }else {
	            	User deletedUser = findById(deleteUserId);
	    			if(deletedUser == null) {
	    				logger.info("************************deleteUser STARTED ***************************");
	    				
	    					 List<User> users= null;
	    				      getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user is not found to delete",users);
	    				    logger.info("************************deleteUser ENDED ***************************");
	    				    return ResponseEntity.ok().body(getObjectResponse);
	    			}else {
	    				Calendar cal = Calendar.getInstance();
	    				int day = cal.get(Calendar.DATE);
	    				int month = cal.get(Calendar.MONTH) + 1;
	    				int year = cal.get(Calendar.YEAR);
	    				String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	    				deletedUser.setDelete_date(date);
	    				userRepository.save(deletedUser);
	    				userRepository.deleteUserOfUser(deletedUser.getId());
	    				List<User> users= null;
	    				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",users);
	    				logger.info("************************deleteUser ENDED ***************************");
	    				return ResponseEntity.ok().body(getObjectResponse);
	    			}
	            }
		}
		
	}
}
