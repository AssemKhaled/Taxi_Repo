package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;;

public interface ComputedService {
	public ResponseEntity<?> createComputed(String TOKEN,Attribute attribute,Long userId);
	public ResponseEntity<?> getAllComputed(String TOKEN,Long id);
	public ResponseEntity<?> getComputedById(String TOKEN,Long attributeId,Long userId);
	public ResponseEntity<?> editComputed(String TOKEN,Attribute  attribute,Long id);
	public ResponseEntity<?> deleteComputed(String TOKEN,Long attributeId,Long userId);
	public ResponseEntity<?> assignComputedToGroup(String TOKEN,Long groupId , Map<String, List> data, Long userId);
	public ResponseEntity<?> assignComputedToDevice(String TOKEN,Long deviceId , Map<String, List> data, Long userId);



}
