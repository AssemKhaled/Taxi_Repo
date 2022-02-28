package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.TripDetails;
import com.example.examplequerydslspringdatajpamaven.responses.IncomeSummaryReportTotals;
import com.example.examplequerydslspringdatajpamaven.responses.IncomeSummaryStatisticsPerDayChart;
import com.example.examplequerydslspringdatajpamaven.responses.IncomeSummaryStatisticsPerDriverOrVehicleChart;
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

    @Query(value = "Select SUM(tc_trip_details.total_cost) AS totalIncome, COUNT(*) AS totalNumberOfTrips, SUM(tc_trip_details.total_vat) AS totalVat" +
            " From tc_trip_details Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date_time BETWEEN :start AND :end", nativeQuery = true)
    List<IncomeSummaryReportTotals> incomeSummaryStatistics(@Param("driverIds") List<Long> driverIds, @Param("start") Date start,@Param("end") Date end);

    @Query(value = "Select SUM(tc_trip_details.total_cost) "+
            " From tc_trip_details Where tc_trip_details.driver_id IN(:driverIds) AND tc_trip_details.payment_method =:method "
            + "AND tc_trip_details.pickup_date_time BETWEEN :start AND :end", nativeQuery = true)
    Double totalCostByPaymentMethod(@Param("driverIds") List<Long> driverIds, @Param("method") Integer method,  @Param("start") Date start,@Param("end") Date end);

    @Query(value = "Select  tc_trip_details.pickup_date AS pickupDate , SUM(tc_trip_details.total_cost) AS totalIncome" +
            " From tc_trip_details Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_trip_details.pickup_date", nativeQuery = true)
    List<IncomeSummaryStatisticsPerDayChart> incomeSummaryStatisticsChartsPerDay(@Param("driverIds") List<Long> driverIds,
                                                                                 @Param("start") Date start, @Param("end") Date end);

    @Query(value = "Select tc_trip_details.driver_id AS id , tc_drivers.name AS name, SUM(tc_trip_details.total_cost) AS totalIncome" +
            " From tc_trip_details  INNER Join tc_drivers ON tc_trip_details.driver_id = tc_drivers.id" +
            " Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_trip_details.driver_id", nativeQuery = true)
    List<IncomeSummaryStatisticsPerDriverOrVehicleChart> incomeSummaryStatisticsChartsPerDriver(@Param("driverIds") List<Long> driverIds,
                                                                                                @Param("start") Date start, @Param("end") Date end);

    @Query(value = "Select tc_devices.id AS id , tc_devices.name AS name, SUM(tc_trip_details.total_cost) AS totalIncome" +
            " From tc_trip_details  INNER Join tc_devices ON tc_trip_details.driver_id = tc_devices.driverId" +
            " INNER Join tc_device_driver ON tc_trip_details.driver_id = tc_device_driver.driverid" +
            " Where tc_trip_details.driver_id IN(:driverIds)"
            + " and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_devices.id", nativeQuery = true)
    List<IncomeSummaryStatisticsPerDriverOrVehicleChart> incomeSummaryStatisticsChartsPerDevice(@Param("driverIds") List<Long> driverIds,
                                                                                                @Param("start") Date start, @Param("end") Date end);
}