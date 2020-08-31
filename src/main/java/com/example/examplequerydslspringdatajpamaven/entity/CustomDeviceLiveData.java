package com.example.examplequerydslspringdatajpamaven.entity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.ColumnResult;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomDeviceLiveData {
	
	public CustomDeviceLiveData() {
		// TODO Auto-generated constructor stub
	}
	private int id;
	private String deviceName;
	private String uniqueId;
	private String lastUpdate;
	private Double weight;
	private Double latitude;
	private Double operator;
	private Double longitude;
	private String address;
	private Object attributes;
	private String crash;
	private String batteryUnpluged;
	private String PowerUnpluged;
	private String todayHoursString;
	private String deviceWorkingHoursPerDay;
	private String driverWorkingHoursPerDay;
	private Double power;
	private String photo;
	private Float speed ;
	private String status;
	private String vehicleStatus;
	private String positionId;
	private JSONObject jsonAttributes;
	//private Double sensor1;
	//private Double sensor2;
	private String hours;
	private Boolean motion;
	private Boolean valid;
	private String totalDistance;
	private Boolean ignition;
	private String alarm;
	private Double battery;
	private String driverName;
	private String leftLetter;
	private String middleLetter;
	private String rightLetter;
	private String plate_num;
	private String sequence_number;
	private String owner_name;

     

	public CustomDeviceLiveData(Double weight, Double latitude, Double longitude, String address, Object attributes,
			Float speed) {
		super();
		this.weight = weight;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.attributes = attributes;
		this.speed = speed;
	}
	public CustomDeviceLiveData(int id ,String deviceName , String lastUpdate , String address , Object attributes ,  Double latitude ,
			  Double longitude ,Float speed , String photo , String positionId) {
		this.id = id ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.address = address;
		this.attributes = attributes;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.photo = photo;
		this.positionId = positionId;
	    if(this.lastUpdate != null) {
	    	SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
	    	TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh"); //Target timezone
	         
	        Date currentDate = new Date();
	        String deviceLastUpdate = FORMATTER.format(lastUpdate);
	       
	        System.out.println(FORMATTER.format(currentDate));  //Date in current timezone
	         
	        FORMATTER.setTimeZone(etTimeZone);
	         String now = FORMATTER.format(currentDate);
	         System.out.println(now);
	        try {
				Date date1=new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss").parse(now);
				Date date2=new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss").parse(deviceLastUpdate);
				long diff = date1.getTime() - date2.getTime();
				long diffMinutes = diff / (60 * 1000);  
				 
                if(diffMinutes <=3 && diffMinutes >=0)
                {
                   this.status="online";
                }
                else if(diffMinutes >3 && diffMinutes <8)
                {
                   this.status="out of network";
                }
                else
                {
                	this.status="offline";
                }
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
 
	    }
		if(attributes != null) {
			
			
		   ObjectMapper mapper = new ObjectMapper();
     	   String json = null;
     	   try {
	 		   json = mapper.writeValueAsString(attributes);
		   } catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

         	JSONObject jsonObject = new JSONObject(json);

         	if(jsonObject.has("power")) {
				this.power =  jsonObject.getDouble("power");
			}else {
				this.power =0.0;
			}
			if(jsonObject.has("alarm")) {
				this.alarm = jsonObject.getString("alarm");
				if(alarm.equals("crash")) {
					this.crash = "Yes";
					this.batteryUnpluged = "No";
					this.PowerUnpluged = "No";
				}
				else if(alarm.equals("batteryUnpluged")) {
					this.batteryUnpluged = "Yes";
					this.crash = "No";
					this.PowerUnpluged = "No";
				}else if(alarm.equals("PowerUnpluged")) {
					this.batteryUnpluged = "No";
					this.crash = "No";
					this.PowerUnpluged = "Yes";
				}else {
					this.batteryUnpluged = "No";
					this.crash = "No";
					this.PowerUnpluged = "No";
				}
				
			}
			
			if(jsonObject.has("weight")) {
				this.weight = jsonObject.getDouble("weight");
			}else
			{
				this.weight =0.0;
			}
			if(jsonObject.has("todayHoursString")) {
				this.deviceWorkingHoursPerDay = jsonObject.getString("todayHoursString");
				this.driverWorkingHoursPerDay = jsonObject.getString("todayHoursString");
			}else
			{
				this.deviceWorkingHoursPerDay = "00:00";
				this.driverWorkingHoursPerDay = "00:00";
			}
			if(jsonObject.has("hours")){
				DecimalFormat df = new DecimalFormat("###.###");
				String minutes = df.format((jsonObject.getDouble("hours")/ (1000*60))% 60);
				String hour = df.format(jsonObject.getDouble("hours")/ (1000*60*60));
				this.hours = hour+" h "+minutes+" m ";
			}
			if(jsonObject.has("battery")) {
				this.battery = jsonObject.getDouble("battery");
			}
			if(jsonObject.has("motion")) {
				this.motion = jsonObject.getBoolean("motion");
			}
			if(jsonObject.has("totalDistance")) {
				DecimalFormat df = new DecimalFormat("######.##");
				this.totalDistance = df.format((jsonObject.getDouble("totalDistance")/1000));
			}
			if(jsonObject.has("ignition")) {
				this.ignition = jsonObject.getBoolean("ignition");
			}
//			if(jsonObject.has("adc1")) {
//				this.sensor1 = jsonObject.getDouble("adc1");
//			}
//			if(jsonObject.has("adc2")) {
//				this.sensor2 = jsonObject.getDouble("adc2");
//			}
			
		}else {
			this.weight =0.0;
			this.deviceWorkingHoursPerDay = "00:00";
			this.driverWorkingHoursPerDay = "00:00";
			
		}

		
		
		
	}
	public CustomDeviceLiveData(int id ,String deviceName , String lastUpdate , String address , Object attributes ,  Double latitude ,
			  Double longitude ,Float speed ,String positionId, String leftLetter,String middleLetter,String rightLetter,String driverName ) {
		this.id = id ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.address = address;
		this.attributes = attributes;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.positionId = positionId;
		this.leftLetter= leftLetter;
		this.rightLetter= rightLetter;
		this.middleLetter= middleLetter;
		this.driverName= driverName;
	    if(this.lastUpdate != null) {
	    	SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
	    	TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh"); //Target timezone
	         
	        Date currentDate = new Date();
	        String deviceLastUpdate = FORMATTER.format(lastUpdate);
	       
	        System.out.println(FORMATTER.format(currentDate));  //Date in current timezone
	         
	        FORMATTER.setTimeZone(etTimeZone);
	         String now = FORMATTER.format(currentDate);
	         System.out.println(now);
	        try {
				Date date1=new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss").parse(now);
				Date date2=new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss").parse(deviceLastUpdate);
				long diff = date1.getTime() - date2.getTime();
				long diffMinutes = diff / (60 * 1000);  
				 
              if(diffMinutes <=3 && diffMinutes >=0)
              {
                 this.status="online";
              }
              else if(diffMinutes >3 && diffMinutes <8)
              {
                 this.status="out of network";
              }
              else
              {
              	this.status="offline";
              }
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  

	    }
		if(attributes != null) {

			ObjectMapper mapper = new ObjectMapper();
     	   String json = null;
     	   try {
	 		   json = mapper.writeValueAsString(attributes);
		   } catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

         	JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("power")) {
				this.power =  jsonObject.getDouble("power");
			}else {
				this.power =0.0;
			}
			if(jsonObject.has("alarm")) {
				this.alarm = jsonObject.getString("alarm");
				if(alarm.equals("crash")) {
					this.crash = "Yes";
					this.batteryUnpluged = "No";
					this.PowerUnpluged = "No";
				}
				else if(alarm.equals("batteryUnpluged")) {
					this.batteryUnpluged = "Yes";
					this.crash = "No";
					this.PowerUnpluged = "No";
				}else if(alarm.equals("PowerUnpluged")) {
					this.batteryUnpluged = "No";
					this.crash = "No";
					this.PowerUnpluged = "Yes";
				}else {
					this.batteryUnpluged = "No";
					this.crash = "No";
					this.PowerUnpluged = "No";
				}
				
			}
			
			if(jsonObject.has("weight")) {
				this.weight = jsonObject.getDouble("weight");
			}else
			{
				this.weight =0.0;
			}
			if(jsonObject.has("todayHoursString")) {
				this.deviceWorkingHoursPerDay = jsonObject.getString("todayHoursString");
				this.driverWorkingHoursPerDay = jsonObject.getString("todayHoursString");
			}else
			{
				this.deviceWorkingHoursPerDay = "00:00";
				this.driverWorkingHoursPerDay = "00:00";
			}
			if(jsonObject.has("hours")){
				DecimalFormat df = new DecimalFormat("###.###");
				String minutes = df.format((jsonObject.getDouble("hours")/ (1000*60))% 60);
				String hour = df.format(jsonObject.getDouble("hours")/ (1000*60*60));
				this.hours = hour+" h "+minutes+" m ";
			}
			if(jsonObject.has("battery")) {
				this.battery = jsonObject.getDouble("battery");
			}
			if(jsonObject.has("motion")) {
				this.motion = jsonObject.getBoolean("motion");
			}
			if(jsonObject.has("totalDistance")) {
				DecimalFormat df = new DecimalFormat("######.##");
				this.totalDistance = df.format((jsonObject.getDouble("totalDistance")/1000));
			}
			if(jsonObject.has("ignition")) {
				this.ignition = jsonObject.getBoolean("ignition");
			}
//			if(jsonObject.has("adc1")) {
//				this.sensor1 = jsonObject.getDouble("adc1");
//			}
//			if(jsonObject.has("adc2")) {
//				this.sensor2 = jsonObject.getDouble("adc2");
//			}
			
		}else {
			this.weight =0.0;
			this.deviceWorkingHoursPerDay = "00:00";
			this.driverWorkingHoursPerDay = "00:00";
			
		}

		
		
		
	}
	
	public CustomDeviceLiveData(int id ,String deviceName , String lastUpdate ,
			String positionId, String leftLetter,String middleLetter,String rightLetter,String driverName,
			Double latitude ,Double longitude,Object attributes,String address,Float speed,String plate_num
			,String sequence_number,String owner_name,Boolean valid) {
		this.id = id ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.leftLetter= leftLetter;
		this.rightLetter= rightLetter;
		this.middleLetter= middleLetter;
		this.driverName= driverName;
		this.latitude= latitude;
		this.longitude= longitude;
		this.attributes= attributes;
		this.address= address;
		this.speed = speed;
		this.plate_num = plate_num;
		this.sequence_number = sequence_number;
		this.owner_name = owner_name;
		this.valid = valid;
		if(attributes != null) {
		   ObjectMapper mapper = new ObjectMapper();
     	   String json = null;
     	   try {
	 		   json = mapper.writeValueAsString(attributes);
		   } catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

         	JSONObject jsonObject = new JSONObject(json);


			if(jsonObject.has("todayHoursString")) {
				this.todayHoursString = jsonObject.getString("todayHoursString");
			}else
			{
				this.todayHoursString = "00:00";
			}
			if(jsonObject.has("totalDistance")) {
				DecimalFormat df = new DecimalFormat("######.##");
				this.totalDistance = df.format((jsonObject.getDouble("totalDistance")/1000));
			}
			else {
				this.totalDistance = "0.0";
			}
			if(jsonObject.has("weight")) {
				this.weight = jsonObject.getDouble("weight");
			}else
			{
				this.weight =0.0;
			}
		}
		else {
			this.todayHoursString = "00:00";
			this.totalDistance = "0.0";
			this.weight =0.0;
		}
	}
	public CustomDeviceLiveData(int id ,String deviceName , String lastUpdate ,
			String positionId, String leftLetter,String middleLetter,String rightLetter,String driverName,
			String plate_num,String sequence_number,String owner_name) {
		this.id = id ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.leftLetter= leftLetter;
		this.rightLetter= rightLetter;
		this.middleLetter= middleLetter;
		this.driverName= driverName;
		this.plate_num = plate_num;
		this.sequence_number = sequence_number;
		this.owner_name = owner_name;
		
	}
	public CustomDeviceLiveData(int id ,String deviceName , String uniqueId,String lastUpdate ,String positionId,String photo) {
		this.id = id ;
		this.uniqueId = uniqueId ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.photo=photo;

	   		
	}
	public CustomDeviceLiveData(int id ,String deviceName , String uniqueId,String lastUpdate ,String positionId,String photo
			,Object attributes , Float speed,Double latitude ,Double longitude,Boolean valid) {
		this.id = id ;
		this.uniqueId = uniqueId ;
		this.deviceName = deviceName ;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.photo=photo;
		this.attributes= attributes;
		this.speed= speed;
		this.latitude= latitude;
		this.longitude= longitude;
		this.valid= valid;

	   		
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Object getAttributes() {
		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}

	public String getCrash() {
		return crash;
	}

	public void setCrash(String crash) {
		this.crash = crash;
	}

	public String getBatteryUnpluged() {
		return batteryUnpluged;
	}

	public void setBatteryUnpluged(String batteryUnpluged) {
		this.batteryUnpluged = batteryUnpluged;
	}

	public String getPowerUnpluged() {
		return PowerUnpluged;
	}

	public void setPowerUnpluged(String powerUnpluged) {
		PowerUnpluged = powerUnpluged;
	}

	public String getTodayHoursString() {
		return todayHoursString;
	}

	public void setTodayHoursString(String todayHoursString) {
		this.todayHoursString = todayHoursString;
	}
	public String getDeviceWorkingHoursPerDay() {
		return deviceWorkingHoursPerDay;
	}
	public void setDeviceWorkingHoursPerDay(String deviceWorkingHoursPerDay) {
		this.deviceWorkingHoursPerDay = deviceWorkingHoursPerDay;
	}
	public String getDriverWorkingHoursPerDay() {
		return driverWorkingHoursPerDay;
	}
	public void setDriverWorkingHoursPerDay(String driverWorkingHoursPerDay) {
		this.driverWorkingHoursPerDay = driverWorkingHoursPerDay;
	}
	public Double getPower() {
		return power;
	}

	public void setPower(Double power) {
		this.power = power;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}


	public JSONObject getJsonAttributes() {
		return jsonAttributes;
	}


	public void setJsonAttributes(JSONObject jsonAttributes) {
		this.jsonAttributes = jsonAttributes;
	}


//	public Double getSensor1() {
//		return sensor1;
//	}
//
//
//	public void setSensor1(Double sensor1) {
//		this.sensor1 = sensor1;
//	}
//
//
//	public Double getSensor2() {
//		return sensor2;
//	}
//
//
//	public void setSensor2(Double sensor2) {
//		this.sensor2 = sensor2;
//	}


	public String getHours() {
		return hours;
	}


	public void setHours(String hours) {
		this.hours = hours;
	}


	public Boolean getMotion() {
		return motion;
	}


	public void setMotion(Boolean motion) {
		this.motion = motion;
	}


	public String getTotalDistance() {
		return totalDistance;
	}


	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}


	public Boolean getIgnition() {
		return ignition;
	}


	public void setIgnition(Boolean ignition) {
		this.ignition = ignition;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public String getAlarm() {
		return alarm;
	}


	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}


	public Double getBattery() {
		return battery;
	}


	public void setBattery(Double battery) {
		this.battery = battery;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getLeftLetter() {
		return leftLetter;
	}
	public void setLeftLetter(String leftLetter) {
		this.leftLetter = leftLetter;
	}
	public String getMiddleLetter() {
		return middleLetter;
	}
	public void setMiddleLetter(String middleLetter) {
		this.middleLetter = middleLetter;
	}
	public String getRightLetter() {
		return rightLetter;
	}
	public void setRightLetter(String rightLetter) {
		this.rightLetter = rightLetter;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public Double getOperator() {
		return operator;
	}
	public void setOperator(Double operator) {
		this.operator = operator;
	}
	public String getPlate_num() {
		return plate_num;
	}
	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}
	public String getSequence_number() {
		return sequence_number;
	}
	public void setSequence_number(String sequence_number) {
		this.sequence_number = sequence_number;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}
	public String getVehicleStatus() {
		return vehicleStatus;
	}
	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	
	

	

}
