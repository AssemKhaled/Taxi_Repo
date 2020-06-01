package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.EventReportByCurl;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.Position;
import com.example.examplequerydslspringdatajpamaven.entity.StopReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.example.examplequerydslspringdatajpamaven.entity.TripReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.EventRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.PositionRepository;
import com.example.examplequerydslspringdatajpamaven.repository.PositionSqlRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class ReportServiceImpl extends RestServiceController implements ReportService {
	
	 @Value("${stopsUrl}")
	 private String stopsUrl;
	 
	 @Value("${tripsUrl}")
	 private String tripsUrl;
	 
	 @Value("${eventsUrl}")
	 private String eventsUrl;
	 
	 @Value("${summaryUrl}")
	 private String summaryUrl;
	
	@Autowired
	EventRepository eventRepository;
	@Autowired
	PositionRepository poitionRepository;
	
	@Autowired
	PositionSqlRepository  positionSqlRepository;
	
	
	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	GroupsServiceImpl groupsServiceImpl;
	
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	DriverServiceImpl driverServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	private static final Log logger = LogFactory.getLog(ReportServiceImpl.class);
	
	GetObjectResponse getObjectResponse;

	@Override
	public ResponseEntity<?> getEventsReport(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String type,String search,Long userId) {
		logger.info("************************ getEventsReport STARTED ***************************");
	
		List<EventReport> eventReport = new ArrayList<EventReport>();
		List<Position>position=new ArrayList<Position>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		if(userId != 0) {
			
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "EVENTREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENTREPORT list",null);
					 logger.info("************************ EVENTREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			List<Long>allDevices= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",eventReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							
							search = "%"+search+"%";
							Integer size = null;
							if(type.equals("")) {
								eventReport = eventRepository.getEvents(allDevices, offset, start, end,search);
								if(eventReport.size()>0) {
									size=eventRepository.getEventsSize(allDevices,start, end);
									for(int i=0;i<eventReport.size();i++) {
										if(eventReport.get(i).getEventType().equals("alarm")) {
											JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
											eventReport.get(i).setEventType(obj.getString("alarm"));
										}
									}
									
								}
							}
							else {
								eventReport = eventRepository.getEventsSort(allDevices, offset, start, end,type,search);
								if(eventReport.size()>0) {
									size=eventRepository.getEventsSizeSort(allDevices,start, end,type);
									for(int i=0;i<eventReport.size();i++) {
										if(eventReport.get(i).getEventType().equals("alarm")) {
											JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
											eventReport.get(i).setEventType(obj.getString("alarm"));
										}
									}
									
								}
								
							}
								  
								  
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
							logger.info("************************ getEventsReport ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			Device device =deviceServiceImpl.findById(deviceId);
			
			
				
			if(device != null) {
				
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",eventReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					search = "%"+search+"%";
					Integer size = null;
					allDevices.add(deviceId);
					if(type.equals("")) {
						eventReport = eventRepository.getEvents(allDevices, offset, start, end,search);
						if(eventReport.size()>0) {
							size=eventRepository.getEventsSize(allDevices,start, end);
							for(int i=0;i<eventReport.size();i++) {
								if(eventReport.get(i).getEventType().equals("alarm")) {
									JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
									eventReport.get(i).setEventType(obj.getString("alarm"));
								}
							}
							
						}
					}
					else {
						eventReport = eventRepository.getEventsSort(allDevices, offset, start, end,type,search);
						if(eventReport.size()>0) {
							size=eventRepository.getEventsSizeSort(allDevices,start, end,type);
							for(int i=0;i<eventReport.size();i++) {
								if(eventReport.get(i).getEventType().equals("alarm")) {
									JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
									eventReport.get(i).setEventType(obj.getString("alarm"));
								}
							}
							
						}
						
					}

//					List<String> positionIds = new ArrayList<>();
//					for (EventReport eventreport : eventReport) {
//						positionIds.add(eventreport.getPositionId());
//					}
//					//here
//					position=poitionRepository.findAllByidIn(positionIds);
//					for(Position allpositions: position) {
//						for (EventReport eventreport : eventReport) {			
////						 if(allpositions.getId().equals(eventreport.getPositionId())) {				 					
////						 eventreport.setLongitude(allpositions.getLongitude());
////						// eventreport.setAttributes(allpositions.getAttributes());
////						 eventreport.setLatitude(allpositions.getLatitude());
////						 }
//					 }
//				 }
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id are Required",eventReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
			
	}
	
	@Override
	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String search,Long userId) {
		logger.info("************************ HoursDev STARTED ***************************");
	
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURSREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DEVICEWORKINGHOURSREPORT list",null);
					 logger.info("************************ DEVICEWORKINGHOURSREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			List<Long>allDevices= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							search = "%"+search+"%";		
							deviceHours = deviceRepository.getDeviceWorkingHours(allDevices,offset,start,end);
							Integer size = null;
							if(deviceHours.size()>0) {
		       				    size=deviceRepository.getDeviceWorkingHoursSize(allDevices,start, end);
								for(int i=0;i<deviceHours.size();i++) {
									JSONObject obj = new JSONObject(deviceHours.get(i).getAttributes());
									if(obj.has("todayHoursString")) {
										deviceHours.get(i).setHours(obj.getString("todayHoursString"));
									}
									else {
										deviceHours.get(i).setHours("0");
			
									}
									
								}
							
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
							logger.info("************************ HoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
							
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",deviceHours);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					//here
					search = "%"+search+"%";		
					allDevices.add(deviceId);
					deviceHours = deviceRepository.getDeviceWorkingHours(allDevices,offset,start,end);
//					 List<Integer> deviceIds = new ArrayList<>();
//					    for (DeviceWorkingHours devicehours: deviceHours) {
//					    	deviceIds.add(devicehours.getDeviceId());
//					   }
//						 List<Position>allPositions=poitionRepository.findAllBydeviceidIn(deviceIds);
//						 String start="";
//						 String end="";
//						 Position pos=poitionRepository.findTopByOrderByOrderdevicetimeDesc(start,end);
//						 if(start.equals("0") || end.equals("0")) {
//								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
//								return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//							}
//							else {
//								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//								inputFormat.setLenient(false);
//								outputFormat.setLenient(false);
//			
//								Date dateFrom;
//								Date dateTo;
//								try {
//									dateFrom = inputFormat.parse(start);
//									dateTo = inputFormat.parse(end);
//									
//									start = outputFormat.format(dateFrom);
//									end = outputFormat.format(dateTo);
//									
//									Date today=new Date();
//			
//									if(dateFrom.getTime() > dateTo.getTime()) {
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",deviceHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",deviceHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									
//									
//			
//			
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",deviceHours);
//									return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//								}
//							}
//					 	for(Position allpositions: allPositions) {
//						 for (DeviceWorkingHours devicehours: deviceHours) {			
//							 if(allpositions.getDeviceid().equals(devicehours.getDeviceId())) {				 
//								 devicehours.setPositionId(allpositions.getId());
//								 devicehours.setAttributes(allpositions.getAttributes());
//								 devicehours.setDeviceTime(allpositions.getDevicetime());
//							
//							 }
//						 }
//					 	}
					Integer size = null;
					if(deviceHours.size()>0) {
       				    size=deviceRepository.getDeviceWorkingHoursSize(allDevices,start, end);
						for(int i=0;i<deviceHours.size();i++) {
							JSONObject obj = new JSONObject(deviceHours.get(i).getAttributes());
							if(obj.has("todayHoursString")) {
								deviceHours.get(i).setHours(obj.getString("todayHoursString"));
							}
							else {
								deviceHours.get(i).setHours("0");
	
							}
							
						}
					
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
					logger.info("************************ HoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				//}
				
				}
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",deviceHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User Id is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDeviceWorkingHoursExport(String TOKEN,Long deviceId,Long groupId,String start,String end,Long userId) {
		logger.info("************************ exportHoursDev STARTED ***************************");
	
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<Long>allDevices= new ArrayList<>();
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURSREPORT", "export")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DEVICEWORKINGHOURSREPORT export",null);
					 logger.info("************************ DEVICEWORKINGHOURSREPORTExport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							deviceHours = deviceRepository.getDeviceWorkingHoursExport(allDevices,start,end);
							Integer size = null;
							if(deviceHours.size()>0) {
								size=deviceRepository.getDeviceWorkingHoursSize(allDevices,start, end);
								for(int i=0;i<deviceHours.size();i++) {
									JSONObject obj = new JSONObject(deviceHours.get(i).getAttributes());
									if(obj.has("todayHoursString")) {
										
										deviceHours.get(i).setHours(obj.getString("todayHoursString"));
									}
									else {
										deviceHours.get(i).setHours("0");

									}
									
								}
								
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
							logger.info("************************ exportHoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
							
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",deviceHours);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					allDevices.add(deviceId);
					deviceHours = deviceRepository.getDeviceWorkingHoursExport(allDevices,start,end);
//					 List<Integer> deviceIds = new ArrayList<>();
//					    for (DeviceWorkingHours devicehours: deviceHours) {
//					    	deviceIds.add(devicehours.getDeviceId());
//					   }
//						 List<Position>allPositions=poitionRepository.findAllBydeviceidIn(deviceIds);
//						 String start="";
//						 String end="";
//						 Position pos=poitionRepository.findTopByOrderByOrderdevicetimeDesc(start,end);
//						 if(start.equals("0") || end.equals("0")) {
//								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
//								return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//							}
//							else {
//								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//								inputFormat.setLenient(false);
//								outputFormat.setLenient(false);
//			
//								Date dateFrom;
//								Date dateTo;
//								try {
//									dateFrom = inputFormat.parse(start);
//									dateTo = inputFormat.parse(end);
//									
//									start = outputFormat.format(dateFrom);
//									end = outputFormat.format(dateTo);
//									
//									Date today=new Date();
//			
//									if(dateFrom.getTime() > dateTo.getTime()) {
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",deviceHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",deviceHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									
//									
//			
//			
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",deviceHours);
//									return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//								}
//							}
//					 	for(Position allpositions: allPositions) {
//						 for (DeviceWorkingHours devicehours: deviceHours) {			
//							 if(allpositions.getDeviceid().equals(devicehours.getDeviceId())) {				 
//								 devicehours.setPositionId(allpositions.getId());
//								 devicehours.setAttributes(allpositions.getAttributes());
//								 devicehours.setDeviceTime(allpositions.getDevicetime());
//							
//							 }
//						 }
//					 	}
					Integer size = null;
					if(deviceHours.size()>0) {
						size=deviceRepository.getDeviceWorkingHoursSize(allDevices,start, end);
						for(int i=0;i<deviceHours.size();i++) {
							JSONObject obj = new JSONObject(deviceHours.get(i).getAttributes());
							if(obj.has("todayHoursString")) {
								
								deviceHours.get(i).setHours(obj.getString("todayHoursString"));
							}
							else {
								deviceHours.get(i).setHours("0");

							}
							
						}
						
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
					logger.info("************************ exportHoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",deviceHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID and User ID is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDriverWorkingHours(String TOKEN,Long driverId,Long groupId,int offset,String start,String end,String search,Long userId) {
		logger.info("************************ HoursDev STARTED ***************************");
	
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if( userId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DRIVERWORKINGHOURSREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DRIVERWORKINGHOURSREPORT list",null);
					 logger.info("************************ DRIVERWORKINGHOURSREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			List<Long>allDrivers= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDrivers = groupRepository.getDriversFromGroup(groupId);

						}
						else if(group.getType().equals("device")) {
							allDrivers = groupRepository.getDriverFromDevices(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDrivers = groupRepository.getDriversFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							search = "%"+search+"%";
							driverHours = driverRepository.getDriverWorkingHours(allDrivers,offset,start,end);
							Integer size = null;
							if(driverHours.size()>0) {
		       				    size=driverRepository.getDriverWorkingHoursSize(allDrivers,start,end);
								for(int i=0;i<driverHours.size();i++) {
									JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
									if(obj.has("todayHoursString")) {
										driverHours.get(i).setHours(obj.getString("todayHoursString"));
									}
									else {
										driverHours.get(i).setHours("0");
			
									}
									
								}
							
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
							logger.info("************************ HoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
							
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Driver driver =driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",driverHours);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",driverHours);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>driverParent = driver.getUserDriver();
							if(driverParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : driverParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					search = "%"+search+"%";
					allDrivers.add(driverId);
					driverHours = driverRepository.getDriverWorkingHours(allDrivers,offset,start,end);
//					 List<Integer> deviceIds = new ArrayList<>();
//					    for (DriverWorkingHours devicehours: driverHours) {
//					    	deviceIds.add(devicehours.getDeviceId());
//					   }
//						 List<Position>allPositions=poitionRepository.findAllBydeviceidIn(deviceIds);
//						 String start="";
//						 String end="";
//						 Position pos=poitionRepository.findTopByOrderByOrderdevicetimeDesc(start,end);
//						 if(start.equals("0") || end.equals("0")) {
//								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",driverHours);
//								return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//							}
//							else {
//								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//								inputFormat.setLenient(false);
//								outputFormat.setLenient(false);
//			
//								Date dateFrom;
//								Date dateTo;
//								try {
//									dateFrom = inputFormat.parse(start);
//									dateTo = inputFormat.parse(end);
//									
//									start = outputFormat.format(dateFrom);
//									end = outputFormat.format(dateTo);
//									
//									Date today=new Date();
//			
//									if(dateFrom.getTime() > dateTo.getTime()) {
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",driverHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
//										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",driverHours);
//										return  ResponseEntity.badRequest().body(getObjectResponse);
//									}
//									
//									
//			
//			
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",driverHours);
//									return  ResponseEntity.badRequest().body(getObjectResponse);
//			
//								}
//							}
////					 	for(Position allpositions: allPositions) {
//						 for (DriverWorkingHours driverhours: driverHours) {			
//							 if(allpositions.getDeviceid().equals(driverhours.getDeviceId())) {				 
//								 driverhours.setAttributes(allpositions.getAttributes());
//								 driverhours.setDeviceTime(allpositions.getDevicetime());
//							
//							 }
//						 }
//					 	}
					Integer size = null;
					if(driverHours.size()>0) {
       				    size=driverRepository.getDriverWorkingHoursSize(allDrivers,start,end);
						for(int i=0;i<driverHours.size();i++) {
							JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
							if(obj.has("todayHoursString")) {
								driverHours.get(i).setHours(obj.getString("todayHoursString"));
							}
							else {
								driverHours.get(i).setHours("0");
	
							}
							
						}
					
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
					logger.info("************************ HoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Driver ID is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID and userId is Required",driverHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDriverWorkingHoursExport(String TOKEN,Long driverId,Long groupId,String start,String end,Long userId) {
		logger.info("************************ exportHoursDev STARTED ***************************");
	
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if( userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DRIVERWORKINGHOURSREPORT", "export")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DRIVERWORKINGHOURSREPORT export",null);
					 logger.info("************************ DRIVERWORKINGHOURSREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			List<Long>allDrivers= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDrivers = groupRepository.getDriversFromGroup(groupId);

						}
						else if(group.getType().equals("device")) {
							allDrivers = groupRepository.getDriverFromDevices(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDrivers = groupRepository.getDriversFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							
							driverHours = driverRepository.getDriverWorkingHoursExport(allDrivers,start,end);
							
							Integer size = null;
							if(driverHours.size()>0) {
								size=driverRepository.getDriverWorkingHoursSize(allDrivers,start, end);
								for(int i=0;i<driverHours.size();i++) {
									JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
									if(obj.has("todayHoursString")) {
										driverHours.get(i).setHours(obj.getString("todayHoursString"));
									}
									else {
										driverHours.get(i).setHours("0");

									}
									
								}
								
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
							logger.info("************************ exportHoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Driver driver =driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",driverHours);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",driverHours);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>driverParent = driver.getUserDriver();
							if(driverParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : driverParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ getDriverWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					allDrivers.add(driverId);
					driverHours = driverRepository.getDriverWorkingHoursExport(allDrivers,start,end);
					
					Integer size = null;
					if(driverHours.size()>0) {
						size=driverRepository.getDriverWorkingHoursSize(allDrivers,start, end);
						for(int i=0;i<driverHours.size();i++) {
							JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
							if(obj.has("todayHoursString")) {
								driverHours.get(i).setHours(obj.getString("todayHoursString"));
							}
							else {
								driverHours.get(i).setHours("0");

							}
							
						}
						
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
					logger.info("************************ exportHoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Driver ID is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID and userId is Required",driverHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getEventsReportToExcel(String TOKEN,Long deviceId,Long groupId,String start,String end,String type,Long userId) {
		logger.info("************************ getEventsReport STARTED ***************************");
	
		List<EventReport> eventReport = new ArrayList<EventReport>();
		List<Position>position=new ArrayList<Position>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "EVENTREPORT", "export")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENTREPORT export",null);
					 logger.info("************************ EVENTREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			List<Long>allDevices= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",eventReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							
							if(type.equals("")) {
								eventReport = eventRepository.getEventsToExcel(allDevices,start,end);

							}
							else {
								eventReport = eventRepository.getEventsToExcelSort(allDevices,start,end,type);

							}
							if(eventReport.size()>0) {
								for(int i=0;i<eventReport.size();i++) {
									if(eventReport.get(i).getEventType().equals("alarm")) {
										JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
										eventReport.get(i).setEventType(obj.getString("alarm"));
									}
								}
								
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
							logger.info("************************ getEventsReport ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);	  
								  
							
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",eventReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					allDevices.add(deviceId);
					if(type.equals("")) {
						eventReport = eventRepository.getEventsToExcel(allDevices,start,end);

					}
					else {
						eventReport = eventRepository.getEventsToExcelSort(allDevices,start,end,type);

					}
					//here
//					  List<String> positionIds = new ArrayList<>();
//						for (EventReport eventreport : eventReport) {
//							positionIds.add(eventreport.getPositionId());
//						}
						//here
//						position=poitionRepository.findAllByidIn(positionIds);
//						for(Position allpositions: position) {
//							for (EventReport eventreport : eventReport) {			
//							 if(allpositions.getId().equals(eventreport.getPositionId())) {				 					
//							 eventreport.setLongitude(allpositions.getLongitude());
//							// eventreport.setAttributes(allpositions.getAttributes());
//							 eventreport.setLatitude(allpositions.getLatitude());
//							 }
//						 }
//					 }
					if(eventReport.size()>0) {
						for(int i=0;i<eventReport.size();i++) {
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",eventReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	@Override
	public ResponseEntity<?> getNotifications(String TOKEN,Long userId, int offset,String search) {
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
		
			User user = userServiceImpl.findById(userId);
			if(user != null) {
				userServiceImpl.resetChildernArray();
				 if(user.getAccountType() == 4) {
					 Set<User> parentClients = user.getUsersOfUser();
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
						
						 search = "%"+search+"%";
							notifications= eventRepository.getNotifications(usersIds, offset,search);
							Integer size=0;
							if(notifications.size()>0) {
								size=eventRepository.getNotificationsSize(userId);
								for(int i=0;i<notifications.size();i++) {
									if(notifications.get(i).getEventType().equals("alarm")) {
										JSONObject obj = new JSONObject(notifications.get(i).getAttributes());
										notifications.get(i).setEventType(obj.getString("alarm"));
									}
								}
									
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications,size);
							logger.info("************************ getNotifications ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
					 }
				 }
				 
				 List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
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
				 System.out.println("Ids"+usersIds.toString());
				
					search = "%"+search+"%";
					notifications= eventRepository.getNotifications(usersIds, offset,search);
					Integer size=0;
					if(notifications.size()>0) {
						size=eventRepository.getNotificationsSize(userId);
						for(int i=0;i<notifications.size();i++) {
							if(notifications.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(notifications.get(i).getAttributes());
								notifications.get(i).setEventType(obj.getString("alarm"));
							}
						}
							
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications,size);
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				
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

	
	 
	@Override
	public ResponseEntity<?> getStopsReport(String TOKEN,Long deviceId,Long groupId,  String type, String from, String to, int page, int start,
			int limit,Long userId) {

		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "STOPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get STOPREPORT list",null);
					 logger.info("************************ STOPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			 if(groupId != 0) {
			    	
			    	Group group=groupRepository.findOne(groupId);

					if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : groupParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							List<Long>allDevices= new ArrayList<>();
							if(group.getType().equals("driver")) {
								allDevices = groupRepository.getDevicesFromDriver(groupId);

							}
							else if(group.getType().equals("device")) {
								allDevices = groupRepository.getDevicesFromGroup(groupId);
								
							}
							else if(group.getType().equals("geofence")) {
								allDevices = groupRepository.getDevicesFromGeofence(groupId);

							}
							else {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(from.equals("0") || to.equals("0")) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							else {
								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
								inputFormat.setLenient(false);
								outputFormat.setLenient(false);
								Date dateFrom;
								Date dateTo;
								try {
									dateFrom = inputFormat.parse(from);
									dateTo = inputFormat.parse(to);

									from = outputFormat.format(dateFrom);
									to = outputFormat.format(dateTo);
									
									Date today=new Date();

									if(dateFrom.getTime() > dateTo.getTime()) {
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);

								}
								
								String plainCreds = "admin@fuinco.com:admin";
								byte[] plainCredsBytes = plainCreds.getBytes();
								
								byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
								String base64Creds = new String(base64CredsBytes);

								HttpHeaders headers = new HttpHeaders();
								headers.add("Authorization", "Basic " + base64Creds);
								
								  String GET_URL = stopsUrl;
								  RestTemplate restTemplate = new RestTemplate();
								  restTemplate.getMessageConverters()
							        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

								  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
									        .queryParam("type", type)
									        .queryParam("from", from)
									        .queryParam("to", to)
									        .queryParam("page", page)
									        .queryParam("start", start)
									        .queryParam("limit",limit).build();
								  HttpEntity<String> request = new HttpEntity<String>(headers);
								  String URL = builder.toString();
								  if(allDevices.size()>0) {
									  for(int i=0;i<allDevices.size();i++) {
										  URL +="&deviceId="+allDevices.get(i);
									  }
								  }
								  ResponseEntity<List<StopReport>> rateResponse =
									        restTemplate.exchange(URL,
									                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
									            });
								  stopReport = rateResponse.getBody();
								  
								  
								  
								  if(stopReport.size()>0) {

									  for(StopReport stopReportOne : stopReport ) {
										  Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
										  Set<Driver>  drivers = device.getDriver();
										  for(Driver driver : drivers ) {

											 stopReportOne.setDriverName(driver.getName());
											 stopReportOne.setDriverUniqueId(driver.getUniqueid());
										}
									  }
								  }
								  
								  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport,stopReport.size());
								  logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.ok().body(getObjectResponse);

							}
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			
			Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",stopReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",stopReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);


					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = stopsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  stopReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();

					  ResponseEntity<List<StopReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
						            });
					  stopReport = rateResponse.getBody();
					  
					  
					  
					  if(stopReport.size()>0) {
						  Set<Driver>  drivers = device.getDriver();

						  for(StopReport stopReportOne : stopReport ) {
							  for(Driver driver : drivers ) {

								 stopReportOne.setDriverName(driver.getName());
								 stopReportOne.setDriverUniqueId(driver.getUniqueid());
							}
						  }
					  }
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport,stopReport.size());
					  logger.info("************************ getStopsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);


					
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",stopReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		

	}

	@Override
	public ResponseEntity<?> getTripsReport(String TOKEN,Long deviceId,Long groupId, String type, String from, String to, int page, int start,
			int limit,Long userId) {

		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "TRIPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get TRIPREPORT list",null);
					 logger.info("************************ TRIPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
		    if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);
							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = tripsUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }
							  ResponseEntity<List<TripReport>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
								            });
							  tripReport = rateResponse.getBody();
							  if(tripReport.size()>0) {

								  for(TripReport tripReportOne : tripReport ) {
										
									  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());
									  Set<Driver>  drivers = device.getDriver();
									  for(Driver driver : drivers ) {

										 tripReportOne.setDriverName(driver.getName());
										 tripReportOne.setDriverUniqueId(driver.getUniqueid());

										
										 
									}
									  if( device.getFuel() != null) {
											 
											Double litres=0.0;
											Double Fuel =0.0;
											Double distance=0.0;
											
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=Double.parseDouble(obj.get("fuelPerKM").toString());
											}

											distance = Double.parseDouble(tripReportOne.getDistance());
											if(distance > 0) {
												Fuel = (distance/100)*litres;
											}

											tripReportOne.setSpentFuel(Double.toString(Fuel));

											

										 }
									  Long time=(long) 0;

									  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

									  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
									  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
									  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
									  
									  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
									  tripReportOne.setDuration(totalHours);

								  }
							  }
							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
							  logger.info("************************ getTripsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = tripsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  tripReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  ResponseEntity<List<TripReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
						            });
					  tripReport = rateResponse.getBody();
					  if(tripReport.size()>0) {
						  Set<Driver>  drivers = device.getDriver();

						  for(TripReport tripReportOne : tripReport ) {
							  for(Driver driver : drivers ) {

								 tripReportOne.setDriverName(driver.getName());
								 tripReportOne.setDriverUniqueId(driver.getUniqueid());

								 
								 
							}
							  if( device.getFuel() != null) {
									 
									Double litres=0.0;
									Double Fuel =0.0;
									Double distance=0.0;
									
									JSONObject obj = new JSONObject(device.getFuel());	
									if(obj.has("fuelPerKM")) {
										litres=Double.parseDouble(obj.get("fuelPerKM").toString());
									}

									distance = Double.parseDouble(tripReportOne.getDistance());
									if(distance > 0) {
										Fuel = (distance/100)*litres;
									}

									tripReportOne.setSpentFuel(Double.toString(Fuel));

									

								 }
							  Long time=(long) 0;

							  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
							  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
							  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
							  
							  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
							  tripReportOne.setDuration(totalHours);
						  }
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
					  logger.info("************************ getTripsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is Required",tripReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		


	}
	
	@Override
	public ResponseEntity<?> getEventsReportByType(String TOKEN,Long deviceId,Long groupId, String type, String from, String to, int page, int start,
			int limit,Long userId) {

		logger.info("************************ getEventsReport STARTED ***************************");

		List<EventReportByCurl> eventReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "TRIPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get TRIPREPORT list",null);
					 logger.info("************************ TRIPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
		    if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);
							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = eventsUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }

							  ResponseEntity<List<EventReportByCurl>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<EventReportByCurl>>() {
								            });
							  eventReport = rateResponse.getBody();
							  if(eventReport.size()>0) {

								  for(EventReportByCurl eventReportOne : eventReport ) {
									  Device device= deviceServiceImpl.findById(eventReportOne.getDeviceId());
									  Set<Driver>  drivers = device.getDriver();
									  for(Driver driver : drivers ) {

										  eventReportOne.setDriverName(driver.getName());
										  eventReportOne.setDeviceName(device.getName());


										 
									}
									  
									  if(eventReportOne.getType().equals("alarm")) {

									        ObjectMapper objectMapper = new ObjectMapper();
									        Map<String, String> map = objectMapper.convertValue(eventReportOne.getAttributes(), Map.class);
											eventReportOne.setType(map.get("alarm"));
										}
									  

									
										
								  }
							  }	  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
							  logger.info("************************ getEventsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
							 
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }

            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",eventReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",eventReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = eventsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
					  ResponseEntity<List<EventReportByCurl>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<EventReportByCurl>>() {
						            });
					  eventReport = rateResponse.getBody();
					  if(eventReport.size()>0) {
						  Set<Driver>  drivers = device.getDriver();

						  for(EventReportByCurl eventReportOne : eventReport ) {
							  for(Driver driver : drivers ) {

								  eventReportOne.setDriverName(driver.getName());
								  eventReportOne.setDeviceName(device.getName());

								 
								 
							}
							if(eventReportOne.getType().equals("alarm")) {

						        ObjectMapper objectMapper = new ObjectMapper();
						        Map<String, String> map = objectMapper.convertValue(eventReportOne.getAttributes(), Map.class);
								eventReportOne.setType(map.get("alarm"));
							}
								
						  }
					  }	  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
					  logger.info("************************ getEventsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",eventReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",eventReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		


	}
	
	@Override
	public ResponseEntity<?> getSummaryReport(String TOKEN,Long deviceId, Long groupId,String type, String from, String to, int page, int start,
			int limit,Long userId) {

		logger.info("************************ getEventsReport STARTED ***************************");

		List<SummaryReport> summaryReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",summaryReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",summaryReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "TRIPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get TRIPREPORT list",null);
					 logger.info("************************ TRIPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
            if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);
							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
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

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
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
							  System.out.println(summaryReport);

							  if(summaryReport.size()>0) {

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
							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
							  logger.info("************************ getEventsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",summaryReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",summaryReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
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

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//                    summaryReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  ResponseEntity<List<SummaryReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
						            });
					  summaryReport = rateResponse.getBody();
					  if(summaryReport.size()>0) {
						  Set<Driver>  drivers = device.getDriver();

						  for(SummaryReport summaryReportOne : summaryReport ) {
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
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
					  logger.info("************************ getEventsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",summaryReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",summaryReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		


	}
	
	@Override
	public ResponseEntity<?> getSensorsReport(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String search,Long userId) {
		logger.info("************************ getSensorsReport STARTED ***************************");
	
		List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURSREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DEVICEWORKINGHOURSREPORT list",null);
					 logger.info("************************ DEVICEWORKINGHOURSREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			List<Long>allDevices= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							search = "%"+search+"%";
							positionsList = positionSqlRepository.getSensorsList(allDevices,start, end,offset);
							Integer size = null;
							if(positionsList.size()>0) {
		       				    size=positionSqlRepository.getSensorsListSize(allDevices,start, end);
								for(int i=0;i<positionsList.size();i++) {
									JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
									
									if(obj.has("weight")) {
										positionsList.get(i).setWeight(obj.get("weight").toString());
									}
									if(obj.has("adc1")) {
										positionsList.get(i).setSensor1(obj.get("adc1").toString());
									}
									if(obj.has("adc2")) {
										positionsList.get(i).setSensor2(obj.get("adc2").toString());
									}

									
									
								}
							
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionsList,size);
							logger.info("************************ HoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);

						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",positionsList);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",positionsList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",positionsList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",positionsList);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					search = "%"+search+"%";
					allDevices.add(deviceId);
					positionsList = positionSqlRepository.getSensorsList(allDevices,start, end,offset);
					Integer size = null;
					if(positionsList.size()>0) {
       				    size=positionSqlRepository.getSensorsListSize(allDevices,start, end);
						for(int i=0;i<positionsList.size();i++) {
							JSONObject obj = new JSONObject(positionsList.get(i).getAttributes());
							
							if(obj.has("weight")) {
								positionsList.get(i).setWeight(obj.get("weight").toString());
							}
							if(obj.has("adc1")) {
								positionsList.get(i).setSensor1(obj.get("adc1").toString());
							}
							if(obj.has("adc2")) {
								positionsList.get(i).setSensor2(obj.get("adc2").toString());
							}

							
							
						}
					
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionsList,size);
					logger.info("************************ HoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positionsList);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID and User Id is Required",positionsList);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	@Override
	public ResponseEntity<?> getSensorsReportExport(String TOKEN,Long deviceId,Long groupId,String start,String end,Long userId) {
		logger.info("************************ exportHoursDev STARTED ***************************");
	
		List<CustomPositions> positionList = new ArrayList<CustomPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionList);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionList);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURSREPORT", "export")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DEVICEWORKINGHOURSREPORT export",null);
					 logger.info("************************ DEVICEWORKINGHOURSREPORTExport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			List<Long>allDevices= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							positionList = positionSqlRepository.getSensorsListExport(allDevices,start, end);
							Integer size = null;
							if(positionList.size()>0) {
								for(int i=0;i<positionList.size();i++) {
									JSONObject obj = new JSONObject(positionList.get(i).getAttributes());
									
									if(obj.has("weight")) {
										positionList.get(i).setWeight(obj.get("weight").toString());
									}
									if(obj.has("adc1")) {
										positionList.get(i).setSensor1(obj.get("adc1").toString());
									}
									if(obj.has("adc2")) {
										positionList.get(i).setSensor2(obj.get("adc2").toString());
									}
									
									
								}
								
							}
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionList,size);
							logger.info("************************ exportHoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);

							

						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",positionList);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",positionList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",positionList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",positionList);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionList);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionList);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					positionList = positionSqlRepository.getSensorsListExport(allDevices,start, end);
					Integer size = null;
					if(positionList.size()>0) {
						for(int i=0;i<positionList.size();i++) {
							JSONObject obj = new JSONObject(positionList.get(i).getAttributes());
							
							if(obj.has("weight")) {
								positionList.get(i).setWeight(obj.get("weight").toString());
							}
							if(obj.has("adc1")) {
								positionList.get(i).setSensor1(obj.get("adc1").toString());
							}
							if(obj.has("adc2")) {
								positionList.get(i).setSensor2(obj.get("adc2").toString());
							}
							
							
						}
						
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionList,size);
					logger.info("************************ exportHoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positionList);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID and User ID is Required",positionList);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	@Override
	public ResponseEntity<?> getNumTripsReport(String TOKEN, Long deviceId, Long driverId, Long groupId, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "TRIPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get TRIPREPORT list",null);
					 logger.info("************************ TRIPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
		    if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);
							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = tripsUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }
							  ResponseEntity<List<TripReport>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
								            });
							  tripReport = rateResponse.getBody();
							 
							  List<Map> data = new ArrayList<Map>();
							  if(tripReport.size()>0) {

								  Map devicesStatus = new HashMap();
							      devicesStatus.put("groupName", group.getName());
								  devicesStatus.put("groupId" ,group.getId());
								  
								  data.add(devicesStatus);
							  }
							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,tripReport.size());
							  logger.info("************************ getTripsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
		    
		    if(driverId != 0) {
		    	
		    	Driver driver=driverRepository.findOne(driverId);

				if(driver != null) {
					if(driver.getDelete_date() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>driverParents = driver.getUserDriver();
								if(driverParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : driverParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						Set<Device>  devices = driver.getDevice();
						for(Device device : devices ) {
	
							deviceId = device.getId();
						}
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
		    
		    
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = tripsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
					  ResponseEntity<List<TripReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
						            });
					  tripReport = rateResponse.getBody();
					  List<Map> data = new ArrayList<>();
					  if(tripReport.size()>0) {

						  Map devicesStatus = new HashMap();
					      devicesStatus.put("deviceName", device.getName());
						  devicesStatus.put("deviceId" ,device.getId());
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {

							  devicesStatus.put("driverName", driver.getName());
							  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  }

						  data.add(devicesStatus);
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,tripReport.size());
					  logger.info("************************ getTripsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is Required",tripReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> getNumStopsReport(String TOKEN, Long deviceId, Long driverId, Long groupId, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "STOPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get STOPREPORT list",null);
					 logger.info("************************ STOPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			 if(groupId != 0) {
			    	
			    	Group group=groupRepository.findOne(groupId);

					if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : groupParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							List<Long>allDevices= new ArrayList<>();
							if(group.getType().equals("driver")) {
								allDevices = groupRepository.getDevicesFromDriver(groupId);

							}
							else if(group.getType().equals("device")) {
								allDevices = groupRepository.getDevicesFromGroup(groupId);
								
							}
							else if(group.getType().equals("geofence")) {
								allDevices = groupRepository.getDevicesFromGeofence(groupId);

							}
							else {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(from.equals("0") || to.equals("0")) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							else {
								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
								inputFormat.setLenient(false);
								outputFormat.setLenient(false);
								Date dateFrom;
								Date dateTo;
								try {
									dateFrom = inputFormat.parse(from);
									dateTo = inputFormat.parse(to);

									from = outputFormat.format(dateFrom);
									to = outputFormat.format(dateTo);
									
									Date today=new Date();

									if(dateFrom.getTime() > dateTo.getTime()) {
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);

								}
								
								String plainCreds = "admin@fuinco.com:admin";
								byte[] plainCredsBytes = plainCreds.getBytes();
								
								byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
								String base64Creds = new String(base64CredsBytes);

								HttpHeaders headers = new HttpHeaders();
								headers.add("Authorization", "Basic " + base64Creds);
								
								  String GET_URL = stopsUrl;
								  RestTemplate restTemplate = new RestTemplate();
								  restTemplate.getMessageConverters()
							        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

								  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
									        .queryParam("type", type)
									        .queryParam("from", from)
									        .queryParam("to", to)
									        .queryParam("page", page)
									        .queryParam("start", start)
									        .queryParam("limit",limit).build();
								  HttpEntity<String> request = new HttpEntity<String>(headers);
								  String URL = builder.toString();
								  if(allDevices.size()>0) {
									  for(int i=0;i<allDevices.size();i++) {
										  URL +="&deviceId="+allDevices.get(i);
									  }
								  }
								  ResponseEntity<List<StopReport>> rateResponse =
									        restTemplate.exchange(URL,
									                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
									            });
								  stopReport = rateResponse.getBody();
								  
								  List<Map> data = new ArrayList<Map>();
								  if(stopReport.size()>0) {

									  Map devicesStatus = new HashMap();
								      devicesStatus.put("groupName", group.getName());
									  devicesStatus.put("groupId" ,group.getId());
									  
									  data.add(devicesStatus);
								  }
								  
								  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,stopReport.size());
								  logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.ok().body(getObjectResponse);

							}
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			
			 
			    if(driverId != 0) {
			    	
			    	Driver driver=driverRepository.findOne(driverId);

					if(driver != null) {
						if(driver.getDelete_date() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>driverParents = driver.getUserDriver();
									if(driverParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : driverParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							Set<Device>  devices = driver.getDevice();
							for(Device device : devices ) {
		
								deviceId = device.getId();
							}
							
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			    
			    
			Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",stopReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",stopReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);


					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = stopsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  stopReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();

					  ResponseEntity<List<StopReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
						            });
					  stopReport = rateResponse.getBody();
					  
					  
					  List<Map> data = new ArrayList<>();
					  if(stopReport.size()>0) {

						  Map devicesStatus = new HashMap();
					      devicesStatus.put("deviceName", device.getName());
						  devicesStatus.put("deviceId" ,device.getId());
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {

							  devicesStatus.put("driverName", driver.getName());
							  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  }

						  data.add(devicesStatus);
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,stopReport.size());
					  logger.info("************************ getStopsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);


					
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",stopReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> geTotalTripsReport(String TOKEN, Long deviceId, Long driverId, Long groupId, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "TRIPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get TRIPREPORT list",null);
					 logger.info("************************ TRIPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
		    if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						List<Long>allDevices= new ArrayList<>();
						if(group.getType().equals("driver")) {
							allDevices = groupRepository.getDevicesFromDriver(groupId);

						}
						else if(group.getType().equals("device")) {
							allDevices = groupRepository.getDevicesFromGroup(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDevices = groupRepository.getDevicesFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(from.equals("0") || to.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);
							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(from);
								dateTo = inputFormat.parse(to);

								from = outputFormat.format(dateFrom);
								to = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							String plainCreds = "admin@fuinco.com:admin";
							byte[] plainCredsBytes = plainCreds.getBytes();
							
							byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
							String base64Creds = new String(base64CredsBytes);

							HttpHeaders headers = new HttpHeaders();
							headers.add("Authorization", "Basic " + base64Creds);
							
							  String GET_URL = tripsUrl;
							  RestTemplate restTemplate = new RestTemplate();
							  restTemplate.getMessageConverters()
						        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

							  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
								        .queryParam("type", type)
								        .queryParam("from", from)
								        .queryParam("to", to)
								        .queryParam("page", page)
								        .queryParam("start", start)
								        .queryParam("limit",limit).build();
							  HttpEntity<String> request = new HttpEntity<String>(headers);
							  String URL = builder.toString();
							  if(allDevices.size()>0) {
								  for(int i=0;i<allDevices.size();i++) {
									  URL +="&deviceId="+allDevices.get(i);
								  }
							  }
							  ResponseEntity<List<TripReport>> rateResponse =
								        restTemplate.exchange(URL,
								                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
								            });
							  tripReport = rateResponse.getBody();
							 
							  List<Map> data = new ArrayList<Map>();
							  if(tripReport.size()>0) {

								  Double totalDistance = 0.0 ;
								  double roundOffDistance = 0.0;
								  Long time = (long) 0;
								  Double totalFuel=0.0;
								  double roundOffFuel = 0.0;
								  String totalDuration = "00:00:00";
								  
								  for(TripReport tripReportOne : tripReport ) {

									  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
										  totalDistance += Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
										  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;


									  }
									  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {

										  time += Math.abs(  Long.parseLong(tripReportOne.getDuration())  );
										  
		     							  Long hours =   TimeUnit.MILLISECONDS.toHours(time) ;
										  Long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
										  Long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
										  
										  totalDuration = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds);

									  }
									  if(tripReportOne.getSpentFuel() != null && tripReportOne.getSpentFuel() != "") {
										  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());
										  if( device.getFuel() != null) {
												 
												int litres=0;
												int Fuel =0;
												int distance=0;
												JSONObject obj = new JSONObject(device.getFuel());	
												if(obj.has("fuelPerKM")) {
													litres=Math.abs( Integer.parseInt(obj.get("fuelPerKM").toString()) );
												}

												distance =Math.abs( Integer.parseInt(tripReportOne.getDistance()) );
												if(distance > 0) {
													Fuel = (distance/100)*litres;
												}

												tripReportOne.setSpentFuel(Integer.toString(Fuel));


											 }
											totalFuel += Double.parseDouble(tripReportOne.getSpentFuel());
											roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;

									  }

								  }
								  

								  Map devicesStatus = new HashMap();
							      devicesStatus.put("totalDrivingHours",totalDuration);
							      devicesStatus.put("totalDistance", roundOffDistance);
							      devicesStatus.put("totalSpentFuel", roundOffFuel);
							      devicesStatus.put("groupName", group.getName());
								  devicesStatus.put("groupId" ,group.getId());
								  
								  data.add(devicesStatus);
							  }
							  
							  
							  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,tripReport.size());
							  logger.info("************************ getTripsReport ENDED ***************************");
								return  ResponseEntity.ok().body(getObjectResponse);
						}
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
		    
		    if(driverId != 0) {
		    	
		    	Driver driver=driverRepository.findOne(driverId);

				if(driver != null) {
					if(driver.getDelete_date() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>driverParents = driver.getUserDriver();
								if(driverParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : driverParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						Set<Device>  devices = driver.getDevice();
						for(Device device : devices ) {
	
							deviceId = device.getId();
						}
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
		    
		    
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",tripReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = tripsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
					  ResponseEntity<List<TripReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
						            });
					  tripReport = rateResponse.getBody();
					  List<Map> data = new ArrayList<>();
					  if(tripReport.size()>0) {
						  
						  Double totalDistance = 0.0 ;
						  double roundOffDistance = 0.0;
						  Long time = (long) 0;
						  Double totalFuel=0.0;
						  double roundOffFuel = 0.0;
						  String totalDuration = "00:00:00";
						  
						  for(TripReport tripReportOne : tripReport ) {

							  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
								  totalDistance += Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
								  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;


							  }
							  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {

								  time += Math.abs(  Long.parseLong(tripReportOne.getDuration())  );

     							  Long hours =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  totalDuration = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds);

							  }
							  if(tripReportOne.getSpentFuel() != null && tripReportOne.getSpentFuel() != "") {
								  if( device.getFuel() != null) {
										 
										int litres=0;
										int Fuel =0;
										int distance=0;
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=Math.abs( Integer.parseInt(obj.get("fuelPerKM").toString()) );
										}

										distance =Math.abs( Integer.parseInt(tripReportOne.getDistance()) );
										if(distance > 0) {
											Fuel = (distance/100)*litres;
										}

										tripReportOne.setSpentFuel(Integer.toString(Fuel));


									 }
									totalFuel += Double.parseDouble(tripReportOne.getSpentFuel());
									roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;

							  }

						  }
						  

						  Map devicesStatus = new HashMap();
					      devicesStatus.put("totalSpentFuel", roundOffFuel);
					      devicesStatus.put("totalDrivingHours",totalDuration);
					      devicesStatus.put("totalDistance", roundOffDistance);
					      devicesStatus.put("deviceName", device.getName());
						  devicesStatus.put("deviceId" ,device.getId());
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {

							  devicesStatus.put("driverName", driver.getName());
							  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  }

						  data.add(devicesStatus);
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,tripReport.size());
					  logger.info("************************ getTripsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",tripReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId is Required",tripReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getTotalStopsReport(String TOKEN, Long deviceId, Long driverId, Long groupId, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId !=0) {
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "STOPREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get STOPREPORT list",null);
					 logger.info("************************ STOPREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			 if(groupId != 0) {
			    	
			    	Group group=groupRepository.findOne(groupId);

					if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofence",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this geofnece",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : groupParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							List<Long>allDevices= new ArrayList<>();
							if(group.getType().equals("driver")) {
								allDevices = groupRepository.getDevicesFromDriver(groupId);

							}
							else if(group.getType().equals("device")) {
								allDevices = groupRepository.getDevicesFromGroup(groupId);
								
							}
							else if(group.getType().equals("geofence")) {
								allDevices = groupRepository.getDevicesFromGeofence(groupId);

							}
							else {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(from.equals("0") || to.equals("0")) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							else {
								SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
								SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
								inputFormat.setLenient(false);
								outputFormat.setLenient(false);
								Date dateFrom;
								Date dateTo;
								try {
									dateFrom = inputFormat.parse(from);
									dateTo = inputFormat.parse(to);

									from = outputFormat.format(dateFrom);
									to = outputFormat.format(dateTo);
									
									Date today=new Date();

									if(dateFrom.getTime() > dateTo.getTime()) {
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}
									if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
										getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);

								}
								
								String plainCreds = "admin@fuinco.com:admin";
								byte[] plainCredsBytes = plainCreds.getBytes();
								
								byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
								String base64Creds = new String(base64CredsBytes);

								HttpHeaders headers = new HttpHeaders();
								headers.add("Authorization", "Basic " + base64Creds);
								
								  String GET_URL = stopsUrl;
								  RestTemplate restTemplate = new RestTemplate();
								  restTemplate.getMessageConverters()
							        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

								  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
									        .queryParam("type", type)
									        .queryParam("from", from)
									        .queryParam("to", to)
									        .queryParam("page", page)
									        .queryParam("start", start)
									        .queryParam("limit",limit).build();
								  HttpEntity<String> request = new HttpEntity<String>(headers);
								  String URL = builder.toString();
								  if(allDevices.size()>0) {
									  for(int i=0;i<allDevices.size();i++) {
										  URL +="&deviceId="+allDevices.get(i);
									  }
								  }
								  ResponseEntity<List<StopReport>> rateResponse =
									        restTemplate.exchange(URL,
									                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
									            });
								  stopReport = rateResponse.getBody();
								  List<String> duplicateAddressList = new ArrayList<String>();				  
								  List<Map> data = new ArrayList<Map>();
								  if(stopReport.size()>0) {
									  Long timeDuration = (long) 0;
									  Long timeEngine= (long) 0;
									  String totalDuration = "00:00:00";
									  String totalEngineHours = "00:00:00";
									  Double totalFuel=0.0;
									  double roundOffFuel = 0.0;
									  
									  for(StopReport stopReportOne : stopReport ) {

										  if(stopReportOne.getAddress() != null && stopReportOne.getAddress() != "") {
											  duplicateAddressList.add(stopReportOne.getAddress());
										  
										  }
										  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

											  timeDuration += Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

			     							  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
											  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
											  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
											  
											  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);

										  }
										  
										  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

											  timeEngine += Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

			     							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
											  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
											  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
											  
											  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

										  }


										  if(stopReportOne.getSpentFuel() != null && stopReportOne.getSpentFuel() != "") {
											  Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
											  if( device.getFuel() != null) {
													 
													int litres=0;
													int Fuel =0;
													int distance=0;
													JSONObject obj = new JSONObject(device.getFuel());	
													if(obj.has("fuelPerKM")) {
														litres=Math.abs( Integer.parseInt(obj.get("fuelPerKM").toString()) );
													}

													distance =Math.abs( Integer.parseInt(stopReportOne.getDistance()) );
													if(distance > 0) {
														Fuel = (distance/100)*litres;
													}

													stopReportOne.setSpentFuel(Integer.toString(Fuel));


												 }
												totalFuel += Double.parseDouble(stopReportOne.getSpentFuel());
												roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;

										  }

									  }
									  

									  Map devicesStatus = new HashMap();;
								      devicesStatus.put("totalDuration", totalDuration);
								      devicesStatus.put("totalEngineHours", totalEngineHours);
								      devicesStatus.put("totalSpentFuel", roundOffFuel);
								      devicesStatus.put("groupName", group.getName());
									  devicesStatus.put("groupId" ,group.getId());
								        Map<String, Long> couterMap = duplicateAddressList.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting()));
									  devicesStatus.put("totalVisitedPlace" ,couterMap.size());
									  
									  data.add(devicesStatus);
								  }
								  
								  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,stopReport.size());
								  logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.ok().body(getObjectResponse);

							}
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			
			 
			    if(driverId != 0) {
			    	
			    	Driver driver=driverRepository.findOne(driverId);

					if(driver != null) {
						if(driver.getDelete_date() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}
									Set<User>driverParents = driver.getUserDriver();
									if(driverParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this driver",null);
										 return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : driverParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
							}
							if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
								logger.info("************************ getgroupById ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							Set<Device>  devices = driver.getDevice();
							for(Device device : devices ) {
		
								deviceId = device.getId();
							}
							
							
							
						}
						else {
							getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
							return  ResponseEntity.status(404).body(getObjectResponse);

						}
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This driver id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
			    }
			    
			    
			Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",stopReport);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",stopReport);
						return  ResponseEntity.badRequest().body(getObjectResponse);


					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allow to get data as not from parents ",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = stopsUrl;
					  RestTemplate restTemplate = new RestTemplate();
					  restTemplate.getMessageConverters()
				        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

					  UriComponents builder = UriComponentsBuilder.fromHttpUrl(GET_URL)
						        .queryParam("deviceId",deviceId)
						        .queryParam("type", type)
						        .queryParam("from", from)
						        .queryParam("to", to)
						        .queryParam("page", page)
						        .queryParam("start", start)
						        .queryParam("limit",limit).build();
					  HttpEntity<String> request = new HttpEntity<String>(headers);
//					  stopReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();

					  ResponseEntity<List<StopReport>> rateResponse =
						        restTemplate.exchange(builder.toString(),
						                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
						            });
					  stopReport = rateResponse.getBody();
					  List<String> duplicateAddressList = new ArrayList<String>();				  
					  List<Map> data = new ArrayList<>();
					  if(stopReport.size()>0) {
						  Long timeDuration = (long) 0;
						  Long timeEngine= (long) 0;
						  String totalDuration = "00:00:00";
						  String totalEngineHours = "00:00:00";
						  Double totalFuel=0.0;
						  double roundOffFuel = 0.0;
						  
						  for(StopReport stopReportOne : stopReport ) {
								  if(stopReportOne.getAddress() != null && stopReportOne.getAddress() != "") {
									  duplicateAddressList.add(stopReportOne.getAddress());
								  
								  }
							  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

								  timeDuration += Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

     							  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
								  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
								  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
								  
								  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);

							  }
							  
							  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

								  timeEngine += Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

     							  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
								  
								  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

							  }


							  if(stopReportOne.getSpentFuel() != null && stopReportOne.getSpentFuel() != "") {
								  if( device.getFuel() != null) {
										 
										int litres=0;
										int Fuel =0;
										int distance=0;
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=Math.abs( Integer.parseInt(obj.get("fuelPerKM").toString()) );
										}

										distance =Math.abs( Integer.parseInt(stopReportOne.getDistance()) );
										if(distance > 0) {
											Fuel = (distance/100)*litres;
										}

										stopReportOne.setSpentFuel(Integer.toString(Fuel));


									 }
									totalFuel += Double.parseDouble(stopReportOne.getSpentFuel());
									roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;

							  }

						  }
						  

						  Map devicesStatus = new HashMap();;
					      devicesStatus.put("totalDuration", totalDuration);
					      devicesStatus.put("totalEngineHours", totalEngineHours);
					      devicesStatus.put("totalSpentFuel", roundOffFuel);
					      devicesStatus.put("deviceName", device.getName());
					        Map<String, Long> couterMap = duplicateAddressList.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting()));
						  devicesStatus.put("totalVisitedPlace" ,couterMap.size());
						  devicesStatus.put("deviceId" ,device.getId());
						  Set<Driver>  drivers = device.getDriver();
						  for(Driver driver : drivers ) {

							  devicesStatus.put("driverName", driver.getName());
							  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  }



						  data.add(devicesStatus);
					  }
					  
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,stopReport.size());
					  logger.info("************************ getStopsReport ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);


					
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",stopReport);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",stopReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getNumberDriverWorkingHours(String TOKEN, Long driverId, Long groupId, int offset,
			String start, String end, String search, Long userId) {
		logger.info("************************ HoursDev STARTED ***************************");
		
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if( userId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			User loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(loggedUser.getAccountType()!= 1) {
				if(!userRoleService.checkUserHasPermission(userId, "DRIVERWORKINGHOURSREPORT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get DRIVERWORKINGHOURSREPORT list",null);
					 logger.info("************************ DRIVERWORKINGHOURSREPORT ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			List<Long>allDrivers= new ArrayList<>();

			if(groupId != 0) {
		    	
		    	Group group=groupRepository.findOne(groupId);

				if(group != null) {
					if(group.getIs_deleted() == null) {
						boolean isParent = false;
						if(loggedUser.getAccountType().equals(4)) {
							Set<User> clientParents = loggedUser.getUsersOfUser();
							if(clientParents.isEmpty()) {
								getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : clientParents) {
									parent = object ;
								}
								Set<User>groupParents = group.getUserGroup();
								if(groupParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									 return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User parentObject : groupParents) {
										if(parentObject.getId().equals(parent.getId())) {
											isParent = true;
											break;
										}
									}
								}
							}
						}
						if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",null);
							logger.info("************************ getgroupById ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(group.getType().equals("driver")) {
							allDrivers = groupRepository.getDriversFromGroup(groupId);

						}
						else if(group.getType().equals("device")) {
							allDrivers = groupRepository.getDriverFromDevices(groupId);
							
						}
						else if(group.getType().equals("geofence")) {
							allDrivers = groupRepository.getDriversFromGeofence(groupId);

						}
						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group not has type from  (driver,device,geofence,attribute,command,maintenance,notification)",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(start.equals("0") || end.equals("0")) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);

						}
						else {
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							inputFormat.setLenient(false);
							outputFormat.setLenient(false);

							Date dateFrom;
							Date dateTo;
							try {
								dateFrom = inputFormat.parse(start);
								dateTo = inputFormat.parse(end);
								
								start = outputFormat.format(dateFrom);
								end = outputFormat.format(dateTo);
								
								Date today=new Date();

								if(dateFrom.getTime() > dateTo.getTime()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}
								
								


							} catch (ParseException e) {
								// TODO Auto-generated catch block
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",null);
								return  ResponseEntity.badRequest().body(getObjectResponse);

							}
							search = "%"+search+"%";
							driverHours = driverRepository.getNumberDriverWorkingHours(allDrivers,start,end);
							  Long time= (long) 0;
							  String totalHours = "00:00:00";
							if(driverHours.size()>0) {
								for(int i=0;i<driverHours.size();i++) {
									JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
									if(obj.has("todayHours")) {
										time += Math.abs(  obj.getLong("todayHours")  );

										  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
										  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
										  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
										  
										  totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

									}
									
								}
								
							
							}
							List<Map> data = new ArrayList<Map>();
							  Map devicesStatus = new HashMap();;
						      devicesStatus.put("groupName", group.getName());
						      devicesStatus.put("totalHours", totalHours);
			
							  data.add(devicesStatus);
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
							logger.info("************************ HoursDev ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
							
						}
						
					 
						
						
						
					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
						return  ResponseEntity.status(404).body(getObjectResponse);

					}
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group id is not Found",null);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
		    }
			
			Driver driver =driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",driverHours);
					return  ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					inputFormat.setLenient(false);
					outputFormat.setLenient(false);

					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);
						
						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);
						
						Date today=new Date();

						if(dateFrom.getTime() > dateTo.getTime()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD",driverHours);
						return  ResponseEntity.badRequest().body(getObjectResponse);

					}
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>driverParent = driver.getUserDriver();
							if(driverParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : driverParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
					}
					if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",null);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					search = "%"+search+"%";
					allDrivers.add(driverId);
					
					driverHours = driverRepository.getNumberDriverWorkingHours(allDrivers,start,end);
					  Long time= (long) 0;
					  String totalHours = "00:00:00";
					if(driverHours.size()>0) {
						for(int i=0;i<driverHours.size();i++) {
							JSONObject obj = new JSONObject(driverHours.get(i).getAttributes());
							if(obj.has("todayHours")) {
								time += Math.abs(  obj.getLong("todayHours")  );

								  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

							}
							
						}
						
					
					}
					  List<Map> data = new ArrayList<Map>();
					  Map devicesStatus = new HashMap();;
				      devicesStatus.put("driverName", driver.getName());
				      devicesStatus.put("totalHours", totalHours);
	
					  data.add(devicesStatus);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
					logger.info("************************ HoursDev ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Driver ID is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID and userId is Required",driverHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
	}

	@Override
	public ResponseEntity<?> getviewTrip(String TOKEN, Long deviceId, Long startPositionId, Long endPositionId) {
		logger.info("************************ getStopsReport STARTED ***************************");

		List<TripPositions> positions = new ArrayList<TripPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positions);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

			    
		Device device = deviceServiceImpl.findById(deviceId);
		
		if(device != null) {
			positions = positionSqlRepository.getTripPositions(deviceId, startPositionId, endPositionId);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positions,positions.size());
			logger.info("************************ getStopsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positions);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
	}

}
