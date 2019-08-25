package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public ResponseEntity<?> getAllGeofences(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getAllGeo(String TOKEN,Long id);
	public ResponseEntity<?> getGeofenceById(String TOKEN,Long geofenceId);
	public Geofence getById(Long geofenceId);
	public ResponseEntity<?> deleteGeofence(String TOKEN,Long geofenceId,Long userId);
	public List<Geofence> checkDublicateGeofenceInAdd(Long userId,String name);
	public ResponseEntity<?> addGeofence(String TOKEN,Geofence geofence,Long id);
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId,Long userId,String name);
	public ResponseEntity<?> editGeofence(String TOKEN,Geofence geofence,Long id);
	
	public Set<Geofence> getMultipleGeofencesById(Long [] ids);





}
