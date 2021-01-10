package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Attribute;

@Service
public interface ComputedService {
	public ResponseEntity<?> createComputed(String TOKEN,Attribute attribute,Long userId);
	public ResponseEntity<?> getAllComputed(String TOKEN,Long id,int offset,String search,String exportData);
	public ResponseEntity<?> getComputedById(String TOKEN,Long attributeId,Long userId);
	public ResponseEntity<?> editComputed(String TOKEN,Attribute  attribute,Long id);
	public ResponseEntity<?> deleteComputed(String TOKEN,Long attributeId,Long userId);
	public ResponseEntity<?> assignComputedToGroup(String TOKEN,Long groupId , Map<String, List> data, Long userId);
	public ResponseEntity<?> assignComputedToDevice(String TOKEN,Long deviceId , Map<String, List> data, Long userId);
	public ResponseEntity<?> getComputedSelect(String TOKEN,Long loggedUserId,Long userId, Long deviceId, Long groupId);
	public ResponseEntity<?> getComputedUnSelect(String TOKEN,Long loggedUserId,Long userId);
	public ResponseEntity<?> assignClientComputeds(String TOKEN,Long loggedUserId,Long userId,Long [] ComputedIds);
	public ResponseEntity<?> getClientComputeds(String TOKEN,Long loggedUserId,Long userId);
}
