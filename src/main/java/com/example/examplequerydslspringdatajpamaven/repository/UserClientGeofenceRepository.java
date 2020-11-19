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
import com.example.examplequerydslspringdatajpamaven.entity.userClientGeofence;

/**
 * Queries related to type 3 and 4 for tc_user_client_geofence
 * @author fuinco
 *
 */
@Component
public interface UserClientGeofenceRepository extends JpaRepository<userClientGeofence, Long>, QueryDslPredicateExecutor<userClientGeofence>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_geofence where tc_user_client_geofence.userid=:userId", nativeQuery = true)
	public void deleteGeofencesByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_geofence where tc_user_client_geofence.userid=:userId", nativeQuery = true)
	public List<userClientGeofence> getGeofnecesOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_geofence.geofenceid from tc_user_client_geofence where tc_user_client_geofence.userid=:userId", nativeQuery = true)
	public List<Long> getGeofneceIds(@Param("userId") Long userId);
	
	@Query(value = "select tc_geofences.id as id , tc_geofences.name as name from tc_geofences "
			+ " INNER JOIN tc_user_client_geofence ON tc_user_client_geofence.geofenceid=tc_geofences.id "
			+ "  where tc_user_client_geofence.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getGeofencesOfUserList(@Param("userId") Long userId);
	
	
	@Query(value = "select tc_user_client_geofence.geofenceid from tc_user_client_geofence "
			+ " where tc_user_client_geofence.userid=:userId and tc_user_client_geofence.geofenceid=:geofenceId  ", nativeQuery = true)
	public List<Long> getGeofence(@Param("userId") Long userId,@Param("geofenceId") Long geofenceId);
	
	
	@Query(value = "select * from tc_user_client_geofence where tc_user_client_geofence.geofenceid IN (:geofenceIds) and tc_user_client_geofence.userid !=:userId", nativeQuery = true)
	public List<userClientGeofence> getGeofeneceByGeoIds(@Param("geofenceIds") Long[] geofenceIds ,@Param("userId") Long userId);

	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_geofence where tc_user_client_geofence.geofenceid=:geofenceId", nativeQuery = true)
	public void deleteGeofById(@Param("geofenceId") Long geofenceId);
	
	@Query(value = "select tc_user_client_geofence.geofenceid from tc_user_client_geofence where tc_user_client_geofence.geofenceid=:geofenceId", nativeQuery = true)
	public List<Long> getGeofToDelete(@Param("geofenceId") Long geofenceId);
}
