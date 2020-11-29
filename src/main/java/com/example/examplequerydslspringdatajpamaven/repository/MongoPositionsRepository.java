package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoPositionsRepository extends MongoRepository<MongoPositions,String>{

	public Integer countByDeviceidIn(List<Long> deviceIds);
	
	@Query("{ '_id' : { $in: ?0 } }")
	public List<MongoPositions> findByIdIn(List<String> positionIds);
	
	@Query("{ '_id' : ?0 }")
	public MongoPositions findById(String positionId);
	
	
	@Query("{ 'deviceid' : { $in: ?0 } , 'deviceName' : { $exists : false }}")
	public List<MongoPositions> findByDeviceIdIn(List<Long> deviceIds,Pageable pageable);
	

	

}

