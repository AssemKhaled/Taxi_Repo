package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;

public interface EventRepository  extends JpaRepository<Event, Long>, QueryDslPredicateExecutor<Event>{

	@Query(value ="SELECT tc_events.id , tc_events.type as type , tc_devices.name as name FROM tc_events INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid "
			+ " where tc_events.deviceid=:deviceId "
			+ " and tc_events.servertime BETWEEN :start and :end "
			+ " ORDER BY tc_events.servertime DESC LIMIT :offset,10", nativeQuery = true)
	public List<EventReport> getEvents(@Param("deviceId") Long deviceId,@Param("offset") int offset,@Param("start") String start,@Param("end") String end);
	
}
