package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.example.examplequerydslspringdatajpamaven.entity.Device;


public interface DeviceRepository extends  JpaRepository<Device, Long>, QueryDslPredicateExecutor<Device> {

	@Query(value = " select  * from tc_devices d where d.delete_date is NULL", nativeQuery = true)
	public List<Device> getName();
}

