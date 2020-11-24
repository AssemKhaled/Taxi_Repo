package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoEventsRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

/**
 * service functionality related to charts
 * @author fuinco
 *
 */
@Component
@Service
public class ChartServiceImpl extends RestServiceController implements ChartService{
	
	private static final Log logger = LogFactory.getLog(ChartServiceImpl.class);
	GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	MongoPositionRepo mongoPositionRepo;
	
	@Autowired 
	DeviceRepository deviceRepository;

	@Autowired
	UserClientDeviceRepository userClientDeviceRepository;
	
	@Value("${summaryUrl}")
	private String summaryUrl;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	MongoEventsRepo mongoEventsRepo;
	
	
	
	/**
	 * get chart of status (online,offline,out of network) and total devices
	 */
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
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		Integer onlineDevices = 0;
		Integer outOfNetworkDevices = 0;
		Integer totalDevices = 0;
		Integer offlineDevices = 0;
		
		
		 userService.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 if(loggedUser.getAccountType().equals(4)) {
			
			List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);

			
			if(deviceIds.size()>0) {
				List<String> onlineDeviceIds = deviceRepository.getNumberOfOnlineDevicesListByIds(deviceIds);
				List<String> OutDeviceIds = deviceRepository.getNumberOfOutOfNetworkDevicesListByIds(deviceIds);
				
				onlineDevices = onlineDeviceIds.size();
				outOfNetworkDevices = OutDeviceIds.size();
				
				totalDevices = deviceRepository.getTotalNumberOfUserDevicesByIds(deviceIds);
				offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;

			}
			
			
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("total_devices", totalDevices);
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		 }
		 else {
			 List<User>childernUsers = userService.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
			
			List<String> onlineDeviceIds = deviceRepository.getNumberOfOnlineDevicesList(usersIds);
			List<String> OutDeviceIds = deviceRepository.getNumberOfOutOfNetworkDevicesList(usersIds);
			
			onlineDevices = onlineDeviceIds.size();
			outOfNetworkDevices = OutDeviceIds.size();
			
			totalDevices = deviceRepository.getTotalNumberOfUserDevices(usersIds);
			offlineDevices = totalDevices - onlineDevices - outOfNetworkDevices;

			
			Map devicesStatus = new HashMap();
			devicesStatus.put("online_devices", onlineDevices);
			devicesStatus.put("unknown_devices" ,outOfNetworkDevices);
			devicesStatus.put("offline_devices", offlineDevices);
			devicesStatus.put("total_devices", totalDevices);
			List<Map> data = new ArrayList<>();
			data.add(devicesStatus);
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }

	}


	/**
	 * get chart of distance and fuel from summary report traccar for top 10 today
	 *  
	 */
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
		
		
		if(userId == 0) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userService.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		userService.resetChildernArray();
		 List<Long>userIds= new ArrayList<>();

		 if(loggedUser.getAccountType().equals(4)) {
			
			List<Long> allDevices = userClientDeviceRepository.getDevicesIds(userId);
			List<Map> data = new ArrayList<>();

			if(allDevices.size()>0) {

				
				List<SummaryReport> summaryReport = new ArrayList<>();
			 
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
				  ResponseEntity<List<SummaryReport>> rateResponse =
					        restTemplate.exchange(URL,
					                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
					            });
				  
				  summaryReport = rateResponse.getBody();
		
				  if(summaryReport.size()>0) {
		
					  for(SummaryReport summaryReportOne : summaryReport ) {
						  
							Map devicesList = new HashMap();
		
		
						  Double totalDistance = 0.0 ;
						  double roundOffDistance = 0.0;
						  Double litres=10.0;
						  double roundOffFuel=0.0;
						  Double Fuel =0.0;
						  Double distance=0.0;
						  
						  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
							  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
							  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
							  summaryReportOne.setDistance(Double.toString(roundOffDistance));


						  }
						  
						  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
						  if(device != null) {
							  Set<Driver>  drivers = device.getDriver();
							  for(Driver driver : drivers ) {
		
								 summaryReportOne.setDriverName(driver.getName());
								 
							  }

							  
							  if(device.getFuel() != null) {
									if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=obj.getDouble("fuelPerKM");
											
										}
									}
							   }
							  
							 
						   }
						  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
						  if(distance > 0) {
							Fuel = (distance*litres)/100;
						  }
	
						  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
						  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
						  
							devicesList.put("spentFuel", summaryReportOne.getSpentFuel());
							devicesList.put("distance", summaryReportOne.getDistance());
							devicesList.put("deviceId", summaryReportOne.getDeviceId());
							devicesList.put("deviceName", summaryReportOne.getDeviceName());
							
							if(data.size() == 10) {
					    		Double newData = Double.parseDouble( devicesList.get("distance").toString() );
		
						    	for(int k=0;k<data.size();k++) {
						    		Double oldData = Double.parseDouble( data.get(k).get("distance").toString() );
		
						    		if(newData > oldData) {
		
						    			data.get(k).replace("spentFuel", devicesList.get("spentFuel"));
						    			data.get(k).replace("distance", devicesList.get("distance"));
						    			data.get(k).replace("deviceId", devicesList.get("deviceId"));
						    			data.get(k).replace("deviceName", devicesList.get("deviceName"));
						    			break;
		
						    		}
		
						    	
		
						    	}
		
						    }
						    if(data.size() < 10) {
								data.add(devicesList);
						    }
							
						 }
						  
				  }
		    
			}


		
		
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		 }
		 else {
			userIds.add(userId);
			List<SummaryReport> summaryReport = new ArrayList<>();


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
			  ResponseEntity<List<SummaryReport>> rateResponse =
				        restTemplate.exchange(URL,
				                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
				            });
			  
			  summaryReport = rateResponse.getBody();
			  List<Map> data = new ArrayList<>();
	
			  if(summaryReport.size()>0) {
	
				  for(SummaryReport summaryReportOne : summaryReport ) {
					  
						Map devicesList = new HashMap();
	
					  Double totalDistance = 0.0 ;
					  double roundOffDistance = 0.0;
					  Double litres=10.0;
					  double roundOffFuel=0.0;
					  Double Fuel =0.0;
					  Double distance=0.0;
					  
					  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  summaryReportOne.setDistance(Double.toString(roundOffDistance));


					  }
					  
					  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
					  if(device != null) {
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {
	
							 summaryReportOne.setDriverName(driver.getName());
							 
						  }
						  
						  
						  if(device.getFuel() != null) {
								if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
									JSONObject obj = new JSONObject(device.getFuel());	
									if(obj.has("fuelPerKM")) {
										litres=obj.getDouble("fuelPerKM");
										
									}

								}
						  }
						  
					   }
						  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
						  if(distance > 0) {
							Fuel = (distance*litres)/100;
						  }
	
						  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
						  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
					  
						devicesList.put("spentFuel", summaryReportOne.getSpentFuel());
						devicesList.put("distance", summaryReportOne.getDistance());
						devicesList.put("deviceId", summaryReportOne.getDeviceId());
						devicesList.put("deviceName", summaryReportOne.getDeviceName());
						
						if(data.size() == 10) {
				    		Double newData = Double.parseDouble( devicesList.get("distance").toString() );
	
					    	for(int k=0;k<data.size();k++) {
					    		Double oldData = Double.parseDouble( data.get(k).get("distance").toString() );
	
					    		if(newData > oldData) {
	
					    			data.get(k).replace("spentFuel", devicesList.get("spentFuel"));
					    			data.get(k).replace("distance", devicesList.get("distance"));
					    			data.get(k).replace("deviceId", devicesList.get("deviceId"));
					    			data.get(k).replace("deviceName", devicesList.get("deviceName"));
					    			break;
	
					    		}
	
					    	
	
					    	}
	
					    }
					    if(data.size() < 10) {
							data.add(devicesList);
					    }
						
					 }
					  
			  }
	    
		
		
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
			logger.info("************************ getDistanceFuelEngine ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
			
	}


	/**
	 * get number notification type chart for each one (offline,online,alarm ..etc)
	 */
	@Override
	public ResponseEntity<?> getNotificationsChart(String TOKEN, Long userId) {
		logger.info("************************ getNotifications STARTED ***************************");
		
		List<EventReport> notifications = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId != 0) {
		
			User user = userService.findById(userId);
			if(user != null) {
				userService.resetChildernArray();
				 List<Long>usersIds= new ArrayList<>();

				 if(user.getAccountType() == 4) {
					 
					List<Long> allDevices = userClientDeviceRepository.getDevicesIds(userId);
					List<Map> data = new ArrayList<>();

					if(allDevices.size()>0) {
						notifications = mongoEventsRepo.getAllNotificationsTodayChart(allDevices);
						

						Integer deviceOverspeed = 0;
						Integer ignitionOn = 0;
						Integer driverChanged = 0;
						Integer deviceOffline = 0;
						Integer geofenceEnter = 0;
						Integer commandResult = 0;
						Integer deviceMoving = 0;
						Integer textMessage = 0;
						Integer deviceOnline = 0;
						Integer deviceUnknown = 0;
						Integer maintenance = 0;
						Integer alarm = 0;
						Integer deviceFuelDrop = 0;
						Integer ignitionOff = 0;
						Integer geofenceExit = 0;
						Integer deviceStopped = 0;

						if(notifications.size()>0) {
							for(int i=0;i<notifications.size();i++) {

								if(notifications.get(i).getEventType().equals("deviceStopped")) {
									deviceOverspeed = deviceOverspeed+1;
								}
								if(notifications.get(i).getEventType().equals("ignitionOn")) {
									ignitionOn = ignitionOn+1;
								}
								if(notifications.get(i).getEventType().equals("driverChanged")) {
									driverChanged = driverChanged+1;
								}
								if(notifications.get(i).getEventType().equals("deviceOffline")) {
									deviceOffline = deviceOffline+1;
								}
								if(notifications.get(i).getEventType().equals("geofenceEnter")) {
									geofenceEnter = geofenceEnter+1;
								}
								if(notifications.get(i).getEventType().equals("commandResult")) {
									commandResult = commandResult+1;
								}
								if(notifications.get(i).getEventType().equals("deviceMoving")) {
									deviceMoving = deviceMoving+1;
								}
								if(notifications.get(i).getEventType().equals("textMessage")) {
									textMessage = textMessage+1;
								}
								if(notifications.get(i).getEventType().equals("deviceOnline")) {
									deviceOnline = deviceOnline+1;
								}
								if(notifications.get(i).getEventType().equals("deviceUnknown")) {
									deviceUnknown = deviceUnknown+1;
								}
								if(notifications.get(i).getEventType().equals("maintenance")) {
									maintenance = maintenance+1;
								}
								if(notifications.get(i).getEventType().equals("alarm")) {
									alarm = alarm+1;
								}
								if(notifications.get(i).getEventType().equals("deviceFuelDrop")) {
									deviceFuelDrop = deviceFuelDrop+1;
								}
								if(notifications.get(i).getEventType().equals("ignitionOff")) {
									ignitionOff = ignitionOff+1;
								}
								if(notifications.get(i).getEventType().equals("geofenceExit")) {
									geofenceExit = geofenceExit+1;
								}
								if(notifications.get(i).getEventType().equals("deviceStopped")) {
									deviceStopped = deviceStopped+1;
								}
								
							}
								
						}
						
						
						Map notificationList1 = new HashMap();
						notificationList1.put("type", "deviceOverspeed");
						notificationList1.put("count", deviceOverspeed);
						data.add(notificationList1);
						
						Map notificationList2 = new HashMap();
						notificationList2.put("type", "ignitionOn");
						notificationList2.put("count", ignitionOn);
						data.add(notificationList2);
						
						Map notificationList3 = new HashMap();
						notificationList3.put("type", "driverChanged");
						notificationList3.put("count", driverChanged);
						data.add(notificationList3);
						
						Map notificationList4 = new HashMap();
						notificationList4.put("type", "deviceOffline");
						notificationList4.put("count", deviceOffline);
						data.add(notificationList4);
						
						Map notificationList5 = new HashMap();
						notificationList5.put("type", "geofenceEnter");
						notificationList5.put("count", geofenceEnter);
						data.add(notificationList5);
						
						Map notificationList6 = new HashMap();
						notificationList6.put("type", "commandResult");
						notificationList6.put("count", commandResult);
						data.add(notificationList6);
						
						Map notificationList7 = new HashMap();
						notificationList7.put("type", "deviceMoving");
						notificationList7.put("count", deviceMoving);
						data.add(notificationList7);
						
						Map notificationList8 = new HashMap();
						notificationList8.put("type", "textMessage");
						notificationList8.put("count", textMessage);
						data.add(notificationList8);
						
						Map notificationList9 = new HashMap();
						notificationList9.put("type", "deviceOnline");
						notificationList9.put("count", deviceOnline);
						data.add(notificationList9);
						
						Map notificationList10 = new HashMap();
						notificationList10.put("type", "deviceUnknown");
						notificationList10.put("count", deviceUnknown);
						data.add(notificationList10);
						

						Map notificationList11 = new HashMap();
						notificationList11.put("type", "maintenance");
						notificationList11.put("count", maintenance);
						data.add(notificationList11);
						

						Map notificationList12 = new HashMap();
						notificationList12.put("type", "alarm");
						notificationList12.put("count", alarm);
						data.add(notificationList12);
						
						Map notificationList13 = new HashMap();
						notificationList13.put("type", "deviceFuelDrop");
						notificationList13.put("count", deviceFuelDrop);
						data.add(notificationList13);
						
						Map notificationList14 = new HashMap();
						notificationList14.put("type", "ignitionOff");
						notificationList14.put("count", ignitionOff);
						data.add(notificationList14);
						

						Map notificationList15 = new HashMap();
						notificationList15.put("type", "geofenceExit");
						notificationList15.put("count", geofenceExit);
						data.add(notificationList15);
						
						Map notificationList16 = new HashMap();
						notificationList16.put("type", "deviceStopped");
						notificationList16.put("count", deviceStopped);
						data.add(notificationList16);
						
						
					}

				    
					
							
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,notifications.size());
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				 }
				 else {
					 usersIds.add(userId);
					 
					 List<Long> allDevices = deviceRepository.getDevicesUsers(usersIds);

				    notifications = mongoEventsRepo.getAllNotificationsTodayChart(allDevices);
					
					List<Map> data = new ArrayList<>();

					Integer deviceOverspeed = 0;
					Integer ignitionOn = 0;
					Integer driverChanged = 0;
					Integer deviceOffline = 0;
					Integer geofenceEnter = 0;
					Integer commandResult = 0;
					Integer deviceMoving = 0;
					Integer textMessage = 0;
					Integer deviceOnline = 0;
					Integer deviceUnknown = 0;
					Integer maintenance = 0;
					Integer alarm = 0;
					Integer deviceFuelDrop = 0;
					Integer ignitionOff = 0;
					Integer geofenceExit = 0;
					Integer deviceStopped = 0;

					if(notifications.size()>0) {
						for(int i=0;i<notifications.size();i++) {

							if(notifications.get(i).getEventType().equals("deviceStopped")) {
								deviceOverspeed = deviceOverspeed+1;
							}
							if(notifications.get(i).getEventType().equals("ignitionOn")) {
								ignitionOn = ignitionOn+1;
							}
							if(notifications.get(i).getEventType().equals("driverChanged")) {
								driverChanged = driverChanged+1;
							}
							if(notifications.get(i).getEventType().equals("deviceOffline")) {
								deviceOffline = deviceOffline+1;
							}
							if(notifications.get(i).getEventType().equals("geofenceEnter")) {
								geofenceEnter = geofenceEnter+1;
							}
							if(notifications.get(i).getEventType().equals("commandResult")) {
								commandResult = commandResult+1;
							}
							if(notifications.get(i).getEventType().equals("deviceMoving")) {
								deviceMoving = deviceMoving+1;
							}
							if(notifications.get(i).getEventType().equals("textMessage")) {
								textMessage = textMessage+1;
							}
							if(notifications.get(i).getEventType().equals("deviceOnline")) {
								deviceOnline = deviceOnline+1;
							}
							if(notifications.get(i).getEventType().equals("deviceUnknown")) {
								deviceUnknown = deviceUnknown+1;
							}
							if(notifications.get(i).getEventType().equals("maintenance")) {
								maintenance = maintenance+1;
							}
							if(notifications.get(i).getEventType().equals("alarm")) {
								alarm = alarm+1;
							}
							if(notifications.get(i).getEventType().equals("deviceFuelDrop")) {
								deviceFuelDrop = deviceFuelDrop+1;
							}
							if(notifications.get(i).getEventType().equals("ignitionOff")) {
								ignitionOff = ignitionOff+1;
							}
							if(notifications.get(i).getEventType().equals("geofenceExit")) {
								geofenceExit = geofenceExit+1;
							}
							if(notifications.get(i).getEventType().equals("deviceStopped")) {
								deviceStopped = deviceStopped+1;
							}
							
						}
							
					}
					
					
					Map notificationList1 = new HashMap();
					notificationList1.put("type", "deviceOverspeed");
					notificationList1.put("count", deviceOverspeed);
					data.add(notificationList1);
					
					Map notificationList2 = new HashMap();
					notificationList2.put("type", "ignitionOn");
					notificationList2.put("count", ignitionOn);
					data.add(notificationList2);
					
					Map notificationList3 = new HashMap();
					notificationList3.put("type", "driverChanged");
					notificationList3.put("count", driverChanged);
					data.add(notificationList3);
					
					Map notificationList4 = new HashMap();
					notificationList4.put("type", "deviceOffline");
					notificationList4.put("count", deviceOffline);
					data.add(notificationList4);
					
					Map notificationList5 = new HashMap();
					notificationList5.put("type", "geofenceEnter");
					notificationList5.put("count", geofenceEnter);
					data.add(notificationList5);
					
					Map notificationList6 = new HashMap();
					notificationList6.put("type", "commandResult");
					notificationList6.put("count", commandResult);
					data.add(notificationList6);
					
					Map notificationList7 = new HashMap();
					notificationList7.put("type", "deviceMoving");
					notificationList7.put("count", deviceMoving);
					data.add(notificationList7);
					
					Map notificationList8 = new HashMap();
					notificationList8.put("type", "textMessage");
					notificationList8.put("count", textMessage);
					data.add(notificationList8);
					
					Map notificationList9 = new HashMap();
					notificationList9.put("type", "deviceOnline");
					notificationList9.put("count", deviceOnline);
					data.add(notificationList9);
					
					Map notificationList10 = new HashMap();
					notificationList10.put("type", "deviceUnknown");
					notificationList10.put("count", deviceUnknown);
					data.add(notificationList10);
					

					Map notificationList11 = new HashMap();
					notificationList11.put("type", "maintenance");
					notificationList11.put("count", maintenance);
					data.add(notificationList11);
					

					Map notificationList12 = new HashMap();
					notificationList12.put("type", "alarm");
					notificationList12.put("count", alarm);
					data.add(notificationList12);
					
					Map notificationList13 = new HashMap();
					notificationList13.put("type", "deviceFuelDrop");
					notificationList13.put("count", deviceFuelDrop);
					data.add(notificationList13);
					
					Map notificationList14 = new HashMap();
					notificationList14.put("type", "ignitionOff");
					notificationList14.put("count", ignitionOff);
					data.add(notificationList14);
					

					Map notificationList15 = new HashMap();
					notificationList15.put("type", "geofenceExit");
					notificationList15.put("count", geofenceExit);
					data.add(notificationList15);
					
					Map notificationList16 = new HashMap();
					notificationList16.put("type", "deviceStopped");
					notificationList16.put("count", deviceStopped);
					data.add(notificationList16);
					
					
					
							
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,notifications.size());
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
				 }

				   

				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}


	/**
	 * get top 10 device and driver working hours and ignition on off for today
	 */
	@Override
	public ResponseEntity<?> getMergeHoursIgnition(String TOKEN, Long userId) {
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
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Logged user is not found",devices);
			logger.info("************************ getIgnition ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		 userService.resetChildernArray();
		 List<Long>usersIds= new ArrayList<>();
		 if(loggedUser.getAccountType().equals(4)) {
			
			 List<Long> allDevices = userClientDeviceRepository.getDevicesIds(userId);
			 List<Map> finalData = new ArrayList<>();

			 if(allDevices.size()>0) {
				 List<String> positionIds = deviceRepository.getAllPositionsObjectIdsByIds(allDevices);
				
				 
				 Integer ignitionON= 0;
				 Integer ignitionOFF= 0;

				 ignitionON = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", true);
				 ignitionOFF = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", false);
				 

				List<Map> data = new ArrayList<>();
				
				data = mongoPositionRepo.getCharts(positionIds);

			    
			    
			    Map dev = new HashMap();
			    dev.put("ignition_on", ignitionON);
			    dev.put("ignition_off" ,ignitionOFF);
				
				
			    Map ig = new HashMap();
			    ig.put("status",dev);
			    ig.put("hours",data);

			    finalData.add(ig);
				
				
			 }
			 
			 
			
			
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",finalData,finalData.size());
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

		 }
		 else {
			 List<User>childernUsers = userService.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
			 
			 List<String> positionIds = deviceRepository.getAllPositionsObjectIds(usersIds);

			 Integer ignitionON= 0;
			 Integer ignitionOFF= 0;

			 ignitionON = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", true);
			 ignitionOFF = mongoPositionRepo.getCountFromAttrbuitesChart(positionIds, "ignition", false);



			List<Map> data = new ArrayList<>();
			
			data = mongoPositionRepo.getCharts(positionIds);
			List<Map> finalData = new ArrayList<>();

		   
		    
		    Map dev = new HashMap();
		    dev.put("ignition_on", ignitionON);
		    dev.put("ignition_off" ,ignitionOFF);
			
			
		    Map ig = new HashMap();
		    ig.put("status",dev);
		    ig.put("hours",data);

		    finalData.add(ig);
			
			
			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",finalData,finalData.size());
			logger.info("************************ getIgnitionMotion ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		 }
		 
		
	}
	
	

}
