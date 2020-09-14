package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.BillingsList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceCalibrationData;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.ExpiredVehicles;
import com.example.examplequerydslspringdatajpamaven.entity.LastLocationsList;
import com.example.examplequerydslspringdatajpamaven.entity.NewcustomerDivice;

@Component
public interface DeviceRepository extends  JpaRepository<Device, Long>, QueryDslPredicateExecutor<Device> {
	
	@Query(nativeQuery = true, name = "getDevicesList")
	List<CustomDeviceList> getDevicesList(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesListApp")
	List<CustomDeviceList> getDevicesListApp(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
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
	

	@Query(value = "SELECT tc_devices.id FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
			+ " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null", nativeQuery = true)
	public List<Long> getNumberOfOnlineDevicesList(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT tc_devices.id FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
			+ "where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
			+ " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null", nativeQuery = true)
	public List<Long> getNumberOfOutOfNetworkDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT count(tc_devices.id) FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserDevices(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.id FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public List<Long> getTotalNumberOfUserDevicesList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_devices.positionid FROM tc_devices"
			+ " INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " + 
			"AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public List<String> getAllPositionsObjectIds(@Param("userIds")List<Long> userIds);
	//here
	@Query(nativeQuery = true, name = "getDevicesLiveData")
	//List<CustomDeviceLiveData> getAllDevicesLiveData(@Param("userIds")List<Long> userIds,@Param("offset") int offset);
    List<CustomDeviceLiveData> getAllDevicesLiveData(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDevicesData")
    List<CustomDeviceLiveData> getAllDevicesData(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = " SELECT  count(tc_devices.id) FROM tc_devices " + 
			" INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid " + 
			" where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null " ,nativeQuery = true )
	public Integer getAllDevicesLiveDataSize(@Param("userIds")List<Long> userIds);
	
	//here
	@Query(nativeQuery = true, name = "getDevicesLiveDataMap")
	//List<CustomDeviceLiveData> getAllDevicesLiveDataMap(@Param("userIds")List<Long> userIds);
 	List<CustomDeviceLiveData> getAllDevicesLiveDataMap(@Param("userIds")List<Long> userIds);
	
	@Query(nativeQuery = true, name = "getDevicesDataMap")
 	List<CustomMapData> getAllDevicesDataMap(@Param("userIds")List<Long> userIds);

	@Query(nativeQuery = true, name = "vehicleInfo")
	public List<CustomDeviceList> vehicleInfo(@Param("deviceId")Long deviceId);
	
	@Query(nativeQuery = true, name = "getVehicleInfoData")
	public List<CustomDeviceList> vehicleInfoData(@Param("deviceId")Long deviceId);

	
	@Query(value = "SELECT count(*) FROM tc_devices "
			+ " INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
			+ " WHERE tc_user_device.userid IN(:userIds) and tc_devices.delete_date is null",nativeQuery = true )
	public Integer getDevicesListSize(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT tc_devices.calibrationData FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getCalibrationDataCCC(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.fuel FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getFuelData(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.sensorSettings FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getSensorSettings(@Param("deviceId")Long deviceId);
	
	@Query(value = "SELECT tc_devices.icon FROM tc_devices WHERE tc_devices.id=:deviceId AND tc_devices.delete_date IS NULL ",nativeQuery = true)
	public String getIcon(@Param("deviceId")Long deviceId);
	
	/*@Query(nativeQuery = true, name = "getBillingsList")
	public List<BillingsList> billingInfo(@Param("userId")Long userId,@Param("start")String start,@Param("end")String end,@Param("offset")int offset,@Param("search")String search);

	@Query(value = "SELECT COUNT(*) from ("
			+ " SELECT COUNT(distinct tc_devices.id) as deviceNumbers,tc_users.name as ownerName , " + 
			" DATE_FORMAT(tc_positions.fixtime, '%Y-%m') as workingDate " + 
			" from tc_positions " + 
			" INNER JOIN tc_devices ON tc_positions.deviceid = tc_devices.id  " + 
			" INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id  " + 
			" INNER JOIN tc_users ON tc_users.id = tc_user_device.userid  " + 
			" where (tc_positions.fixtime between :start and  :end ) and tc_positions.fixtime > '2018-01-01' " + 
			" and ( (tc_devices.delete_date is null) or (tc_devices.delete_date > :start ) )" + 
			" AND tc_users.id =:userId group by workingDate ) AS DATA ",nativeQuery = true )
	public Integer getBillingInfotSize(@Param("userId")Long userId,@Param("start")String start,@Param("end")String end);
	*/
	@Query(value = "select tc_user_device.deviceid from tc_user_device where tc_user_device.userid in ( :userIds) ", nativeQuery = true)
	public List<Long> getDevicesUsers(@Param("userIds")List<Long> userIds);
	

	@Query(value = "SELECT tc_notifications.id,tc_notifications.type FROM tc_notifications " + 
			" INNER JOIN tc_device_notification ON tc_device_notification.notificationid = tc_notifications.id " + 
			" WHERE tc_device_notification.deviceid =:deviceId ",nativeQuery = true)
	public List<DeviceSelect> getNotificationsDeviceSelect(@Param("deviceId") Long deviceId);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.attribute FROM tc_attributes " + 
			" INNER JOIN tc_device_attribute ON tc_device_attribute.attributeid = tc_attributes.id " + 
			" WHERE tc_device_attribute.deviceid =:deviceId ",nativeQuery = true)
	public List<DeviceSelect> getAttributesDeviceSelect(@Param("deviceId") Long deviceId);
	
	
	@Query(value = "select * from tc_devices where tc_devices.id in ( :deviceIds) ", nativeQuery = true)
	public List<Device> getDevicesByDevicesIds(@Param("deviceIds")List<Long> deviceIds);
	
	
	@Query(nativeQuery = true, name = "getDevicesSendList")
	public List<LastLocationsList> getAllDevicesIdsToSendLocation();
	
	@Query(nativeQuery = true, name = "getExpiredVehicles")
	public List<ExpiredVehicles> getAllExpiredIds(@Param("currentDate")Date currentDate);
	
}

