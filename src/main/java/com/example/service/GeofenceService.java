package com.example.service;

import java.util.List;
import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public Set<Geofence> getAllGeofences(Long id);
	public Geofence getGeofenceById(Long geofenceId);
	public void deleteGeofence(Long geofenceId);
	public List<Geofence> checkDublicateGeofenceInAdd(Long userId,String name);
	public String addGeofence(Geofence geofence,Long id);
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId,Long userId,String name);
	public void editGeofence(Geofence geofence);
	
	public List<Geofence> getMultipleGeofencesById(Long [] ids);





}
