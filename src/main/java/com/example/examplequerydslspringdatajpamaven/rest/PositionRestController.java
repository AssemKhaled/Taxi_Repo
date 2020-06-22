package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.repository.PositionRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

@CrossOrigin
@Component
@RequestMapping(path = "/positions")
public class PositionRestController {
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	GetObjectResponse getObjectResponse;

	
	@Autowired
	private PositionRepository positionRepository;
	@GetMapping("/test")
	public ResponseEntity<?> pos() {
 
		 List<MongoPositions> positions = null;
		 
		 String protocol ="ali";
		 Date servertime = new Date();
		 Date devicetime= new Date();
		 Date fixtime= new Date();
		 Integer valid =0;
		 Double latitude =1.012555;
		 Double longitude=15.2666655;
		 float altitude =10;
		 float speed=90;
		 float course=10;
		 String address="giza";
		 String attributes="alarm";
		 Double accuracy=1.124;
		 String network="web";
		 Integer is_sent =0;
		 Integer is_offline=1;
		 float weight =1200;
		 
//		MongoPositions position = new MongoPositions(null, protocol, servertime, devicetime, fixtime, valid, latitude, longitude, altitude, speed, course, address, attributes, accuracy, network, is_sent, is_offline, weight);

//		positionRepository.save(position);
		positions = positionRepository.findByProtocol("ahmed");
    	getObjectResponse = new GetObjectResponse(HttpStatus.OK.value() , "success" ,positions);
		logger.info("************************ editDevice ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);		
	}
}
