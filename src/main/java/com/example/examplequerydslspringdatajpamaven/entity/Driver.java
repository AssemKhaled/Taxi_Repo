package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SqlResultSetMappings({
	
	@SqlResultSetMapping(
        name="DriverList",
        classes={
           @ConstructorResult(
                targetClass=CustomDriverList.class,
                  columns={
                     @ColumnResult(name="id",type=Long.class),
                     @ColumnResult(name="name",type=String.class),
                     @ColumnResult(name="uniqueid",type=String.class),
                     @ColumnResult(name="attributes",type=String.class),
                     @ColumnResult(name="mobile_num",type=String.class),
                     @ColumnResult(name="birth_date",type=String.class),
                     @ColumnResult(name="email",type=String.class),
                     @ColumnResult(name="reference_key",type=String.class),
                     @ColumnResult(name="is_deleted",type=String.class),
                     @ColumnResult(name="delete_date",type=String.class),
                     @ColumnResult(name="reject_reason",type=String.class),
                     @ColumnResult(name="date_type",type=String.class),
                     @ColumnResult(name="is_valid",type=String.class),
                     @ColumnResult(name="photo",type=String.class),
                     @ColumnResult(name="companyName",type=String.class)
                     }
           )
        }
	)
	
	
})
@NamedNativeQueries({
	
	@NamedNativeQuery(name="getDriverList", 
			resultSetMapping="DriverList", 
			query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
					+ " tc_drivers.attributes as attributes," + 
					" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
					+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
					" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
					+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
					" tc_drivers.photo as photo,tc_users.name as companyName  "
					+ " FROM tc_drivers "
					+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
					" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
					" WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null " 
					+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
					+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
					" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListExport", 
	resultSetMapping="DriverList", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " ),
	
	@NamedNativeQuery(name="getDriverListByIds", 
	resultSetMapping="DriverList", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is null " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
			" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListByIdsExport", 
	resultSetMapping="DriverList", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is null " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " )

	
})

/**
 * 
 * Model of table tc_drivers in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_drivers")
@JsonIgnoreProperties(value = { "device" })
public class Driver extends Attributes{
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "uniqueid")
	private String uniqueid;
	
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
	private String delete_date=null;
	
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
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "driverGroup")
    private Set<Group> groups = new HashSet<>();


	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	 
	 
	

}
