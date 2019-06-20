package com.example.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

public class GeofenceServiceImpl implements GeofenceService {
	
	@Autowired
	GeofenceRepository geofenceRepository;
	
	@Autowired
	UserRepository userRepository;

	@Override
	public Set<Geofence> getAllGeofences(int id) {

		User user=userRepository.getUserData(id);
		Set<Geofence> geofences = user.getGeofences();
		
		return geofences;
	}

	@Override
	public Geofence getGeofenceById(int geofenceId) {
		
		return geofenceRepository.getGeofenceById(geofenceId);

	}

	@Override
	public void deleteGeofence(int geofenceId) {

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		geofenceRepository.deleteGeofence(geofenceId,currentDate);
		geofenceRepository.deleteGeofenceId(geofenceId);
	}
	
	

}
