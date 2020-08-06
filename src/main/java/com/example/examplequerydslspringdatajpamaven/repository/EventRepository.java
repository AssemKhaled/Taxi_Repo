package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;

@Repository
public interface EventRepository  extends JpaRepository<Event, Long>, QueryDslPredicateExecutor<Event>{

	@Query(nativeQuery = true, name = "getEvents")
	public List<EventReport> getEvents(@Param("deviceIds")List<Long> deviceIds,@Param("offset")int offset,
			@Param("start")String start,@Param("end")String end,@Param("search")String search);
	
	@Query(nativeQuery = true, name = "getEventsScheduled")
	public List<EventReport> getEventsScheduled(@Param("deviceIds")List<Long> deviceIds,@Param("start")String start,@Param("end")String end);
	
	@Query(nativeQuery = true, name = "getEventsSort")
	public List<EventReport> getEventsSort(@Param("deviceIds")List<Long> deviceIds,@Param("offset")int offset,
			@Param("start")String start,@Param("end")String end,@Param("type")String type,@Param("search")String search);
	
	@Query (value = "SELECT count(tc_events.id) " + 
			" FROM tc_events INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid " + 
			" LEFT JOIN tc_positions ON tc_positions.id=tc_events.positionid " + 
			" LEFT JOIN tc_geofences ON tc_geofences.id=tc_events.geofenceid " + 
			" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_events.deviceid " + 
			" LEFT JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id " + 
			" WHERE tc_events.servertime BETWEEN :start AND :end and tc_events.deviceid IN(:deviceIds) AND tc_devices.delete_date IS NULL AND " + 
			" tc_drivers.delete_date IS NULL AND tc_geofences.delete_date IS NULL ",nativeQuery =  true)
	public Integer getEventsSize(@Param("deviceIds")List<Long> deviceIds,@Param("start")String start,
			@Param("end")String end);
	
	@Query (value = "SELECT count(tc_events.id) " + 
			" FROM tc_events INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid " + 
			" LEFT JOIN tc_positions ON tc_positions.id=tc_events.positionid " + 
			" LEFT JOIN tc_geofences ON tc_geofences.id=tc_events.geofenceid " + 
			" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_events.deviceid " + 
			" LEFT JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id " + 
			" WHERE tc_events.servertime BETWEEN :start AND :end and tc_events.deviceid  IN(:deviceIds)"
			+ " AND tc_devices.delete_date IS NULL AND tc_events.type like :type and " + 
			" tc_drivers.delete_date IS NULL AND tc_geofences.delete_date IS NULL ",nativeQuery =  true)
	public Integer getEventsSizeSort(@Param("deviceIds")List<Long> deviceIds,@Param("start")String start,
			@Param("end")String end,@Param("type")String type);
	
	
	@Query(nativeQuery = true, name = "getNotifications")
	public List<EventReport> getNotifications(@Param("userId") List<Long> userId,@Param("offset")int offset,@Param("search")String search);


	@Query(nativeQuery = true, name = "getNotificationsChart")
	public List<EventReport> getNotificationsChart(@Param("userId") List<Long> userId);

	@Query (value = "SELECT count(tc_events.id)"+
			" FROM tc_user_device" + 
			" INNER JOIN tc_events ON tc_user_device.deviceid=tc_events.deviceid " + 
			" WHERE tc_user_device.userid IN(:userId) " + 
			" AND Date(tc_events.servertime)=CURRENT_DATE() ",nativeQuery =  true)
	public Integer getNotificationsSize(@Param("userId") List<Long> userId);

	
}
