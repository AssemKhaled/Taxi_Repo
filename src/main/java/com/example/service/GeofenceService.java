package com.example.service;

import java.util.List;
import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public Set<Geofence> getAllGeofences(int id);
	public Geofence getGeofenceById(int geofenceId);
	public void deleteGeofence(int geofenceId);
	public List<Geofence> checkDublicateGeofenceInAdd(int userId,String name);
	public String addGeofence(Geofence geofence,int id);
	public List<Geofence> checkDublicateGeofenceInEdit(int geofenceId,int userId,String name);
	public void editGeofence(Geofence geofence);





}
