package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.EventRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
@Component
public class ReportServiceImpl extends RestServiceController implements ReportService {
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	DeviceRepository deviceRepository;
	
	@Autowired
	DriverRepository driverRepository;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	DriverServiceImpl driverServiceImpl;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	private static final Log logger = LogFactory.getLog(ReportServiceImpl.class);
	
	GetObjectResponse getObjectResponse;

	@Override
	public ResponseEntity<?> getEventsReport(String TOKEN,Long deviceId,int offset,String start,String end,String search) {
		logger.info("************************ getEventsReport STARTED ***************************");
	
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
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
					search = "%"+search+"%";
					eventReport = eventRepository.getEvents(deviceId, offset, start, end,search);
					Integer size = null;
					if(eventReport.size()>0) {
						size=eventRepository.getEventsSize(deviceId,start, end);
						for(int i=0;i<eventReport.size();i++) {
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
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
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",eventReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN,Long deviceId,int offset,String start,String end,String search) {
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
		if(deviceId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
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
					search = "%"+search+"%";
					deviceHours = deviceRepository.getDeviceWorkingHours(deviceId,start, end,offset,search);
					Integer size = null;
					if(deviceHours.size()>0) {
       				    size=deviceRepository.getDeviceWorkingHoursSize(deviceId,start, end);
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
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",deviceHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDeviceWorkingHoursExport(String TOKEN,Long deviceId,String start,String end) {
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
		if(deviceId != 0) {
			
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
					deviceHours = deviceRepository.getDeviceWorkingHoursExport(deviceId,start, end);
					Integer size = null;
					if(deviceHours.size()>0) {
						//size=deviceRepository.getDeviceWorkingHoursSize(deviceId,start, end);
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
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",deviceHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDriverWorkingHours(String TOKEN,Long driverId,int offset,String start,String end,String search) {
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
		if(driverId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
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
					search = "%"+search+"%";
					driverHours = driverRepository.getDriverWorkingHours(driverId,start, end,offset,search);
					Integer size = null;
					if(driverHours.size()>0) {
       				    size=driverRepository.getDriverWorkingHoursSize(driverId,start, end);
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
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",driverHours);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",driverHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getDriverWorkingHoursExport(String TOKEN,Long driverId,String start,String end) {
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
		if(driverId != 0) {
			
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
					driverHours = driverRepository.getDriverWorkingHoursExport(driverId,start, end);
					Integer size = null;
					if(driverHours.size()>0) {
						//size=driverRepository.getDriverWorkingHoursSize(driverId,start, end);
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
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",driverHours);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		

	}
	@Override
	public ResponseEntity<?> getEventsReportToExcel(String TOKEN,Long deviceId,String start,String end) {
		logger.info("************************ getEventsReport STARTED ***************************");
	
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",eventReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId != 0) {
			
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
					eventReport = eventRepository.getEventsToExcel(deviceId,start,end);
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
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			User user = userServiceImpl.findById(userId);
			if(user != null) {
				if(user.getDelete_date()==null) {
					search = "%"+search+"%";
					notifications= eventRepository.getNotifications(userId, offset,search);
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
	public ResponseEntity<?> getStopsReport(String TOKEN,Long deviceId, String type, String from, String to, int page, int start,
			int limit) {

		logger.info("************************ getStopsReport STARTED ***************************");

		List<?> stopReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",stopReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId != 0) {
			
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

					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = "http://31.204.150.201:8080/api/reports/stops";
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
					  stopReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",stopReport);
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
	public ResponseEntity<?> getTripsReport(String TOKEN,Long deviceId, String type, String from, String to, int page, int start,
			int limit) {

		logger.info("************************ getTripsReport STARTED ***************************");

		List<?> tripReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",tripReport);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(deviceId != 0) {
			
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

					String plainCreds = "admin@fuinco.com:admin";
					byte[] plainCredsBytes = plainCreds.getBytes();
					
					byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
					String base64Creds = new String(base64CredsBytes);

					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", "Basic " + base64Creds);
					
					  String GET_URL = "http://31.204.150.201:8080/api/reports/trips";
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
					  tripReport=restTemplate.exchange(builder.toString(), HttpMethod.GET, request,List.class).getBody();
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",tripReport);
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
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",tripReport);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		


	}

	

}
