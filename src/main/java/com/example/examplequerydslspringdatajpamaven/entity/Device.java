package com.example.examplequerydslspringdatajpamaven.entity;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tc_devices" , schema = "sareb_blue")
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
	
	/*@Column(name = "positionid")
	private Integer positionId;
	
	@Column(name = "groupid")
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
	private String referenceKey;
	
	//should be isDeleted
	@Column(name = "is_deleted")
	private Integer is_deleted;
	
	@Column(name = "delete_date")
//	@CsvBindByName
//	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
//	@Temporal(TemporalType.TIMESTAMP)
	private String deleteDate;
	
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
	private String rejectReason;
	
	@Column(name = "sequence_number")
	private String sequence_number;
	
	@Column(name = "is_valid")
	private Integer is_valid;
	
	@Column(name = "calibrationData")
	private String calibrationData;
	
	@Column(name = "lineData")
	private String lineData;
	
	@Column(name = "last_weight")
	private Integer lastWeight;
	
	@Column(name = "owner_name")
	private String ownerName;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "owner_id")
	private String ownerId;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "made_year")
	private String madeYear;
	
	@Column(name = "color")
	private String color;
	
	@Column(name = "license_exp")
	private Date licenseExp;
	
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

	/*public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public Integer getGroupId() {
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
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public Integer getIsDeleted() {
		return is_deleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.is_deleted = isDeleted;
	}

	public String getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(String deleteDate) {
		this.deleteDate = deleteDate;
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
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
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
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getMadeYear() {
		return madeYear;
	}

	public void setMadeYear(String madeYear) {
		this.madeYear = madeYear;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Date getLicenseExp() {
		return licenseExp;
	}

	public void setLicenseExp(Date licenseExp) {
		this.licenseExp = licenseExp;
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
	
	@OneToMany(mappedBy="device", cascade = CascadeType.ALL,
	        orphanRemoval = true)
	private Set<Event> events;

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}
	
 
 
	
}

