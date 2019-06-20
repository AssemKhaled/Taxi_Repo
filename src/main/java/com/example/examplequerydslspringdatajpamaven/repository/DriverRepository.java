package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long>, QueryDslPredicateExecutor<Driver> {

	@Transactional
    @Modifying
	@Query(value = "Update tc_drivers driver Set driver.delete_date=:date where driver.id=:driverId", nativeQuery = true)
	public void deleteDriver(@Param("driverId") Long driverId,@Param("date") String currentDate);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_driver where tc_user_driver.driverid=:driverId", nativeQuery = true)
	public void deleteDriverId(@Param("driverId") Long driverId);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where ( tc_drivers.name=:name OR tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId) and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInAdd(@Param("userId") Long id,@Param("name") String name,@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where ( tc_drivers.name=:name OR tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId) and tc_drivers.id !=:driverId and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInEdit(@Param("driverId") Long driverId,@Param("userId") Long userId,@Param("name") String name,@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum);
	
	
}
