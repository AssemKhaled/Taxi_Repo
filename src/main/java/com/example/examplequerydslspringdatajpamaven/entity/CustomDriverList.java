package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Initial Model to bind data with query
 * @author fuinco
 *
 */
public class CustomDriverList {

	private Long id;
	private String name;
	private String uniqueid;
	private String attributes;
	private String mobile_num;
	private String birth_date;
	private String email;
	private String reference_key;
	private String is_deleted;
	private String delete_date;
	private String reject_reason;
	private String date_type;
	private String is_valid;
	private String photo;
	private String companyName;
	private String create_date_elm;
	private String update_date_elm;
	private String delete_date_elm;
	
	
	
	public String getCreate_date_elm() {
		return create_date_elm;
	}
	public void setCreate_date_elm(String create_date_elm) {
		this.create_date_elm = create_date_elm;
	}
	public String getUpdate_date_elm() {
		return update_date_elm;
	}
	public void setUpdate_date_elm(String update_date_elm) {
		this.update_date_elm = update_date_elm;
	}
	public String getDelete_date_elm() {
		return delete_date_elm;
	}
	public void setDelete_date_elm(String delete_date_elm) {
		this.delete_date_elm = delete_date_elm;
	}
	public CustomDriverList(Long id, String name, String uniqueid, String attributes, String mobile_num,
			String birth_date, String email, String reference_key, String is_deleted, String delete_date,
			String reject_reason, String date_type, String is_valid, String photo, String companyName,
			String create_date_elm,String delete_date_elm,String update_date_elm ) {
		this.id = id;
		this.name = name;
		this.uniqueid = uniqueid;
		this.attributes = attributes;
		this.mobile_num = mobile_num;
		this.birth_date = birth_date;
		this.email = email;
		this.reference_key = reference_key;
		this.is_deleted = is_deleted;
		this.delete_date = delete_date;
		this.reject_reason = reject_reason;
		this.date_type = date_type;
		this.is_valid = is_valid;
		this.photo = photo;
		this.companyName = companyName;
		this.create_date_elm = create_date_elm;
		this.delete_date_elm = delete_date_elm;
		this.update_date_elm = update_date_elm;

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
	public String getIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(String is_deleted) {
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
	public String getDate_type() {
		return date_type;
	}
	public void setDate_type(String date_type) {
		this.date_type = date_type;
	}
	public String getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	
	
}
