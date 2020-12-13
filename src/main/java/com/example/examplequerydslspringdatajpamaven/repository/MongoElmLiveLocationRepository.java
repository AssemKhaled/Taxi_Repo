package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLiveLocation;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositionsElm;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoElmLiveLocationRepository extends MongoRepository<MongoElmLiveLocation,String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoElmLiveLocation> deleteByIdIn(List<String> positionIds);
	
	@Query("{ '_id' : { $exists: true }}")
	public List<MongoElmLiveLocation> findByIdsIn(Pageable pageable);
}
