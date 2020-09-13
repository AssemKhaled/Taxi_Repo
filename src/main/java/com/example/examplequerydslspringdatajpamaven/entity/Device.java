package com.example.examplequerydslspringdatajpamaven.entity;

import java.sql.Date;

import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Column;
import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SqlResultSetMappings({
	@SqlResultSetMapping(
	        name="DevicesSendList",
	        classes={
	           @ConstructorResult(
	                targetClass=LastLocationsList.class,
	                  columns={

	 	                     @ColumnResult(name="deviceid",type=Long.class),
	 	                     @ColumnResult(name="deviceRK",type=String.class),
	 	                     @ColumnResult(name="driver_RK",type=String.class),
	 	                     @ColumnResult(name="driverid",type=Long.class),
	 	                     @ColumnResult(name="drivername",type=String.class),
	 	                     @ColumnResult(name="devicename",type=String.class),
	 	                     @ColumnResult(name="userid",type=Long.class),
	 	                     @ColumnResult(name="username",type=String.class),
	 	                     @ColumnResult(name="userRK",type=String.class)

	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="DevicesList",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	                     @ColumnResult(name="id",type=int.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="uniqueId",type=String.class),
	                     @ColumnResult(name="sequenceNumber",type=String.class),
	                     @ColumnResult(name="lastUpdate",type=String.class),
	                     @ColumnResult(name="referenceKey",type=String.class),
	                     @ColumnResult(name="driverName",type=String.class),
	                     @ColumnResult(name="companyName",type=String.class),
	                     @ColumnResult(name="geofenceName",type=String.class),

	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="DevicesListApp",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	                     @ColumnResult(name="id",type=int.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="uniqueId",type=String.class),
	                     @ColumnResult(name="sequenceNumber",type=String.class),
	                     @ColumnResult(name="lastUpdate",type=String.class),
	                     @ColumnResult(name="referenceKey",type=String.class),
	                     @ColumnResult(name="driverName",type=String.class),
	                     @ColumnResult(name="driver_num",type=String.class),
	                     @ColumnResult(name="companyName",type=String.class),
	                     @ColumnResult(name="geofenceName",type=String.class),
	                     @ColumnResult(name="positionId",type=String.class),

	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="DeviceWorkingHours",
	        classes={
	           @ConstructorResult(
	                targetClass=DeviceWorkingHours.class,
	                  columns={
	                     @ColumnResult(name="deviceTime",type=String.class),
	                     @ColumnResult(name="positionId",type=String.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="deviceId",type=Integer.class),
	                     @ColumnResult(name="deviceName",type=String.class)
	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="billingsList",
	        classes={
	           @ConstructorResult(
	                targetClass=BillingsList.class,
	                  columns={
	                     @ColumnResult(name="deviceNumbers",type=Long.class),
	                     @ColumnResult(name="workingDate",type=String.class),
	                     @ColumnResult(name="ownerName",type=String.class)
	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="DeviceLiveData",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceLiveData.class,
	                  columns={
	                     @ColumnResult(name="id"),
	                     @ColumnResult(name="deviceName"),
	                     @ColumnResult(name="lastUpdate",type=String.class),
	                     @ColumnResult(name="address"),
	                     @ColumnResult(name="attributes"),
	                     @ColumnResult(name="latitude"),
	                     @ColumnResult(name="longitude"),
	                     @ColumnResult(name="speed"),
	                     @ColumnResult(name="photo"),
	                     @ColumnResult(name="positionId")
	                     
	                     }
	           )
	        }
	        ),@SqlResultSetMapping(
	    	        name="DevicesLiveDataMap",
	    	        classes={
	    	           @ConstructorResult(
	    	                targetClass=CustomDeviceLiveData.class,
	    	                  columns={
	    	                     @ColumnResult(name="id",type=int.class),
	    	                     @ColumnResult(name="deviceName",type=String.class),
	    	                     @ColumnResult(name="lastUpdate",type=String.class),
	    	                     @ColumnResult(name="positionId",type=String.class),
	    	                     @ColumnResult(name="leftLetter",type=String.class),
	    	                     @ColumnResult(name="middleLetter",type=String.class),
	    	                     @ColumnResult(name="rightLetter",type=String.class),
	    	                     @ColumnResult(name="driverName",type=String.class),
	    	                     @ColumnResult(name="latitude",type=Double.class),
	    	                     @ColumnResult(name="longitude",type=Double.class),
	    	                     @ColumnResult(name="attributes",type=String.class),
	    	                     @ColumnResult(name="address",type=String.class),
	    	                     @ColumnResult(name="speed",type=Float.class),
	    	                     @ColumnResult(name="plate_num",type=String.class),
	    	                     @ColumnResult(name="sequence_number",type=String.class),
	    	                     @ColumnResult(name="owner_name",type=String.class),
	    	                     @ColumnResult(name="valid",type=Boolean.class),

	    	                     }
	    	           )
	    	        }
	    	),@SqlResultSetMapping(
	    	        name="DevicesDataMap",
	    	        classes={
	    	           @ConstructorResult(
	    	                targetClass=CustomMapData.class,
	    	                  columns={
	    	                     @ColumnResult(name="id",type=Long.class),
	    	                     @ColumnResult(name="deviceName",type=String.class),
	    	                     @ColumnResult(name="lastUpdate",type=String.class),
	    	                     @ColumnResult(name="positionId",type=String.class),
	    	                     }
	    	           )
	    	        }
	    	),
	@SqlResultSetMapping(
	        name="DevicesLiveData",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceLiveData.class,
	                  columns={
	                     @ColumnResult(name="id",type=int.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="uniqueId",type=String.class),
	                     @ColumnResult(name="lastUpdate",type=String.class),
	                     @ColumnResult(name="positionId",type=String.class),
	                     @ColumnResult(name="photo",type=String.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="speed",type=Float.class),
	                     @ColumnResult(name="latitude",type=Double.class),
	                     @ColumnResult(name="longitude",type=Double.class),
	                     @ColumnResult(name="valid",type=Boolean.class),

	                     }
	           ),
	           
	        }),
	@SqlResultSetMapping(
	        name="DevicesData",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceLiveData.class,
	                  columns={
	                     @ColumnResult(name="id",type=int.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="uniqueId",type=String.class),
	                     @ColumnResult(name="lastUpdate",type=String.class),
	                     @ColumnResult(name="positionId",type=String.class),
	                     @ColumnResult(name="photo",type=String.class)

	                     }
	           ),
	           
	        }),
	@SqlResultSetMapping(
	        name="vehicleInfo",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	                		 @ColumnResult(name="id",type=int.class),
	 	                     @ColumnResult(name="deviceName",type=String.class),
	 	                     @ColumnResult(name="uniqueId",type=String.class),
	 	                     @ColumnResult(name="sequenceNumber",type=String.class),
	 	                     @ColumnResult(name="driverName",type=String.class),
	 	                     @ColumnResult(name="driverId",type=Long.class),
	 	                     @ColumnResult(name="driverPhoto",type=String.class),
	 	                     @ColumnResult(name="driverUniqueId",type=String.class),
	 	                     @ColumnResult(name="plateType",type=String.class),
	 	                     @ColumnResult(name="vehiclePlate",type=String.class),
	 	                     @ColumnResult(name="ownerName",type=String.class),
	 	                     @ColumnResult(name="ownerId",type=String.class),
	 	                     @ColumnResult(name="userName",type=String.class),
	 	                     @ColumnResult(name="brand",type=String.class),
	 	                     @ColumnResult(name="model",type=String.class),
	 	                     @ColumnResult(name="madeYear",type=String.class),
	 	                     @ColumnResult(name="color",type=String.class),
	 	                     @ColumnResult(name="licenceExptDate",type=String.class),
	 	                     @ColumnResult(name="carWeight",type=String.class),
		 	                 @ColumnResult(name="positionId",type=String.class),
		                     @ColumnResult(name="geofenceName",type=String.class),

	             	
	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="vehicleInfoData",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	                		 @ColumnResult(name="id",type=int.class),
	 	                     @ColumnResult(name="deviceName",type=String.class),
	 	                     @ColumnResult(name="uniqueId",type=String.class),
	 	                     @ColumnResult(name="sequenceNumber",type=String.class),
	 	                     @ColumnResult(name="driverName",type=String.class),
	 	                     @ColumnResult(name="driverId",type=Long.class),
	 	                     @ColumnResult(name="driverPhoto",type=String.class),
	 	                     @ColumnResult(name="driverUniqueId",type=String.class),
	 	                     @ColumnResult(name="plateType",type=String.class),
	 	                     @ColumnResult(name="vehiclePlate",type=String.class),
	 	                     @ColumnResult(name="ownerName",type=String.class),
	 	                     @ColumnResult(name="ownerId",type=String.class),
	 	                     @ColumnResult(name="userName",type=String.class),
	 	                     @ColumnResult(name="brand",type=String.class),
	 	                     @ColumnResult(name="model",type=String.class),
	 	                     @ColumnResult(name="madeYear",type=String.class),
	 	                     @ColumnResult(name="color",type=String.class),
	 	                     @ColumnResult(name="licenceExptDate",type=String.class),
	 	                     @ColumnResult(name="carWeight",type=String.class),
		 	                 @ColumnResult(name="positionId",type=String.class),
		 	                 @ColumnResult(name="latitude",type=String.class),
		 	                 @ColumnResult(name="longitude",type=String.class),
		 	                 @ColumnResult(name="speed",type=String.class),
		 	                 @ColumnResult(name="address",type=String.class),
		 	                 @ColumnResult(name="attributes",type=String.class),
		                     @ColumnResult(name="geofenceName",type=String.class),

	             	
	                     }
	           )
	        }
	)

})

