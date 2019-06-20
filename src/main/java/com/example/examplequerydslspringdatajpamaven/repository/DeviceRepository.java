package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.example.examplequerydslspringdatajpamaven.entity.Device;


public interface DeviceRepository extends  JpaRepository<Device, Long>, QueryDslPredicateExecutor<Device> {

	@Query(value = " select  * from tc_devices d where d.delete_date is NULL", nativeQuery = true)
	public List<Device> getName();
	
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
}

