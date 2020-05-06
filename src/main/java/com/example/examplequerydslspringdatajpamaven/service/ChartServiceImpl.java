package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.PositionSqlRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;


@Component
public class ChartServiceImpl extends RestServiceController implements ChartService{
	
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired 
	DeviceRepository deviceRepository;
	

	@Autowired 
	PositionSqlRepository positionRepository;
	
	@Autowired
	private DriverServiceImpl driverService;
	
	@Value("${summaryUrl}")
	private String summaryUrl;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	@Override
	public ResponseEntity<?> getStatus(String TOKEN, Long userId) {
		logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userService.resetChildernArray();
		 if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 
				    Integer onlineDevices = deviceRepository.getNumberOfOnlineDevices(usersIds);
					Integer outOfNetworkDevices = deviceRepository.getNumberOfOutOfNetworkDevices(usersIds);
					Integer totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
					Integer offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
					Integer drivers = driverService.getTotalNumberOfUserDrivers(usersIds);
					
					Map devicesStatus = new HashMap();
					devicesStatus.put("online_devices", onlineDevices);
					devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
					devicesStatus.put("offline_devices", offlineDevices);
					devicesStatus.put("total_drivers", drivers);
					devicesStatus.put("total_devices", totalDevices);
					List<Map> data = new ArrayList<>();
					data.add(devicesStatus);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
		 List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
		 List<Long>usersIds= new ArrayList<>();
		 if(childernUsers.isEmpty()) {
			 usersIds.add(userId);
		 }
		 else {
			 usersIds.add(userId);
			 for(User object : childernUsers) {
				 usersIds.add(object.getId());
			 }
		 }
		Integer onlineDevices = deviceRepository.getNumberOfOnlineDevices(usersIds);
		Integer outOfNetworkDevices = deviceRepository.getNumberOfOutOfNetworkDevices(usersIds);
		Integer totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
		Integer offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;
		Integer drivers = driverService.getTotalNumberOfUserDrivers(usersIds);
		
		Map devicesStatus = new HashMap();
		devicesStatus.put("online_devices", onlineDevices);
		devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
		devicesStatus.put("offline_devices", offlineDevices);
		devicesStatus.put("total_drivers", drivers);
		devicesStatus.put("total_devices", totalDevices);
		List<Map> data = new ArrayList<>();
		data.add(devicesStatus);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}