@NamedNativeQueries({
	
	@NamedNativeQuery(name="getBillingsList", 
		     resultSetMapping="billingsList", 
		     query="SELECT COUNT(distinct tc_devices.id) as deviceNumbers,tc_users.name as ownerName ," + 
		     		" DATE_FORMAT(tc_positions.fixtime, '%Y-%m') as workingDate " + 
		     		" from tc_positions " +  
		     		" INNER JOIN tc_devices ON tc_positions.deviceid = tc_devices.id  " + 
		     		" INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id " + 
		     		" INNER JOIN tc_users ON tc_users.id = tc_user_device.userid " + 
		     		" where (tc_positions.fixtime between :start and  :end ) and tc_positions.fixtime > '2018-01-01' " + 
		     		" and ( (tc_devices.delete_date is null) or (tc_devices.delete_date > :start ) )" +
		     		"  AND (tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "+
		     		" AND tc_users.id =:userId group by workingDate limit :offset,10 " ),

@NamedNativeQuery(name="getDevicesList", 
     resultSetMapping="DevicesList", 
     query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName, tc_devices.uniqueid as uniqueId,"
     		+ " tc_devices.sequence_number as sequenceNumber ,tc_devices.lastupdate as lastUpdate "
     		+ " ,tc_devices.reference_key as referenceKey, "
     		+ " tc_drivers.name as driverName,tc_users.name as companyName ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
     		+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
     		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
     		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null"
     		+ " AND ( tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
     		+ " OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
     		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,10"),

@NamedNativeQuery(name="getDevicesListApp", 
resultSetMapping="DevicesListApp", 
query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName, tc_devices.uniqueid as uniqueId,"
		+ " tc_devices.sequence_number as sequenceNumber ,tc_devices.lastupdate as lastUpdate "
		+ " ,tc_devices.reference_key as referenceKey, "
		+ " tc_drivers.name as driverName,tc_drivers.mobile_num as driver_num,tc_users.name as companyName ,GROUP_CONCAT(tc_geofences.name )AS geofenceName ,"
		+" tc_devices.positionid as positionId "
		+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date is null "
		+ " INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null"
		+ " AND ( tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
		+ " OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,3"),


@NamedNativeQuery(name="getDevicesLiveData", 
resultSetMapping="DevicesLiveData", 
query=" SELECT  tc_devices.id as id ,tc_devices.uniqueid as uniqueId ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate, " + 
		"  tc_devices.positionid as positionId, " + 
		" tc_devices.photo as photo ,tc_positions.attributes as attributes,tc_positions.speed as speed ,"
		+ " tc_positions.latitude as latitude, " + 
		" tc_positions.longitude as longitude  ,tc_positions.valid as valid  FROM tc_devices "+
		" Left JOIN tc_positions ON tc_positions.id=tc_devices.positionid  "
	+ " INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " 
	+ " where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null "
	+ "  AND ((tc_devices.name LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))))"
	+ " GROUP BY tc_devices.id LIMIT :offset,10"),

@NamedNativeQuery(name="getDevicesData", 
resultSetMapping="DevicesData", 
query=" SELECT  tc_devices.id as id ,tc_devices.uniqueid as uniqueId ,tc_devices.name as deviceName ,"
		+ " tc_devices.lastupdate as lastUpdate, " + 
		"  tc_devices.positionid as positionId, " + 
		" tc_devices.photo as photo  FROM tc_devices "
		+ " INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " 
		+ " where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null "
		+ "  AND ( (tc_devices.name LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))))"
		+ " GROUP BY tc_devices.id LIMIT :offset,10"),
		
@NamedNativeQuery(name="getDevicesLiveDataMap", 
resultSetMapping="DevicesLiveDataMap", 
query="SELECT tc_devices.id as id ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate,tc_devices.positionid as positionId , " 
		+ " tc_devices.left_letter as leftLetter , " + 
		" tc_devices.middle_letter as middleLetter,tc_devices.right_letter as rightLetter ,tc_drivers.name driverName, "  
		+" tc_positions.latitude as latitude,tc_positions.longitude as longitude ,tc_positions.attributes as attributes,tc_positions.address as address,tc_positions.speed as speed,"
		+ " tc_devices.plate_num as  plate_num , tc_devices.sequence_number as  sequence_number ,"
		+ " tc_devices.owner_name as  owner_name ,tc_positions.valid as valid"
		+ " FROM tc_devices " + 
		" Left JOIN tc_positions ON tc_positions.id=tc_devices.positionid  " + 
		" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid" + 
		" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
		" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
		" where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null  GROUP BY tc_devices.id,tc_drivers.id"),


@NamedNativeQuery(name="getDevicesDataMap", 
resultSetMapping="DevicesDataMap", 
query="SELECT tc_devices.id as id ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate,"
		+ "tc_devices.positionid as positionId "
		+ " FROM tc_devices " + 
		" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid" + 
		" where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null"
		+ " GROUP BY tc_devices.id"),



@NamedNativeQuery(name="vehicleInfo", 
resultSetMapping="vehicleInfoData", 
query=" SELECT tc_drivers.id as driverId,tc_drivers.uniqueid as driverUniqueId,tc_drivers.name as driverName,tc_drivers.photo as driverPhoto," + 
		" tc_devices.id as id,tc_devices.name as deviceName,tc_devices.uniqueid as uniqueId,tc_devices.sequence_number as sequenceNumber," + 
		" tc_devices.owner_name as ownerName,tc_devices.owner_id as ownerId, " + 
		" tc_devices.username as userName,tc_devices.model as model , " + 
		" tc_devices.brand as brand,tc_devices.made_year as madeYear, " + 
		" tc_devices.color as color,tc_devices.car_weight as carWeight, " + 
		" tc_devices.license_exp as licenceExptDate, " + 
		" CONCAT_WS(' ',tc_devices.plate_num,tc_devices.right_letter,tc_devices.middle_letter,tc_devices.left_letter) as vehiclePlate, " + 
		" tc_devices.plate_type as plateType,tc_positions.id as positionId,tc_positions.latitude as latitude,tc_positions.longitude as longitude, " + 
		" tc_positions.speed as speed,tc_positions.address as address,tc_positions.attributes as attributes " + 
		" ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"+
		" FROM tc_devices  " + 
		" LEFT JOIN tc_positions ON tc_positions.id = tc_devices.positionid " + 
		" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
		" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
		" LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" +
 		" LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid"+
		" WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL"
		+ " GROUP BY tc_devices.id,tc_drivers.id "),

@NamedNativeQuery(name="getDevicesSendList", 
resultSetMapping="DevicesSendList", 
query=  "SELECT tc_devices.id as deviceid,tc_devices.reference_key as deviceRK , " + 
		" tc_drivers.reference_key as driver_RK ,tc_drivers.id as driverid,tc_drivers.name as drivername,  " + 
		" tc_devices.name as devicename, tc_users.id as userid ,tc_users.name as username ,tc_users.reference_key as userRK FROM tc_devices  " + 
		" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
		" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid  " + 
		" INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id " + 
		" INNER JOIN tc_users ON tc_user_device.userid=tc_users.id " + 
		" where " + 
		" tc_devices.is_deleted IS NULL " + 
		" AND tc_devices.create_date Is NOT NULL  " + 
		" AND tc_devices.expired IS False " + 
		" AND tc_drivers.is_deleted IS NULL " + 
		" AND tc_users.is_deleted IS NULL " + 
		" AND tc_devices.reference_key IS NOT NULL" ),

@NamedNativeQuery(name="getVehicleInfoData", 
resultSetMapping="vehicleInfo", 
query=" SELECT tc_drivers.id as driverId,tc_drivers.uniqueid as driverUniqueId,tc_drivers.name as driverName,tc_drivers.photo as driverPhoto," + 
		" tc_devices.id as id,tc_devices.name as deviceName,tc_devices.uniqueid as uniqueId,tc_devices.sequence_number as sequenceNumber," + 
		" tc_devices.owner_name as ownerName,tc_devices.owner_id as ownerId, " + 
		" tc_devices.username as userName,tc_devices.model as model , " + 
		" tc_devices.brand as brand,tc_devices.made_year as madeYear, " + 
		" tc_devices.color as color,tc_devices.car_weight as carWeight, " + 
		" tc_devices.license_exp as licenceExptDate, " + 
		" CONCAT_WS(' ',tc_devices.plate_num,tc_devices.right_letter,tc_devices.middle_letter,tc_devices.left_letter) as vehiclePlate, " + 
		" tc_devices.plate_type as plateType,tc_devices.positionid as positionId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName" +
		" FROM tc_devices  " + 
		" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
		" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
		" LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" +
 		" LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid"+
		" WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL"
		+ " GROUP BY tc_devices.id,tc_drivers.id ")


})

@Entity
@Table(name = "tc_devices" , schema = "sareb_gold")
@JsonIgnoreProperties(value = { "events","hibernateLazyInitializer", "handler" })
public class Device {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name") 
	private String name;
	
	@Column(name = "uniqueid")
	private String uniqueId;
	
	@Column(name = "lastupdate")
//	@CsvBindByName
//	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
//	@Temporal(TemporalType.TIMESTAMP)
	private String lastUpdate;
	
	@Column(name = "positionid")
	private String positionid;
	
	/*@Column(name = "groupid")
	private Integer groupId;*/
	
	@Column(name = "attributes")
	private String attributes;
	
	@Column(name = "phone") 
	private String phone;
	
	@Column(name = "model")
	private String model;
	
	@Column(name = "plate_num")
	private String plate_num;
	
	@Column(name = "right_letter")
	private String right_letter;
	
	@Column(name = "middle_letter")
	private String middle_letter;
	
	@Column(name = "left_letter")
	private String left_letter;
	
	@Column(name = "plate_type")
	private Integer plate_type;
	
	@Column(name = "reference_key")
	private String reference_key;
	
	//should be isDeleted
	@Column(name = "is_deleted")
	private Integer is_deleted;
	
	@Column(name = "delete_date")
//	@CsvBindByName
//	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
//	@Temporal(TemporalType.TIMESTAMP)
	private String delete_date;
	
	//should by initSensor
   @Column(name = "init_sensor")
	private Integer init_sensor;
	
   //should be initSensor2
	@Column(name = "init_sensor2")
	private Integer init_sensor2;
	
	//wanted to be carWeight
	@Column(name = "car_weight")
	private Integer car_weight;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "sequence_number")
	private String sequence_number;
	
	@Column(name = "is_valid")
	private Integer is_valid;
	
	@Column(name = "expired")
	private Integer expired;
	
	@Column(name = "calibrationData",length=1080)
	private String calibrationData;
	
	@Column(name = "fuel",length=1080)
	private String fuel;
	
	@Column(name = "sensorSettings",length=1080)
	private String sensorSettings;
	
	@Column(name = "lineData")
	private String lineData;
	
	@Column(name = "create_date")
	private String create_date;
	
	@Column(name = "last_weight")
	private Integer lastWeight;
	
	@Column(name = "owner_name")
	private String owner_name;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "owner_id")
	private String owner_id;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "made_year")
	private String made_year;
	
	@Column(name = "color")
	private String color;
	
	@Column(name = "license_exp")
	private String license_exp;
	
	//should be date_type
	@Column(name = "date_type")
	private Integer date_type;

	@Column(name = "photo")
	private String photo;
	
	@Column(name = "icon")
	private String icon;
	
	@Column(name = "protocol")
	private String protocol;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "device_type")
	private String device_type;
	
    /*@ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE
            },
            mappedBy = "devices")
    private List<User> user;*/
