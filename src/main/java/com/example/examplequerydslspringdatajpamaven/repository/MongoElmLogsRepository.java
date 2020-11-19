package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoElmLogsRepository extends MongoRepository<MongoElmLogs, String>{
	
	public List<MongoElmLogs> findAllByUserIdIn(List<Long> userIds,Pageable pageable);
	
	public Integer countByUserIdIn(List<Long> userIds);

	public List<MongoElmLogs> findByUserId(Long userId,Pageable pageable);
	
	public List<MongoElmLogs> findByDriverId(Long driverId,Pageable pageable);
	
	public List<MongoElmLogs> findByDeviceId(Long deviceId,Pageable pageable);

	public Integer countByUserId(Long userId);
	
	public Integer countByDriverId(Long driverId);
	
	public Integer countByDeviceId(Long deviceId);

}

