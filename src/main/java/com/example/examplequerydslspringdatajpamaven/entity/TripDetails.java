package com.example.examplequerydslspringdatajpamaven.entity;


import com.example.examplequerydslspringdatajpamaven.responses.IncomeReportDetailsPerDay;
import com.example.examplequerydslspringdatajpamaven.responses.IncomeReportDetailsPerDriverOrVehicle;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@SqlResultSetMappings({
    @SqlResultSetMapping(
        name="incomeReportDetailsPerDayMapping",
        classes={
            @ConstructorResult(
                targetClass= IncomeReportDetailsPerDay.class,
                columns={
                    @ColumnResult(name="pickupDate",type=Date.class),
                    @ColumnResult(name="filter",type=Date.class),
                    @ColumnResult(name="totalIncome",type=Double.class),
                    @ColumnResult(name="totalNumberOfTrips",type=int.class),
                    @ColumnResult(name="TotalVat",type=Double.class),
                    @ColumnResult(name="NetProfit",type=Double.class),
                    @ColumnResult(name="TotalCash",type=Double.class),
                    @ColumnResult(name="TotalCredit",type=Double.class),
                }
            ),
        }
    ),
    @SqlResultSetMapping(
        name="incomeReportDetailsPerVehicleOrDriverMapping",
        classes={
            @ConstructorResult(
                targetClass= IncomeReportDetailsPerDriverOrVehicle.class,
                columns={
                    @ColumnResult(name="id",type=Long.class),
                    @ColumnResult(name="name",type=String.class),
                    @ColumnResult(name="filter",type=String.class),
                    @ColumnResult(name="totalIncome",type=Double.class),
                    @ColumnResult(name="totalNumberOfTrips",type=int.class),
                    @ColumnResult(name="TotalVat",type=Double.class),
                    @ColumnResult(name="NetProfit",type=Double.class),
                    @ColumnResult(name="TotalCash",type=Double.class),
                    @ColumnResult(name="TotalCredit",type=Double.class),
                }
            ),
        }
    ),
})

@NamedNativeQueries({
    @NamedNativeQuery(name="incomeReportDetailsPerDay",
        resultSetMapping="incomeReportDetailsPerDayMapping",
        query="Select tc_trip_details.pickup_date AS pickupDate, tc_trip_details.pickup_date AS filter," +
                "SUM(tc_trip_details.total_cost) AS totalIncome, COUNT(*) AS totalNumberOfTrips," +
                "SUM(tc_trip_details.total_vat) AS totalVat, SUM(tc_trip_details.actual_cost) AS netProfit," +
                "SUM(CASE WHEN tc_trip_details.payment_method = 0 THEN tc_trip_details.total_cost END ) totalCash," +
                "SUM(CASE WHEN tc_trip_details.payment_method = 1 THEN tc_trip_details.total_cost END ) totalCredit" +
                " FROM tc_trip_details Where tc_trip_details.driver_id IN(:driverIds)" +
                "and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_trip_details.pickup_date LIMIT :offset,10"
    ),
    @NamedNativeQuery(name="incomeReportDetailsPerDriver",
        resultSetMapping="incomeReportDetailsPerVehicleOrDriverMapping",
        query="Select tc_trip_details.driver_id AS id , tc_drivers.name AS name, tc_drivers.name AS filter," +
                " SUM(tc_trip_details.total_cost) AS totalIncome, COUNT(*) AS totalNumberOfTrips," +
                " SUM(tc_trip_details.total_vat) AS totalVat, SUM(tc_trip_details.actual_cost) AS NetProfit," +
                " SUM(CASE WHEN tc_trip_details.payment_method = 0 THEN tc_trip_details.total_cost END ) totalCash," +
                " SUM(CASE WHEN tc_trip_details.payment_method = 1 THEN tc_trip_details.total_cost END ) totalCredit" +
                " FROM tc_trip_details  INNER Join tc_drivers ON tc_trip_details.driver_id = tc_drivers.id" +
                " Where tc_trip_details.driver_id IN(:driverIds)" +
                " and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_trip_details.driver_id LIMIT :offset,10"
    ),
    @NamedNativeQuery(name="incomeReportDetailsPerDevice",
        resultSetMapping="incomeReportDetailsPerVehicleOrDriverMapping",
        query="Select tc_devices.id AS id , tc_devices.name AS name, tc_devices.name AS filter," +
                " SUM(tc_trip_details.total_cost) AS totalIncome, COUNT(*) AS totalNumberOfTrips," +
                " SUM(tc_trip_details.total_vat) AS totalVat, SUM(tc_trip_details.actual_cost) AS NetProfit," +
                " SUM(CASE WHEN tc_trip_details.payment_method = 0 THEN tc_trip_details.total_cost END ) totalCash," +
                " SUM(CASE WHEN tc_trip_details.payment_method = 1 THEN tc_trip_details.total_cost END ) totalCredit" +
                " FROM tc_trip_details  INNER Join tc_devices ON tc_trip_details.driver_id = tc_devices.driverId" +
                " INNER Join tc_device_driver ON tc_trip_details.driver_id = tc_device_driver.driverid" +
                " Where tc_trip_details.driver_id IN(:driverIds)" +
                " and tc_trip_details.pickup_date_time BETWEEN :start AND :end GROUP BY tc_devices.id LIMIT :offset,10"
    )
})

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "tc_trip_details")
public class TripDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "pickup_area")
    private String pickupArea;

    @Column(name = "drop_area")
    private String dropArea;

    @Column(name = "pickup_date_time")
    private Date pickupDatetime;

    @Column(name = "pickup_date")
    private Date pickupDate;

    @Column(name = "drop_date_time")
    private Date dropDateTime;

    @Column(name = "vehicle_status")
    private String vehicleStatus;

    @Column(name = "numberOfPassengers")
    private Integer numberOfPassengers;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "payment_method")
    private Integer paymentMethod;

    @Column(name = "pickup_lat")
    private Double pickupLat;

    @Column(name = "pickup_long")
    private Double pickupLong;

    @Column(name = "drop_lat")
    private Long dropLat;

    @Column(name = "drop_long")
    private Long dropLong;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "driverEmergencyButtonStatus")
    private Boolean driverEmergencyButtonStatus;

    @Column(name = "passengerEmergencyButtonStatus")
    private Boolean passengerEmergencyButtonStatus;

    @Column(name = "trip_num")
    private Double tripNum;

    @Column(name = "duration")
    private String duration;

    @Column(name = "trip_local_id")
    private String tripLocalId;

    @Column(name = "total_distance_cost")
    private Double totalDistanceCost;

    @Column(name = "total_waiting_cost")
    private Double totalWaitingCost;

    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "total_vat")
    private Double totalVat;

    @Column(name = "total_waiting_time")
    private Long totalWaitingTime;

    @Column(name = "basic_cost")
    private Long basicCost;

    @Column(name = "actual_cost")
    private Long actualCost;

}


