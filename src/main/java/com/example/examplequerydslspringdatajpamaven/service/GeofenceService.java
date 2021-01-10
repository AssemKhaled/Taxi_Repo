package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

@Service
public interface GeofenceService {

	public ResponseEntity<?> getAllGeofences(String TOKEN,Long id,int offset,String search,String exportData);
	public ResponseEntity<?> getAllGeo(String TOKEN,Long id);
	public ResponseEntity<?> getGeofenceById(String TOKEN,Long geofenceId,Long userId);
	public Geofence getById(Long geofenceId);
	public ResponseEntity<?> deleteGeofence(String TOKEN,Long geofenceId,Long userId);
	public List<Geofence> checkDublicateGeofenceInAdd(Long userId,String name);
	public ResponseEntity<?> addGeofence(String TOKEN,Geofence geofence,Long id);
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId,Long userId,String name);
	public ResponseEntity<?> editGeofence(String TOKEN,Geofence geofence,Long id);
	
	public Set<Geofence> getMultipleGeofencesById(Long [] ids);

	public ResponseEntity<?> getGeofenceSelect(String TOKEN,Long loggedUserId,Long userId,Long deviceId,Long groupId);
	public ResponseEntity<?> getGeofenceUnSelectOfClient(String TOKEN,Long loggedUserId,Long userId);


	public ResponseEntity<?> assignClientGeofences(String TOKEN,Long loggedUserId,Long userId,Long [] geofenceIds);

	public ResponseEntity<?> getClientGeofences(String TOKEN,Long loggedUserId,Long userId);


}
