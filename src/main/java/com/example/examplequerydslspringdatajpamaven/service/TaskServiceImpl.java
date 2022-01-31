package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.Task;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.TaskRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

@Service
public class TaskServiceImpl extends RestServiceController implements TaskService{

	private static final Log logger = LogFactory.getLog(LoginServiceImpl.class);
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private TokenSecurity tokenSecurity;
	
	@Autowired
	private UserRepository userRepository;
	 
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private JWKValidator jwkValidator;
	
	@Autowired
	private TaskRepository taskRepository;

    public TaskServiceImpl() {
    }

    @Override
	public ResponseEntity<?> loginTask(String authorization) {
		
		logger.info("************************ Login STARTED ***************************");
		 if(authorization != "" && authorization.toLowerCase().startsWith("basic")) {
			 

				String base64Credentials = authorization.substring("Basic".length()).trim();
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);

				final String[] values = credentials.split(":", 2);
				String email = values[0].toString();
				String password = values[1].toString();
				String hashedPassword = userServiceImpl.getMd5(password);
				User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
				if(user == null)
				{
					List<Map> loggedUser = null;
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "invalid email or password",loggedUser);
					logger.info("************************ Login ENDED ***************************");
					return  ResponseEntity.status(404).body(getObjectResponse);
					
				}
				else {
					
					String loggedEmail= user.getEmail();
					
					
					String token =  jwkValidator.createJWT(loggedEmail, null);
					Map userInfo = new HashMap();
					userInfo.put("name" ,user.getName());
					userInfo.put("email", user.getEmail());
					userInfo.put("token",token);
					
					
					
					
					List<Map> loggedUser = new ArrayList<>();
					loggedUser.add(userInfo);
			        
			        
				    //TokenSecurity.getInstance().addActiveUser(user.getId(),token,requestLastUpdate); 
			    
			        tokenSecurity.addActiveUser(user.getId(),token); 
			        
				    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",loggedUser);
					logger.info("************************ Login ENDED ***************************");
					
					return  ResponseEntity.ok().body(getObjectResponse);
					
				}
		 }
		 else
		 {

			 List<User> loggedUser = null ;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",loggedUser);
			 logger.info("************************ Login ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			 
			 
		 }
	
	}

	@Override
	public ResponseEntity<?> list(String TOKEN,int offset,int limit) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN) != null)
		{
			return super.checkActive(TOKEN);
		}
		
		List<Task> list = new ArrayList<Task>();
		Integer size = 0;
		
		list = taskRepository.getList(offset, limit);
		size = taskRepository.getListSize();
		
	    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",list,size);		
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	@Override
	public ResponseEntity<?> add(String TOKEN,Task task) {
		// TODO Auto-generated method stub
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN) != null)
		{
			return super.checkActive(TOKEN);
		}
		
		taskRepository.save(task);
		
	    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);		
		return  ResponseEntity.ok().body(getObjectResponse);
	}
}
