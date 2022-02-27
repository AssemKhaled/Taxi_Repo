package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.TripDetails;
import com.example.examplequerydslspringdatajpamaven.responses.TotalIncomeForDrivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TripDetailsRepository extends JpaRepository<TripDetails, Long> {

    List<TripDetails> findAllByDriverIdInAndDropDateTimeIsNotNull(List<Long> driversId);
    List<TripDetails> findAllByDriverIdInAndPickupDatetimeIsNotNullAndDropDateTimeIsNull(List<Long> driversId);

    Integer countAllByDriverIdInAndPickupDateAndDropDateTimeIsNotNull(List<Long> driversId, Date today);
    Integer countAllByDriverIdInAndPickupDateAndDropDateTimeIsNull(List<Long> driversId, Date today);

    @Query(value = "Select SUM(tc_trip_details.total_cost) From tc_trip_details Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date =:today", nativeQuery = true)
    Double totalTodayIncome(@Param("driverIds") List<Long> driverIds, @Param("today") Date today);

    @Query(value = "Select tc_trip_details.driver_id, SUM(tc_trip_details.total_cost), COUNT(tc_trip_details.id) AS numberOfCompletedTrips" +
            " From tc_trip_details Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date =:today GROUP BY tc_trip_details.driver_id", nativeQuery = true)
    List<TotalIncomeForDrivers> totalTodayIncomeByDriversIds(@Param("driverIds") List<Long> driverIds, @Param("today") Date today);
}
