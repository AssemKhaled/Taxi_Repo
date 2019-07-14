package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
@Component
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
	
	@Query(value = "SELECT tc_drivers.* FROM tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid=:userId and tc_drivers.delete_date is null"
			+ " and ((tc_drivers.name Like %:search%) OR (tc_drivers.uniqueid Like %:search%) OR (tc_drivers.mobile_num Like %:search%) OR (tc_drivers.birth_date Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Driver> getAllDrivers(@Param("userId") Long userId,@Param("offset") int offset,@Param("search") String search);
	
	//added by maryam
	@Query(value = "SELECT  * FROM tc_drivers A " + 
			" INNER JOIN tc_user_driver ON tc_user_driver.driverid =A.id " + 
			" WHERE tc_user_driver.userid= :userId AND delete_date IS NULL " + 
			" And Not EXISTS " + 
			" (SELECT *  FROM tc_drivers B " + 
			" INNER JOIN tc_user_driver ON tc_user_driver.driverid =B.id " + 
			" INNER JOIN tc_device_driver ON tc_device_driver.driverid =B.id " + 
			" WHERE A.id=B.id AND tc_user_driver.userid= :userId AND delete_date IS NULL )", nativeQuery = true)
	public List<Driver> getUnassignedDrivers(@Param("userId") Long userId);
}
