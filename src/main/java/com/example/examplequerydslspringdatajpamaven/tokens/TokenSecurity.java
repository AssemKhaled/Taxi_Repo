package com.example.examplequerydslspringdatajpamaven.tokens;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Token;
import com.example.examplequerydslspringdatajpamaven.entity.UserTokens;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserTokensRepository;

/**
 * Create token and expire time
 * @author fuinco
 *
 */
//@Component
@Service
//	@Configuration
public class TokenSecurity {

	 @Autowired
	 public UserTokensRepository userTokensRepository;
	  
	//private List<Token> ActiveUsers;
	
	/** private constructor to prevent others from instantiating this class */
    /*private TokenSecurity() {
    	this.ActiveUsers = new ArrayList<>();
    }*/
   
//	private TokenSecurity() {
//
//    }
	
    /** Create an instance of the class at the time of class loading */
//    private static final TokenSecurity instance = new TokenSecurity();
    
    
    public void addActiveUser(Long userId, String token) {
    	UserTokens userToken = new UserTokens();
    	
    	userToken.setUserid(userId);
    	userToken.setTokenid(token);
    	 
    	userTokensRepository.save(userToken);
	    
    }
    public Boolean removeActiveUser(String token) {
    	
    	List<UserTokens> userTokens = userTokensRepository.getUserToken(token);

    	if(userTokens.size() > 0) {
    		for(UserTokens userToken:userTokens) {
        		userTokensRepository.deleteTokenOfUser(userToken.getTokenid());
    		}
			return true;
    	}
    	else {
    		
        	return false;

    	}
    	
    	
    }
    public Boolean removeActiveUserById(Long userId) {
    	
    	List<UserTokens> userTokens = userTokensRepository.getUserTokenById(userId);
    	
    	if(userTokens.size() > 0) {
    		for(UserTokens userToken:userTokens) {
        		userTokensRepository.deleteTokenOfUserById(userToken.getUserid());
    		}
			return true;
    	}
    	else {
        	return false;

    	}
    	
    	
    }
    
    public Boolean checkToken(String token) {
    	List<UserTokens> userTokens = userTokensRepository.getUserToken(token);

    	if(userTokens.size() > 0) {
        	return true;
    	}
    	else {
			return false;
    	}
    }
    
    /** Provide a global point of access to the instance */
//    public static TokenSecurity getInstance() {
//        return instance;
//    }

	

	/*public List<Token> getActiveUsers() {
		return ActiveUsers;
	}

	public void setActiveUsers(List<Token> activeUsers) {
		ActiveUsers = activeUsers;
	}
    
    public void addActiveUser(Long userId, String token , String lastUpdate) {
    	 Token security = new Token(userId,token,lastUpdate);
    	 
    	this.ActiveUsers.add(security);	
    	
    }
    
    public Boolean removeActiveUser(String token) {
    	List<Token> activeNow = getActiveUsers();
    	
    	int i =0;
    	for(Token user : activeNow) {
    		
    		if(user.getToken().equals(token)) {
    		
    			activeNow.remove(i);
    			
    			return true;
    		}
    		i++;
    	}
    	return false;
    }
    
    public Boolean removeActiveUserById(Long userId) {
    	List<Token>activeNow = getActiveUsers();
    	
    	int i = 0;
    	for(Token user : activeNow) {
    		 if(user.getUserId() == userId) {
    			 activeNow.remove(i);
     			
     			return true;
    		 }
    		 i++;
    	}
    	return false;
    }
    
    public Boolean checkToken(String token) {
    	List<Token> activeNow = getActiveUsers();
    	
    	int i =0;
    	for(Token user : activeNow) {
    		
    		if(user.getToken().equals(token)) {
    			SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
    	    	TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh"); 
    	         
    	        Date currentDate = new Date();
    	        String requestLastUpdate = FORMATTER.format(currentDate);
    			user.setLastUpdate(requestLastUpdate);
    			
    			return true;
    		}
    		i++;
    	}
    	return false;
    }*/
    
    
}
