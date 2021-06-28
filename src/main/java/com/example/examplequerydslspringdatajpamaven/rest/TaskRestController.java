package com.example.examplequerydslspringdatajpamaven.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Task;
import com.example.examplequerydslspringdatajpamaven.service.TaskServiceImpl;

@CrossOrigin
@Component
@RequestMapping(path = "/task")
public class TaskRestController {

	@Autowired
	private TaskServiceImpl taskServiceImpl;
	
	@GetMapping(path = "/login")
	public 	ResponseEntity<?> loginTask(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

		
		return taskServiceImpl.loginTask(authtorization);
	}
	
	@GetMapping(path = "/list")
	public ResponseEntity<?> list(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                      @RequestParam(value = "offset", defaultValue = "0") int offset,
			                      @RequestParam(value = "limit", defaultValue = "0") int limit){

		
		return taskServiceImpl.list(TOKEN,offset,limit);
	}
	
	@PostMapping(path = "/add")
	public ResponseEntity<?> add(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                     @RequestBody(required = false) Task task){

		
		return taskServiceImpl.add(TOKEN,task);
	}
	
	
}
