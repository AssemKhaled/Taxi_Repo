package com.example.service;

import java.util.Set;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

public interface GeofenceService {

	public Set<Geofence> getAllGeofences(int id);
	public Geofence getGeofenceById(int geofenceId);
	public void deleteGeofence(int geofenceId);



}
