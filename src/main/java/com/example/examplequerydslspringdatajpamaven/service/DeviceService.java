package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import org.springframework.http.ResponseEntity;


import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;

public interface DeviceService {

	public List<Device> getAllUserDevices();
	
	public String createDevice(Device device);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public String deleteDevice(Device device);
	
	public Device findById(Long deviceId);

	public  List<DeviceSelect> getDeviceSelect(Long userId);

	public String assignDeviceToDriver(Device device);
	
	public String assignDeviceToGeofences(Device device);
	
	public ResponseEntity<?> testgetDeviceById();
}
