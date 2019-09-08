package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
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
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_driver where tc_device_driver.driverid=:driverId", nativeQuery = true)
	public void deleteDriverDeviceId(@Param("driverId") Long driverId);
	
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where tc_drivers.name=:name and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInAddName(@Param("userId") Long id,@Param("name") String name);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where (tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId) and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInAddUniqueMobile(@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where tc_drivers.name=:name and tc_drivers.id !=:driverId and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInEditName(@Param("driverId") Long driverId,@Param("userId") Long userId,@Param("name") String name);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where (tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId) and tc_drivers.id !=:driverId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInEditUniqueMobile(@Param("driverId") Long driverId,@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum);
	
	
	@Query(value = "SELECT tc_drivers.* FROM tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null"
			+ " and ((tc_drivers.name Like %:search%) OR (tc_drivers.uniqueid Like %:search%) OR (tc_drivers.mobile_num Like %:search%) OR (tc_drivers.birth_date Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Driver> getAllDrivers(@Param("userIds") List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
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
	
	@Query(value = "SELECT count(tc_drivers.id) FROM tc_drivers INNER JOIN tc_user_driver "
			+ "ON tc_user_driver.driverid = tc_drivers.id AND "
			+ "tc_user_driver.userid IN (:userIds) WHERE tc_drivers.delete_date is null",nativeQuery = true)
	
	public Integer getTotalNumberOfUserDrivers(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT count(*) FROM tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null", nativeQuery = true)
	public Integer getAllDriversSize(@Param("userIds") List<Long> userIds);
	
	
	@Query(nativeQuery = true, name = "getDriverWorkingHoursExport")
	public List<DriverWorkingHours> getDriverWorkingHoursExport(@Param("driverId")Long driverId,@Param("start")String start,@Param("end")String end);
	
	@Query(nativeQuery = true, name = "getDriverWorkingHours")
	public List<DriverWorkingHours> getDriverWorkingHours(@Param("driverId")Long driverId,@Param("start")String start,@Param("end")String end,@Param("offset")int offset,@Param("search")String search);

	@Query(value = "SELECT count(CAST(devicetime AS DATE)) "
			+ " FROM tc_positions INNER JOIN tc_device_driver ON tc_device_driver.deviceid=tc_positions.deviceid  "
			+ " INNER JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id"
			+ "  WHERE tc_positions.deviceid=(SELECT tc_device_driver.deviceid FROM tc_drivers INNER JOIN tc_device_driver ON tc_device_driver.driverid=tc_drivers.id WHERE tc_drivers.id=:driverId) AND  devicetime IN (SELECT devicetime "
			+ "  FROM (SELECT MAX(devicetime) as devicetime FROM tc_positions "
			+ "  WHERE deviceid=(SELECT tc_device_driver.deviceid FROM tc_drivers "
			+ "  INNER JOIN tc_device_driver ON tc_device_driver.driverid=tc_drivers.id "
			+ "  WHERE tc_drivers.id=:driverId) AND devicetime<=:end AND  devicetime>=:start "
			+ "  group by CAST(devicetime AS DATE) )as t1) order by devicetime DESC",nativeQuery = true )
	
	public Integer getDriverWorkingHoursSize(@Param("driverId")Long driverId,@Param("start")String start,@Param("end")String end);

	@Query(value = "SELECT tc_drivers.id,tc_drivers.name FROM tc_drivers"
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid=:userId and tc_drivers.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getDriverSelect(@Param("userId")Long userId);
}
