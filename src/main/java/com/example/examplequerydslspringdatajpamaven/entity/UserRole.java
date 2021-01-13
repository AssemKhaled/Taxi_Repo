package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of table tc_user_roles in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_roles")
public class UserRole extends Attributes{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "roleId")
	private Long roleId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "permissions" , length = 100000)
	private String permissions ;
	
	@Column(name = "delete_date")
	private String delete_date;
	
	@Column(name = "userId")
	private Long userId;
	

	public Long getId() {
		return roleId;
	}

	public void setId(Long id) {
		this.roleId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public String getDelete_date() {
		return delete_date;
	}

	public void setDelete_date(String delete_date) {
		this.delete_date = delete_date;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

	

}
