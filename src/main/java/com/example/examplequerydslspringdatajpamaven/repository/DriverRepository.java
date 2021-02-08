package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;

/**
 * Queries on table tc_drivers
 * @author fuinco
 *
 */
@Component
public interface DriverRepository extends JpaRepository<Driver, Long>, QueryDslPredicateExecutor<Driver> {

	@Transactional
    @Modifying
	@Query(value = "Update tc_drivers driver Set driver.delete_date=:date where driver.id=:driverId", nativeQuery = true)
	public void deleteDriver(@Param("driverId") Long driverId,@Param("date") String currentDate);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_driver where tc_device_driver.driverid=:driverId", nativeQuery = true)
	public void deleteDriverDeviceId(@Param("driverId") Long driverId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_driver where tc_group_driver.driverid=:driverId", nativeQuery = true)
	public void deleteDriverGroupId(@Param("driverId") Long driverId);
	
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where tc_drivers.name=:name and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInAddEmail(@Param("userId") Long id,@Param("name") String name);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where (tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId OR tc_drivers.email=:email ) and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInAddUniqueMobile(@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum,@Param("email") String email);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where tc_drivers.name=:name and tc_drivers.id !=:driverId and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInEditEmail(@Param("driverId") Long driverId,@Param("userId") Long userId,@Param("name") String name);
	
	@Query(value = "select * from tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " where (tc_drivers.mobile_num=:mobileNum OR tc_drivers.uniqueid=:uniqueId OR tc_drivers.email=:email ) and tc_drivers.id !=:driverId and tc_drivers.delete_date IS NULL", nativeQuery = true)
	public List<Driver> checkDublicateDriverInEditUniqueMobile(@Param("driverId") Long driverId,@Param("uniqueId") String uniqueId,@Param("mobileNum") String mobileNum,@Param("email") String email);
	
	
	@Query(value = "SELECT tc_drivers.* FROM tc_drivers INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null"
			+ " and ((tc_drivers.name Like %:search%) OR (tc_drivers.uniqueid Like %:search%) OR (tc_drivers.mobile_num Like %:search%) OR (tc_drivers.birth_date Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Driver> getAllDrivers(@Param("userIds") List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	
	@Query(nativeQuery = true, name = "getDriverList")
	public List<CustomDriverList> getAllDriversCustom(@Param("userIds") List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListExport")
	public List<CustomDriverList> getAllDriversCustomExport(@Param("userIds") List<Long> userIds,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListByIds")
	public List<CustomDriverList> getAllDriversCustomByIds(@Param("driverIds") List<Long> driverIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListByIdsExport")
	public List<CustomDriverList> getAllDriversCustomByIdsExport(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	
	@Query(value = "SELECT  * FROM tc_drivers as A " + 
			" INNER JOIN tc_user_driver ON tc_user_driver.driverid =A.id " + 
			" WHERE tc_user_driver.userid IN (:userIds) AND delete_date IS NULL " + 
			" And Not EXISTS " + 
			" (SELECT *  FROM tc_drivers as B " + 
			" INNER JOIN tc_user_driver ON tc_user_driver.driverid =B.id " + 
			" INNER JOIN tc_device_driver ON tc_device_driver.driverid =B.id " + 
			" WHERE A.id=B.id AND tc_user_driver.userid IN (:userIds) AND delete_date IS NULL )", nativeQuery = true)
	public List<Driver> getUnassignedDrivers(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT  * FROM tc_drivers as A " + 
			" Inner join tc_user_client_driver on tc_user_client_driver.driverid = A.id " + 
			" where tc_user_client_driver.userid IN (:userIds) and A.delete_date is null " + 
			" And Not EXISTS (select * from tc_drivers as B " + 
			" Inner join tc_device_driver on tc_device_driver.driverid = B.id " + 
			" where A.id=B.id AND B.delete_date IS NULL )", nativeQuery = true)
	public List<Driver> getUnassignedDriversByIds(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT count(tc_drivers.id) FROM tc_drivers INNER JOIN tc_user_driver "
			+ "ON tc_user_driver.driverid = tc_drivers.id AND "
			+ "tc_user_driver.userid IN (:userIds) WHERE tc_drivers.delete_date is null",nativeQuery = true)
	
	public Integer getTotalNumberOfUserDrivers(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null" 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSize(@Param("userIds") List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is null" 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSizeByIds(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_drivers.id,tc_drivers.name FROM tc_drivers"
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getDriverSelect(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT tc_drivers.id,tc_drivers.name FROM tc_drivers"
			+ " WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getDriverSelectByIds(@Param("driverIds") List<Long> driverIds);
	
	@Query(value = "SELECT tc_drivers.id,tc_drivers.name FROM tc_drivers"
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id"
			+ " WHERE tc_user_driver.userid IN(:loggedUserId) and tc_drivers.delete_date is null "
			+ " and tc_drivers.id Not IN(Select tc_user_client_driver.driverid from tc_user_client_driver where tc_user_client_driver.userid !=:userId) ",nativeQuery = true)
	public List<DriverSelect> getDriverUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId")Long userId);
	
	@Query(value = "Select tc_device_driver.deviceid from tc_device_driver " + 
			" INNER JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id " + 
			" where tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is null ",nativeQuery = true)
	public List<Long> devicesOfDrivers(@Param("driverIds") List<Long> driverIds);
	
	
	@Query(value = "select * from tc_drivers " + 
			"	inner join tc_device_driver on tc_device_driver.driverid = tc_drivers.id " + 
			"	where deviceid=:deviceId",nativeQuery = true)
	public Driver driverOfDevice(@Param("deviceId") Long deviceId);
	
	@Query(value = "select tc_drivers.id from tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id "
			+ " where tc_drivers.name=:name and tc_drivers.uniqueid=:uniqueid  "
			+ " and tc_user_driver.userid=:userId and tc_drivers.delete_date IS NULL order by tc_drivers.id DESC limit 0,1 ", nativeQuery = true)
	public Long getDriverIdByName(@Param("userId") Long id,@Param("name") String name,@Param("uniqueid") String uniqueid);
}
