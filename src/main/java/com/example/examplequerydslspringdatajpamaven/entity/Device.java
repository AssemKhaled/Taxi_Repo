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
	        name="DevicesList",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	                     @ColumnResult(name="id"),
	                     @ColumnResult(name="deviceName"),
	                     @ColumnResult(name="uniqueId"),
	                     @ColumnResult(name="sequenceNumber"),
	                     @ColumnResult(name="referenceKey"),
	                     @ColumnResult(name="driverName"),
	                     @ColumnResult(name="geofenceName"),
	                     @ColumnResult(name="lastUpdate")
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
	                     @ColumnResult(name="positionId",type=Long.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="deviceId",type=Long.class),
	                     @ColumnResult(name="deviceName",type=String.class)
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
	                     @ColumnResult(name="lastUpdate"),
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
	                     @ColumnResult(name="lastUpdate",type=Date.class),
	                     @ColumnResult(name="address",type=String.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="latitude",type=Double.class),
	                     @ColumnResult(name="longitude",type=Double.class),
	                     @ColumnResult(name="speed",type=Float.class),
	                     @ColumnResult(name="positionId",type=Integer.class),
	                     @ColumnResult(name="leftLetter",type=String.class),
	                     @ColumnResult(name="middleLetter",type=String.class),
	                     @ColumnResult(name="rightLetter",type=String.class),
	                     @ColumnResult(name="driverName",type=String.class)
	                     
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
	                     @ColumnResult(name="id"),
	                     @ColumnResult(name="deviceName"),
	                     @ColumnResult(name="lastUpdate"),
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
	),
	@SqlResultSetMapping(
	        name="vehicleInfoData",
	        classes={
	           @ConstructorResult(
	                targetClass=CustomDeviceList.class,
	                  columns={
	 	                 @ColumnResult(name="id",type=int.class),
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
	                     @ColumnResult(name="carWeight",type=String.class)	                     
	             	
	                     }
	           )
	        }
	)

})

