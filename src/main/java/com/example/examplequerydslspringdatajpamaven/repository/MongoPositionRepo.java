package com.example.examplequerydslspringdatajpamaven.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gte;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;

import ch.qos.logback.core.Context;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;

@Repository
public class MongoPositionRepo {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public Integer getDeviceIdDistincit(List<Long> allDevices){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices)),
	            group("deviceid"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);

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
	
	public ArrayList<Map<Object,Object>> getLastPoints(int deviceId){
    	ArrayList<Map<Object,Object>> lastPoints = new ArrayList<Map<Object,Object>>();
    	
        BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId)),
	            project("deviceid","servertime","latitude","longitude"),
	            sort(Sort.Direction.DESC, "servertime"),
	            limit(5)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);
    	
	        


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	
	            	Map<Object,Object> points = new HashMap<Object, Object>();
	            	points.put("lat", object.getDouble("latitude"));
	            	points.put("long", object.getDouble("longitude"));
					

	            	lastPoints.add(points);
	            	
	            }
	        }


    	return lastPoints;
	}
	public List<TripPositions> getTripPositions(Long deviceId,Date start,Date end){
	
        List<TripPositions> positions = new ArrayList<TripPositions>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("servertime").gte(start).lte(end)),
	            project("deviceid","servertime","latitude","longitude"),
	            sort(Sort.Direction.DESC, "servertime")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
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
	public List<DeviceWorkingHours> getDeviceCustom(List<Long> allDevices,int offset,Date start,Date end,String custom,String value){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		
		Object v = null;
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
		}
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
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)
	    				.and("attributes."+custom).gte(v)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	public List<DeviceWorkingHours> getDeviceCustomScheduled(List<Long> allDevices,Date start,Date end,String custom,String value){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		Object v = null;
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
		}
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
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)
	    				.and("attributes."+custom).gte(v)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	    		sort(Sort.Direction.DESC, "servertime")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	public Integer getDeviceCustomSize(List<Long> allDevices,Date start,Date end,String custom,String value){

		Integer size = 0;

		BasicDBObject basicDBObject = new BasicDBObject();
		Object v = null;
		if(custom.equals("ignition") || custom.equals("motion")) {
			v = Boolean.parseBoolean(value);
		}
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
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)
	    				.and("attributes."+custom).gte(v)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	    		sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


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
	public Integer getSensorsListSize(List<Long> allDevices,Date start,Date end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","speed").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



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
	
	public List<CustomPositions> getSensorsList(List<Long> allDevices,int offset,Date start,Date end){

		List<CustomPositions> positions = new ArrayList<CustomPositions>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","speed").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);

	        
	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	CustomPositions device = new CustomPositions();
	            	
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.has("speed")) {
		            	device.setSpeed(object.getDouble("speed"));    		
	                }
					if(object.has("servertime")) {
		            	device.setServertime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	
	public List<CustomPositions> getPositionsListScheduled(List<Long> allDevices,Date start,Date end){

		List<CustomPositions> positions = new ArrayList<CustomPositions>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes","speed").and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime"),
	            sort(Sort.Direction.DESC, "servertime")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);

	        
	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	CustomPositions device = new CustomPositions();
	            	
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.has("speed")) {
		            	device.setSpeed(object.getDouble("speed"));    		
	                }
					if(object.has("servertime")) {
		            	device.setServertime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	
	
	public List<DeviceWorkingHours> getDeviceWorkingHours(List<Long> allDevices,int offset,Date start,Date end){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();


		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)), 
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	

	            	if(object.has("attributes")) {
                    	
	                	device.setAttributes(object.get("attributes").toString());

	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	
	public List<DriverWorkingHours> getDriverWorkingHours(List<Long> allDevices,int offset,Date start,Date end){

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime"),
	            skip(offset),
	            limit(10)
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DriverWorkingHours device = new DriverWorkingHours();
	            	
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
	            	driverHours.add(device);
	            }
	        }
        
		return driverHours;
	}
	
	public List<DeviceWorkingHours> getDeviceWorkingHoursScheduled(List<Long> allDevices,Date start,Date end){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)), 
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DeviceWorkingHours device = new DeviceWorkingHours();
	            	
	            	
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
	            	deviceHours.add(device);
	            }
	        }
        
		return deviceHours;
	}
	
	public List<DriverWorkingHours> getDriverWorkingHoursScheduled(List<Long> allDevices,Date start,Date end){

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	    
	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	       if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");
	            Iterator<Object> iterator = list.iterator();
	            while (iterator.hasNext()) {
	            	JSONObject object = (JSONObject) iterator.next();
	            	DriverWorkingHours device = new DriverWorkingHours();
	            	
	            	if(object.has("attributes")) {
	                	device.setAttributes(object.get("attributes").toString());
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("servertime")) {
		            	device.setDeviceTime(object.getString("servertime"));    		
	                }
					if(object.has("_id")) {
		            	JSONObject objId = (JSONObject) object.get("_id");
		            	if(objId.has("$oid")) {
			            	device.setPositionId(objId.getString("$oid"));
						}
	
					}
					
	            	
	            	
					driverHours.add(device);
	            }
	        }
        
		return driverHours;
	}
	
	
	public Integer getDeviceWorkingHoursSize(List<Long> allDevices,Date start,Date end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)), 
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



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
	public Integer getDriverWorkingHoursSize(List<Long> allDevices,Date start,Date end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("servertime").gte(start).lte(end)),
	            project("deviceid","attributes").and("servertime").dateAsFormattedString("%Y-%m-%d").as("servertime"),
	            group("deviceid","servertime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



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