//	@JsonIgnore 
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_device",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
	private Set<User> user = new HashSet<>();
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_driver",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "driverid") }
    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
	private Set<Driver> driver = new HashSet<>();
	@JsonIgnore
	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}
			)
	@JoinTable(
			name = "tc_device_geofence",
			joinColumns = {@JoinColumn (name = "deviceid")},
			inverseJoinColumns = {@JoinColumn(name = "geofenceid")}
			)
	private Set<Geofence> geofence = new HashSet<>();
   

	public Device() {
		
	}
   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	
	/*public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}*/

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
     
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPlateNum() {
		return plate_num;
	}

	public void setPlateNum(String plateNum) {
		this.plate_num = plateNum;
	}

	public String getRightLetter() {
		return right_letter;
	}

	public void setRightLetter(String rightLetter) {
		this.right_letter = rightLetter;
	}

	public String getMiddleLetter() {
		return middle_letter;
	}

	public void setMiddleLetter(String middleLetter) {
		this.middle_letter = middleLetter;
	}

	public String getLeftLetter() {
		return left_letter;
		
	}

	public void setLeftLetter(String leftLetter) {
		this.left_letter = leftLetter;
	}
	
	public Integer getPlateType() {
		return plate_type;
	}

	public void setPlateType(Integer plateType) {
		this.plate_type = plateType;
	}

	public String getReferenceKey() {
		return reference_key;
	}

	public void setReferenceKey(String referenceKey) {
		this.reference_key = referenceKey;
	}

	public Integer getIsDeleted() {
		return is_deleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.is_deleted = isDeleted;
	}

	public String getDeleteDate() {
		return delete_date;
	}

	public void setDeleteDate(String deleteDate) {
		this.delete_date = deleteDate;
	}

	public Integer getInitSensor() {
		return init_sensor;
	}

	public void setInitSensor(Integer initSensor) {
		this.init_sensor = initSensor;
	}

	public Integer getInitSensor2() {
		return init_sensor2;
	}

	public void setInitSensor2(Integer initSensor2) {
		this.init_sensor2 = initSensor2;
	}

	public Integer getCarWeight() {
		return car_weight;
	}

	public void setCarWeight(Integer carWeight) {
		this.car_weight = carWeight;
	}

	public String getRejectReason() {
		return reject_reason;
	}

	public void setRejectReason(String rejectReason) {
		this.reject_reason = rejectReason;
	}

	public String getSequenceNumber() {
		return sequence_number;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequence_number = sequenceNumber;
	}

	public Integer getIsValid() {
		return is_valid;
	}

	public void setIsValid(Integer isValid) {
		this.is_valid = isValid;
	}

	public String getCalibrationData() {
		return calibrationData;
	}

	public void setCalibrationData(String calibrationData) {
		this.calibrationData = calibrationData;
	}

	public String getLineData() {
		return lineData;
	}

	public void setLineData(String lineData) {
		this.lineData = lineData;
	}

	public Integer getLastWeight() {
		return lastWeight;
	}

	public void setLastWeight(Integer lastWeight) {
		this.lastWeight = lastWeight;
	}

	public String getOwnerName() {
		return owner_name;
	}

	public void setOwnerName(String ownerName) {
		this.owner_name = ownerName;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public String getOwnerId() {
		return owner_id;
	}

	public void setOwnerId(String ownerId) {
		this.owner_id = ownerId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getMadeYear() {
		return made_year;
	}

	public void setMadeYear(String madeYear) {
		this.made_year = madeYear;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLicenseExp() {
		return license_exp;
	}

	public void setLicenseExp(String licenseExp) {
		this.license_exp = licenseExp;
	}

	public Integer getDateType() {
		return date_type;
	}

	public void setDateType(Integer dateType) {
		this.date_type = dateType;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Set<User> getUser() {
		return user;
	}

	public void setUser(Set<User> user) {
		this.user = user;
	}

	public Set<Driver> getDriver() {
		return driver;
	}

	public void setDriver(Set<Driver> driver) {
		this.driver = driver;
	}

	public Set<Geofence> getGeofence() {
		return geofence;
	}

	public void setGeofence(Set<Geofence> geofence) {
		this.geofence = geofence;
	}

	
	@OneToMany(mappedBy="device", cascade = CascadeType.ALL)
	private Set<Event> events;

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	public String getPositionid() {
		return positionid;
	}

	public void setPositionid(String positionid) {
		this.positionid = positionid;
	}

	public String getPlate_num() {
		return plate_num;
	}

	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}

	public String getRight_letter() {
		return right_letter;
	}

	public void setRight_letter(String right_letter) {
		this.right_letter = right_letter;
	}

	public String getMiddle_letter() {
		return middle_letter;
	}

	public void setMiddle_letter(String middle_letter) {
		this.middle_letter = middle_letter;
	}

	public String getLeft_letter() {
		return left_letter;
	}

	public void setLeft_letter(String left_letter) {
		this.left_letter = left_letter;
	}

	public Integer getPlate_type() {
		return plate_type;
	}

	public void setPlate_type(Integer plate_type) {
		this.plate_type = plate_type;
	}

	public String getReference_key() {
		return reference_key;
	}

	public void setReference_key(String reference_key) {
		this.reference_key = reference_key;
	}

	public Integer getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Integer is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getDelete_date() {
		return delete_date;
	}

	public void setDelete_date(String delete_date) {
		this.delete_date = delete_date;
	}

	public Integer getInit_sensor() {
		return init_sensor;
	}

	public void setInit_sensor(Integer init_sensor) {
		this.init_sensor = init_sensor;
	}

	public Integer getInit_sensor2() {
		return init_sensor2;
	}

	public void setInit_sensor2(Integer init_sensor2) {
		this.init_sensor2 = init_sensor2;
	}

	public Integer getCar_weight() {
		return car_weight;
	}

	public void setCar_weight(Integer car_weight) {
		this.car_weight = car_weight;
	}

	public String getReject_reason() {
		return reject_reason;
	}

	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}

	public String getSequence_number() {
		return sequence_number;
	}

	public void setSequence_number(String sequence_number) {
		this.sequence_number = sequence_number;
	}

	public Integer getIs_valid() {
		return is_valid;
	}

	public void setIs_valid(Integer is_valid) {
		this.is_valid = is_valid;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getMade_year() {
		return made_year;
	}

	public void setMade_year(String made_year) {
		this.made_year = made_year;
	}

	public String getLicense_exp() {
		return license_exp;
	}

	public void setLicense_exp(String license_exp) {
		this.license_exp = license_exp;
	}

	public Integer getDate_type() {
		return date_type;
	}

	public void setDate_type(Integer date_type) {
		this.date_type = date_type;
	}
	
	
	public String getSensorSettings() {
		return sensorSettings;
	}

	public void setSensorSettings(String sensorSettings) {
		this.sensorSettings = sensorSettings;
	}


	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "deviceGroup")
    private Set<Group> groups = new HashSet<>();


	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_notification",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "notificationid") }
    )
	private Set<Notification> notificationDevice= new HashSet<>();


	public Set<Notification> getNotificationDevice() {
		return notificationDevice;
	}

	public void setNotificationDevice(Set<Notification> notificationDevice) {
		this.notificationDevice = notificationDevice;
	}
 
 
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_attribute",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "attributeid") }
    )
	private Set<Attribute> attributeDevice= new HashSet<>();


	public Set<Attribute> getAttributeDevice() {
		return attributeDevice;
	}

	public void setAttributeDevice(Set<Attribute> attributeDevice) {
		this.attributeDevice = attributeDevice;
	}

	public Integer getExpired() {
		return expired;
	}

	public void setExpired(Integer expired) {
		this.expired = expired;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	
	
}

