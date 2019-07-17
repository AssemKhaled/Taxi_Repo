package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;

@Repository
public interface EventRepository  extends JpaRepository<Event, Long>, QueryDslPredicateExecutor<Event>{

	@Query(nativeQuery = true, name = "getEvents")
	public List<EventReport> getEvents(@Param("deviceId")Long deviceId,@Param("offset")int offset,
			@Param("start")String start,@Param("end")String end,@Param("search")String search);
	
	@Query(nativeQuery = true, name = "getNotifications")
	public List<EventReport> getNotifications(@Param("userId")Long userId,@Param("offset")int offset,@Param("search")String search);

	
}
