package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public ResponseEntity<?> getAllGeofences(Long id,int offset,String search);
	public ResponseEntity<?> getGeofenceById(Long geofenceId);
	public Geofence getById(Long geofenceId);
	public ResponseEntity<?> deleteGeofence(Long geofenceId);
	public List<Geofence> checkDublicateGeofenceInAdd(Long userId,String name);
	public ResponseEntity<?> addGeofence(Geofence geofence,Long id);
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId,Long userId,String name);
	public ResponseEntity<?> editGeofence(Geofence geofence,Long id);
	
	public Set<Geofence> getMultipleGeofencesById(Long [] ids);





}
