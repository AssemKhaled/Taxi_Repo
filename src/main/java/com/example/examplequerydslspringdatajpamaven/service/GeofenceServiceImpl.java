package com.example.examplequerydslspringdatajpamaven.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;

@Component
public class GeofenceServiceImpl implements GeofenceService {
	
	private static final Log logger = LogFactory.getLog(GeofenceServiceImpl.class);

	@Autowired
	GeofenceRepository geofenceRepository;
	
	@Autowired
	UserRepository userRepository;

	@Override
	public List<Geofence> getAllGeofences(Long id,int offset,String search) {

		//User user=userRepository.getUserData(id);
		//Set<Geofence> geofences = user.getGeofences();
		logger.info("************************ getAllUserGeofences STARTED ***************************");

		List<Geofence> geofences = geofenceRepository.getAllGeofences(id,offset,search);
		
		logger.info("************************ getAllUserGeofences ENDED ***************************");

		return geofences;
	}

	@Override
	public Geofence getGeofenceById(Long geofenceId) {
		logger.info("************************ getGeofenceById STARTED ***************************");

		Geofence geofence=geofenceRepository.findOne(geofenceId);
		
		logger.info("************************ getGeofenceById ENDED ***************************");

		return geofence;

	}

	@Override
	public void deleteGeofence(Long geofenceId) {

		logger.info("************************ deleteGeofence STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String currentDate=formatter.format(date);
		logger.info("************************ deleteGeofence ENDED ***************************");

		geofenceRepository.deleteGeofence(geofenceId,currentDate);
		geofenceRepository.deleteGeofenceId(geofenceId);
		geofenceRepository.deleteGeofenceDeviceId(geofenceId);

		
	}

	@Override
	public List<Geofence> checkDublicateGeofenceInAdd(Long id, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInAdd(id, name);
		
	}
	
	@Override
	public String addGeofence(Geofence geofence) {
		logger.info("************************ addGeofence STARTED ***************************");

		geofenceRepository.save(geofence);

		logger.info("************************ addGeofence ENDED ***************************");

		return "Add successfully";
	
		
		
	}
	
	@Override
	public List<Geofence> checkDublicateGeofenceInEdit(Long geofenceId, Long userId, String name) {
		
		return geofenceRepository.checkDublicateGeofenceInEdit(geofenceId,userId,name);
		
	}

	@Override
	public void editGeofence(Geofence geofence) {

		geofenceRepository.save(geofence);

	}

	@Override
	public List<Geofence> getMultipleGeofencesById(Long [] ids) {
		// TODO Auto-generated method stub
		
		return geofenceRepository.getMultipleGeofencesById(ids);
	}
	

}
