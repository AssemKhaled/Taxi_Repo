package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Group;

/**
 * Queries related to table tc_groups
 * @author fuinco
 *
 */
@Component
public interface GroupRepository extends  JpaRepository<Group, Long>, QueryDslPredicateExecutor<Group> {
	
	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Group> getAllGroups(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public List<Group> getAllGroupsExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	
	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds)and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Group> getAllGroupsByIds(@Param("groupIds")List<Long> groupIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds) and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public List<Group> getAllGroupsByIdsExport(@Param("groupIds")List<Long> groupIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSize(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds)and tc_groups.is_deleted is null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSizeByIds(@Param("groupIds")List<Long> groupIds,@Param("search") String search);
	
	@Transactional
    @Modifying
	@Query(value = "Update tc_groups Set tc_groups.is_deleted=1 where tc_groups.id=:groupId", nativeQuery = true)
	public void deleteGroup(@Param("groupId") Long groupId);
	
	
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
	
	@Query(value = "select tc_group_geofence.geofenceid from tc_group_geofence where tc_group_geofence.groupid=:groupId ", nativeQuery = true)
	public List<Long> getGeofneceFromGroup(@Param("groupId") Long groupId);
	
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
	
	@Query(value = "SELECT tc_devices.id,tc_devices.name FROM tc_devices " + 
			" INNER JOIN tc_group_device ON tc_group_device.deviceid = tc_devices.id " + 
			" WHERE tc_group_device.groupid =:groupId and tc_devices.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getGroupDevicesSelect(@Param("groupId") Long groupId);
	
	@Query(value = "SELECT tc_drivers.id,tc_drivers.name FROM tc_drivers " + 
			" INNER JOIN tc_group_driver ON tc_group_driver.driverid = tc_drivers.id " + 
			" WHERE tc_group_driver.groupid =:groupId and tc_drivers.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getGroupDriverSelect(@Param("groupId") Long groupId);
	
	@Query(value = "SELECT tc_geofences.id,tc_geofences.name FROM tc_geofences " + 
			" INNER JOIN tc_group_geofence ON tc_group_geofence.geofenceid = tc_geofences.id " + 
			" WHERE tc_group_geofence.groupid =:groupId and tc_geofences.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getGroupGeofencesSelect(@Param("groupId") Long groupId);
	
	@Query(value = "SELECT tc_notifications.id,tc_notifications.type FROM tc_notifications " + 
			" INNER JOIN tc_group_notification ON tc_group_notification.notificationid = tc_notifications.id " + 
			" WHERE tc_group_notification.groupid =:groupId and tc_notifications.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getGroupNotificationsSelect(@Param("groupId") Long groupId);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.description FROM tc_attributes " + 
			" INNER JOIN tc_group_attribute ON tc_group_attribute.attributeid = tc_attributes.id " + 
			" WHERE tc_group_attribute.groupid =:groupId and tc_attributes.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getGroupAttrbuitesSelect(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_group_attribute.attributeid from tc_group_attribute where tc_group_attribute.groupid=:groupId ", nativeQuery = true)
	public List<Long> getAttrbuiteFromGroup(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_group_notification.notificationid from tc_group_notification where tc_group_notification.groupid=:groupId ", nativeQuery = true)
	public List<Long> getNotifcationFromGroup(@Param("groupId") Long groupId);
	
	@Query(value = "SELECT tc_groups.id,tc_groups.name FROM tc_groups"
			+ " INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) and tc_groups.is_deleted is null",nativeQuery = true)
	public List<DriverSelect> getGroupSelect(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT tc_groups.id,tc_groups.name FROM tc_groups"
			+ " INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) and tc_groups.is_deleted is null and  tc_groups.type IN(:type)",nativeQuery = true)
	public List<DriverSelect> getGroupSelectByType(@Param("userIds") List<Long> userIds,@Param("type") List<String> type);
	
	@Query(value = "SELECT tc_groups.id,tc_groups.name FROM tc_groups"
			+ " WHERE tc_groups.id IN(:groupIds) and tc_groups.is_deleted is null",nativeQuery = true)
	public List<DriverSelect> getGroupSelectByIds(@Param("groupIds") List<Long> groupIds);
	
	@Query(value = "SELECT tc_groups.id,tc_groups.name FROM tc_groups"
			+ " WHERE tc_groups.id IN(:groupIds) and tc_groups.is_deleted is null and tc_groups.type IN(:type)",nativeQuery = true)
	public List<DriverSelect> getGroupSelectByIdsByType(@Param("groupIds") List<Long> groupIds,@Param("type") List<String> type);
	
	@Query(value = "SELECT tc_groups.id,tc_groups.name FROM tc_groups"
			+ " INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:loggedUserId) and tc_groups.is_deleted is null "
			+ " and tc_groups.id Not IN(Select tc_user_client_group.groupid from tc_user_client_group where tc_user_client_group.userid !=:userId ) " ,nativeQuery = true)
	public List<DriverSelect> getGroupUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_groups.id from tc_groups " + 
			" inner join tc_user_group on tc_groups.id = tc_user_group.groupid " + 
			" where tc_groups.name =:name and tc_groups.is_deleted is null and tc_user_group.userid=:userId order by tc_groups.id DESC limit 0,1" ,nativeQuery = true)
	public Long getGroupIdByName(@Param("userId") Long userId,@Param("name") String name);
	
}
