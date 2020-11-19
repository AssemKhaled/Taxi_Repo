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
import com.example.examplequerydslspringdatajpamaven.entity.userClientPoint;

/**
 * Queries related to type 3 and 4 for tc_user_client_point
 * @author fuinco
 *
 */
@Component
public interface UserClientPointRepository extends JpaRepository<userClientPoint, Long>, QueryDslPredicateExecutor<userClientPoint>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_point where tc_user_client_point.userid=:userId", nativeQuery = true)
	public void deletePointsByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_point where tc_user_client_point.userid=:userId", nativeQuery = true)
	public List<userClientPoint> getPointsOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_points.id,tc_points.name from tc_points "
			+ " INNER JOIN tc_user_client_point ON tc_user_client_point.pointid=tc_points.id "
			+ "  where tc_user_client_point.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getPointsOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_point.pointid from tc_user_client_point "
			+ " where tc_user_client_point.userid=:userId and tc_user_client_point.pointid=:pointId  ", nativeQuery = true)
	public List<Long> getPoint(@Param("userId") Long userId,@Param("pointId") Long pointId);
	
	@Query(value = "select * from tc_user_client_point where tc_user_client_point.pointid IN (:pointIds) and tc_user_client_point.userid !=:userId", nativeQuery = true)
	public List<userClientPoint> getPointByPoiIds(@Param("pointIds") Long[] pointIds ,@Param("userId") Long userId);

	
	@Query(value = "select tc_user_client_point.pointid from tc_user_client_point where tc_user_client_point.userid=:userId", nativeQuery = true)
	public List<Long> getPointIds(@Param("userId") Long userId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_point where tc_user_client_point.pointid=:pointId", nativeQuery = true)
	public void deletePointsById(@Param("pointId") Long pointId);
	
	@Query(value = "select tc_user_client_point.pointid from tc_user_client_point where tc_user_client_point.pointid=:pointId", nativeQuery = true)
	public List<Long> getPointsToDelete(@Param("pointId") Long pointId);
	
	
}