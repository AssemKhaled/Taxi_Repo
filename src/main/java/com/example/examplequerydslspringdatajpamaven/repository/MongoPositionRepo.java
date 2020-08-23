package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	public List<TripPositions> getTripPositions(Long deviceId,String start,String end){
	
        List<TripPositions> positions = new ArrayList<TripPositions>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("devicetime").gte(start).lte(end)),
	            project("deviceid","devicetime","latitude","longitude"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
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
	public List<DeviceWorkingHours> getDeviceCustom(List<Long> allDevices,int offset,String start,String end,String custom,String value){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$strLenCP", "$attributes");

						return filterExpression;
					}
				}).as("len"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$len", 2};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$subtract", array );
						return filterExpression;
					}
				}).as("length"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$attributes", 1, "$length"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$substr", array );
						return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object split[] = new Object[] { "$attribute", "," };
					    ArrayList<Integer> array = new ArrayList<Integer>();
						Object arrayThis[] = new Object[] {"$$this", ":"};

						DBObject $let =  new BasicDBObject();
						$let.put("vars",new BasicDBObject("splitted",new BasicDBObject("$split",arrayThis)));
						
						DBObject in =  new BasicDBObject();
						Object arrayElemAtK[] = new Object[] {"$$splitted", 0};
						Object arrayElemAtV[] = new Object[] {"$$splitted", 1};
						in.put("k", new BasicDBObject("$arrayElemAt",arrayElemAtK));
						in.put("v", new BasicDBObject("$arrayElemAt",arrayElemAtV));
						
						$let.put("in",in);


						DBObject output =  new BasicDBObject("$let",$let);

						Object arrayConcat[] = new Object[] {output};

						Object concat[] = new Object[] {"$$value.elements",arrayConcat};

	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    data.put("input", new BasicDBObject("$split",split));
	                    data.put("initialValue", new BasicDBObject("elements", array));
	                    data.put("in", new BasicDBObject("elements", new BasicDBObject("$concatArrays", concat)));

	                    filterExpression.put("$reduce", data );

	                    return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.k");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.v");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("values"),
	            project("deviceid","devicetime","attributes","values","keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$keys", custom};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$indexOfArray", array );
						return filterExpression;
					}
				}).as("index"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$values", "$index"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$arrayElemAt", array );
						return filterExpression;
					}
				}).as("output"),
	            match(Criteria.where("output").lte(value)), 
	            sort(Sort.Direction.DESC, "devicetime"),
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	public List<DeviceWorkingHours> getDeviceCustomScheduled(List<Long> allDevices,String start,String end,String custom,String value){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();
		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$strLenCP", "$attributes");

						return filterExpression;
					}
				}).as("len"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$len", 2};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$subtract", array );
						return filterExpression;
					}
				}).as("length"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$attributes", 1, "$length"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$substr", array );
						return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object split[] = new Object[] { "$attribute", "," };
					    ArrayList<Integer> array = new ArrayList<Integer>();
						Object arrayThis[] = new Object[] {"$$this", ":"};

						DBObject $let =  new BasicDBObject();
						$let.put("vars",new BasicDBObject("splitted",new BasicDBObject("$split",arrayThis)));
						
						DBObject in =  new BasicDBObject();
						Object arrayElemAtK[] = new Object[] {"$$splitted", 0};
						Object arrayElemAtV[] = new Object[] {"$$splitted", 1};
						in.put("k", new BasicDBObject("$arrayElemAt",arrayElemAtK));
						in.put("v", new BasicDBObject("$arrayElemAt",arrayElemAtV));
						
						$let.put("in",in);


						DBObject output =  new BasicDBObject("$let",$let);

						Object arrayConcat[] = new Object[] {output};

						Object concat[] = new Object[] {"$$value.elements",arrayConcat};

	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    data.put("input", new BasicDBObject("$split",split));
	                    data.put("initialValue", new BasicDBObject("elements", array));
	                    data.put("in", new BasicDBObject("elements", new BasicDBObject("$concatArrays", concat)));

	                    filterExpression.put("$reduce", data );

	                    return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.k");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.v");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("values"),
	            project("deviceid","devicetime","attributes","values","keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$keys", custom};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$indexOfArray", array );
						return filterExpression;
					}
				}).as("index"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$values", "$index"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$arrayElemAt", array );
						return filterExpression;
					}
				}).as("output"),
	            match(Criteria.where("output").lte(value)), 
	            sort(Sort.Direction.DESC, "devicetime")
	            
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	public Integer getDeviceCustomSize(List<Long> allDevices,String start,String end,String custom,String value){

		Integer size = 0;

		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$strLenCP", "$attributes");

						return filterExpression;
					}
				}).as("len"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$len", 2};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$subtract", array );
						return filterExpression;
					}
				}).as("length"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$attributes", 1, "$length"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$substr", array );
						return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object split[] = new Object[] { "$attribute", "," };
					    ArrayList<Integer> array = new ArrayList<Integer>();
						Object arrayThis[] = new Object[] {"$$this", ":"};

						DBObject $let =  new BasicDBObject();
						$let.put("vars",new BasicDBObject("splitted",new BasicDBObject("$split",arrayThis)));
						
						DBObject in =  new BasicDBObject();
						Object arrayElemAtK[] = new Object[] {"$$splitted", 0};
						Object arrayElemAtV[] = new Object[] {"$$splitted", 1};
						in.put("k", new BasicDBObject("$arrayElemAt",arrayElemAtK));
						in.put("v", new BasicDBObject("$arrayElemAt",arrayElemAtV));
						
						$let.put("in",in);


						DBObject output =  new BasicDBObject("$let",$let);

						Object arrayConcat[] = new Object[] {output};

						Object concat[] = new Object[] {"$$value.elements",arrayConcat};

	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    data.put("input", new BasicDBObject("$split",split));
	                    data.put("initialValue", new BasicDBObject("elements", array));
	                    data.put("in", new BasicDBObject("elements", new BasicDBObject("$concatArrays", concat)));

	                    filterExpression.put("$reduce", data );

	                    return filterExpression;
					}
				}).as("attribute"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.k");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						
	                    DBObject filterExpression = new BasicDBObject();
	                    DBObject data = new BasicDBObject();
	                    DBObject filter = new BasicDBObject();
	                    filter.put("input", "$attribute.elements");
	                    filter.put("as", "el");
	                    filter.put("cond", new BasicDBObject("$not","$el.key"));

	                    data.put("input",new BasicDBObject("$filter",filter));
	                    data.put("in","$$this.v");

	                    filterExpression.put("$map", data );
						return filterExpression;
					}
				}).as("values"),
	            project("deviceid","devicetime","attributes","values","keys").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$keys", custom};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$indexOfArray", array );
						return filterExpression;
					}
				}).as("index"),
	            project("deviceid","devicetime","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
						Object array[] = new Object[] { "$values", "$index"};
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$arrayElemAt", array );
						return filterExpression;
					}
				}).as("output"),
	            match(Criteria.where("output").lte(value)), 
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));

	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);


	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

            	JSONObject object = (JSONObject) list.get(0);

            	size = object.getInt("size");
	            

	        }
		return size;
	}
	public Integer getSensorsListSize(List<Long> allDevices,String start,String end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("fixtime").gte(start).lte(end)),
	            sort(Sort.Direction.DESC, "servertime"),
	            count().as("size")
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

            	JSONObject object = (JSONObject) list.get(0);

            	size = object.getInt("size");
	            

	        }
		return size;
	}
	
	public List<CustomPositions> getSensorsList(List<Long> allDevices,int offset,String start,String end){

		List<CustomPositions> positions = new ArrayList<CustomPositions>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("fixtime").gte(start).lte(end)),
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
	                	device.setAttributes(object.getString("attributes"));
	
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
	
	public List<CustomPositions> getPositionsListScheduled(List<Long> allDevices,String start,String end){

		List<CustomPositions> positions = new ArrayList<CustomPositions>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("fixtime").gte(start).lte(end)),
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
	                	device.setAttributes(object.getString("attributes"));
	
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
	
	
	public List<DeviceWorkingHours> getDeviceWorkingHours(List<Long> allDevices,int offset,String start,String end){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	
	public List<DriverWorkingHours> getDriverWorkingHours(List<Long> allDevices,int offset,String start,String end){

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	
	public List<DeviceWorkingHours> getDeviceWorkingHoursScheduled(List<Long> allDevices,String start,String end){

		List<DeviceWorkingHours> deviceHours = new ArrayList<DeviceWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime")
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	
	public List<DriverWorkingHours> getDriverWorkingHoursScheduled(List<Long> allDevices,String start,String end){

		List<DriverWorkingHours> driverHours = new ArrayList<DriverWorkingHours>();

				
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
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
	                	device.setAttributes(object.getString("attributes"));
	
	            	}
	            	if(object.has("deviceid")) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
					if(object.has("devicetime")) {
		            	device.setDeviceTime(object.getString("devicetime"));    		
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
	
	
	public Integer getDeviceWorkingHoursSize(List<Long> allDevices,String start,String end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

            	JSONObject object = (JSONObject) list.get(0);

            	size = object.getInt("size");
	            

	        }
		return size;
	}
	public Integer getDriverWorkingHoursSize(List<Long> allDevices,String start,String end){

		Integer size = 0;

		
		BasicDBObject basicDBObject = new BasicDBObject();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)), 
	            project("deviceid","attributes").and(new AggregationExpression() {
					@Override
					public DBObject toDbObject(AggregationOperationContext context) {
	                    DBObject filterExpression = new BasicDBObject();
	                    filterExpression.put("$dateFromString", new BasicDBObject("dateString","$devicetime"));

						return filterExpression;
					}
				}).as("devicetime"),
	            project("deviceid","attributes").and("devicetime").dateAsFormattedString("%Y-%m-%d").as("devicetime"),
	            group("deviceid","devicetime").last("$$ROOT").as("test"),
	            replaceRoot("test"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	            
	        ).withOptions(new AggregationOptions(false, false, basicDBObject));


	        AggregationResults<MongoPositions> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);



	        if(groupResults.getRawResults().containsField("cursor")) {
	            JSONObject obj = new JSONObject(groupResults.getRawResults().get("cursor").toString());
	            
			    JSONArray list = (JSONArray) obj.get("firstBatch");

            	JSONObject object = (JSONObject) list.get(0);

            	size = object.getInt("size");
	            

	        }
		return size;
	}
}
