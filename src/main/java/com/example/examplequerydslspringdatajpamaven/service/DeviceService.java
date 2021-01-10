package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import java.util.Map;

import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Device;

@Service
public interface DeviceService {
	
	public Device findById(Long deviceId);

	public ResponseEntity<?>  getAllUserDevices(String TOKEN,Long userId , int offset, String search, String exportData);
	
	public ResponseEntity<?> createDevice(String TOKEN,Device device,Long userId);
	
	public ResponseEntity<?> editDevice(String TOKEN,Device device,Long userId);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public  ResponseEntity<?> deleteDevice(String TOKEN,Long userId, Long deviceId);
	
	public ResponseEntity<?>  findDeviceById(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?> getDeviceSelectGroup(String TOKEN,Long loggedUserId,Long userId,Long groupId);
	public ResponseEntity<?> getDeviceSelect(String TOKEN,Long loggedUserId,Long userId);

	public ResponseEntity<?> getDeviceUnSelect(String TOKEN,Long loggedUserId,Long userId);


	public ResponseEntity<?> assignDeviceToDriver(String TOKEN,Long deviceId , Long driverId , Long userId);
	
	public ResponseEntity<?> assignDeviceToGeofences(String TOKEN,Long deviceId,Long [] geoIds,Long userId );

	public ResponseEntity<?> getDeviceDriver(String TOKEN,Long deviceId);
		
	public ResponseEntity<?> getDeviceGeofences(String TOKEN,Long deviceId);
	
	public ResponseEntity<?> getDeviceStatus(String TOKEN,Long userId);
	
	public ResponseEntity<?> getAllDeviceLiveData(String TOKEN,Long userId,int offset,String search);
	
	public ResponseEntity<?> vehicleInfo(String TOKEN,Long deviceId,Long userId);
	
	public ResponseEntity<?> assignDeviceToUser(String TOKEN,Long userId,Long deviceId , Long toUserId);

	public ResponseEntity<?> getAllDeviceLiveDataMap(String TOKEN,Long userId);

	public ResponseEntity<?> getCalibrationData(String TOKEN,Long userId,Long deviceId);
	public ResponseEntity<?> getFuelData(String TOKEN,Long userId,Long deviceId);
	public ResponseEntity<?> getSensorSettings(String TOKEN,Long userId,Long deviceId);
	public ResponseEntity<?> getIcon(String TOKEN,Long userId,Long deviceId);

	public ResponseEntity<?> addDataToCaliberation(String TOKEN,Long userId,Long deviceId,Map<String, List> data);
	public ResponseEntity<?> addDataToFuel(String TOKEN,Long userId,Long deviceId,Map<String, Object> data);
	public ResponseEntity<?> addSensorSettings(String TOKEN,Long userId,Long deviceId,Map<String, Object> data);
	public ResponseEntity<?> addIcon(String TOKEN,Long userId,Long deviceId,Map<String, Object> data);
	public ResponseEntity<?> sendCommand(String TOKEN,Long userId,Long deviceId,Map<String, Object> data);

	
	public String calculateSlopeAndFactor(JSONArray  data);

	public String sendCommandToServer(Map<String, Object> objectData);
	
	public ResponseEntity<?> getDeviceDataSelected(String TOKEN,Long deviceId,String type);

	


	public ResponseEntity<?> assignClientDevices(String TOKEN,Long loggedUserId,Long userId,Long [] deviceIds);

	public ResponseEntity<?> getClientDevices(String TOKEN,Long loggedUserId,Long userId);
	
	public ResponseEntity<?> updateLineData();
	public ResponseEntity<?> updatePositionData();



}