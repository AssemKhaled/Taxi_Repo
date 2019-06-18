package com.example.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;


@Service
public class DeviceServiceImpl implements DeviceService {

	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	
	@Autowired 
	DeviceRepository deviceRepository;
	
	@Override
	public List<Device> getAllUserDevices() {
		// TODO Auto-generated method stub
		
		logger.info("************************ getAllUserDevices STARTED ***************************");
	    List<Device> data=deviceRepository.getName();
		logger.info("************************ getAllUserDevices ENDED ***************************");
		return data;
	}

	@Override
	public Device createDevice(Device device) {
		// TODO Auto-generated method stub
		logger.info("************************ createDevices STARTED ***************************");
	    Device data=device;
	    deviceRepository.save(device);
		logger.info("************************ createDevices ENDED ***************************");
		return data;
		
	}

}
