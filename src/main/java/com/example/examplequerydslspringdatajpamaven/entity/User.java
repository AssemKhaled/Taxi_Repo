package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name ="tc_users",schema="sareb_blue")
public class User {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
//	@JsonIgnore
	@Column (name = "hashedpassword")
	private String password;
	
//	@JsonProperty
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "commercial_num")
	private String commercial_num;
	
	@Column(name = "identity_num")
	private String identity_num;
	
	@Column(name = "company_num")
	private String company_num;
	
	@Column(name = "manager_name")
	private String manager_name;
	
	@Column(name = "manager_phone")
	private String manager_phone;
	
	@Column(name = "manager_mobile")
	private String manager_mobile;
	
	@Column(name = "commercial_reg")
	private String commercial_reg;
	
	@Column(name = "reference_key")
	private String reference_key;
	
	@Column(name = "is_deleted")
	private Integer is_deleted;
	
	@Column(name = "company_phone")
	private String company_phone;
	
	@Column(name = "delete_date")
	private String delete_date;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "Iscompany")
	private Integer IsCompany;
	
	 @JsonIgnore
	 @ManyToMany(
	            fetch = FetchType.LAZY,
	            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	            mappedBy = "user"
	    )
//	    @OnDelete(action = OnDeleteAction.CASCADE)
	    private Set<Device> devices = new HashSet<>();
	    @JsonIgnore
		@ManyToMany(
	            fetch = FetchType.LAZY,
	            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	    )
	    @JoinTable(
	            name = "tc_user_user",
	            joinColumns = { @JoinColumn(name = "userid") },
	            inverseJoinColumns = { @JoinColumn(name = "manageduserid") }
	    )
//	    @OnDelete(action = OnDeleteAction.CASCADE)
		private Set<User> usersOfUser = new HashSet<>();
	    
	    @JsonIgnore
	    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	            mappedBy = "userDriver")
	    private Set<Driver> drivers = new HashSet<>();
	    
	    @JsonIgnore
	    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	            mappedBy = "userGeofence")
	    private Set<Geofence> geofences = new HashSet<>();

	/*@Column(name="active")
	@CsvBindByName
	private Boolean active;
	@Enumerated(EnumType.STRING)
	private UsersTypes type;*/
	public User() {
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	@JsonProperty(value = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Integer getIsCompany() {
		return IsCompany;
	}

	public void setIsCompany(Integer isCompany) {
		this.IsCompany = isCompany;
	}

    public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	

	/*public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public UsersTypes getType() {
		return type;
	}

	public void setType(UsersTypes type) {
		this.type = type;
	}*/

	

	public Set<Driver> getDrivers() {
		return drivers;
	}
	public void setDrivers(Set<Driver> drivers) {
		this.drivers = drivers;
	}

	
	


	public Set<Geofence> getGeofences() {
		return geofences;
	}
	public void setGeofences(Set<Geofence> geofences) {
		this.geofences = geofences;
	}
	
	


	public Set<User> getUsersOfUser() {
		return usersOfUser;
	}
	public void setUsersOfUser(Set<User> usersOfUser) {
		this.usersOfUser = usersOfUser;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCommercial_num() {
		return commercial_num;
	}
	public void setCommercial_num(String commercial_num) {
		this.commercial_num = commercial_num;
	}
	public String getIdentity_num() {
		return identity_num;
	}
	public void setIdentity_num(String identity_num) {
		this.identity_num = identity_num;
	}
	public String getCompany_num() {
		return company_num;
	}
	public void setCompany_num(String company_num) {
		this.company_num = company_num;
	}
	public String getManager_name() {
		return manager_name;
	}
	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}
	public String getManager_phone() {
		return manager_phone;
	}
	public void setManager_phone(String manager_phone) {
		this.manager_phone = manager_phone;
	}
	public String getManager_mobile() {
		return manager_mobile;
	}
	public void setManager_mobile(String manager_mobile) {
		this.manager_mobile = manager_mobile;
	}
	public String getCommercial_reg() {
		return commercial_reg;
	}
	public void setCommercial_reg(String commercial_reg) {
		this.commercial_reg = commercial_reg;
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
	public String getCompany_phone() {
		return company_phone;
	}
	public void setCompany_phone(String company_phone) {
		this.company_phone = company_phone;
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


	
	



}
