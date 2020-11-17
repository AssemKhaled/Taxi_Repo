package com.example.examplequerydslspringdatajpamaven.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.Characters;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.MongoEvents;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.mongodb.BasicDBObject;

@Repository
public class MongoEventsRepo {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MongoPositionsRepository mongoPositionsRepository;
	
	public Integer getEventsWithoutTypeSize(List<Long> allDevices,Date start, Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		Integer size = 0;
		
		List<EventReport> events = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","type","positionid").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
		
	        
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

			    if(!list.isNull(0)) {
			    	JSONObject object = (JSONObject) list.get(0);
	            	size = object.getInt("size");
			    }
	            

	        }
	        
		return size;
	}
	public Integer getEventsWithTypeSize(List<Long> allDevices,Date start, Date end,String type){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		Integer size = 0;
		
		List<EventReport> events = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end).and("type").in(type)),
	            project("deviceid","attributes","type","positionid").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
		
	        
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

			    if(!list.isNull(0)) {
			    	JSONObject object = (JSONObject) list.get(0);
	            	size = object.getInt("size");
			    }
	            

	        }
	        
		return size;
	}
	
	public List<EventReport> getEventsScheduled(List<Long> allDevices,Date start, Date end){
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<EventReport> events = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport event = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null") {
                    	
                    	event.setAttributes(object.get("attributes").toString());

	            	}
                    if(object.has("driverName") && object.get("driverName").toString() != "null") {
                    	
                    	event.setDriverName(object.get("driverName").toString());

	            	}
                    
					if(object.has("deviceName") && object.get("deviceName").toString() != "null") {
						
						event.setDeviceName(object.get("deviceName").toString());
					
					}
	            	if(object.has("deviceid")) {
	            		event.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null") {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						event.setServerTime(outputFormat.format(dateTime)); 
						
	                }
					if(object.has("type") && object.get("type").toString() != "null" ) {
						event.setEventType(object.getString("type"));    		
	                }
					if(object.has("alarm") && object.get("alarm").toString() != "null") {
                    	event.setEventType(object.getString("alarm"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						if(pos.toString() != "null") {
							event.setPositionId(pos.toString());  
							MongoPositions position = mongoPositionsRepository.findById(event.getPositionId());
							if(position != null) {
								event.setLatitude(position.getLatitude());
								event.setLongitude(position.getLongitude());
								
							}
						}
						
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		event.setEventId(objId.getString("$oid"));
						}
	
					}
					events.add(event);

	            }
	        }
	        
		return events;
	}
	
	public List<EventReport> getEventsWithoutType(List<Long> allDevices, int offset,Date start, Date end){
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<EventReport> events = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport event = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null") {
                    	
                    	event.setAttributes(object.get("attributes").toString());

	            	}
                    if(object.has("driverName") && object.get("driverName").toString() != "null") {
                    	
                    	event.setDriverName(object.get("driverName").toString());

	            	}
                    
					if(object.has("deviceName") && object.get("deviceName").toString() != "null") {
						
						event.setDeviceName(object.get("deviceName").toString());
					
					}
	            	if(object.has("deviceid")) {
	            		event.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null" ) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						event.setServerTime(outputFormat.format(dateTime)); 
						
						
	                }
					if(object.has("type") && object.get("type").toString() != "null" ) {
						event.setEventType(object.getString("type"));    		
	                }
					if(object.has("alarm") && object.get("alarm").toString() != "null") {
                    	event.setEventType(object.getString("alarm"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						if(pos.toString() != "null") {
							event.setPositionId(pos.toString());  
							MongoPositions position = mongoPositionsRepository.findById(event.getPositionId());
							if(position != null) {
								event.setLatitude(position.getLatitude());
								event.setLongitude(position.getLongitude());
								
							}
						}
						
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		event.setEventId(objId.getString("$oid"));
						}
	
					}
					events.add(event);

	            }
	        }
	        
		return events;
	}
	
	
	public List<EventReport> getEventsWithType(List<Long> allDevices, int offset,Date start, Date end,String type){
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<EventReport> events = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end).and("type").in(type)),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport event = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null" ) {
                    	
                    	event.setAttributes(object.get("attributes").toString());

	            	}
                    if(object.has("driverName") && object.get("driverName").toString() != "null") {
                    	
                    	event.setDriverName(object.get("driverName").toString());

	            	}
                    
					if(object.has("deviceName") && object.get("deviceName").toString() != "null") {
						
						event.setDeviceName(object.get("deviceName").toString());
					
					}
	            	if(object.has("deviceid")) {
	            		event.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null") {
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						event.setServerTime(outputFormat.format(dateTime)); 
	                }
					if(object.has("type") && object.get("type").toString() != "null") {
						event.setEventType(object.getString("type"));    		
	                }
					if(object.has("alarm") && object.get("alarm").toString() != "null") {
                    	event.setEventType(object.getString("alarm"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						
						if(pos.toString() != "null") {
							event.setPositionId(pos.toString());  
							MongoPositions position = mongoPositionsRepository.findById(event.getPositionId());
							if(position != null) {
								event.setLatitude(position.getLatitude());
								event.setLongitude(position.getLongitude());
								
							}
						}
						
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		event.setEventId(objId.getString("$oid"));
						}
	
					}
					events.add(event);

	            }
	        }
	        
		return events;
	}
	

	public List<EventReport> getAllNotificationsTodayChart(List<Long> allDevices){
		
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

	    
		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		List<EventReport> notifications = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(dateFrom).lte(dateTo)),
	            project("deviceid","attributes","type","positionid").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime")            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);

	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport notification = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null") {
                    	
                    	notification.setAttributes(object.get("attributes").toString());

	            	}
	            	if(object.has("deviceid") ) {
	            		notification.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null") {

						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						notification.setServerTime(outputFormat.format(dateTime)); 
	                }
					if(object.has("type") && object.get("type").toString() != "null") {
						notification.setEventType(object.getString("type"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						if(pos.toString() != "null") {
							notification.setPositionId(pos.toString());    		
						}
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		notification.setEventId(objId.getString("$oid"));
						}
	
					}
					notifications.add(notification);

	            }
	        }
	        
		return notifications;
	}
	
	public List<EventReport> getNotificationsToday(List<Long> allDevices,int offset){
	
	
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

	    
		
		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		
		List<EventReport> notifications = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(dateFrom).lte(dateTo)),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","driverid","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);

	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport notification = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null") {
                    	
                    	notification.setAttributes(object.get("attributes").toString());

	            	}
	            	if(object.has("deviceid")) {
	            		notification.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.has("driverid")) {
	            		notification.setDriverId(object.getLong("driverid"));
	
	            	}
	            	if(object.has("driverName") && object.get("driverName").toString() != "null") {
	            		notification.setDriverName(object.get("driverName").toString());
	            	}
	            	if(object.has("deviceName") && object.get("deviceName").toString() != "null") {
	            		notification.setDeviceName(object.get("deviceName").toString());
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null") {
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						notification.setServerTime(outputFormat.format(dateTime));    		
	                }
					if(object.has("type") && object.get("type").toString() != "null") {
						notification.setEventType(object.getString("type"));    		
	                }
					if(object.has("alarm") && object.get("alarm").toString() != "null") {
						notification.setEventType(object.getString("alarm"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						if(pos.toString() != "null") {
							notification.setPositionId(pos.toString());
						}
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		notification.setEventId(objId.getString("$oid"));
						}
	
					}
					notifications.add(notification);

	            }
	        }
	        
		return notifications;
	}
	public List<EventReport> getNotificationsTodaySearch(List<Long> allDevices,String search,int offset){
		
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

	    
		
		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		
		List<EventReport> notifications = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
		 Criteria orCriteria = new Criteria();

	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(dateFrom).lte(dateTo)),
	    		match(orCriteria.orOperator(
	    				Criteria.where("type").regex(search, "i"),Criteria.where("deviceName").regex(search, "i"),
	    				Criteria.where("driverName").regex(search, "i"),Criteria.where("attributes.alarm").regex(search, "i"))
	    				),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","driverid","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);

	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	EventReport notification = new EventReport();
	            	
                    if(object.has("attributes") && object.get("attributes").toString() != "null") {
                    	
                    	notification.setAttributes(object.get("attributes").toString());

	            	}
	            	if(object.has("deviceid")) {
	            		notification.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.has("driverid")) {
	            		notification.setDriverId(object.getLong("driverid"));
	
	            	}
	            	if(object.has("driverName") && object.get("driverName").toString() != "null") {
	            		notification.setDriverName(object.get("driverName").toString());
	            	}
	            	if(object.has("deviceName") && object.get("deviceName").toString() != "null") {
	            		notification.setDeviceName(object.get("deviceName").toString());
	
	            	}
					if(object.has("servertime") && object.get("servertime").toString() != "null") {
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						notification.setServerTime(outputFormat.format(dateTime));    		
	                }
					if(object.has("type") && object.get("type").toString() != "null") {
						notification.setEventType(object.getString("type"));    		
	                }
					if(object.has("alarm") && object.get("alarm").toString() != "null") {
						notification.setEventType(object.getString("alarm"));    		
	                }
					if(object.has("positionid")) {
						Object pos = object.get("positionid");
						if(pos.toString() != "null") {
							notification.setPositionId(pos.toString());    		

						}
						
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
		            		notification.setEventId(objId.getString("$oid"));
						}
	
					}
					notifications.add(notification);

	            }
	        }
	        
		return notifications;
	}
	
	public Integer getNotificationsTodaySize(List<Long> allDevices){

		Integer size = 0;

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		
		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		List<EventReport> notifications = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(dateFrom).lte(dateTo)),
	            project("deviceid","attributes","type").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
	        
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

			    if(!list.isNull(0)) {
			    	JSONObject object = (JSONObject) list.get(0);
	            	size = object.getInt("size");
			    }
	            

	        }
	        
		return size;
	}
	
	public Integer getNotificationsTodaySizeSearch(List<Long> allDevices,String search){

		Integer size = 0;

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		
		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		List<EventReport> notifications = new ArrayList<EventReport>();


		BasicDBObject basicDBObject = new BasicDBObject();
		 Criteria orCriteria = new Criteria();

	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(dateFrom).lte(dateTo)),
	            match(orCriteria.orOperator(
	    				Criteria.where("type").regex(search, "i"),Criteria.where("deviceName").regex(search, "i"),
	    				Criteria.where("driverName").regex(search, "i"),Criteria.where("attributes.alarm").regex(search, "i"))
	    				),
	            project("deviceid","attributes","type","positionid","deviceName","driverName","driverid","attributes.alarm").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoEvents> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_events", MongoEvents.class);
	        
	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

			    if(!list.isNull(0)) {
			    	JSONObject object = (JSONObject) list.get(0);
	            	size = object.getInt("size");
			    }
	            

	        }
	        
		return size;
	}
	
}
