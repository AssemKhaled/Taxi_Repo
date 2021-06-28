package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Task;

@Service
public interface TaskService {

	public ResponseEntity<?> loginTask(String authorization);
	public ResponseEntity<?> list(String Token,int offset,int limit);
	public ResponseEntity<?> add(String Token,Task task);

}
