package com.example.examplequerydslspringdatajpamaven.rest;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;


@RestController
@RequestMapping(path = "/reports")
@CrossOrigin
public class ReportRestController {
	
	@Autowired
	ReportServiceImpl reportServiceImpl;
	
	@Autowired
	DeviceServiceImpl deviceServiceImpl;
	

	@RequestMapping(value = "/getEventsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "start", defaultValue = "0") String start,
			@RequestParam (value = "end", defaultValue = "0") String end) {
		
		GetObjectResponse getObjectResponse ;
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(deviceId != 0) {
			offset=offset-1;
			if(offset <0) {
				offset=0;
			}
			Device device =deviceServiceImpl.findById(deviceId);
			if(device != null) {
				if(start.equals("0") || end.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",eventReport);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(start);
						dateTo = inputFormat.parse(end);

						start = outputFormat.format(dateFrom);
						end = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					eventReport=reportServiceImpl.getEventsReport(deviceId, offset, start, end);
					if(eventReport.size()>0) {
						
						for(int i=0;i<eventReport.size();i++) {
							if(eventReport.get(i).getEventType().equals("alarm")) {
								JSONObject obj = new JSONObject(eventReport.get(i).getAttributes());
								eventReport.get(i).setEventType(obj.getString("alarm"));
							}
						}
						
					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",eventReport);
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",eventReport);

			}
			
			

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",eventReport);

		}
		
    	return  ResponseEntity.ok(getObjectResponse);
		
		
		
	
	}
	
	@RequestMapping(value = "/getStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStops(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			@RequestParam (value = "type", defaultValue = "allEvents") String type,
			@RequestParam (value = "from", defaultValue = "0") String from,
			@RequestParam (value = "to", defaultValue = "0") String to,
			@RequestParam (value = "page", defaultValue = "1") int page,
			@RequestParam (value = "start", defaultValue = "0") int start,
			@RequestParam (value = "limit", defaultValue = "25") int limit) {
		
		GetObjectResponse getObjectResponse ;
		List<?> stopReport = new ArrayList<>();
		if(deviceId != 0) {
			
			Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",stopReport);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

					
				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",stopReport);
		
			}
			
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",stopReport);

		}
		
    	return  ResponseEntity.ok(getObjectResponse);
		
		
	}
	
	
	@RequestMapping(value = "/getTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTrips(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			@RequestParam (value = "type", defaultValue = "allEvents") String type,
			@RequestParam (value = "from", defaultValue = "0") String from,
			@RequestParam (value = "to", defaultValue = "0") String to,
			@RequestParam (value = "page", defaultValue = "1") int page,
			@RequestParam (value = "start", defaultValue = "0") int start,
			@RequestParam (value = "limit", defaultValue = "25") int limit) {
		
		GetObjectResponse getObjectResponse ;
		List<?> tripReport = new ArrayList<>();
		
		if(deviceId != 0) {
			
            Device device = deviceServiceImpl.findById(deviceId);
			
			if(device != null) {
				if(from.equals("0") || to.equals("0")) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date from and to is Required",tripReport);

				}
				else {
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date dateFrom;
					Date dateTo;
					try {
						dateFrom = inputFormat.parse(from);
						dateTo = inputFormat.parse(to);

						from = outputFormat.format(dateFrom);
						to = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

				}
				
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",tripReport);
		
			}
			
			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",tripReport);

		}
		
    	return  ResponseEntity.ok(getObjectResponse);
		 
		
		
		
		
	}
	

}
