package com.example.service;

import java.util.ArrayList;
import java.util.Calendar;
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
	public String createDevice(Device device) {
		// TODO Auto-generated method stub
		logger.info("************************ createDevices STARTED ***************************");
	    Device data=device;
	    //check duplication 
	    List<Integer> duplictionList = checkDeviceDuplication(device);
	    if(duplictionList.size()>0)
	    {
	    	//throw duplication exception with duplication list
	    	return duplictionList.toString();
	    }
	    else
	    {
	    	deviceRepository.save(device);
			logger.info("************************ createDevices ENDED ***************************");
			return "added successfully";
	    }
	    
		
	}

	@Override
	public List<Integer> checkDeviceDuplication(Device device) {
		// TODO Auto-generated method stub
		logger.info("************************ checkDeviceDuplication STARTED ***************************");
		if(device.getName() == null)
		{
			//throw bad request exception 
			 logger.info("************************ checkDeviceDuplication ENDED ***************************");
			 return null;
		}
		else
		{
			String deviceName = device.getName();
			String deviceUniqueId = device.getUniqueId();
			String deviceSequenceNumber = device.getSequenceNumber();
			String devicePlateNum = device.getPlateNum();
			String deviceLeftLetter = device.getLeftLetter();
			String deviceMiddleLetter = device.getMiddleLetter();
			String deviceRightLetter = device.getRightLetter();
			System.out.println("left_letter"+deviceLeftLetter );
		    List<Device>duplicatedDevices = deviceRepository.checkDeviceDuplication(deviceName,deviceUniqueId,deviceSequenceNumber,devicePlateNum,deviceLeftLetter,deviceMiddleLetter,deviceRightLetter);
		    List<Integer>duplicationCodes = new ArrayList<Integer>();
		    for (Device matchedDevice : duplicatedDevices) 
		    { 
//		        Set<User> userCreater = device.getUser();
		    	if(matchedDevice.getId() != device.getId()) {
		    		if(matchedDevice.getName() != null) {
				        if(matchedDevice.getName().equals(device.getName()))
				        {
				        	
				        	duplicationCodes.add(1);
				        }
			    	}
			    	if(matchedDevice.getUniqueId() != null) {
			    		if(matchedDevice.getUniqueId().equals(device.getUniqueId()) ) {
				        	duplicationCodes.add(2);
				        }
			    	}
			        if(matchedDevice.getSequenceNumber() != null) {
			        	if(matchedDevice.getSequenceNumber().equals(device.getSequenceNumber()) ) {
				        	duplicationCodes.add(3);
				        }
			        }
			        if(matchedDevice.getPlateNum() != null || matchedDevice.getLeftLetter() != null 
			        	|| matchedDevice.getMiddleLetter() != null || matchedDevice.getRightLetter() != null) {
			        	if(matchedDevice.getPlateNum().equals(device.getPlateNum())  
			 		           && matchedDevice.getLeftLetter().equals(device.getLeftLetter())
			 		           && matchedDevice.getMiddleLetter().equals(device.getMiddleLetter())
			 		           && matchedDevice.getRightLetter().equals(device.getRightLetter())) {
			 		        	duplicationCodes.add(4);
			 		        }
			        }
		    	}
		    	
		    	
		        
		        
		    }
		    logger.info("************************ checkDeviceDuplication ENDED ***************************");
		    return duplicationCodes;
		}
		
		
	}

	@Override
	public String deleteDevice(Device device) {
		 Calendar cal = Calendar.getInstance();
		 int day = cal.get(Calendar.DATE);
	     int month = cal.get(Calendar.MONTH) + 1;
	     int year = cal.get(Calendar.YEAR);
	     String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	     device.setDeleteDate(date);
	     deviceRepository.save(device);
	     deviceRepository.deleteUserDevice(device.getId());
		return "deleted successfully";
	}

	@Override
	public Device findById(Long deviceId) {
		// TODO Auto-generated method stub
		Device device = deviceRepository.findOne(deviceId);
		if(device.getDeleteDate() != null) {
			//throw not found 
			return null;
		}
		else
		{
			return device;
		}
		
	}

	@Override
	public String assignDeviceToDriver(Device device) {
		// TODO Auto-generated method stub
		deviceRepository.save(device);
		
		return "ok";
	}

	@Override
	public String assignDeviceToGeofences(Device device) {
		// TODO Auto-generated method stub
		deviceRepository.save(device);
		return "ok";
	}

}
