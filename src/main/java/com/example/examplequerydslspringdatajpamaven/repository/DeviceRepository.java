package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.ExpiredVehicles;
import com.example.examplequerydslspringdatajpamaven.entity.LastLocationsList;

/**
 * Queries on table tc_devices 
 * @author fuinco
 *
 */
@Component
public interface DeviceRepository extends  JpaRepository<Device, Long>, QueryDslPredicateExecutor<Device> {
	
	@Query(value = "SELECT * from tc_devices where tc_devices.simcardNumber=:simcardNumber and tc_devices.delete_date is null",nativeQuery = true)
	public List<Device> checkSIMCard(@Param("simcardNumber")String simcardNumber);
	
	@Query(value = "SELECT * from tc_devices where tc_devices.simcardNumber=:simcardNumber and tc_devices.delete_date is null and tc_devices.id !=:id",nativeQuery = true)
	public List<Device> checkSIMCardEdit(@Param("simcardNumber")String simcardNumber,@Param("id")Long id);
	
	@Query(nativeQuery = true, name = "getDevicesList")
	List<CustomDeviceList> getDevicesList(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesListExport")
	List<CustomDeviceList> getDevicesListExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesListByIds")
	List<CustomDeviceList> getDevicesListByIds(@Param("deviceIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesListByIdsExport")
	List<CustomDeviceList> getDevicesListByIdsExport(@Param("deviceIds")List<Long> userIds,@Param("search") String search);

	@Query(value = "select * from tc_devices " + 
			" inner join tc_user_client_device on tc_devices.id = tc_user_client_device.deviceid " + 
			" where tc_user_client_device.userid=:userId and delete_date is null ", nativeQuery = true)
	public Set<Device> getDevicesOfTypeUser(@Param("userId") Long userId);
	
	@Query(nativeQuery = true, name = "getDevicesListApp")
	List<CustomDeviceList> getDevicesListApp(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDevicesListAppByIds")
	List<CustomDeviceList> getDevicesListAppByIds(@Param("deviceIds")List<Long> deviceIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT car_weight FROM tc_devices where id=:deviceId",nativeQuery = true )
	public Float getWeight(@Param("deviceId")Long deviceId);
	
	@Query (value = "select * from tc_devices d where d.delete_date is NULL and (d.name = :deviceName or d.uniqueid = :deviceUniqueId "
			+ "or d.sequence_number = :deviceSequenceNumber or (d.plate_num = :devicePlateNum and d.left_letter = :deviceLeftLetter and"
			+ " d.middle_letter = :deviceMiddleLetter and d.right_letter = :deviceRightLetter))",nativeQuery =  true)
	public List<Device>checkDeviceDuplication(@Param("deviceName")String deviceName, @Param("deviceUniqueId")String deviceUniqueId,
			                                  @Param("deviceSequenceNumber")String deviceSequenceNumber, @Param("devicePlateNum")String devicePlateNum,
			                                  @Param("deviceLeftLetter")String deviceLeftLetter, @Param("deviceMiddleLetter")String deviceMiddleLetter,
			                                  @Param("deviceRightLetter")String deviceRightLetter);
	
	@Modifying
    @Transactional
	@Query(value = "delete  from tc_user_device where deviceid = :deviceId", nativeQuery = true )
	public void deleteUserDevice(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.id,tc_devices.name FROM tc_devices"
			+ " INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id"
			+ " WHERE tc_user_device.userid IN (:userIds ) and tc_devices.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getDeviceSelect(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.id,tc_devices.name FROM tc_devices"
			+ " INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id"
			+ " WHERE tc_user_device.userid IN (:loggedUserId ) and tc_devices.delete_date is null "
			+ " and tc_devices.id Not IN(Select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.userid !=:userId) ",nativeQuery = true)
	public List<DeviceSelect> getDeviceUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId")Long userId);
	
	@Query(value = "SELECT tc_devices.id,tc_devices.name FROM tc_devices"
			+ " WHERE tc_devices.id IN (:deviceIds ) and tc_devices.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getDeviceSelectByIds(@Param("deviceIds")List<Long> deviceIds);
	

	@Query(value = "SELECT tc_devices.positionid FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
			+ " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null and tc_devices.positionid is not null ", nativeQuery = true)
	public List<String> getNumberOfOnlineDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices "
			+ " where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
			+ " AND tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
	public List<String> getNumberOfOnlineDevicesListByIds(@Param("deviceIds")List<Long> deviceIds);
	
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
			+ " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
	public List<String> getNumberOfOutOfNetworkDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 8 minute)=false "
			+ " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
	public List<String> getNumberOfOfflineDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices "
			+ " where  tc_devices.lastupdate>date_sub(now(), interval 8 minute)=false "
			+ " AND tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
	public List<String> getNumberOfOfflineDevicesListByIds(@Param("deviceIds")List<Long> deviceIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
			+ " AND tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
	public List<String> getNumberOfOutOfNetworkDevicesListByIds(@Param("deviceIds")List<Long> deviceIds);
	
	@Query(value = "SELECT count(tc_devices.id) FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserDevices(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT count(tc_devices.id) FROM tc_devices  " + 
			" Where tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserDevicesByIds(@Param("deviceIds")List<Long> deviceIds);
	
	@Query(value = "SELECT tc_devices.id FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public List<Long> getTotalNumberOfUserDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices"
			+ " INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null and tc_devices.positionid is not null "
			+ " group by tc_devices.id",nativeQuery = true )
	public List<String> getAllPositionsObjectIds(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices " +
			" where tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null "
			+ " group by tc_devices.id ",nativeQuery = true )
	public List<String> getAllPositionsObjectIdsByIds(@Param("deviceIds")List<Long> deviceIds);
	
	
	@Query(nativeQuery = true, name = "getDevicesLiveData")
    List<CustomDeviceLiveData> getAllDevicesLiveData(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesData")
    List<CustomDeviceLiveData> getAllDevicesData(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesDataByIds")
    List<CustomDeviceLiveData> getAllDevicesDataByIds(@Param("deviceIds")List<Long> deviceIds,@Param("offset") int offset,@Param("search") String search);

	
	@Query(value = " SELECT  count(tc_devices.id) FROM tc_devices " + 
			" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " + 
			" where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null " 
			+ "  AND ( (tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.name LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))))" ,nativeQuery = true )
	public Integer getAllDevicesLiveDataSize(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = " SELECT  count(tc_devices.id) FROM tc_devices " + 
			" where tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null " 
			+ "  AND ( (tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.name LIKE LOWER(CONCAT('%',:search, '%'))) OR (tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))))" ,nativeQuery = true )
	public Integer getAllDevicesLiveDataSizeByIds(@Param("deviceIds")List<Long> deviceIds,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDevicesLiveDataMap")
 	List<CustomDeviceLiveData> getAllDevicesLiveDataMap(@Param("userIds")List<Long> userIds);
	
	@Query(nativeQuery = true, name = "getDevicesDataMapNoPosition")
 	List<CustomMapData> getAllDevicesDataMap(@Param("userIds")List<Long> userIds);
	
	@Query(nativeQuery = true, name = "getDevicesDataMapByIdsNoPosition")
 	List<CustomMapData> getAllDevicesDataMapByIds(@Param("deviceIds")List<Long> deviceIds);

	@Query(nativeQuery = true, name = "vehicleInfo")
	public List<CustomDeviceList> vehicleInfo(@Param("deviceId")Long deviceId);
	
	@Query(nativeQuery = true, name = "getVehicleInfoData")
	public List<CustomDeviceList> vehicleInfoData(@Param("deviceId")Long deviceId);

	@Query(value = " SELECT count(*) From ( "
			+ "SELECT count(*) X "
     		+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
     		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
     		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null"
     		+ " AND ( tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
     		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
     		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id ) Y" ,nativeQuery = true )
	public Integer getDevicesListSize(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = " SELECT count(*) From ( "
			+ " SELECT count(*) X"
     		+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
     		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
     		+ " where tc_devices.id IN(:deviceIds) and tc_devices.delete_date is null"
     		+ " AND ( tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
     		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
     		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id ) Y " ,nativeQuery = true )
	public Integer getDevicesListSizeByIds(@Param("deviceIds")List<Long> deviceIds,@Param("search") String search);
	
	
	@Query(value = "SELECT tc_devices.calibrationData FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getCalibrationDataCCC(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.* FROM tc_devices "
			+ " WHERE tc_devices.lineData is null and tc_devices.calibrationData is not null ",nativeQuery = true)
	public List<Device> getAllDevicesNotHaveLineData();
	
	@Query(value = "SELECT tc_devices.fuel FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getFuelData(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.sensorSettings FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getSensorSettings(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.icon FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getIcon(@Param("deviceId")Long deviceId);
	
	@Query(value = "select tc_user_device.deviceid from tc_user_device where tc_user_device.userid in ( :userIds) ", nativeQuery = true)
	public List<Long> getDevicesUsers(@Param("userIds")List<Long> userIds);
	

	@Query(value = "SELECT tc_notifications.id,tc_notifications.type FROM tc_notifications " + 
			" INNER JOIN tc_device_notification ON tc_device_notification.notificationid = tc_notifications.id " + 
			" WHERE tc_device_notification.deviceid =:deviceId and tc_notifications.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getNotificationsDeviceSelect(@Param("deviceId") Long deviceId);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.description FROM tc_attributes " + 
			" INNER JOIN tc_device_attribute ON tc_device_attribute.attributeid = tc_attributes.id " + 
			" WHERE tc_device_attribute.deviceid =:deviceId and tc_attributes.delete_date is null",nativeQuery = true)
	public List<DeviceSelect> getAttributesDeviceSelect(@Param("deviceId") Long deviceId);
	
	
	@Query(value = "select * from tc_devices where tc_devices.id in ( :deviceIds) ", nativeQuery = true)
	public List<Device> getDevicesByDevicesIds(@Param("deviceIds")List<Long> deviceIds);
	
	
	@Query(nativeQuery = true, name = "getDevicesSendList")
	public List<LastLocationsList> getAllDevicesIdsToSendLocation();
	
	@Query(value = "SELECT tc_devices.id FROM tc_devices " + 
			" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_devices.id " + 
			" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid  " + 
			" INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id " + 
			" INNER JOIN tc_users ON tc_user_device.userid=tc_users.id " + 
			" where " + 
			" tc_devices.delete_date IS NULL " + 
			" AND tc_devices.positionid Is NOT NULL " + 
			" AND tc_devices.create_date Is NOT NULL " + 
			" AND tc_devices.expired IS False " + 
			" AND tc_drivers.delete_date IS NULL " + 
			" AND tc_users.delete_date IS NULL " + 
			" AND tc_devices.reference_key IS NOT NULL", nativeQuery = true)
	public List<Long> getAllDevicesIdsToSendLocationIds();
	
	@Query(nativeQuery = true, name = "getExpiredVehicles")
	public List<ExpiredVehicles> getAllExpiredIds(@Param("currentDate")Date currentDate);
	
	@Query(value = " SELECT tc_devices.id FROM tc_devices " + 
			" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " + 
			" where tc_user_device.userid IN (:userId) and tc_devices.delete_date is null "
			+ " and tc_devices.name=:name and tc_devices.uniqueid=:uniqueid " ,nativeQuery = true )
	public Long getDeviceIdByName(@Param("userId") Long userId,@Param("name") String name,@Param("uniqueid") String uniqueid);
	
	
	@Query(value = "Select * From tc_devices Where tc_devices.sequence_number=:sequenceNumber"
			+ " and tc_devices.delete_date is null limit 0,1", nativeQuery = true)
	public Device getDeviceBySequenceNumber(@Param("sequenceNumber") String sequenceNumber);
	
	@Query(value = "select tc_devices.id from tc_devices", nativeQuery = true)
	public List<Long> getAllDeviceIds();
}

