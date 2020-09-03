package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Points;
import com.example.examplequerydslspringdatajpamaven.entity.Schedule;;

@Service
public interface PointsRepository  extends JpaRepository<Points, Long>, QueryDslPredicateExecutor<Points>{

	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null"
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPoints(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is null", nativeQuery = true)
	public Integer getAllPointsSize(@Param("userIds")List<Long> userIds);
}
