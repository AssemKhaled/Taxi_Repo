package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;

@Component
public interface DeviceRepository extends  JpaRepository<Device, Long>, QueryDslPredicateExecutor<Device> {

	@Query(nativeQuery = true, name = "getDevicesList")
	List<CustomDeviceList> getDevicesList(@Param("userId")Long userId,@Param("offset") int offset,@Param("search") String search);
	
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
			+ " WHERE tc_user_device.userid=:userId",nativeQuery = true)
	public List<DeviceSelect> getDeviceSelect(@Param("userId")Long userId);
	
	@Query(value = "SELECT count(tc_positions.devicetime) FROM tc_devices INNER JOIN tc_positions ON tc_positions.id=tc_devices.positionid AND\n" + 
			" tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false AND  \n" + 
			" tc_devices.lastupdate=date_sub(now(), interval 0 minute)=false INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id\n" + 
			" AND tc_user_device.userid = :userId" , nativeQuery = true)
	public Integer getNumberOfOnlineDevices(@Param("userId")Long userId);
	
	@Query(value = "SELECT count(tc_positions.devicetime) FROM tc_devices INNER JOIN tc_positions ON tc_positions.id=tc_devices.positionid AND \n" + 
			"tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false INNER JOIN \n" + 
			" tc_user_device ON tc_user_device.deviceid=tc_devices.id AND tc_user_device.userid = :userId", nativeQuery = true)
	public Integer getNumberOfOutOfNetworkDevices(@Param("userId")Long userId);
	
	@Query(value = "SELECT count(tc_devices.id) FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid \n" + 
			"AND tc_user_device.userid = :userId WHERE tc_devices.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserDevices(@Param("userId")Long userId);
	
	@Query(nativeQuery = true, name = "getDevicesLiveData")
	List<CustomDeviceLiveData> getAllDevicesLiveData(@Param("userId")Long userId,@Param("offset") int offset,@Param("search") String search);
	

	@Query(nativeQuery = true, name = "getDeviceLiveData")
	List<CustomDeviceLiveData> getDeviceLiveData(@Param("deviceId")Long deviceId);
	

	@Query(nativeQuery = true, name = "vehicleInfo")
	public List<CustomDeviceList> vehicleInfo(@Param("deviceId")Long deviceId);

	
	
}
