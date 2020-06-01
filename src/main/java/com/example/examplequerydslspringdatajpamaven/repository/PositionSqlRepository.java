package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Position;
import com.example.examplequerydslspringdatajpamaven.entity.PositionSql;
import com.example.examplequerydslspringdatajpamaven.entity.TripPositions;

public interface PositionSqlRepository extends  JpaRepository<PositionSql, Long>, QueryDslPredicateExecutor<PositionSql> {
	@Query(nativeQuery = true, name = "getPositionsList")
	public List<CustomPositions> getSensorsList(@Param("deviceId")List<Long>  deviceId,@Param("start")String start,@Param("end")String end,@Param("offset")int offset);
	
	@Query(nativeQuery = true, name = "getPositionsListExport")
	public List<CustomPositions> getSensorsListExport(@Param("deviceId")List<Long> deviceId,@Param("start")String start,@Param("end")String end);

	@Query(value = "SELECT count(CAST(tc_positions.id AS DATE)) FROM tc_positions " 
			+ " INNER JOIN tc_devices ON tc_devices.id=tc_positions.deviceid "
			+ " WHERE deviceid IN(:deviceId) AND tc_positions.fixtime<=:end AND "
			+ " tc_positions.fixtime>=:start AND tc_devices.delete_date IS NULL",nativeQuery = true )
	public Integer getSensorsListSize(@Param("deviceId")List<Long>  deviceId,@Param("start")String start,@Param("end")String end);
	
	
	@Query(nativeQuery = true, name = "getAttrbuitesList")
	public  List<CustomPositions> getAttrbuites(@Param("userIds")List<Long> userIds);
	
	@Query(nativeQuery = true, name = "getDriverHoursList")
	public  List<CustomPositions> getDriverHoursList(@Param("userIds")List<Long> userIds);
	

	@Query(value = "SELECT latitude,longitude FROM tc_positions "
			+ " where deviceid=:deviceId AND  id BETWEEN :start  AND :end "
			+ " AND latitude!=0 AND longitude !=0 order by devicetime ASC ",nativeQuery = true)
	public List<TripPositions> getTripPositions(@Param("deviceId")Long deviceId,@Param("start")Long start,@Param("end")Long end);
}
