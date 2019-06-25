package com.example.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getName() {
		User x = userRepository.getName();
		//System.out.println(userRepository);

		return x;
	}

	@Override
	public Set<Device> UserDevice() {
		// TODO Auto-generated method stub
		User x=userRepository.getAll();
	    Set<Device> devices = x.getDevices();
		return devices ;
	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
		return user;
	}

	@Override
	public Set<User> usersOfUser(Long userId) {
		// User x=userRepository.getAll();
		 Set<User> users = userRepository.getUsersOfUser(userId);
		return users;
	}

	@Override
	public User createUser(User user) {
		// TODO Auto-generated method stub
		String password = user.getPassword();
	    String hashedPassword = getMd5(password);
	    
	    user.setPassword(hashedPassword);
	    List<Integer> duplictionList = checkUserDuplication(user);
	    if(duplictionList.size()>0)
	    {
	    	System.out.println("duplication" +duplictionList.toString() );
	    	//throw duplication exception with duplication list
	    	return null;
	    }
	    else
	    {
	    
			
	    
	    	userRepository.save(user);
	    	return user;
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
	public String deleteUser(User user) {
		 Calendar cal = Calendar.getInstance();
		 int day = cal.get(Calendar.DATE);
	     int month = cal.get(Calendar.MONTH) + 1;
	     int year = cal.get(Calendar.YEAR);
	     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	     user.setDelete_date(date);
	     userRepository.save(user);
	    userRepository.deleteUserOfUser(user.getId());
		return "deleted successfully";
	} 

	

}
