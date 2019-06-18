package com.example.service;

import java.util.List;

import com.example.examplequerydslspringdatajpamaven.entity.Device;

public interface DeviceService {

	public List<Device> getAllUserDevices();
	
	public Device createDevice(Device device);
}
