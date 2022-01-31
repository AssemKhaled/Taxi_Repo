package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLiveLocation;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoElmLogsRepository extends MongoRepository<MongoElmLogs, String>{
	
	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoElmLogs> deleteByIdIn(List<String> positionIds);
	
	public List<MongoElmLogs> findAllByUserIdIn(List<Long> userIds,Pageable pageable);
	
	public Integer countByUserIdIn(List<Long> userIds);

	public List<MongoElmLogs> findByUserId(Long userId,Pageable pageable);
	
	public List<MongoElmLogs> findByDriverId(Long driverId,Pageable pageable);
	
	public List<MongoElmLogs> findByDeviceId(Long deviceId,Pageable pageable);

	public Integer countByUserId(Long userId);
	
	public Integer countByDriverId(Long driverId);
	
	public Integer countByDeviceId(Long deviceId);

}