	@Override
	public ResponseEntity<?> getIgnitionMotion(String TOKEN, Long userId) {
		logger.info("************************ getIgnitionMotion STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getIgnition ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userService.resetChildernArray();
		 if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getIgnition ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 
				    List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
				    positionsList = positionRepository.getAttrbuites(usersIds); 
					List<String> ignitionON= new ArrayList<String>();				  
					List<String> ignitionOFF = new ArrayList<String>();				  
					List<String> motionON= new ArrayList<String>();				  
					List<String> motionOFF = new ArrayList<String>();	
				    if(positionsList.size()>0) {
						for(int i=0;i<positionsList.size();i++) {
							JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
							
							if(obj.has("ignition")) {
								if(obj.get("ignition").equals(true)) {
									ignitionON.add(positionsList.get(i).getDeviceName());
								}
								else {
									ignitionOFF.add(positionsList.get(i).getDeviceName());

								}
								if(obj.get("motion").equals(true)) {
									motionON.add(positionsList.get(i).getDeviceName());
								}
								else {
									motionOFF.add(positionsList.get(i).getDeviceName());

								}
							}
							
							
						}
					
					}
				    Map devicesList = new HashMap();
				    devicesList.put("ignition_on", ignitionON.size());
				    devicesList.put("ignition_off" ,ignitionOFF.size());
				    devicesList.put("ignition_on_list", ignitionON);
					devicesList.put("ignition_off_list", ignitionOFF);
					
					devicesList.put("motion_on", motionON.size());
					devicesList.put("motion_off" ,motionOFF.size());
					devicesList.put("motion_on_list", motionON);
					devicesList.put("motion_off_list", motionOFF);
					
					
					List<Map> data = new ArrayList<>();
					data.add(devicesList);
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				 logger.info("************************ getIgnitionMotion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
		 List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
		 List<Long>usersIds= new ArrayList<>();
		 if(childernUsers.isEmpty()) {
			 usersIds.add(userId);
		 }
		 else {
			 usersIds.add(userId);
			 for(User object : childernUsers) {
				 usersIds.add(object.getId());
			 }
		 }
	    List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
	    positionsList = positionRepository.getAttrbuites(usersIds); 
		List<String> ignitionON= new ArrayList<String>();				  
		List<String> ignitionOFF = new ArrayList<String>();				  
		List<String> motionON= new ArrayList<String>();				  
		List<String> motionOFF = new ArrayList<String>();	
	    if(positionsList.size()>0) {
			for(int i=0;i<positionsList.size();i++) {
				JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
				
				if(obj.has("ignition")) {
					if(obj.get("ignition").equals(true)) {
						ignitionON.add(positionsList.get(i).getDeviceName());
					}
					else {
						ignitionOFF.add(positionsList.get(i).getDeviceName());

					}
					if(obj.get("motion").equals(true)) {
						motionON.add(positionsList.get(i).getDeviceName());
					}
					else {
						motionOFF.add(positionsList.get(i).getDeviceName());

					}
				}
				
				
			}
		
		}
	    Map devicesList = new HashMap();
	    devicesList.put("ignition_on", ignitionON.size());
	    devicesList.put("ignition_off" ,ignitionOFF.size());
	    devicesList.put("ignition_on_list", ignitionON);
		devicesList.put("ignition_off_list", ignitionOFF);
		
		devicesList.put("motion_on", motionON.size());
		devicesList.put("motion_off" ,motionOFF.size());
		devicesList.put("motion_on_list", motionON);
		devicesList.put("motion_off_list", motionOFF);
		
		
		List<Map> data = new ArrayList<>();
		data.add(devicesList);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getIgnitionMotion ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}


	@Override
	public ResponseEntity<?> getDriverHours(String TOKEN, Long userId) {
		logger.info("************************ getIgnitionMotion STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getIgnition ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userService.resetChildernArray();
		 if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getIgnition ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
				    positionsList = positionRepository.getDriverHoursList(usersIds);
					List<Map> data = new ArrayList<>();
				    if(positionsList.size()>0) {
						for(int i=0;i<positionsList.size();i++) {
							JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
							Map devicesList = new HashMap();
							if(obj.has("todayHoursString")) {
								SimpleDateFormat time = new SimpleDateFormat("HH:mm");
								try {
									Date date =  time.parse((String) obj.get("todayHoursString"));
								    devicesList.put("hours", date.getHours());
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


							}
							else {
							    devicesList.put("hours",0);

							}
						    devicesList.put("driverName", positionsList.get(i).getDriverName());
						    devicesList.put("deviceName", positionsList.get(i).getDeviceName());

							data.add(devicesList);

							
						}
					
					}
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				 logger.info("************************ getIgnitionMotion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
		 List<User>childernUsers = userService.getActiveAndInactiveChildern(userId);
		 List<Long>usersIds= new ArrayList<>();
		 if(childernUsers.isEmpty()) {
			 usersIds.add(userId);
		 }
		 else {
			 usersIds.add(userId);
			 for(User object : childernUsers) {
				 usersIds.add(object.getId());
			 }
		 }
	    List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
	    positionsList = positionRepository.getDriverHoursList(usersIds);
		List<Map> data = new ArrayList<>();
	    if(positionsList.size()>0) {
			for(int i=0;i<positionsList.size();i++) {
				JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
				Map devicesList = new HashMap();
				if(obj.has("todayHoursString")) {
					SimpleDateFormat time = new SimpleDateFormat("HH:mm");
					try {
						Date date =  time.parse((String) obj.get("todayHoursString"));
					    devicesList.put("hours", date.getHours());
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
				else {
				    devicesList.put("hours",0);

				}
			    devicesList.put("driverName", positionsList.get(i).getDriverName());
			    devicesList.put("deviceName", positionsList.get(i).getDeviceName());

				data.add(devicesList);

				
			}
		
		}
	    
		
		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getIgnitionMotion ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}


	@Override
	public ResponseEntity<?> getDistanceFuelEngine(String TOKEN, Long userId) {
		logger.info("************************ getDistanceFuelEngine STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser.equals(null)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userService.resetChildernArray();
		 if(loggedUser.getAccountType().equals(4)) {
			 Set<User> parentClients = loggedUser.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get devices of this user",null);
				 logger.info("************************ getDistanceFuelEngine ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
				 }
				 List<Long>usersIds= new ArrayList<>();
				 usersIds.add(parentClient.getId());
				 List<SummaryReport> summaryReport = new ArrayList<>();

					List<Long>userIds= new ArrayList<>();
					userIds.add(userId);

					List<Long>allDevices= new ArrayList<>();
					allDevices = deviceRepository.getDevicesUsers(userIds);

				 
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = summaryUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
					  
					    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
						inputFormat.setLenient(false);
						outputFormat.setLenient(false);
						Date dateTo;
						Date dateFrom = new Date();
						Calendar c = Calendar.getInstance(); 
						c.setTime(dateFrom); 
						c.add(Calendar.DATE, 1);
						dateTo = c.getTime();
						String from = "";
						String to = "";
						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
							
						

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("type", "allEvents")
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", 1)
						        .queryParam("start", 0)
						        .queryParam("limit",25).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
					  String URL = builder.toString();
					  if(allDevices.size()>0) {
						  for(int i=0;i<allDevices.size();i++) {
							  URL +="&deviceId="+allDevices.get(i);
						  }
					  }
					  System.out.println(URL);
					  ResponseEntity<List<SummaryReport>> rateResponse =
						        restTemplate.exchange(URL,
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
						            });
					  summaryReport = rateResponse.getBody();
					  if(summaryReport.size()>0) {
						  System.out.println(summaryReport.get(0));

						  for(SummaryReport summaryReportOne : summaryReport ) {
							  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
							  if(device != null) {
								  Set<Driver>  drivers = device.getDriver();
								  for(Driver driver : drivers ) {

									 summaryReportOne.setDriverName(driver.getName());
									 if(device.getFuel() != null) {
										 
										
										int litres=0;
										int Fuel =0;
										int distance=0;
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=obj.getInt("fuelPerKM");
										}

										distance = Integer.parseInt(summaryReportOne.getDistance());
										if(distance > 0) {
											Fuel = (distance/100)*litres;
										}

										summaryReportOne.setSpentFuel(Integer.toString(Fuel));

										

									 }
								}
							  }
							 }
							  
					  }
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport);
				 logger.info("************************ getDistanceFuelEngine ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			 }
		 }
			List<SummaryReport> summaryReport = new ArrayList<>();

			List<Long>userIds= new ArrayList<>();
			userIds.add(userId);

			List<Long>allDevices= new ArrayList<>();
			allDevices = deviceRepository.getDevicesUsers(userIds);

		 
			String plainCreds = "admin@fuinco.com:admin";
			byte[] plainCredsBytes = plainCreds.getBytes();
			
			byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
			String base64Creds = new String(base64CredsBytes);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + base64Creds);
			
			  String GET_URL = summaryUrl;
			  RestTemplate restTemplate = new RestTemplate();
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
			    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
				inputFormat.setLenient(false);
				outputFormat.setLenient(false);
				Date dateTo;
				Date dateFrom = new Date();
				Calendar c = Calendar.getInstance(); 
				c.setTime(dateFrom); 
				c.add(Calendar.DATE, 1);
				dateTo = c.getTime();
				String from = "";
				String to = "";
				from = outputFormat.format(dateFrom);
				to = outputFormat.format(dateTo);
					
				

			  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
				        .queryParam("type", "allEvents")
				        .queryParam("from", from)
				        .queryParam("to", to)
				        .queryParam("page", 1)
				        .queryParam("start", 0)
				        .queryParam("limit",25).build();
			  HttpEntity<String> request = new HttpEntity<String>(headers);
			  String URL = builder.toString();
			  if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  URL +="&deviceId="+allDevices.get(i);
				  }
			  }
			  System.out.println(URL);
			  ResponseEntity<List<SummaryReport>> rateResponse =
				        restTemplate.exchange(URL,
				                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
				            });
			  summaryReport = rateResponse.getBody();
			  if(summaryReport.size()>0) {
				  System.out.println(summaryReport.get(0));

				  for(SummaryReport summaryReportOne : summaryReport ) {
					  if(!summaryReportOne.getEngineHours().equals(null)) {
						  if(!summaryReportOne.getEngineHours().equals("0")) {
								SimpleDateFormat time = new SimpleDateFormat("HH:mm");
								try {
									Date date =  time.parse((String) summaryReportOne.getEngineHours());
									summaryReportOne.setEngineHours(String.valueOf(date.getHours()));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


							}
						  
					  }
					  

					  
					  
					  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
					  if(device != null) {
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {

							 summaryReportOne.setDriverName(driver.getName());
							 if(device.getFuel() != null) {
								 
								
								int litres=0;
								int Fuel =0;
								int distance=0;
								JSONObject obj = new JSONObject(device.getFuel());	
								if(obj.has("fuelPerKM")) {
									litres=obj.getInt("fuelPerKM");
								}

								distance = Integer.parseInt(summaryReportOne.getDistance());
								if(distance > 0) {
									Fuel = (distance/100)*litres;
								}

								summaryReportOne.setSpentFuel(Integer.toString(Fuel));

								

							 }
						}
					  }
					 }
					  
			  }
	    
		
		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport);
		logger.info("************************ getDistanceFuelEngine ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
	

}
