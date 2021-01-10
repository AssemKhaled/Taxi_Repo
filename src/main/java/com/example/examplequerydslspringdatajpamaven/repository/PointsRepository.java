package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Points;

/**
 * Queries related to tc_points
 * @author fuinco
 *
 */
@Service
public interface PointsRepository  extends JpaRepository<Points, Long>, QueryDslPredicateExecutor<Points>{

	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPoints(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPointsByIds(@Param("pointIds")List<Long> pointIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsByIdsExport(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null ", nativeQuery = true)
	public List<Points> getAllPointsMap(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is null ", nativeQuery = true)
	public List<Points> getAllPointsMapByIds(@Param("pointIds")List<Long> pointIds);
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSize(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSizeByIds(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
	@Query(value = "SELECT tc_points.id,tc_points.name FROM tc_points " 
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getPointSelect(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT tc_points.id,tc_points.name FROM tc_points " 
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getPointSelectByIds(@Param("pointIds") List<Long> pointIds);
	
	@Query(value = "SELECT tc_points.id,tc_points.name FROM tc_points " 
			+ " WHERE tc_points.userId IN(:loggedUserId) and tc_points.delete_date is null "
			+ " and tc_points.id Not IN(Select tc_user_client_point.pointid from tc_user_client_point where tc_user_client_point.userid !=:userId ) ",nativeQuery = true)
	public List<DriverSelect> getPointUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId);
	
	
	@Query(value = "SELECT tc_points.id FROM tc_points " 
			+ " WHERE tc_points.userId IN(:userId) and tc_points.delete_date is null "
			+ " and tc_points.name=:name and tc_points.latitude=:lat and tc_points.longitude=:longt",nativeQuery = true)
	public Long getPointIdByName(@Param("userId") Long userId,@Param("name") String name,@Param("lat") Double lat,@Param("longt") Double longt);
	
	
}
