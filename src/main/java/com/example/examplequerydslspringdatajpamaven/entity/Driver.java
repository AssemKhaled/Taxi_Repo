package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "tc_drivers" , schema = "sareb_blue")
public class Driver {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "uniqueid")
	private String uniqueid;

	@Column(name = "attributes")
	private String attributes;
	
	@Column(name = "mobile_num")
	private String mobile_num;
	
	@Column(name = "birth_date")
	private String birth_date;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "reference_key")
	private String reference_key;
	
	@Column(name = "is_deleted")
	private Integer is_deleted=null;
	
	@Column(name = "delete_date")
	private String delete_date;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "date_type")
	private Integer date_type=null;
	
	@Column(name = "is_valid")
	private Integer is_valid=null;
	
	@Column(name = "photo")
	private String photo;
	
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "driver"
    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Device> device = new HashSet<>();
	
	
	public Driver() {
		
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

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getMobile_num() {
		return mobile_num;
	}

	public void setMobile_num(String mobile_num) {
		this.mobile_num = mobile_num;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getReject_reason() {
		return reject_reason;
	}

	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}

	public Integer getDate_type() {
		return date_type;
	}

	public void setDate_type(Integer date_type) {
		this.date_type = date_type;
	}

	public Integer getIs_valid() {
		return is_valid;
	}

	public void setIs_valid(Integer is_valid) {
		this.is_valid = is_valid;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@JsonIgnore 
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tc_user_driver",joinColumns = { @JoinColumn(name = "driverid") },
    inverseJoinColumns = { @JoinColumn(name = "userid") })
    private Set<User> userDriver = new HashSet<>();
    
	public Set<User> getUserDriver() {
		return userDriver;
	}

	public void setUserDriver(Set<User> userDriver) {
		this.userDriver = userDriver;
	}

	public Set<Device> getDevice() {
		return device;
	}

	public void setDevice(Set<Device> device) {
		this.device = device;
	}
	
	
	

}
