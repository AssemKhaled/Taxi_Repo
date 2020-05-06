package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.Group;

public interface GroupRepository extends  JpaRepository<Group, Long>, QueryDslPredicateExecutor<Group> {
	
	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Group> getAllGroups(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is null", nativeQuery = true)
	public Integer getAllGroupsSize(@Param("userIds")List<Long> userIds);
	
	@Transactional
    @Modifying
	@Query(value = "Update tc_groups Set tc_groups.is_deleted=1 where tc_groups.id=:groupId", nativeQuery = true)
	public void deleteGroup(@Param("groupId") Long groupId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_group where tc_user_group.groupid=:groupId", nativeQuery = true)
	public void deleteGroupId(@Param("groupId") Long groupId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_driver where tc_group_driver.groupid=:groupId", nativeQuery = true)
	public void deleteGroupdriverId(@Param("groupId") Long groupId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_geofence where tc_group_geofence.groupid=:groupId", nativeQuery = true)
	public void deleteGroupgeoId(@Param("groupId") Long groupId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_device where tc_group_device.groupid=:groupId", nativeQuery = true)
	public void deleteGroupDeviceId(@Param("groupId") Long groupId);
	
	
	@Query(value = "select * from tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " where tc_groups.name=:name and tc_user_group.userid=:userId and tc_groups.is_deleted IS NULL", nativeQuery = true)
	public List<Group> checkDublicateGroupInAdd(@Param("userId") Long id,@Param("name") String name);
	
	@Query(value = "select * from tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " where tc_groups.name=:name and tc_groups.id !=:groupId and tc_user_group.userid=:userId and tc_groups.is_deleted IS NULL", nativeQuery = true)
	public List<Group> checkDublicateGroupInEdit(@Param("groupId") Long groupId,@Param("userId") Long userId,@Param("name") String name);
	
	@Query(value = "select tc_group_device.deviceid from tc_group_device where tc_group_device.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDevicesFromGroup(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_device_driver.deviceid from tc_device_driver " + 
			" inner join tc_group_driver on tc_group_driver.driverid = tc_device_driver.driverid " + 
			" where tc_group_driver.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDevicesFromDriver(@Param("groupId") Long groupId);
	
	
	@Query(value = "select tc_device_geofence.deviceid from tc_device_geofence " + 
			" inner join tc_group_geofence on tc_group_geofence.geofenceid = tc_device_geofence.geofenceid " + 
			" where tc_group_geofence.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDevicesFromGeofence(@Param("groupId") Long groupId);
	
	
	@Query(value = "select tc_group_driver.driverid from tc_group_driver where tc_group_driver.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDriversFromGroup(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_device_driver.driverid from tc_device_driver " + 
			" inner join tc_group_device on tc_group_device.deviceid = tc_device_driver.deviceid " + 
			" where tc_group_device.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDriverFromDevices(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_device_driver.driverid from tc_device_driver " + 
			" inner join tc_group_device on tc_group_device.deviceid = tc_device_driver.deviceid " + 
			" inner join tc_device_geofence on tc_device_geofence.deviceid =  tc_group_device.deviceid " + 
			" inner join tc_group_geofence on tc_group_geofence.geofenceid =  tc_device_geofence.geofenceid " + 
			" where tc_group_geofence.groupid=:groupId ", nativeQuery = true)
	public List<Long> getDriversFromGeofence(@Param("groupId") Long groupId);
}
