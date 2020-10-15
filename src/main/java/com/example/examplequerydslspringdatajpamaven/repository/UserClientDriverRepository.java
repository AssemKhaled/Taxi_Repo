package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.userClientDriver;

@Component
public interface UserClientDriverRepository extends JpaRepository<userClientDriver, Long>, QueryDslPredicateExecutor<userClientDriver>{

	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_driver where tc_user_client_driver.userid=:userId", nativeQuery = true)
	public void deleteDriversByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_driver where tc_user_client_driver.userid=:userId", nativeQuery = true)
	public List<userClientDriver> getDriversOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_drivers.id as id , tc_drivers.name as name from tc_drivers "
			+ " INNER JOIN tc_user_client_driver ON tc_user_client_driver.driverid=tc_drivers.id "
			+ "  where tc_user_client_driver.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getDriversOfUserList(@Param("userId") Long userId);
}

