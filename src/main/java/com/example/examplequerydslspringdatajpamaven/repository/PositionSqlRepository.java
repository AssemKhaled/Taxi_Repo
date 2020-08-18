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


	@Query(nativeQuery = true, name = "getAttrbuitesList")
	public  List<CustomPositions> getAttrbuites(@Param("userIds")List<Long> userIds);
	
	@Query(nativeQuery = true, name = "getDriverHoursList")
	public  List<CustomPositions> getDriverHoursList(@Param("userIds")List<Long> userIds);
	
}
