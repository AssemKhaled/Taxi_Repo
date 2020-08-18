package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;

public interface MongoPositionsRepository extends MongoRepository<MongoPositions,String>{

	public Integer countByDeviceidIn(List<Long> deviceIds);
	
	@Query("{ '_id' : { $in: ?0 } , 'servertime': {$gte: ?1, $lte:?2 } }")
	public List<MongoPositions> findByIdInToday(List<String> positionIds,String from , String to);
	
	@Query("{ '_id' : { $in: ?0 } }")
	public List<MongoPositions> findByIdIn(List<String> positionIds);
	
	@Query("{ '_id' : ?0 }")
	public MongoPositions findById(String positionId);
	

	

}

