package com.example.examplequerydslspringdatajpamaven.rest;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;


@RestController
@RequestMapping(path = "/reports")
@CrossOrigin
public class ReportRestController {
	
	@Autowired
	ReportServiceImpl reportServiceImpl;
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;
	

	@RequestMapping(value = "/getEventsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			@RequestParam (value = "offset", defaultValue = "0") int offset,
			@RequestParam (value = "start", defaultValue = "0") String start,
			@RequestParam (value = "end", defaultValue = "0") String end) {
		
		GetObjectResponse getObjectResponse ;
		List<EventReport> eventReport = new ArrayList<EventReport>();
		if(deviceId != 0) {
			
			eventReport=reportServiceImpl.getEventsReport(deviceId, offset, start, end);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Device ID is Required",eventReport);

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Device ID is Required",eventReport);

		}
		
    	return  ResponseEntity.ok(getObjectResponse);
		
		
		
	
	}
	
	@RequestMapping(value = "/get_stops_report", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStops(@Param (value = "deviceId") String deviceId,
			@Param (value = "type") String type,
			@Param (value = "from") String from,
			@Param (value = "to") String to,
			@Param (value = "page") String page,
			@Param (value = "start") String start,
			@Param (value = "limit") String limit) {
		
		if(deviceId != null) {
			
			if(type == null) {
				type="allEvents";
			}
			if(from == null) {
				from="0000-00-00T22:00:00.000Z";
			}
			if(to == null) {
				to="0000-00-00T22:00:00.000Z";
			}
			if(page == null) {
				page="1";
			}
			if(start == null) {
				start="0";
			}
			if(limit == null) {
				limit="25";
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
			  //Stop response = restTemplate.exchange(builder.toString(), HttpMethod.GET, request,Stop.class).getBody();
			  return ResponseEntity.ok(restTemplate.exchange(builder.toString(), HttpMethod.GET, request,String.class).getBody());

			
			
		}
		else {
			  return ResponseEntity.ok("no device selected");

		}
		
		 
		
		
		
		
	}
	
	
	@RequestMapping(value = "/get_trips_report", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTrips(@Param (value = "deviceId") String deviceId,
			@Param (value = "type") String type,
			@Param (value = "from") String from,
			@Param (value = "to") String to,
			@Param (value = "page") String page,
			@Param (value = "start") String start,
			@Param (value = "limit") String limit) {
		
		if(deviceId != null) {
			
			if(type == null) {
				type="allEvents";
			}
			if(from == null) {
				from="0000-00-00T22:00:00.000Z";
			}
			if(to == null) {
				to="0000-00-00T22:00:00.000Z";
			}
			if(page == null) {
				page="1";
			}
			if(start == null) {
				start="0";
			}
			if(limit == null) {
				limit="25";
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
			  ResponseEntity<String> response = restTemplate.exchange(builder.toString(), HttpMethod.GET, request,String.class);
			  /*String data= response.getBody().substring(1,response.getBody().length()-1);
			  String res= "{"+data+"}";
			  JSONObject obj = new JSONObject(res);
			  trips */
			  return ResponseEntity.ok(response.getBody());

			
			
		}
		else {
			
			  return ResponseEntity.ok("no device selected");

		}
		
		 
		
		
		
		
	}
	

}
