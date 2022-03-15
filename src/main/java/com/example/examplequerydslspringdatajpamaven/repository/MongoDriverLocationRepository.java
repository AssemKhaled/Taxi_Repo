package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoDriverLocation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MongoDriverLocationRepository extends MongoRepository<MongoDriverLocation, String> {

    List<MongoDriverLocation> findBy_idInAndServerTimeBetweenAndTripIdIsNull(List<String> driverLastLocationId, Date time3MinutesEarlier, Date now);
    List<MongoDriverLocation> findBy_idInAndServerTimeBetweenAndTripIdIsNotNull(List<String> driverLastLocationId, Date time3MinutesEarlier, Date now);

    Integer countAllBy_idInAndServerTimeBetweenAndTripIdIsNull(List<String> driverLastLocationId, Date time3MinutesEarlier, Date now);
    Integer countAllBy_idInAndServerTimeBetweenAndTripIdIsNotNull(List<String> driverLastLocationId, Date time3MinutesEarlier, Date now);

    List<MongoDriverLocation> findBy_idInOrderByServerTimeDesc(List<ObjectId> driverLastLocationId);

    List<MongoDriverLocation> findAllByTripIdOrderByServerTimeDesc(String tripId);
}
