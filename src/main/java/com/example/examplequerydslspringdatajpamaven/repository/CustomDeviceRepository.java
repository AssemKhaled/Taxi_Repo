package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceProjection;
import com.example.examplequerydslspringdatajpamaven.entity.Device;

@Component
public interface CustomDeviceRepository extends  JpaRepository<Device,Long>{

	@Query(value = " select  d.id ,d.name  as name,d.uniqueid as uniqueid ,d.sequence_number as sequence_number,"
			+ "d.lastupdate as  lastupdate from tc_devices d inner join tc_user_device on tc_user_device.deviceid = d.id "
			+ " where tc_user_device.userid = :userId and  d.delete_date is NULL  limit :offset,10", nativeQuery = true)
	public List<CustomDeviceProjection> getUserDevices(@Param("userId")Long userId,@Param("offset") int offset);
//	@SqlResultSetMapping(
//		    name="studentPercentile",
//		    entities={
//		        @EntityResult(
//		           entityClass=CustomStudent.class,
//		              fields={
//		                  @FieldResult(name="id", column="ID"),
//		                  @FieldResult(name="firstName", column="FIRST_NAME"),
//		                   @FieldResult(name="lastName", column="LAST_NAME")
//		              }         
//		        )
//		   }
//		) 
//	public List<CustomDeviceProjection> getUserDevices(@Param("userId")Long userId,@Param("offset") int offset);
}
