package com.example.examplequerydslspringdatajpamaven.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Date;

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

