package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;;

public interface ComputedRepository  extends  JpaRepository<Attribute, Long>, QueryDslPredicateExecutor<Attribute>{

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is null"
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputed(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id " + 
			"  WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is null", nativeQuery = true)
	public Integer getAllComputedSize(@Param("userIds")List<Long> userIds);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_attribute where tc_user_attribute.attributeid=:attributeId", nativeQuery = true)
	public void deleteAttributeId(@Param("attributeId") Long attributeId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_attribute where tc_device_attribute.attributeid=:attributeId", nativeQuery = true)
	public void deleteAttributeDeviceId(@Param("attributeId") Long attributeId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_attribute where tc_group_attribute.attributeid=:attributeId", nativeQuery = true)
	public void deleteAttributeGroupId(@Param("attributeId") Long attributeId);
	
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.attribute FROM tc_attributes"
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and tc_attributes.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getComputedSelect(@Param("userIds") List<Long> userIds);
	
}
