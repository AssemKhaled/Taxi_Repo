package com.example.examplequerydslspringdatajpamaven.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gt;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lte;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.LastElmData;
import com.example.examplequerydslspringdatajpamaven.entity.LastPositionData;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.BooleanOperators.And.and;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gt.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lt.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gte.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lte.valueOf;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;


/**
 * Mongo manual queries on position collection
 * @author fuinco
 *
 */
@Repository
public class MongoPositionRepo {

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MongoElmLiveLocationRepository mongoElmLiveLocationRepository;
	
	public ArrayList<Map<Object,Object>> getLastPoints(Long deviceId){
    	ArrayList<Map<Object,Object>> lastPoints = new ArrayList<Map<Object,Object>>();
    			
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId)),
	            project("deviceid","devicetime","latitude","longitude"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            limit(5)
	            
	    	).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);
    	
           if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	Map<Object,Object> points = new HashMap<Object, Object>();
	            	points.put("lat", object.getDouble("latitude"));
	            	points.put("long", object.getDouble("longitude"));
					

	            	lastPoints.add(points);
	            	
	            }
	        }


    	return lastPoints;
	}
	public List<TripPositions> getTripPositions(Long deviceId,Date start,Date end){
	
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
        List<TripPositions> positions = new ArrayList<TripPositions>();
				
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("devicetime").gte(start).lte(end)),
	            project("deviceid","latitude","longitude").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	TripPositions pos = new TripPositions() {
						
						@Override
						public Double getDriver_long() {
							// TODO Auto-generated method stub
							return object.getDouble("longitude");
						}
						
						@Override
						public Double getDriver_lat() {
							// TODO Auto-generated method stub
							return object.getDouble("latitude");
						}
					};
					positions.add(pos);
					
	            	
	            	
	            }
	        }
        
		return positions;
	}	
	
    public List<String> getElmLogsExpiredDays(Date time){
	
		List<String> data = new ArrayList<String>();
		
		BasicDBObject fromString = new BasicDBObject();
		BasicDBObject dateFromString = new BasicDBObject();
		
		fromString.put("dateString", "$time");
		fromString.put("format", "%Y-%m-%d %H:%M:%S");
		dateFromString.put("$dateFromString", fromString);
		
		
	    Aggregation aggregation = newAggregation(
	    		project().and(new AggregationExpression() {
					
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						// TODO Auto-generated method stub
						return dateFromString;
					}
				}).as("date"),
	    		match(Criteria.where("date").lt(time)),
	    		limit(100)
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_elmLogs", BasicDBObject.class);
	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	if(object.containsField("_id") && object.get("_id") != null) {						
						data.add(object.getObjectId("_id").toString());

					}
	            	
	            	
	            }
	        }
        
		return data;
	}	
    
    public List<String> getElmPositionsExpiredDays(Date time){
    	
		List<String> data = new ArrayList<String>();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("servertime").lt(time)),
	    		limit(100)
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);
	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	if(object.containsField("_id") && object.get("_id") != null) {						
						data.add(object.getObjectId("_id").toString());

					}
	            	
	            	
	            }
	        }
        
		return data;
	}	

	public List<DeviceWorkingHours> getDeviceCustom(List<Long> allDevices,int offset,Date start,Date end,String custom,String value){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		Aggregation aggregation;
		Object v = null;
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
			
			
			aggregation = newAggregation(
		    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
		    				.and("attributes."+custom).in(v)),
		            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
		            sort(Sort.Direction.DESC, "devicetime"),
		            skip(offset),
		            limit(10)
		            
					).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		else {
			
			if(custom.equals("priority") || custom.equals("sat") || custom.equals("event") || custom.equals("rssi")
				|| custom.equals("io69") || custom.equals("di1") || custom.equals("io24") || custom.equals("io68")
				|| custom.equals("io11") || custom.equals("io14")) {
				v = Integer.parseInt(value);
			}
			if(custom.equals("power") || custom.equals("battery")  ) {
				v = Double.parseDouble(value);
			}
			if(custom.equals("adc1") || custom.equals("adc2") || custom.equals("distance") || 
					custom.equals("totalDistance") || custom.equals("totalDistance") || 
					custom.equals("hours") || custom.equals("todayHours") | custom.equals("weight")) {
				v = Double.parseDouble(value);
			}
			if(custom.equals("todayHoursString") || custom.equals("battery unpluged")) {
				v = value;
			}
			    
				
			aggregation = newAggregation(
		    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
		    				.and("attributes."+custom).gte(v)),
		            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
		            sort(Sort.Direction.DESC, "devicetime"),
		            skip(offset),
		            limit(10)
		            
					).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		

	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	                	
	                	JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
	                	if(attr.has(custom)) {
		                	device.setAttributes(custom +":"+attr.get(custom));
	                	}


	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid")  != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime")  != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setDeviceTime(outputFormat.format(dateTime));
						   		
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setPositionId(object.getObjectId("_id").toString());

					}
					
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					

	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	public List<DeviceWorkingHours> getDeviceCustomScheduled(List<Long> allDevices,Date start,Date end,String custom,String value){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		Object v = null;
		Aggregation aggregation ;
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
			aggregation = newAggregation(
		    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
		    				.and("attributes."+custom).in(v)),
		            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
		    		sort(Sort.Direction.DESC, "devicetime")
		            
					).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		else {
			if(custom.equals("priority") || custom.equals("sat") || custom.equals("event") || custom.equals("rssi")
					|| custom.equals("io69") || custom.equals("di1") || custom.equals("io24") || custom.equals("io68")
					|| custom.equals("io11") || custom.equals("io14")) {
					v = Integer.parseInt(value);
				}
				if(custom.equals("power") || custom.equals("battery")  ) {
					v = Double.parseDouble(value);
				}
				if(custom.equals("adc1") || custom.equals("adc2") || custom.equals("distance") || 
						custom.equals("totalDistance") || custom.equals("totalDistance") || 
						custom.equals("hours") || custom.equals("todayHours") | custom.equals("weight")) {
					v = Double.parseDouble(value);
				}
				if(custom.equals("todayHoursString") || custom.equals("battery unpluged")) {
					v = value;
				}
				aggregation = newAggregation(
			    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
			    				.and("attributes."+custom).gte(v)),
			            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
			    		sort(Sort.Direction.DESC, "devicetime")
			            
						).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		
	    

	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	                	
	                	JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
	                	if(attr.has(custom)) {
		                	device.setAttributes(custom +":"+attr.get(custom));
	                	}


	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setDeviceTime(outputFormat.format(dateTime));
						

	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setPositionId(object.getObjectId("_id").toString());

					}
					
					if(object.containsField("deviceName") && object.get("deviceName")!= null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					

	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	public Integer getDeviceCustomSize(List<Long> allDevices,Date start,Date end,String custom,String value){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		Integer size = 0;

		Object v = null;
		Aggregation aggregation;
		
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
		    aggregation = newAggregation(
		    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
		    				.and("attributes."+custom).in(v)),
		            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
		    		sort(Sort.Direction.DESC, "devicetime"),
		            count().as("size")
		    		).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		else {
			
			if(custom.equals("priority") || custom.equals("sat") || custom.equals("event") || custom.equals("rssi")
				|| custom.equals("io69") || custom.equals("di1") || custom.equals("io24") || custom.equals("io68")
				|| custom.equals("io11") || custom.equals("io14")) {
				v = Integer.parseInt(value);
			}
			if(custom.equals("power") || custom.equals("battery")  ) {
				v = Double.parseDouble(value);
			}
			if(custom.equals("adc1") || custom.equals("adc2") || custom.equals("distance") || 
					custom.equals("totalDistance") || custom.equals("totalDistance") || 
					custom.equals("hours") || custom.equals("todayHours") | custom.equals("weight")) {
				v = Double.parseDouble(value);
			}
			if(custom.equals("todayHoursString") || custom.equals("battery unpluged")) {
				v = value;
			}
				
		    aggregation = newAggregation(
		    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)
		    				.and("attributes."+custom).gte(v)),
		            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
		    		sort(Sort.Direction.DESC, "devicetime"),
		            count().as("size")
		    		).withOptions(newAggregationOptions().allowDiskUse(true).build());
		}
		


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	public Integer getSensorsListSize(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		Integer size = 0;

		
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	public List<CustomPositions> getSensorsList(List<Long> allDevices,int offset,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<CustomPositions> positions = new ArrayList<CustomPositions>();

				
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	CustomPositions device = new CustomPositions();
	            	
	            	
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
                       JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("adc1")) {
							device.setSensor1(attr.getDouble("adc1"));
						}
						if(attr.has("adc2")) {
							device.setSensor2(attr.getDouble("adc2"));
						}
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
	            	
					
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	
	public List<CustomPositions> getPositionsListScheduled(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<CustomPositions> positions = new ArrayList<CustomPositions>();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	CustomPositions device = new CustomPositions();
	            	
	            	if(object.containsField("attributes") && object.get("attributes").toString() != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
                       JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("adc1")) {
							device.setSensor1(attr.getDouble("adc1"));
						}
						if(attr.has("adc2")) {
							device.setSensor2(attr.getDouble("adc2"));
						}
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
					
	            	
	            	
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	public List<Map> getCharts(List<String> positionIds){

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
		
		List<Map> positions = new ArrayList<Map>();

		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids).and("devicetime").gte(dateFrom).lte(dateTo)),
	            project("deviceid","attributes","deviceName","driverName","attributes.todayHours","attributes.todayHoursString").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            sort(Sort.Direction.DESC, "attributes.todayHours"),
	            limit(10)
	            

	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	Map position = new HashMap();
	            	

                	position.put("hours","0");

                    if(object.containsField("todayHours")) {

                    	long min = TimeUnit.MILLISECONDS.toMinutes((long) object.getDouble("todayHours"));
                    	
                    	Double hours = (double) min;
						double roundOffDistance = Math.round(hours * 100.0) / 100.0;
                    	position.put("hours",hours/60);

	            	}
	            	if(object.containsField("deviceName") && object.get("deviceName") != null) {
                    	
                    	position.put("deviceName",object.get("deviceName").toString());

	            	}
					if(object.containsField("driverName") && object.get("driverName") != null) {
						
						position.put("driverName",object.get("driverName").toString());
					
					}

					positions.add(position);

	            }
	        }
	        
		return positions;
	}
	
	public List<DeviceWorkingHours> getDeviceWorkingHours(List<Long> allDevices,int offset,Date start,Date end){

		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();


		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	
	            	device.setHours("0");
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
                    	
	                	device.setAttributes(object.get("attributes").toString());
	                	
	                	JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("todayHoursString")) {
							device.setHours(attr.getString("todayHoursString"));
						}
						

	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime") != null) {


						device.setDeviceTime(object.getString("devicetime"));
						
						
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setPositionId(object.getObjectId("_id").toString());

					}
					
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	
						device.setDeviceName(object.getString("deviceName"));
	
					}
					
				
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	
	public List<DriverWorkingHours> getDriverWorkingHours(List<Long> allDevices,int offset,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","deviceName","driverName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DriverWorkingHours device = new DriverWorkingHours();
	            	
	            	
	            	device.setHours("0");
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
                        JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("todayHoursString")) {
							device.setHours(attr.getString("todayHoursString"));
						}
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						device.setDeviceTime(object.getString("devicetime"));

						
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setPositionId(object.getObjectId("_id").toString());

					}
					
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("driverName") && object.get("driverName") != null) {
		            	device.setDriverName(object.getString("driverName"));    		
	                }
					
	            	
	            	
	            	driverHours.add(device);
	            }
	        }
        
		return driverHours;
	}
	
	public List<DeviceWorkingHours> getDeviceWorkingHoursScheduled(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime")
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	

	            	device.setHours("0");
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
                    	
	                	device.setAttributes(object.get("attributes").toString());
	                	
	                	JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("todayHoursString")) {
							device.setHours(attr.getString("todayHoursString"));
						}
						

	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						device.setDeviceTime(object.getString("devicetime"));

						
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
		            	
		            	device.setPositionId(object.getObjectId("_id").toString());

	
					}
					
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	
						device.setDeviceName(object.getString("deviceName"));
	
					}
					
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	
	public List<DriverWorkingHours> getDriverWorkingHoursScheduled(List<Long> allDevices,Date start,Date end){


		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","deviceName","driverName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DriverWorkingHours device = new DriverWorkingHours();
	            	
	            	
	            	device.setHours("0");
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
                        JSONObject attr = new JSONObject(device.getAttributes().toString());
	                	
						if(attr.has("todayHoursString")) {
							device.setHours(attr.getString("todayHoursString"));
						}
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") !=null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.containsField("devicetime") && object.get("devicetime") !=null) {
						
						device.setDeviceTime(object.getString("devicetime"));

						
	                }
					if(object.containsField("_id") && object.get("_id") !=null) {
		            	
		            	device.setPositionId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") !=null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("driverName") && object.get("driverName") !=null) {
		            	device.setDriverName(object.getString("driverName"));    		
	                }
					
	            	
	            	
	            	driverHours.add(device);
	            }
	        }
        
		return driverHours;
	}
	
	
	public Integer getDeviceWorkingHoursSize(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		Integer size = 0;
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes","deviceName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	public Integer getDriverWorkingHoursSize(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		Integer size = 0;
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","deviceName","driverName").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	
	public LastPositionData getLastPosition(Long deviceId){

		LastPositionData position = new LastPositionData();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId)),
	            project("speed","longitude","latitude","attributes","weight").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime").and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "fixtime"),
	            skip(0),
	            limit(1)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	if(object.containsField("servertime") && object.get("servertime") != null) {
	            		
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
	            		
	            		position.setServertime(outputFormat.format(dateTime));
	            		
	            		
	            	}  
                    if(object.containsField("devicetime") && object.get("devicetime") != null) {
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setDevicetime(outputFormat.format(dateTime));
	            		
	            		
	            		
	            	}
                    if(object.containsField("fixtime") && object.get("fixtime") != null) {
	            		
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("fixtime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setFixtime(outputFormat.format(dateTime));
	            		

	            	}
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	            		position.setAttributes(object.get("attributes").toString());
	
	            	}
                    if(object.containsField("latitude") && object.get("latitude") != null) {
	            		
	            		position.setLatitude(object.getDouble("latitude"));

	            	}
                    if(object.containsField("longitude") && object.get("longitude") != null) {
	            		
	            		position.setLongitude(object.getDouble("longitude"));

	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
	            		position.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
	            	if(object.containsField("weight") && object.get("weight") != null) {
	            		position.setWeight(object.getDouble("weight"));    		
	                }
	            	
	            	if(object.containsField("_id") && object.get("_id") != null) {
	            		position.setPositionId(object.getObjectId("_id").toString());
	
					}
					
	            	
	            	
	            }
	        }
        
		return position;
	}
	
	public List<LastPositionData> getLastPositionSpeedZero(Long deviceId){

		List<LastPositionData> positions = new ArrayList<LastPositionData>();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("speed").in(0)),
	            project("speed","longitude","latitude","attributes","weight").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime").and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "fixtime"),
	            skip(0),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	LastPositionData position = new LastPositionData();
	            	
	            	if(object.containsField("servertime") && object.get("servertime") != null) {
	            		
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
	            		
	            		position.setServertime(outputFormat.format(dateTime));
	            		
	            		
	            	}  
                    if(object.containsField("devicetime") && object.get("devicetime") != null) {
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setDevicetime(outputFormat.format(dateTime));
	            		
	            		
	            		
	            	}
                    if(object.containsField("fixtime") && object.get("fixtime") != null) {
	            		
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("fixtime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setFixtime(outputFormat.format(dateTime));
	            		

	            	}
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	            		position.setAttributes(object.get("attributes").toString());
	
	            	}
                    if(object.containsField("latitude") && object.get("latitude") != null) {
	            		
	            		position.setLatitude(object.getDouble("latitude"));

	            	}
                    if(object.containsField("longitude") && object.get("longitude") != null) {
	            		
	            		position.setLongitude(object.getDouble("longitude"));

	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
	            		position.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
	            	
	            	if(object.containsField("weight") && object.get("weight") != null) {
	            		position.setWeight(object.getDouble("weight"));    		
	                }
					
	            	positions.add(position);
	            	
	            }
	        }
        
		return positions;
	}
	
	public List<LastPositionData> getLastPositionGreaterSpeedZero(Long deviceId){

		List<LastPositionData> positions = new ArrayList<LastPositionData>();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("speed").gt(0)),
	            project("speed","longitude","latitude","attributes","weight").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime").and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "fixtime"),
	            skip(0),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
 
	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	LastPositionData position = new LastPositionData();
	            	
                    if(object.containsField("servertime") && object.get("servertime") != null) {
	            		
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
	            		
	            		position.setServertime(outputFormat.format(dateTime));
	            		
	            		
	            	}  
                    if(object.containsField("devicetime") && object.get("devicetime") != null) {
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setDevicetime(outputFormat.format(dateTime));
	            		
	            		
	            		
	            	}
                    if(object.containsField("fixtime") && object.get("fixtime") != null) {
	            		
                    	
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("fixtime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
	            		
	            		position.setFixtime(outputFormat.format(dateTime));
	            		

	            	}
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	            		position.setAttributes(object.get("attributes").toString());
	
	            	}
                    if(object.containsField("latitude") && object.get("latitude") != null) {
	            		
	            		position.setLatitude(object.getDouble("latitude"));

	            	}
                    if(object.containsField("longitude") && object.get("longitude") != null) {
	            		
	            		position.setLongitude(object.getDouble("longitude"));

	            	}
                    
	            	if(object.containsField("speed") && object.get("speed") != null) {
	            		position.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
	            	
	            	if(object.containsField("weight") && object.get("weight") != null) {
	            		position.setWeight(object.getDouble("weight"));    		
	                }
	            	
	            	positions.add(position);
	            	
	            }
	        }
        
		return positions;
	}
	
	public List<LastElmData> getLastPositionVelocityZero(String referenceKey){

		List<LastElmData> positions = new ArrayList<LastElmData>();

						
	    Aggregation aggregation = newAggregation(

				match(Criteria.where("type").in("Location")
						.and("requet.dataObject.vehicleLocations").elemMatch(Criteria.where("referenceKey").in(referenceKey))
						),
	            sort(Sort.Direction.DESC, "_id"),
	            unwind("requet.dataObject.vehicleLocations", "arrayIndex"),
	            match(Criteria.where("requet.dataObject.vehicleLocations.referenceKey").in(referenceKey)
	            		.and("requet.dataObject.vehicleLocations.velocity").in(0)),
	            project("time").and("requet.dataObject.vehicleLocations").as("locations"),
	            skip(0),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_elmLogs", BasicDBObject.class);

            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	    	        
	            	LastElmData position = new LastElmData();
	            	
	            	if(object.containsField("time") && object.get("time") != null) {
	            		position.setSendtime( object.getString("time"));
	            	}  
                    if(object.containsField("locations") && object.get("locations") != null) {
	            		position.setElm_data(object.get("locations").toString());
	            	}


	            	positions.add(position);
	            	
	            }
	        }
        
		return positions;
	}
	
	public List<LastElmData> getLastPositionGreaterVelocityZero(String referenceKey){

		List<LastElmData> positions = new ArrayList<LastElmData>();

						
		Aggregation aggregation = newAggregation(

				match(Criteria.where("type").in("Location")
						.and("requet.dataObject.vehicleLocations").elemMatch(Criteria.where("referenceKey").in(referenceKey))
						),
	            sort(Sort.Direction.DESC, "_id"),
	            unwind("requet.dataObject.vehicleLocations", "arrayIndex"),
	            match(Criteria.where("requet.dataObject.vehicleLocations.referenceKey").in(referenceKey)
	            		.and("requet.dataObject.vehicleLocations.velocity").gt(0)),
	            project("time").and("requet.dataObject.vehicleLocations").as("locations"),
	            skip(0),
	            limit(10)

				).withOptions(newAggregationOptions().allowDiskUse(true).build());

	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_elmLogs", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	    	        
	            	LastElmData position = new LastElmData();
	            	
	            	if(object.containsField("time") && object.get("time") != null) {
	            		position.setSendtime( object.getString("time"));
	            	}  
                    if(object.containsField("locations") && object.get("locations") != null) {
	            		position.setElm_data(object.get("locations").toString());
	            	}


	            	positions.add(position);
	            	
	            }
	        }
        
        
		return positions;
	}
	

	
	public Integer getCountFromAttrbuites(List<String> positionIds,String attr,Boolean value){

		Integer size = 0;
		
		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids).and("attributes."+attr).in(value)),
	            count().as("size")

	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	public Integer getCountFromAttrbuitesChart(List<String> positionIds,String attr,Boolean value){

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
		
		
				
		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids).and("devicetime").gte(dateFrom).lte(dateTo).and("attributes."+attr).in(value)),
	            count().as("size")
   
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	
	
	public Integer getCountFromSpeedGreaterThanZero(List<String> positionIds){

		Integer size = 0;
		
		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids).and("attributes.ignition").in(true).and("speed").gt(0)),
	            count().as("size")

	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	public Integer getCountFromSpeedEqualZero(List<String> positionIds){

		Integer size = 0;

		
		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids).and("speed").in(0)),
	            count().as("size")
     
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	public List<CustomMapData> getOfflineList(List<String> positionIds){

		

		
		List<CustomMapData> positions = new ArrayList<CustomMapData>();

		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}

		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids)),
	            project("deviceid","deviceName","servertime",
	            		"valid","attributes.ignition","attributes.power",
	            		"attributes.operator","latitude","longitude","speed","address").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime")

	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	CustomMapData position = new CustomMapData();

	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	            		position.setId(object.getLong("deviceid"));
	
	            	}
	            	
	            	if(object.containsField("address") && object.get("address") != null) {
 	            		position.setAddress(object.getString("address"));
 	
 	            	}
	            	
	            	if(object.containsField("deviceName") && object.get("deviceName") != null) {
	            		position.setDeviceName(object.getString("deviceName"));
	
	            	}
	            	if(object.containsField("servertime") && object.get("servertime") != null) {
	            		
	            		position.setLastUpdateApp(object.getString("servertime"));
	            		
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
	            		
	            		position.setLastUpdate(outputFormat.format(dateTime));
	
	            	} 
	            	if(object.containsField("_id") && object.get("_id") != null) {
	            		position.setPositionId(object.getObjectId("_id").toString());
	
					}
					
					position.setStatus(4);
					position.setVehicleStatus(3);
					if(object.containsField("valid") && object.get("valid") != null) {
	            		
	            		if(object.getBoolean("valid") == true) {
	            			position.setValid(1);
	
						}
						else {
							position.setValid(0);
	
						}
	
	            	}
					if(object.containsField("ignition")  && object.get("ignition") != null) {
						if(object.getBoolean("ignition") == true) {
	            			position.setIgnition(1);
	
						}
						else {
							position.setIgnition(0);
	
						}
	
	            	}
					
					if(object.containsField("power") && object.get("power") != null) {
	            		position.setPower(object.getDouble("power"));
	
	            	} 

					if(object.containsField("operator") && object.get("operator") != null) {
	            		position.setOperator(object.getDouble("operator"));
	
	            	} 

					
					if(object.containsField("latitude") && object.get("latitude") != null) {
	            		position.setLatitude(object.getDouble("latitude"));
	
	            	} 

					
					if(object.containsField("longitude") && object.get("longitude") != null) {
	            		position.setLongitude(object.getDouble("longitude"));
	
	            	} 

					if(object.containsField("speed") && object.get("speed") != null) {
	            		position.setSpeed(object.getDouble("speed") * (1.852) );
	
	            	} 
					
	            	
					positions.add(position);

	            }
	        }
	        
		return positions;
	}
	
    public List<CustomMapData> getOutOfNetworkList(List<String> positionIds){
		
		List<CustomMapData> positions = new ArrayList<CustomMapData>();

		List<ObjectId> ids = new ArrayList<ObjectId>();

		for(String id:positionIds) {
			if(id != null) {
				ids.add(new ObjectId(id));
			}
		}

		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("_id").in(ids)),
	            project("deviceid","deviceName","servertime",
	            		"valid","attributes.ignition","attributes.power",
	            		"attributes.operator","latitude","longitude","speed","address").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime")

	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	CustomMapData position = new CustomMapData();

	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	            		position.setId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("address") && object.get("address") != null) {
 	            		position.setAddress(object.getString("address"));
 	
 	            	}
 	            	
	            	if(object.containsField("deviceName") && object.get("deviceName") != null) {
	            		position.setDeviceName(object.getString("deviceName"));
	
	            	}
	            	if(object.containsField("servertime") && object.get("servertime") != null) {
	            		position.setLastUpdateApp(object.getString("servertime"));

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
	            		
	            		position.setLastUpdate(outputFormat.format(dateTime));
	            		
	
	            	} 
	            	if(object.containsField("_id") && object.get("_id") != null) {
	            		position.setPositionId(object.getObjectId("_id").toString());

					}
					position.setStatus(6);
					position.setVehicleStatus(2);
					if(object.containsField("valid") && object.get("valid") != null) {
	            		
	            		if(object.getBoolean("valid") == true) {
	            			position.setValid(1);
	
						}
						else {
							position.setValid(0);
	
						}
	
	            	}
					if(object.containsField("ignition") && object.get("ignition") != null) {
						if(object.getBoolean("ignition") == true) {
	            			position.setIgnition(1);
	
						}
						else {
							position.setIgnition(0);
	
						}
	
	            	}
					
					if(object.containsField("power") && object.get("power") != null) {
	            		position.setPower(object.getDouble("power"));
	
	            	} 

					if(object.containsField("operator") && object.get("operator") != null) {
	            		position.setOperator(object.getDouble("operator"));
	
	            	} 

					
					if(object.containsField("latitude") && object.get("latitude") != null) {
	            		position.setLatitude(object.getDouble("latitude"));
	
	            	} 

					
					if(object.containsField("longitude") && object.get("longitude") != null) {
	            		position.setLongitude(object.getDouble("longitude"));
	
	            	} 

					if(object.containsField("speed") && object.get("speed") != null) {
	            		position.setSpeed(object.getDouble("speed") * (1.852) );
	
	            	} 
					
	            	
					positions.add(position);

	            }
	        }
	        
		return positions;
	}
    
    public List<CustomMapData> getOnlineList(List<String> positionIds){
		
 		List<CustomMapData> positions = new ArrayList<CustomMapData>();

 		List<ObjectId> ids = new ArrayList<ObjectId>();

 		for(String id:positionIds) {
 			if(id != null) {
 				ids.add(new ObjectId(id));
 			}
 		}

 		
 	    Aggregation aggregation = newAggregation(
 	            match(Criteria.where("_id").in(ids)),
 	            project("deviceid","deviceName","servertime",
 	            		"valid","attributes.ignition","attributes.power",
 	            		"attributes.operator","latitude","longitude","speed","address").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
 	            sort(Sort.Direction.DESC, "servertime")

 	            
 	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());



 	        AggregationResults<BasicDBObject> groupResults
 	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

 	       if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
 	            	CustomMapData position = new CustomMapData();

 	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
 	            		position.setId(object.getLong("deviceid"));
 	
 	            	}
 	            	if(object.containsField("deviceName") && object.get("deviceName") != null) {
 	            		position.setDeviceName(object.getString("deviceName"));
 	
 	            	}
 	            	if(object.containsField("address") && object.get("address") != null) {
 	            		position.setAddress(object.getString("address"));
 	
 	            	}
 	            	
 	            	if(object.containsField("servertime") && object.get("servertime") != null) {
	            		position.setLastUpdateApp(object.getString("servertime"));

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
	            		
	            		position.setLastUpdate(outputFormat.format(dateTime));
	            		
 	
 	            	} 
 	            	if(object.containsField("_id") && object.get("_id") != null) {
 	            		position.setPositionId(object.getObjectId("_id").toString());

					}
 					position.setVehicleStatus(1);
 					if(object.containsField("valid") && object.get("valid") != null) {
 	            		
 	            		if(object.getBoolean("valid") == true) {
 	            			position.setValid(1);
 	
 						}
 						else {
 							position.setValid(0);
 	
 						}
 	
 	            	}
 					if(object.containsField("speed") && object.get("speed") != null) {
 	            		position.setSpeed(object.getDouble("speed") * (1.852) );
 	            		if(object.getDouble("speed") >0 ) {
         					position.setStatus(2);
         				}
         				if(object.getDouble("speed") == 0 ) {
         					position.setStatus(1);
         				}
 	            	} 
 					
 					if(object.containsField("ignition") && object.get("ignition") != null) {
 						if(object.getBoolean("ignition") == true) {
 	            			position.setIgnition(1);
 	            			
 	            			if(object.getDouble("speed") >0) {
 	         					position.setStatus(2);
 	         				}
 	         				if(object.getDouble("speed") == 0) {
 	         					position.setStatus(1);
 	         				}
 	
 						}
 						else {
 							position.setIgnition(0);
	         				position.setStatus(3);

 	
 						}
 	
 	            	}
 					
 					if(object.containsField("power") && object.get("power") != null) {
 	            		position.setPower(object.getDouble("power"));
 	
 	            	} 

 					if(object.containsField("operator") && object.get("operator") != null) {
 	            		position.setOperator(object.getDouble("operator"));
 	
 	            	} 

 					
 					if(object.containsField("latitude") && object.get("latitude") != null) {
 	            		position.setLatitude(object.getDouble("latitude"));
 	
 	            	} 

 					
 					if(object.containsField("longitude") && object.get("longitude") != null) {
 	            		position.setLongitude(object.getDouble("longitude"));
 	
 	            	} 

 					
 	            	
 					positions.add(position);

 	            }
 	        }
 	        
 		return positions;
 	}
    
}
