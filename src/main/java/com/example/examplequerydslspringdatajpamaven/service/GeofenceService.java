package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public List<Geofence> getAllGeofences(Long id,int offset,String search);
	public Geofence getGeofenceById(Long geofenceId);
	public void deleteGeofence(Long geofenceId);
	public List<Geofence> checkDublicateGeofenceInAdd(Long userId,String name);
	public String addGeofence(Geofence geofence);
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId,Long userId,String name);
	public void editGeofence(Geofence geofence);
	
	public Set<Geofence> getMultipleGeofencesById(Long [] ids);





}
