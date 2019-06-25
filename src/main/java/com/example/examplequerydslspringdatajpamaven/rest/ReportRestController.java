package com.example.examplequerydslspringdatajpamaven.rest;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.Stop;
import com.example.service.ReportServiceImpl;
import com.fasterxml.jackson.annotation.JsonProperty;


@RestController
@RequestMapping(path = "/reports")
public class ReportRestController {
	
	@Autowired
	ReportServiceImpl reportServiceImpl;
	
	@RequestMapping(value = "/get_events_report/{deviceId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestBody Map<String, Object> event,@PathVariable (value = "deviceId") Long deviceId) {
		int offset=0;
		String start=null;
		String end=null;

		if(event.get("offset")!=null) {
			offset=Integer.parseInt(event.get("offset").toString())-1;
			if(offset<0) {
				offset=0;
			}
		}
		if(event.get("start")!=null) {
			start=event.get("start").toString();
		}
		if(event.get("end")!=null) {
			end=event.get("end").toString();
		}
		
		if(deviceId != 0) {
			List<Event> events=reportServiceImpl.getEventsReport(deviceId,offset,start,end);
			ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			if(!events.isEmpty()) {
			    for(int i=0;i<events.size();i++) {
					 Map<String, Object> result = new HashMap<>();
					 if(events.get(i).getType().equalsIgnoreCase("alarm")) {
						 JSONObject obj= new JSONObject(events.get(i).getAttributes());
						 result.put("type", obj.get("alarm"));
					 }
					 else {
						 result.put("type", events.get(i).getType());
					 }
					 result.put("attributes", events.get(i).getAttributes());
					 result.put("servertime", events.get(i).getServertime().toString());
					 result.put("deviceName", events.get(i).getDevice().getName());
					 //result.put("driverName", events.get(i).getDevice().getDriver());
					 
					 data.add(i, result);

				}
			    return ResponseEntity.ok(data);
			}
			else {
				return ResponseEntity.ok("no data available");

			}
			
			
			


		}
		else{
			
			return ResponseEntity.ok("no device selected to get his own events");			
		
		}
		
		
	}
	
	@RequestMapping(value = "/get_stops_report", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
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
	

}
