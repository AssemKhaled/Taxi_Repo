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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.EventReportByCurl;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.StopReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.example.examplequerydslspringdatajpamaven.entity.TripReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoEventsRepo;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionRepo;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * services functionality related to reports
 * @author fuinco
 *
 */
@Component
@Service
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
	private UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private GroupsServiceImpl groupsServiceImpl;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private DriverServiceImpl driverServiceImpl;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private MongoEventsRepo mongoEventsRepo;
	
	@Autowired
	private UserClientGroupRepository userClientGroupRepository;
	
	@Autowired
	private UserClientDriverRepository userClientDriverRepository;
	
	private static final Log logger = LogFactory.getLog(ReportServiceImpl.class);
	
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private MongoPositionRepo mongoPositionRepo;
	
	
	/**
	 * get data of events of one or more device and group from mongo collection tc_events
	 */
	@Override
	public ResponseEntity<?> getEventsReport(String TOKEN,Long [] deviceIds,Long [] groupIds,int offset,String start,String end,String type,String search,Long userId,String exportData) {
	{
		
		
		logger.info("************************ getEventsReport STARTED ***************************");		
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "EVENT", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCENTER", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCEEXIT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENT or GEOFENCENTER or GEOFENCEEXIT list",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
									logger.info("************************ getEventsReport ENDED ***************************");		 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
										logger.info("************************ getEventsReport ENDED ***************************");		 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",null);
								logger.info("************************ getEventsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							logger.info("************************ getEventsReport ENDED ***************************");		
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								logger.info("************************ getEventsReport ENDED ***************************");		
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",eventReport);
						logger.info("************************ getEventsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",eventReport);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",eventReport);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}


			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",eventReport);
				logger.info("************************ getEventsReport ENDED ***************************");		
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
			
	        
	        
			if(type.equals("")) {
				
				if(exportData.equals("exportData")) {
					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
		        }
				
				if(!TOKEN.equals("Schedule")) {
					eventReport = mongoEventsRepo.getEventsWithoutType(allDevices, offset, dateFrom, dateTo);
					if(eventReport.size()>0) {
						size = mongoEventsRepo.getEventsWithoutTypeSize(allDevices, dateFrom, dateTo);
						
						
					}
				}
				else {
					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);
					

				}
				
			}
			else {
				if(exportData.equals("exportData")) {
					eventReport = mongoEventsRepo.getEventsScheduledWithType(allDevices, dateFrom, dateTo,type);

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
		        }
				
				if(!TOKEN.equals("Schedule")) {
					eventReport = mongoEventsRepo.getEventsWithType(allDevices, offset, dateFrom, dateTo, type);
					if(eventReport.size()>0) {
						size = mongoEventsRepo.getEventsWithTypeSize(allDevices, dateFrom, dateTo, type);
						
						
					}
				}
				else {
					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);
					
				}
				
			}
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,size);
			logger.info("************************ getEventsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}		  
				  
			
	  }
			
    }
	
	/**
	 * get data of device hours for each day of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId,String exportData) {

		
		logger.info("************************ getDeviceWorkingHours STARTED ***************************");

		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICEWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get device working hours list",deviceHours);
					logger.info("************************ getDeviceWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
									logger.info("************************ getDeviceWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
										logger.info("************************ getDeviceWorkingHours ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",deviceHours);
								logger.info("************************ getDeviceWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									
									
									

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									
								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length !=0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							logger.info("************************ getDeviceWorkingHours ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								logger.info("************************ getDeviceWorkingHours ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",deviceHours);
						logger.info("************************ getDeviceWorkingHours ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					

					allDevices.add(deviceId);
					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
			logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}


			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",deviceHours);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }

	        if(exportData.equals("exportData")) {
	        	
				deviceHours = mongoPositionRepo.getDeviceWorkingHoursScheduled(allDevices,dateFrom,dateTo);			

				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
				logger.info("************************ getDeviceWorkingHours ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	        }
	        
			if(!TOKEN.equals("Schedule")) {
				
				deviceHours = mongoPositionRepo.getDeviceWorkingHours(allDevices,offset,dateFrom,dateTo);			
					
				if(deviceHours.size()>0) {
   				    size=mongoPositionRepo.getDeviceWorkingHoursSize(allDevices,dateFrom, dateTo);

					
				
				}
			}
			else {
				deviceHours = mongoPositionRepo.getDeviceWorkingHoursScheduled(allDevices,dateFrom,dateTo);			


			}
				
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
			logger.info("************************ getDeviceWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}		  
				  
			
	  

	}
	
	/**
	 * get data of custom attributes (ignition,motion ...etc) of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getCustomReport(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String search, Long userId, String custom, String value,String exportData) {
		
		
		logger.info("************************ getCustomReport STARTED ***************************");

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",deviceHours);
			 logger.info("************************ getCustomReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "CUSTOMREPORT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get custom report list",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
									 logger.info("************************ getCustomReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",deviceHours);
										 logger.info("************************ getCustomReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",deviceHours);
								 logger.info("************************ getCustomReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									
									
									

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									
								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length !=0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
							 logger.info("************************ getCustomReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",deviceHours);
								 logger.info("************************ getCustomReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",deviceHours);
						 logger.info("************************ getCustomReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					

					allDevices.add(deviceId);
					
	
				}
			}
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);
			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	        if(custom.equals("")) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no custom selected",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	        if(value.equals("")) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no value selected",deviceHours);
				 logger.info("************************ getCustomReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
			
	        if(exportData.equals("exportData")) {
				
	        	deviceHours = mongoPositionRepo.getDeviceCustomScheduled(allDevices,dateFrom,dateTo,custom,value);
				
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
				logger.info("************************ getCustomReport ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	        }

			if(!TOKEN.equals("Schedule")) {

				deviceHours = mongoPositionRepo.getDeviceCustom(allDevices, offset, dateFrom, dateTo, custom, value);
				if(deviceHours.size()>0) {
					size=mongoPositionRepo.getDeviceCustomSize(allDevices,dateFrom, dateTo,custom,value);


				}
			}
			else {

				deviceHours = mongoPositionRepo.getDeviceCustomScheduled(allDevices,dateFrom,dateTo,custom,value);
				
			}
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceHours,size);
			logger.info("************************ getCustomReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}		  
			
	}
	
	/**
	 * get data of driver hours for each day of one or more driver and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getDriverWorkingHours(String TOKEN, Long[] driverIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId,String exportData) {
		 logger.info("************************ getDriverWorkingHours STARTED ***************************");

		
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
			 logger.info("************************ getDriverWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVERWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver working hours list",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<Long>();
		List<Long>allDrivers= new ArrayList<Long>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
									 logger.info("************************ getDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
										 logger.info("************************ getDriverWorkingHours ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",driverHours);
								 logger.info("************************ getDriverWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								
								if(group.getType().equals("driver")) {
									allDrivers.addAll(groupRepository.getDriversFromGroup(groupId));

								}
								else if(group.getType().equals("device")) {
									allDrivers.addAll(groupRepository.getDriverFromDevices(groupId));
									
								}
								else if(group.getType().equals("geofence")) {
									allDrivers.addAll(groupRepository.getDriversFromGeofence(groupId));

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(driverIds.length !=0 ) {

			for(Long driverId : driverIds) {


				if(driverId !=0) {

					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {

						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								 logger.info("************************ getDriverWorkingHours ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
									 logger.info("************************ getDriverWorkingHours ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}


						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",driverHours);
							 logger.info("************************ getDriverWorkingHours ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}


						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}

		
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for drivers of groups or drivers that you selected",driverHours);
				 logger.info("************************ getDriverWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
	       
	       if(exportData.equals("exportData")) {

	    	   driverHours = mongoPositionRepo.getDriverWorkingHoursScheduled(allDevices,dateFrom,dateTo);			

				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
				logger.info("************************ getDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	       }
			
		   if(!TOKEN.equals("Schedule")) {
				
				driverHours = mongoPositionRepo.getDriverWorkingHours(allDevices,offset,dateFrom,dateTo);			

				if(driverHours.size()>0) {
   				    size=mongoPositionRepo.getDriverWorkingHoursSize(allDevices,dateFrom,dateTo);

				
				}
				
			}
			else {
				driverHours = mongoPositionRepo.getDriverWorkingHoursScheduled(allDevices,dateFrom,dateTo);			


			}
				
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",driverHours,size);
			logger.info("************************ getDriverWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}	
	}

	/**
	 * get today notifications from mongo collection tc_events
	 */
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
				 List<Long>usersIds= new ArrayList<>();

				 if(user.getAccountType() == 4) {
					

					 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
				     Integer size=0;
					 if(deviceIds.size()>0) {
					    Date date = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
						String currentDate=formatter.format(date);
						
						String from = currentDate +" 00:00:01";
						String to = currentDate +" 23:59:59";
					

						if(search.equals("")) {
							notifications = mongoEventsRepo.getNotificationsTodayByDeviceId(deviceIds, offset);
//							if(notifications.size()>0) {
//								size= mongoEventsRepo.getNotificationsTodaySize(deviceIds);
//									
//							}
						}
						else {
							notifications = mongoEventsRepo.getNotificationsTodaySearchByDeviceId(deviceIds,search, offset);
//							if(notifications.size()>0) {
//								size= mongoEventsRepo.getNotificationsTodaySizeSearch(deviceIds,search);
//									
//							}
						}
						

					 }
					
				    

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications,size);
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				 }
				 else {
					 usersIds.add(userId);


					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
					String currentDate=formatter.format(date);
					
					String from = currentDate +" 00:00:01";
					String to = currentDate +" 23:59:59";
					Integer size=0;
					List<Long> allDevices = deviceRepository.getDevicesUsers(usersIds);

					if(search.equals("")) {
						//notifications = mongoEventsRepo.getNotificationsTodayByDeviceId(allDevices, offset);
						notifications = mongoEventsRepo.getNotificationsTodayByUserId(usersIds, offset);
//						if(notifications.size()>0) {
//							size= mongoEventsRepo.getNotificationsTodaySize(allDevices);
//								
//						}

					}
					else {
						//notifications = mongoEventsRepo.getNotificationsTodaySearchByDeviceId(allDevices,search, offset);
						notifications = mongoEventsRepo.getNotificationsTodaySearchByUserId(usersIds,search, offset);
//						if(notifications.size()>0) {
//							size= mongoEventsRepo.getNotificationsTodaySizeSearch(allDevices,search);
//								
//						}
					}
					
				    

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications,size);
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
				 }
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",notifications);
				logger.info("************************ getNotifications ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			logger.info("************************ getNotifications ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}

	/**
	 * get data of stops of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getStopsReport(String TOKEN, Long[] deviceIds, Long[] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<StopReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "STOP", "list") && !userRoleService.checkUserHasPermission(userId, "DURATIONINSTOP", "list")
					&& !userRoleService.checkUserHasPermission(userId, "ENGINEINSTOP", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get stop or duration in stop or engine in stop list",stopReport);
					logger.info("************************ getStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									logger.info("************************ getStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										logger.info("************************ getStopsReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								logger.info("************************ getStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
								
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							logger.info("************************ getStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								logger.info("************************ getStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						logger.info("************************ getStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		
		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				logger.info("************************ getStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		
		
		 stopReport = (List<StopReport>) returnFromTraccar(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();

		  if(stopReport.size()>0) {

			  
			  Long timeDuration = (long) 0;
			  Long timeEngine= (long) 0;
			  String totalDuration = "00:00:00";
			  String totalEngineHours = "00:00:00";
			  


			  for(StopReport stopReportOne : stopReport ) {
				  Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
				  Set<Driver>  drivers = device.getDriver();

				  for(Driver driver : drivers ) {

					 stopReportOne.setDriverName(driver.getName());
					 stopReportOne.setDriverUniqueId(driver.getUniqueid());
				  }
				  if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {

					  timeDuration = Math.abs(  Long.parseLong(stopReportOne.getDuration())  );

					  Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
					  Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
					  Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));
					  
					  totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);
					  stopReportOne.setDuration(totalDuration.toString());

				  }
				  
				  if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {

					  timeEngine = Math.abs(  Long.parseLong(stopReportOne.getEngineHours())  );

					  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(timeEngine) ;
					  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(timeEngine) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeEngine));
					  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(timeEngine) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeEngine));
					  
					  totalEngineHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
					  stopReportOne.setEngineHours(totalEngineHours.toString());

				  }
				  
				  if(stopReportOne.getStartTime() != null && stopReportOne.getStartTime() != "") {
					    Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(stopReportOne.getStartTime());

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						stopReportOne.setStartTime(outputFormat.format(dateTime));

				  }
				  if(stopReportOne.getEndTime() != null && stopReportOne.getEndTime() != "") {
					    Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(stopReportOne.getEndTime());

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						stopReportOne.setEndTime(outputFormat.format(dateTime));

				  }
			  }
			  
			  
		  }
		
		
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport,stopReport.size());
			logger.info("************************ getStopsReport ENDED ***************************");
		  return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * get data of trips of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getTripsReport(String TOKEN, Long[] deviceIds, Long[] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "TRIP", "list") && !userRoleService.checkUserHasPermission(userId, "TRIPDISTANCESPEED", "list")
					&& !userRoleService.checkUserHasPermission(userId, "TRIPSPENTFUEL", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
					logger.info("************************ getTripsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									logger.info("************************ getTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										logger.info("************************ getTripsReport ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								logger.info("************************ getTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
                            }

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							logger.info("************************ getTripsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								logger.info("************************ getTripsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						logger.info("************************ getTripsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		
		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
				logger.info("************************ getTripsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 tripReport = (List<TripReport>) returnFromTraccar(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 if(tripReport.size()>0) {

			  for(TripReport tripReportOne : tripReport ) {
				  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());

				  Double totalDistance = 0.0 ;
				  double roundOffDistance = 0.0;
				  double roundOffFuel = 0.0;
				  Double litres=10.0;
				  Double Fuel =0.0;
				  Double distance=0.0;
				  
				  Set<Driver>  drivers = device.getDriver();
				  for(Driver driver : drivers ) {

					 tripReportOne.setDriverName(driver.getName());
					 tripReportOne.setDriverUniqueId(driver.getUniqueid());

					
					 
				  }

				  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setDistance(Double.toString(roundOffDistance));


				  }
				  
				  if(device.getFuel() != null) {
						if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
							JSONObject obj = new JSONObject(device.getFuel());	
							if(obj.has("fuelPerKM")) {
								litres=obj.getDouble("fuelPerKM");
								
							}
						}
				   }
					  
				  distance = Double.parseDouble(tripReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }

				  roundOffFuel = Math.round(Fuel * 100.0)/ 100.0;
				  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));

				  
				  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
					  Long time=(long) 0;

					  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

					  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
					  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
					  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
					  
					  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
					  tripReportOne.setDuration(totalHours);
				  }
				 
				  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed())  * (1.852));
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


				  }
				  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed())  * (1.852));
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


				  }
				  
				  if(tripReportOne.getStartTime() != null && tripReportOne.getStartTime() != "") {
					    Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(tripReportOne.getStartTime());

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						tripReportOne.setStartTime(outputFormat.format(dateTime));

				  }
				  if(tripReportOne.getEndTime() != null && tripReportOne.getEndTime() != "") {
					    Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(tripReportOne.getEndTime());

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						tripReportOne.setEndTime(outputFormat.format(dateTime));

				  }
				  
				  

			  }
		  }
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport,tripReport.size());
		logger.info("************************ getTripsReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
		
	}
	
	/**
	 * get data of driving more than 4 hours of one or more device , driver and group from traccar
	 */
	@Override
	public ResponseEntity<?> getDriveMoreThanReport(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getDriveMoreThanReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();
		List<TripReport> tripData = new ArrayList<>();


		List<Long>allDrivers= new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVEMORETHAN", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
					logger.info("************************ getDriveMoreThanReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									logger.info("************************ getDriveMoreThanReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										logger.info("************************ getDriveMoreThanReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
                            if(group.getType() != null) {
                            	if(group.getType().equals("driver")) {
        							
    								allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
    							

    							}
    							else if(group.getType().equals("device")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
    								
    								
    							}
    							else if(group.getType().equals("geofence")) {
    								
    								allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
    								

    							}
                            }
                            

							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									logger.info("************************ getDriveMoreThanReport ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
							logger.info("************************ getDriveMoreThanReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							logger.info("************************ getDriveMoreThanReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								logger.info("************************ getDriveMoreThanReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						logger.info("************************ getDriveMoreThanReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
				logger.info("************************ getDriveMoreThanReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 tripReport = (List<TripReport>) returnFromTraccar(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 if(tripReport.size()>0) {

			  for(TripReport tripReportOne : tripReport ) {
				  
				  Long hours = (long) 0;
				  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
					  Long data = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) ); 
					  hours=   TimeUnit.MILLISECONDS.toHours(data) ;
				  
				  }
				  

				  if(hours > 4) {


					  Double totalDistance = 0.0 ;
					  double roundOffDistance = 0.0;
					  double roundOffFuel = 0.0;
					  Double litres=10.0;
					  Double Fuel =0.0;
					  Double distance=0.0;
					  
					  Device device= deviceServiceImpl.findById(tripReportOne.getDeviceId());
					  Set<Driver>  drivers = device.getDriver();
					  for(Driver driver : drivers ) {

						 tripReportOne.setDriverName(driver.getName());
						 tripReportOne.setDriverUniqueId(driver.getUniqueid());

						
						 
					  }
					  if(tripReportOne.getDistance() != null && tripReportOne.getDistance() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getDistance())/1000  );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setDistance(Double.toString(roundOffDistance));


					  }
					  if(device.getFuel() != null) {
							if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
								JSONObject obj = new JSONObject(device.getFuel());	
								if(obj.has("fuelPerKM")) {
									litres=obj.getDouble("fuelPerKM");
									
								}
							}
					   }
					  
					 
				   
				  distance = Double.parseDouble(tripReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }

				  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
				  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));
					  
					  
					  if(tripReportOne.getDuration() != null && tripReportOne.getDuration() != "") {
						  Long time=(long) 0;

						  time = Math.abs( Long.parseLong(tripReportOne.getDuration().toString()) );

						  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
						  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
						  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
						  
						  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
						  tripReportOne.setDuration(totalHours);
					  }
					  
					  if(tripReportOne.getAverageSpeed() != null && tripReportOne.getAverageSpeed() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getAverageSpeed()) * (1.852) );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setAverageSpeed(Double.toString(roundOffDistance));


					  }
					  if(tripReportOne.getMaxSpeed() != null && tripReportOne.getMaxSpeed() != "") {
						  totalDistance = Math.abs(  Double.parseDouble(tripReportOne.getMaxSpeed()) * (1.852) );
						  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
						  tripReportOne.setMaxSpeed(Double.toString(roundOffDistance));


					  }
					  
					  if(tripReportOne.getStartTime() != null && tripReportOne.getStartTime() != "") {
						    Date dateTime = null;
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
							SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

							try {
								dateTime = inputFormat.parse(tripReportOne.getStartTime());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Calendar calendarTime = Calendar.getInstance();
							calendarTime.setTime(dateTime);
							calendarTime.add(Calendar.HOUR_OF_DAY, 3);
							dateTime = calendarTime.getTime();
							
							tripReportOne.setStartTime(outputFormat.format(dateTime));

					  }
					  if(tripReportOne.getEndTime() != null && tripReportOne.getEndTime() != "") {
						    Date dateTime = null;
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
							SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

							try {
								dateTime = inputFormat.parse(tripReportOne.getEndTime());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Calendar calendarTime = Calendar.getInstance();
							calendarTime.setTime(dateTime);
							calendarTime.add(Calendar.HOUR_OF_DAY, 3);
							dateTime = calendarTime.getTime();
							
							tripReportOne.setEndTime(outputFormat.format(dateTime));

					  }
					  
					  
					  tripData.add(tripReportOne);
					  
				  }
				  
				  

			  }
		  }

		  
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripData,tripData.size());
			logger.info("************************ getDriveMoreThanReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	
	/**
	 * get data of events of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getEventsReportByType(String TOKEN, Long[] deviceIds, Long[] groupIds, String type,
			String from, String to, int page, int start, int limit, Long userId) {
		
		logger.info("************************ getEventsReportByType STARTED ***************************");

		List<EventReportByCurl> eventReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "EVENT", "list") && !userRoleService.checkUserHasPermission(userId, "GEOFENCENTER", "list")
					&& !userRoleService.checkUserHasPermission(userId, "GEOFENCEEXIT", "list") ) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get EVENT or GEOFENCENTER or GEOFENCEEXIT list",eventReport);
					logger.info("************************ getEventsReportByType ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",eventReport);
									logger.info("************************ getEventsReportByType ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",eventReport);
										logger.info("************************ getEventsReportByType ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",eventReport);
								logger.info("************************ getEventsReportByType ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
							logger.info("************************ getEventsReportByType ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",eventReport);
								logger.info("************************ getEventsReportByType ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",eventReport);
						logger.info("************************ getEventsReportByType ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",eventReport);
				logger.info("************************ getEventsReportByType ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 eventReport = (List<EventReportByCurl>) returnFromTraccar(eventsUrl,"events",allDevices, from, to, type, page, start, limit).getBody();

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
				  
				  if(eventReportOne.getServerTime() != null && eventReportOne.getServerTime() != "") {
					    Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(eventReportOne.getServerTime());

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						eventReportOne.setServerTime(outputFormat.format(dateTime));
				  }

				
					
			  }
		  }	  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport,eventReport.size());
			logger.info("************************ getEventsReportByType ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	

	/**
	 * get data of summary of one or more device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getSummaryReport(String TOKEN, Long[] deviceIds, Long[] groupIds, String type, String from,
			String to, int page, int start, int limit, Long userId) {
		logger.info("************************ getSummaryReport STARTED ***************************");
		List<SummaryReport> summaryReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",summaryReport);
			 logger.info("************************ getSummaryReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "SUMMARY", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SUMMARY list",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",summaryReport);
									 logger.info("************************ getSummaryReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",summaryReport);
										 logger.info("************************ getSummaryReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",summaryReport);
								 logger.info("************************ getSummaryReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
							 logger.info("************************ getSummaryReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",summaryReport);
								 logger.info("************************ getSummaryReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",summaryReport);
						 logger.info("************************ getSummaryReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",summaryReport);
				 logger.info("************************ getSummaryReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 summaryReport = (List<SummaryReport>) returnFromTraccar(summaryUrl,"summary",allDevices, from, to, type, page, start, limit).getBody();
		 if(summaryReport.size()>0) {
			  Double totalDistance = 0.0 ;
			  double roundOffDistance = 0.0;
			  double roundOffFuel = 0.0;
			  Double litres=10.0;
			  Double Fuel =0.0;
			  Double distance=0.0;
			  
			  for(SummaryReport summaryReportOne : summaryReport ) {
				  Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
				  if(device != null) {
				  Set<Driver>  drivers = device.getDriver();
				  for(Driver driver : drivers ) {

					 summaryReportOne.setDriverName(driver.getName());
				  }
				  if(summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getDistance())/1000  );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setDistance(Double.toString(roundOffDistance));


				  }
				  
				  if(device.getFuel() != null) {
						if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
							JSONObject obj = new JSONObject(device.getFuel());	
							if(obj.has("fuelPerKM")) {
								litres=obj.getDouble("fuelPerKM");
								
							}
						}
				   }
				  
				  
				  distance = Double.parseDouble(summaryReportOne.getDistance().toString());
				  if(distance > 0) {
					Fuel = (distance*litres)/100;
				  }
	
				  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
				  summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
				  
				
			  }
				  if(summaryReportOne.getEngineHours() != null && summaryReportOne.getEngineHours() != "") {
					  Long time=(long) 0;

					  time = Math.abs( Long.parseLong(summaryReportOne.getEngineHours().toString()) );

					  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
					  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
					  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
					  
					  String totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);
					  summaryReportOne.setEngineHours(totalHours);
				  }

				  if(summaryReportOne.getAverageSpeed() != null && summaryReportOne.getAverageSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getAverageSpeed()) * (1.852) );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setAverageSpeed(Double.toString(roundOffDistance));


				  }
				  if(summaryReportOne.getMaxSpeed() != null && summaryReportOne.getMaxSpeed() != "") {
					  totalDistance = Math.abs(  Double.parseDouble(summaryReportOne.getMaxSpeed()) * (1.852) );
					  roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
					  summaryReportOne.setMaxSpeed(Double.toString(roundOffDistance));


				  }
				  
			}
		  }
		  
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",summaryReport,summaryReport.size());
			 logger.info("************************ getSummaryReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	
	/**
	 * get data of sensor (weight,sensor1,sensor2) of one or more device and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getSensorsReport(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String search, Long userId,String exportData) {
		 logger.info("************************ getSensorsReport STARTED ***************************");

		List<CustomPositions> positionsList = new ArrayList<CustomPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
			 logger.info("************************ getSensorsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
									 logger.info("************************ getSensorsReport ENDED ***************************"); 
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
										 logger.info("************************ getSensorsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",positionsList);
								 logger.info("************************ getSensorsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
							 logger.info("************************ getSensorsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
								 logger.info("************************ getSensorsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",positionsList);
						 logger.info("************************ getSensorsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		Integer size = 0;

		if(exportData.equals("exportData")) {
			
			positionsList = mongoPositionRepo.getPositionsListScheduled(allDevices,dateFrom, dateTo);

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionsList,size);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
			
		}
		if(!TOKEN.equals("Schedule")) {
			search = "%"+search+"%";
			positionsList = mongoPositionRepo.getSensorsList(allDevices, offset, dateFrom, dateTo);
			if(positionsList.size()>0) {
				    size=mongoPositionRepo.getSensorsListSize(allDevices,dateFrom, dateTo);


			
			}
			
		}
		else {
			positionsList = mongoPositionRepo.getPositionsListScheduled(allDevices,dateFrom, dateTo);

		}
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionsList,size);
		logger.info("************************ getSensorsReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	@Override
	public ResponseEntity<?> returnFromTraccar(String url,String report,List<Long> allDevices,String from,String to,String type,int page,int start,int limit) {
		
		 logger.info("************************ returnFromTraccar STARTED ***************************");

		 
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        outputFormat.setLenient(false);

		Date dateFrom;	
		Date dateTo;	
		
		try {
			dateFrom = outputFormat.parse(from);
			
			Calendar calendarFrom = Calendar.getInstance();
			calendarFrom.setTime(dateFrom);
			calendarFrom.add(Calendar.HOUR_OF_DAY, -3);
			dateFrom = calendarFrom.getTime();
			
			from = outputFormat.format(dateFrom);
			

		} catch (ParseException e2) {
			
		}
		
		try {
			dateTo = outputFormat.parse(to);
			
			Calendar calendarFrom = Calendar.getInstance();
			calendarFrom.setTime(dateTo);
			calendarFrom.add(Calendar.HOUR_OF_DAY, -3);
			dateTo = calendarFrom.getTime();
			
			to = outputFormat.format(dateTo);
			

		} catch (ParseException e2) {
			
		}

		String plainCreds = "admin@fuinco.com:admin";
		byte[] plainCredsBytes = plainCreds.getBytes();
		
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		
		  String GET_URL = url;
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
		  
		  if(report.equals("stops")) {
			  ResponseEntity<List<StopReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<StopReport>>() {
		            });
				 logger.info("************************ returnFromTraccar StopReport ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("trips")) {
			  ResponseEntity<List<TripReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<TripReport>>() {
		            });
				 logger.info("************************ returnFromTraccar TripReport ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("events")) {
			  ResponseEntity<List<EventReportByCurl>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<EventReportByCurl>>() {
		            });
				 logger.info("************************ returnFromTraccar EventReportByCurl ENDED ***************************");

			  return rateResponse;
		  }
		  if(report.equals("summary")) {
			  ResponseEntity<List<SummaryReport>> rateResponse =
		        restTemplate.exchange(URL,
		                    HttpMethod.GET,request, new ParameterizedTypeReference<List<SummaryReport>>() {
		            });
				 logger.info("************************ returnFromTraccar SummaryReport ENDED ***************************");

			  return rateResponse;
		  }
		  
		  
			 logger.info("************************ returnFromTraccar ENDED ***************************");

	     return null;
	}

	
	/**
	 * get data of all stops number of one or more driver ,device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getNumStopsReport(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		 logger.info("************************ getNumStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 logger.info("************************ getNumStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
			 
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMSTOPS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									 logger.info("************************ getNumStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										 logger.info("************************ getNumStopsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
									 logger.info("************************ getNumStopsReport ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",stopReport);
							 logger.info("************************ getNumStopsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							 logger.info("************************ getNumStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								 logger.info("************************ getNumStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						 logger.info("************************ getNumStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				 logger.info("************************ getNumStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		 stopReport = (List<StopReport>) returnFromTraccar(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();
		 List<Map> data = new ArrayList<>();

		  if(stopReport.size()>0) {

			  for(Long dev:allDevices) {

				  int count=0;
				  Map devicesStatus = new HashMap();

				  devicesStatus.put("deviceName", null);
				  devicesStatus.put("deviceId" ,null);
				  devicesStatus.put("driverName", null);
				  devicesStatus.put("driverUniqueId",null);
				  
				  devicesStatus.put("stops" ,count);
				  
				  Device device= deviceServiceImpl.findById(dev);
				  
			      devicesStatus.put("deviceName", device.getName());
				  devicesStatus.put("deviceId" ,device.getId());
				  Set<Driver>  drivers = device.getDriver();

				  for(Driver driver : drivers ) {

					  devicesStatus.put("driverName", driver.getName());
					  devicesStatus.put("driverUniqueId", driver.getUniqueid());
					  
				  }
				  
				  for(StopReport stop: stopReport) {


					  if((long) stop.getDeviceId() == (long) dev) {

						  count= count+1;
						  devicesStatus.put("stops" ,count);
					  }
				  }
				  data.add(devicesStatus);

			  }
			 
		  }
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
			 logger.info("************************ getNumStopsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	/**
	 * get totalDuration , totalEngineHours , totalSpentFuel for stops of driver , device and group from traccar
	 */
	@Override
	public ResponseEntity<?> getTotalStopsReport(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		
		 logger.info("************************ getTotalStopsReport STARTED ***************************");

		List<StopReport> stopReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 logger.info("************************ getTotalStopsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMVISITEDPLACES", "list") &&
					!userRoleService.checkUserHasPermission(userId, "ENGINEHOURSNOTMOVING", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
									 logger.info("************************ getTotalStopsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",stopReport);
										 logger.info("************************ getTotalStopsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",stopReport);
									 logger.info("************************ getTotalStopsReport ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",stopReport);
							 logger.info("************************ getTotalStopsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
							 logger.info("************************ getTotalStopsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",stopReport);
								 logger.info("************************ getTotalStopsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",stopReport);
						 logger.info("************************ getTotalStopsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",stopReport);
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		
		 stopReport = (List<StopReport>) returnFromTraccar(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();
		 List<Map> data = new ArrayList<Map>();

			if(stopReport.size()>0) {

				  for(Long dev:allDevices) {
					  
					  Long timeDuration = (long) 0;
					  Long timeEngine= (long) 0;
					  String totalDuration = "00:00:00";
					  String totalEngineHours = "00:00:00";
					  Double totalFuel=0.0;
					  double roundOffFuel = 0.0;
					  
					  List<String> duplicateAddressList = new ArrayList<String>();				  
					  duplicateAddressList.clear();

					  Map devicesStatus = new HashMap();
					  
					  devicesStatus.put("deviceName", null);
					  devicesStatus.put("deviceId" ,null);
					  devicesStatus.put("driverName", null);
					  devicesStatus.put("driverUniqueId",null);
					  
					  devicesStatus.put("totalDuration", totalDuration);
				      devicesStatus.put("totalEngineHours", totalEngineHours);
				      devicesStatus.put("totalSpentFuel", roundOffFuel);
					  devicesStatus.put("totalVisitedPlace" ,0);
					  
					  Device device= deviceServiceImpl.findById(dev);
					  
				      devicesStatus.put("deviceName", device.getName());
					  devicesStatus.put("deviceId" ,device.getId());
					  Set<Driver>  drivers = device.getDriver();

					  for(Driver driver : drivers ) {

						  devicesStatus.put("driverName", driver.getName());
						  devicesStatus.put("driverUniqueId", driver.getUniqueid());
						  
					  }
					  
					  for(StopReport stopReportOne: stopReport) {
						 
						  
						  if((long) stopReportOne.getDeviceId() == (long) dev) {


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
								  Double litres=10.0;
								  Double Fuel =0.0;
								  Double distance=0.0;
								  
								  if(device.getFuel() != null) {
										if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
											JSONObject obj = new JSONObject(device.getFuel());	
											if(obj.has("fuelPerKM")) {
												litres=obj.getDouble("fuelPerKM");
												
											}
										}
								   }
								  
								  
								  distance = Double.parseDouble(stopReportOne.getDistance().toString());
								  if(distance > 0) {
									Fuel = (distance*litres)/100;
								  }
					
								  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
								  stopReportOne.setSpentFuel(Double.toString(roundOffFuel));
								  

								  totalFuel += Double.parseDouble(stopReportOne.getSpentFuel());
								  roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;
									

							  }
							  devicesStatus.put("totalDuration", totalDuration);
						      devicesStatus.put("totalEngineHours", totalEngineHours);
						      devicesStatus.put("totalSpentFuel", roundOffFuel);
						  }
					     
					  }
					  Map<String, Long> couterMap = duplicateAddressList.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting()));
					  devicesStatus.put("totalVisitedPlace" ,couterMap.size());
					  
					  
					  if(devicesStatus.size() > 0) {
						  data.add(devicesStatus);
					  }

				  }
				  
			  }
			  
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
				 logger.info("************************ getTotalStopsReport ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	/**
	 * get totalDrivingHours , totalDistance , totalSpentFuel for trips of driver , device and group from traccar
	 */
	@Override
	public ResponseEntity<?> geTotalTripsReport(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		 logger.info("************************ geTotalTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 logger.info("************************ geTotalTripsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				 logger.info("************************ geTotalTripsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "TOTALDISTANCE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
				 logger.info("************************ geTotalTripsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									 logger.info("************************ geTotalTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										 logger.info("************************ geTotalTripsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								 logger.info("************************ geTotalTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
								 logger.info("************************ geTotalTripsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									 logger.info("************************ geTotalTripsReport ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
							 logger.info("************************ geTotalTripsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							 logger.info("************************ geTotalTripsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								 logger.info("************************ geTotalTripsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						 logger.info("************************ geTotalTripsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
	   		 logger.info("************************ geTotalTripsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		tripReport = (List<TripReport>) returnFromTraccar(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();
		
		List<Map> data = new ArrayList<Map>();

		if(tripReport.size()>0) {

			  for(Long dev:allDevices) {
				  
				  Double totalDistance = 0.0 ;
				  double roundOffDistance = 0.0;
				  Long time = (long) 0;
				  Double totalFuel=0.0;
				  double roundOffFuel = 0.0;
				  String totalDuration = "00:00:00";

				  Map devicesStatus = new HashMap();
				  
				  devicesStatus.put("deviceName", null);
				  devicesStatus.put("deviceId" ,null);
				  devicesStatus.put("driverName", null);
				  devicesStatus.put("driverUniqueId",null);
				  
				  devicesStatus.put("totalDrivingHours",totalDuration);
			      devicesStatus.put("totalDistance", roundOffDistance);
			      devicesStatus.put("totalSpentFuel", roundOffFuel);
			      
			      Device device= deviceServiceImpl.findById(dev);
				  
			      devicesStatus.put("deviceName", device.getName());
				  devicesStatus.put("deviceId" ,device.getId());
				  Set<Driver>  drivers = device.getDriver();

				  for(Driver driver : drivers ) {

					  devicesStatus.put("driverName", driver.getName());
					  devicesStatus.put("driverUniqueId", driver.getUniqueid());
					  
				  }
				  
				  for(TripReport tripReportOne: tripReport) {
					  
					  if((long) tripReportOne.getDeviceId() == (long) dev) {
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
							  Double litres=10.0;
							  Double Fuel =0.0;
							  Double distance=0.0;
							  
							  if(device.getFuel() != null) {
									if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
										JSONObject obj = new JSONObject(device.getFuel());	
										if(obj.has("fuelPerKM")) {
											litres=obj.getDouble("fuelPerKM");
											
										}
									}
							   }
							  
							  
							  distance = Double.parseDouble(tripReportOne.getDistance().toString());
							  if(distance > 0) {
								Fuel = (distance*litres)/100;
							  }
				
							  roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
							  tripReportOne.setSpentFuel(Double.toString(roundOffFuel));
							  

							  totalFuel += Double.parseDouble(tripReportOne.getSpentFuel());
							  roundOffFuel = Math.round(totalFuel * 100.0) / 100.0;
								

						  }
						  devicesStatus.put("totalDrivingHours",totalDuration);
					      devicesStatus.put("totalDistance", roundOffDistance);
					      devicesStatus.put("totalSpentFuel", roundOffFuel);
					  }
				      
					  
				  }
				  if(devicesStatus.size() > 0) {
					  data.add(devicesStatus);
				  }

			  }
			  
		  }
		  
		  
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
			 logger.info("************************ geTotalTripsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	/**
	 * get data of all trips number of one or more device, driver and group from traccar
	 */
	@Override
	public ResponseEntity<?> getNumTripsReport(String TOKEN, Long[] deviceIds, Long[] driverIds, Long[] groupIds,
			String type, String from, String to, int page, int start, int limit, Long userId) {
		
		 logger.info("************************ getNumTripsReport STARTED ***************************");

		List<TripReport> tripReport = new ArrayList<>();

		List<Long>allDrivers= new ArrayList<>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 logger.info("************************ getNumTripsReport ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",tripReport);
				 logger.info("************************ getNumTripsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMTRIPS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get trip list",tripReport);
				 logger.info("************************ getNumTripsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
									 logger.info("************************ getNumTripsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",tripReport);
										 logger.info("************************ getNumTripsReport ENDED ***************************");
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								

								}
								else if(group.getType().equals("device")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
									
									
								}
								else if(group.getType().equals("geofence")) {
									
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
									

								}
							}
						}
			    	}
			    	

				}
			}
		}
		
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",tripReport);
									 logger.info("************************ getNumTripsReport ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",tripReport);
							 logger.info("************************ getNumTripsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}
		
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
							 logger.info("************************ getNumTripsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",tripReport);
								 logger.info("************************ getNumTripsReport ENDED ***************************");
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
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
								isParent = false;
						}
						else {
								isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",tripReport);
						 logger.info("************************ getNumTripsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					allDevices.add(deviceId);

					
	
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(from.equals("0") || to.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			Integer size = 0;
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}
	        

	        for(String d:data) {

	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
	        
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",tripReport);
	   		    logger.info("************************ getNumTripsReport ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
		}
		tripReport = (List<TripReport>) returnFromTraccar(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();

		 List<Map> data = new ArrayList<>();
		  if(tripReport.size()>0) {

			  for(Long dev:allDevices) {

				  int count=0;
				  Map devicesStatus = new HashMap();
				  
				  devicesStatus.put("deviceName", null);
				  devicesStatus.put("deviceId" ,null);
				  devicesStatus.put("driverName", null);
				  devicesStatus.put("driverUniqueId",null);
				  
				  devicesStatus.put("trips" ,count);
				  
				  Device device= deviceServiceImpl.findById(dev);
				  
			      devicesStatus.put("deviceName", device.getName());
				  devicesStatus.put("deviceId" ,device.getId());
				  Set<Driver>  drivers = device.getDriver();

				  for(Driver driver : drivers ) {

					  devicesStatus.put("driverName", driver.getName());
					  devicesStatus.put("driverUniqueId", driver.getUniqueid());
					  
				  }
				  
				  for(TripReport trip: tripReport) {  
					  if((long) trip.getDeviceId() == (long) dev) {
						  
						  count= count+1;
						  devicesStatus.put("trips" ,count);
					  }
				  }
				  if(devicesStatus.size() > 0) {
					  data.add(devicesStatus);
				  }

			  }
			  
		  }
		  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,data.size());
		  logger.info("************************ getNumTripsReport ENDED ***************************");
		  return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	
	/**
	 * get lat and long of trip or location by id and time from ,to from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getviewTrip(String TOKEN, Long deviceId, String from, String to) {
		logger.info("************************ getviewTrip STARTED ***************************");

		List<TripPositions> positions = new ArrayList<TripPositions>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positions);
				logger.info("************************ getviewTrip ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		

		Date dateFrom;
		Date dateTo;

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS SSSS");
		SimpleDateFormat inputFormat1 = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
		inputFormat1.setLenient(false);
		inputFormat.setLenient(false);
		outputFormat.setLenient(false);

		
		try {
			dateFrom = inputFormat2.parse(from);
			from = outputFormat.format(dateFrom);
			

		} catch (ParseException e2) {
			try {
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or yyyy-MM-dd'T'HH:mm:ss.SSS SSSS",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		
		try {
			dateTo = inputFormat2.parse(to);
			to = outputFormat.format(dateTo);
			

		} catch (ParseException e2) {
			try {
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		Device device = deviceServiceImpl.findById(deviceId);
		
		if(device != null) {
			positions = mongoPositionRepo.getTripPositions(deviceId, dateFrom, dateTo);

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positions,positions.size());
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positions);
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
	}

	
	/**
	 * number of drivering hours during period for driver and group from mongo collection tc_positions
	 */
	@Override
	public ResponseEntity<?> getNumberDriverWorkingHours(String TOKEN, Long[] driverIds, Long[] groupIds, int offset,
			String start, String end, String search, Long userId,String exportData) {
		logger.info("************************ getNumberDriverWorkingHours STARTED ***************************");

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NUMBERDRIVERWORKINGHOURS", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver working hours list",driverHours);
					logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		List<Long>allDevices= new ArrayList<>();
		List<Long>allDrivers= new ArrayList<>();


		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
			    	Group group=groupRepository.findOne(groupId);
			    	if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
									logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",driverHours);
										logger.info("************************ getNumberDriverWorkingHours ENDED ***************************"); 
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
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
										isParent = false;
								}
								else {
										isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",driverHours);
								logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									allDrivers.addAll(groupRepository.getDriversFromGroup(groupId));

								}
								else if(group.getType().equals("device")) {
									allDrivers.addAll(groupRepository.getDriverFromDevices(groupId));
									
								}
								else if(group.getType().equals("geofence")) {
									allDrivers.addAll(groupRepository.getDriversFromGeofence(groupId));

								}
							}

							
						}
			    	}
			    	

				}
			}
		}
		if(driverIds.length !=0 ) {
			for(Long driverId : driverIds) {
				if(driverId !=0) {
					
					Driver driver =driverServiceImpl.getDriverById(driverId);
					if(driver != null) {
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
								logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>driverParent = driver.getUserDriver();
								if(driverParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver ",driverHours);
									logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
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
							List<Long> CheckData = userClientDriverRepository.getDriver(userId,driverId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this driver",driverHours);
							logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						allDrivers.add(driverId);
						
						
					}
					
	
				}
			}
		}
		if(allDrivers.size()>0) {

			List<Long> dri = new ArrayList<Long>();
			for(Object obj : allDrivers.toArray()) {
				dri.add(Long.valueOf(obj.toString()));
			}
			allDevices.addAll(driverRepository.devicesOfDrivers(dri));

		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}	
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			search = "%"+search+"%";
			Integer size = 0;
			
			
			String appendString="";
	
			if(allDevices.size()>0) {
				  for(int i=0;i<allDevices.size();i++) {
					  if(appendString != "") {
						  appendString +=","+allDevices.get(i);
					  }
					  else {
						  appendString +=allDevices.get(i);
					  }
				  }
			 }
			allDevices = new ArrayList<Long>();
			String[] data = {};
			if(!appendString.equals("")) {
		        data = appendString.split(",");

			}

	        for(String d:data) {
	        	if(!allDevices.contains(Long.parseLong(d))) {
		        	allDevices.add(Long.parseLong(d));
	        	}
	        }
			
	        if(allDevices.isEmpty()) {

	        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for drivers of groups or drivers that you selected",driverHours);
				logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
	        	return  ResponseEntity.badRequest().body(getObjectResponse);
	        }
	        
			List<Map> dataAll = new ArrayList<Map>();
			
			
	        driverHours = mongoPositionRepo.getDriverWorkingHoursScheduled(allDevices, dateFrom, dateTo);
			if(driverHours.size()>0) {

				  for(Long dev:allDevices) {

						String totalHours = "00:00:00";
						Long time= (long) 0;

					  Map devicesStatus = new HashMap();
					  for(DriverWorkingHours driverH: driverHours) {

						  if( (long) driverH.getDeviceId() == (long) dev ) {
							  
							  devicesStatus.put("deviceId" ,driverH.getDeviceId());

							  if(driverH.getDeviceName() != null) {
								  devicesStatus.put("deviceName", driverH.getDeviceName());
							  }
							  if(driverH.getDriverName() != null) {
								  devicesStatus.put("driverName", driverH.getDriverName());
							  }

						      devicesStatus.put("totalHours", totalHours);
							  


							JSONObject obj = new JSONObject(driverH.getAttributes().toString());

							if(obj.has("todayHours")) {
								  time += Math.abs(  obj.getLong("todayHours") );
								  Long hoursEngine =   TimeUnit.MILLISECONDS.toHours(time) ;
								  Long minutesEngine = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
								  Long secondsEngine = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
								  
								  totalHours = String.valueOf(hoursEngine)+":"+String.valueOf(minutesEngine)+":"+String.valueOf(secondsEngine);

							}
						      devicesStatus.put("totalHours", totalHours);
						  }
					  }
					  if(devicesStatus.size() > 0) {
						  dataAll.add(devicesStatus);
					  }

				  }
				 
			  }

			

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",dataAll,dataAll.size());
			logger.info("************************ getNumberDriverWorkingHours ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
			
			
	}

	

	
	

}
