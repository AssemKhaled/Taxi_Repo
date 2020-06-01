package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;

public interface GroupsService {
	
	public ResponseEntity<?> createGroup(String TOKEN,Group group,Long userId);
	public ResponseEntity<?> getAllGroups(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getGroupById(String TOKEN,Long groupId,Long userId);
	public ResponseEntity<?> editGroup(String TOKEN,Group group,Long id);
	public ResponseEntity<?> deleteGroup(String TOKEN,Long groupId,Long userId);
	public ResponseEntity<?> assignGroupToDriver(String TOKEN,Long groupId , Map<String, List> data, Long userId);
	public ResponseEntity<?> assignGroupToGeofence(String TOKEN,Long groupId , Map<String, List> data, Long userId);
	public ResponseEntity<?> assignGroupToDevice(String TOKEN,Long groupId , Map<String, List> data, Long userId);

	public ResponseEntity<?> getGroupDevices(String TOKEN,Long groupId,String type);

	public ResponseEntity<?> getGroupSelect(String TOKEN,Long userId);

}
