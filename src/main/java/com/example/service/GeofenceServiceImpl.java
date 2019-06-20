package com.example.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
	public Set<Geofence> getAllGeofences(Long id) {

		User user=userRepository.getUserData(id);
		Set<Geofence> geofences = user.getGeofences();
		
		return geofences;
	}

	@Override
	public Geofence getGeofenceById(Long geofenceId) {
		
		return geofenceRepository.findOne(geofenceId);

	}

	@Override
	public void deleteGeofence(Long geofenceId) {

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		geofenceRepository.deleteGeofence(geofenceId,currentDate);
		geofenceRepository.deleteGeofenceId(geofenceId);
		
	}

	@Override
	public List<Geofence> checkDublicateGeofenceInAdd(Long id, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInAdd(id, name);
		
	}
	
	@Override
	public String addGeofence(Geofence geofence,Long id) {
		User userData = userRepository.getUserData(id);
		if(userData != null) {
			Set<User> usergeofence = new HashSet<>();
			usergeofence.add(userData);
			geofence.setUserGeofence(usergeofence);
			geofenceRepository.save(geofence);
			return "Add successfully";
		}
		else {
			return "no user by this id";
		}
		
		
	}
	
	@Override
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId, Long userId, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInEdit(geofenceId,userId,name);
		
	}

	@Override
	public void editGeofence(Geofence geofence) {

		geofenceRepository.save(geofence);

	}
	

}
