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
import com.example.examplequerydslspringdatajpamaven.entity.userClientDevice;

/**
 * Queries related to type 3 and 4 for tc_user_client_device
 * @author fuinco
 *
 */
@Component
public interface UserClientDeviceRepository extends JpaRepository<userClientDevice, Long>, QueryDslPredicateExecutor<userClientDevice>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
	public void deleteDevicesByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
	public List<userClientDevice> getDevicesOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
	public List<Long> getDevicesIds(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_device.deviceid from tc_user_client_device "
			+ " where tc_user_client_device.userid=:userId and tc_user_client_device.deviceid=:deviceId  ", nativeQuery = true)
	public List<Long> getDevice(@Param("userId") Long userId,@Param("deviceId") Long deviceId);
	
	@Query(value = "select tc_devices.id as id , tc_devices.name as name from tc_devices "
			+ " INNER JOIN tc_user_client_device ON tc_user_client_device.deviceid=tc_devices.id "
			+ "  where tc_user_client_device.userid=:userId ", nativeQuery = true)
	public List<DeviceSelect> getDeviceOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select * from tc_user_client_device where tc_user_client_device.deviceid IN (:deviceIds) and tc_user_client_device.userid !=:userId", nativeQuery = true)
	public List<userClientDevice> getDevicesByDevIds(@Param("deviceIds") Long[] deviceIds ,@Param("userId") Long userId);

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_device where tc_user_client_device.deviceid=:deviceId", nativeQuery = true)
	public void deleteDeviceById(@Param("deviceId") Long deviceId);
	
	@Query(value = "select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.deviceid=:deviceId", nativeQuery = true)
	public List<Long> getDevicesToDelete(@Param("deviceId") Long deviceId);
}
