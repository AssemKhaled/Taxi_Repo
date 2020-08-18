package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositionsElm;

public interface MongoPositionsElmRepository extends MongoRepository<MongoPositionsElm,String>{

	@Query("{ 'deviceid' : { $in: ?0 }, 'is_sent': {$eq: null } }")
	public List<MongoPositionsElm> findByDeviceIdIn(List<Long> deviceIds,Pageable pageable);
	
	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoPositionsElm> deleteByIdIn(List<String> positionIds);
	
	
}

