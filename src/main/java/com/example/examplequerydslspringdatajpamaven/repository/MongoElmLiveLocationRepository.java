package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.bson.types.ObjectId;
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
	public List<MongoElmLiveLocation> deleteByIdIn(List<ObjectId> positionIds);
	
	@Query("{ '_id' : { $exists: true }}")
	public List<MongoElmLiveLocation> findByIdsIn(Pageable pageable);

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	List<MongoElmLiveLocation> deleteByIdIn2(List<MongoElmLiveLocation> positionIds);


//	List<MongoElmLiveLocation> findByOrderByLocationTimeAsc( Pageable pageable );
	List<MongoElmLiveLocation> findTop1000ByOrderByLocationTimeAsc( );
//	List<MongoElmLiveLocation> findTop500ByOrderByLocationTimeAsc( );
//	List<MongoElmLiveLocation> findByOrderByLocationTimeDesc();

	Long deleteAllByIdIn(List<ObjectId> ids);




}
