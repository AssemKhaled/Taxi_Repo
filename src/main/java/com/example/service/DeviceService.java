package com.example.service;

import java.util.List;

import com.example.examplequerydslspringdatajpamaven.entity.Device;

public interface DeviceService {

	public List<Device> getAllUserDevices();
	
	public String createDevice(Device device);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public String deleteDevice(Device device);
	
	public Device findById(Long deviceId);
	
	public String assignDeviceToDriver(Device device);
	
	public String assignDeviceToGeofences(Device device);
}
