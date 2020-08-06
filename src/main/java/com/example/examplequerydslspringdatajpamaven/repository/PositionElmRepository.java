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
import com.example.examplequerydslspringdatajpamaven.entity.LastLocationsList;
import com.example.examplequerydslspringdatajpamaven.entity.PositionElm;

@Component
public interface PositionElmRepository  extends JpaRepository<PositionElm, Long>, QueryDslPredicateExecutor<PositionElm> {

	/*@Query(value = "SELECT tc_positions_elm.id as id,tc_positions_elm.servertime as lasttime,tc_positions_elm.deviceid as deviceid, " + 
			" tc_positions_elm.latitude as latitude,tc_positions_elm.longitude as longitude,tc_positions_elm.speed as speed, " + 
			" tc_positions_elm.attributes as attributes, tc_positions_elm.devicetime as devicetime,tc_devices.reference_key as deviceRK ," + 
			" tc_drivers.reference_key as driver_RK ,tc_drivers.id as driverid,tc_drivers.name as drivername, " + 
			" tc_positions_elm.weight as weight ,tc_positions_elm.address as address,tc_positions_elm.is_offline as is_offline , " + 
			" tc_devices.name as devicename, tc_users.id as userid ,tc_users.name as username ,tc_users.reference_key as userRK FROM tc_positions_elm " + 
			" INNER JOIN tc_devices ON tc_devices.id=tc_positions_elm.deviceid " + 
			" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_positions_elm.deviceid " + 
			" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
			" INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_positions_elm.deviceid " + 
			" INNER JOIN tc_users ON tc_user_device.userid=tc_users.id " + 
			" WHERE tc_positions_elm.is_sent IS NULL " + 
			" AND tc_devices.is_deleted IS NULL " + 
			" AND tc_devices.create_date Is NOT NULL " + 
			" AND tc_devices.expired IS False " + 
			" AND tc_drivers.is_deleted IS NULL " + 
			" AND tc_devices.reference_key IS NOT NULL " + 
			" LIMIT 1000", nativeQuery = true)
	public List<LastLocationsList> getAllPositionsNotSent();*/

	@Query(nativeQuery = true, name = "getList")
	List<LastLocationsList> getAllPositionsNotSent();
	
	@Query(value = "SELECT car_weight FROM tc_devices where id=:deviceId",nativeQuery = true )
	public Double getWeight(@Param("deviceId")Long deviceId);
	

	@Modifying
    @Transactional
	@Query(value = "DELETE from tc_positions_elm WHERE tc_positions_elm.id in(:ids)", nativeQuery = true )
	public void deletePositions(@Param("ids")List<Long> ids);

	
}
