package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

@Component
public class LoginServiceImpl implements LoginService  {

	@Autowired
	 UserRepository userRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
//	JWKValidator jwkValidator;
	
	
	@Override
	public User login(String authorization) {
		 if(authorization != null && authorization.toLowerCase().startsWith("basic")) {
			 
			// Authorization: Basic base64credentials
				String base64Credentials = authorization.substring("Basic".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);
				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				String email = values[0].toString();
				String password = values[1].toString();
				String hashedPassword = userServiceImpl.getMd5(password);
				User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
				if(user == null)
				{
					//throw invalid user and passworde 
					System.out.println("no user");
					
				}
				else {
//					String token = jwkValidator.createJWT("mariam", null);
//					System.out.println("User----->" + token);
					
				}
		 }
		 else
		 {
			 //throw ecxeptions bad request
		 }
//		 String hashedPassword = userServiceImpl.getMd5(password);
//		 User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
		// TODO Auto-generated method stub
//		return user;
		return null;
	}
	
	

}
