package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

@Component
public interface GeofenceRepository extends JpaRepository<Geofence, Long>, QueryDslPredicateExecutor<Geofence>{

	
	@Transactional
    @Modifying
	@Query(value = "Update tc_geofences geofence Set geofence.delete_date=:date where geofence.id=:geofenceId", nativeQuery = true)
	public void deleteGeofence(@Param("geofenceId") Long geofenceId,@Param("date") String currentDate);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_geofence where tc_user_geofence.geofenceid=:geofenceId", nativeQuery = true)
	public void deleteGeofenceId(@Param("geofenceId") Long geofenceId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_geofence where tc_device_geofence.geofenceid=:geofenceId", nativeQuery = true)
	public void deleteGeofenceDeviceId(@Param("geofenceId") Long geofenceId);
	
	
	@Query(value = "select * from tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " where tc_geofences.name=:name and tc_user_geofence.userid=:userId and tc_geofences.delete_date IS NULL", nativeQuery = true)
	public List<Geofence> checkDublicateGeofenceInAdd(@Param("userId") Long id,@Param("name") String name);
	
	@Query(value = "select * from tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " where tc_geofences.name=:name and tc_geofences.id !=:geofenceId and tc_user_geofence.userid=:userId and tc_geofences.delete_date IS NULL", nativeQuery = true)
	public List<Geofence> checkDublicateGeofenceInEdit(@Param("geofenceId") Long geofenceId,@Param("userId") Long userId,@Param("name") String name);
	
	@Query(value = "select * from tc_geofences where id in :ids and delete_date is null",nativeQuery = true)
	public List<Geofence> getMultipleGeofencesById(@Param("ids")Long [] ids);
	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid=:userId and tc_geofences.delete_date is null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Geofence> getAllGeofences(@Param("userId") Long userId,@Param("offset") int offset,@Param("search") String search);
	
	
}