@NamedNativeQueries({
	
@NamedNativeQuery(name="getDevicesList", 
     resultSetMapping="DevicesList", 
     query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName, tc_devices.uniqueid as uniqueId,"
     		+ " tc_devices.sequence_number as sequenceNumber ,tc_devices.lastupdate as lastUpdate "
     		+ " ,tc_devices.reference_key as referenceKey, "
     		+ " tc_drivers.name as driverName ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
     		+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
     		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null"
     		+ " AND (tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%'))"
     		+ " OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
     		+ "GROUP BY tc_devices.id,tc_drivers.id LIMIT :offset,10"),

@NamedNativeQuery(name="getDevicesLiveData", 
	resultSetMapping="DevicesLiveData", 
	query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate, "
			+ " tc_positions.address , tc_positions.attributes ,tc_positions.latitude , tc_positions.longitude, "
			+ " tc_positions.speed,tc_devices.photo , tc_positions.id as positionId  FROM tc_devices "
			+ " INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " 
			+ " LEFT JOIN tc_positions ON tc_positions.id=tc_devices.positionid"
			+ "  where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null "
			+ "  AND ((tc_devices.name LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))) "
			+ " OR (tc_positions.address LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_positions.latitude LIKE LOWER(CONCAT('%',:search, '%'))) "
			+ " OR (tc_positions.longitude LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_positions.speed LIKE LOWER(CONCAT('%',:search, '%'))))"
			+ " GROUP BY tc_devices.id LIMIT :offset,10"),

@NamedNativeQuery(name="getDevicesLiveDataMap", 
resultSetMapping="DevicesLiveDataMap", 
query="SELECT tc_devices.id as id ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate, " + 
		" tc_positions.address , tc_positions.attributes ,tc_positions.latitude , tc_positions.longitude, " + 
		" tc_positions.speed, tc_positions.id as positionId ,tc_devices.left_letter as leftLetter , " + 
		" tc_devices.middle_letter as middleLetter,tc_devices.right_letter as rightLetter ,tc_drivers.name driverName " + 
		" FROM tc_devices " + 
		" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid" + 
		" LEFT JOIN tc_positions ON tc_positions.id=tc_devices.positionid " + 
		" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
		" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
		" where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null  GROUP BY tc_devices.id,tc_drivers.id"),


@NamedNativeQuery(name="getDeviceLiveData", 

resultSetMapping="DevicesLiveData", 
query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName , tc_devices.lastupdate as lastUpdate, "
		+ " tc_positions.address , tc_positions.attributes ,tc_positions.latitude , tc_positions.longitude, "
		+ " tc_positions.speed,tc_devices.photo , tc_positions.id as positionId  FROM tc_devices "
		+ " LEFT JOIN tc_positions ON tc_positions.id=tc_devices.positionid"
		+ " where tc_devices.id= :deviceId and tc_devices.delete_date is null "),
@NamedNativeQuery(name="getDeviceWorkingHours", 

resultSetMapping="DeviceWorkingHours", 
query="SELECT CAST(devicetime AS DATE) as deviceTime ,tc_positions.id as positionId,tc_positions.attributes as attributes," + 
		" deviceid as deviceId,tc_devices.name as deviceName FROM tc_positions " + 
		" INNER JOIN tc_devices ON tc_devices.id=tc_positions.deviceid " + 
		" WHERE deviceid=:deviceId AND " + 
		"  ((devicetime Like :search) or (tc_devices.name Like :search) ) "
		+ " And devicetime IN (SELECT devicetime " + 
		" FROM (SELECT MAX(devicetime) as devicetime FROM tc_positions " + 
		" WHERE deviceid=:deviceId AND devicetime<=:end AND  devicetime>=:start group by CAST(devicetime AS DATE) )as t1) "
		+ "order by devicetime DESC limit :offset,10"),

@NamedNativeQuery(name="getDeviceWorkingHoursExport", 
resultSetMapping="DeviceWorkingHours", 
query="SELECT CAST(devicetime AS DATE) as deviceTime ,tc_positions.id as positionId,tc_positions.attributes as attributes," + 
		" deviceid as deviceId,tc_devices.name as deviceName FROM tc_positions " + 
		" INNER JOIN tc_devices ON tc_devices.id=tc_positions.deviceid " + 
		" WHERE deviceid=:deviceId AND " + 
		" devicetime IN (SELECT devicetime " + 
		" FROM (SELECT MAX(devicetime) as devicetime FROM tc_positions " + 
		" WHERE deviceid=:deviceId AND devicetime<=:end AND  devicetime>=:start group by CAST(devicetime AS DATE) )as t1) order by devicetime DESC"),

@NamedNativeQuery(name="vehicleInfo", 
resultSetMapping="vehicleInfoData", 
query=" SELECT tc_drivers.id as driverId,tc_drivers.uniqueid as driverUniqueId,tc_drivers.name as driverName,tc_drivers.photo as driverPhoto,"
		+ " tc_devices.id as id,tc_devices.uniqueid as uniqueId,tc_devices.sequence_number as sequenceNumber,"
		+ " tc_devices.owner_name as ownerName,tc_devices.owner_id as ownerId, "
		+ " tc_devices.username as userName,tc_devices.model as model ,"
		+ " tc_devices.brand as brand,tc_devices.made_year as madeYear,"
		+ " tc_devices.color as color,tc_devices.car_weight as carWeight,"
		+ " tc_devices.license_exp as licenceExptDate,"
		+ " CONCAT_WS(' ',tc_devices.plate_num,tc_devices.right_letter,tc_devices.middle_letter,tc_devices.left_letter) as vehiclePlate,"
		+ " tc_devices.plate_type as plateType"
		+ " FROM tc_devices "
		+ " LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id "
		+ " LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid "
		+ " WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL")


})

@Entity
@Table(name = "tc_devices" , schema = "sareb_blue")
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
	private Integer positionid;
	
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
	
	@Column(name = "calibrationData",length=1024)
	private String calibrationData;
	
	@Column(name = "lineData")
	private String lineData;
	
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

	public Integer getPositionid() {
		return positionid;
	}

	public void setPositionid(Integer positionid) {
		this.positionid = positionid;
	}
	
 
 
	
}

