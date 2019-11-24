	package com.example.examplequerydslspringdatajpamaven.rest;
	
	import java.nio.charset.StandardCharsets;
	import java.util.Base64;

	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;

	import org.omg.CORBA.UserException;
	import org.springframework.beans.factory.annotation.Autowired;	
	import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestHeader;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

	import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
	import com.example.examplequerydslspringdatajpamaven.entity.User;
	import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptionEnum;
	import com.example.examplequerydslspringdatajpamaven.exceptions.UserExceptions;
import com.example.examplequerydslspringdatajpamaven.service.LoginService;

	@CrossOrigin
	@RestController
	public class LoginRestController {
	
		@Autowired
		LoginService loginService;
//		@Autowired
		JWKValidator jwkValidator;
	//////
		@GetMapping(path = "/login")
		public 	ResponseEntity<?> login(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

			
			return loginService.login(authtorization);
		}
		

		@GetMapping(path = "/logout")
		public ResponseEntity<?> logout(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
			
			return loginService.logout(TOKEN);
		}
		@GetMapping(path = "/billing")
		public ResponseEntity<?> billing(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ,
				@RequestParam(value = "loggedId", defaultValue = "0")Long loggedId,
				@RequestParam(value = "userId", defaultValue = "0")Long userId,
				 @RequestParam (value = "start", defaultValue = "0") String start,
				 @RequestParam (value = "end", defaultValue = "0") String end,
				   @RequestParam(value = "offset", defaultValue = "0") int offset,
		           @RequestParam(value = "search", defaultValue = "") String search){
			
			return loginService.getBilling(TOKEN,loggedId,userId,start,end,offset,search);
		}
		
//		@GetMapping(path = "/checkActive")
//		public ResponseEntity<?> checkActive(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
//			
//			return loginService.checActive(TOKEN);
//		}
		
	}
