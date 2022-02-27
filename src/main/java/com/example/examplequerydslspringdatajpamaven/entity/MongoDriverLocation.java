package com.example.examplequerydslspringdatajpamaven.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;

@Document(collection = "driver_location")
public class MongoDriverLocation {

    @Id
    private ObjectId _id;

    @Field("id")
    private Long id;

    @Field("driver_id")
    private Long driverId;

    @Field("driver_lat")
    private String driverLat;

    @Field("driver_long")
    private String driverLong;

    @Field("trip_id")
    private String tripId;

    @Field("location_time")
    private String locationTime;

    @Field("server_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date serverTime;

    @Field("speed")
    private Double speed;

    @Field("address")
    private String address;

    @Field("weight")
    private Double weight;

    @Field("vehicle_status")
    private String vehicleStatus;

    @Field("driverEmergencyButtonStatus")
    private Boolean driverEmergencyButtonStatus;

    @Field("passengerEmergencyButtonStatus")
    private Boolean passengerEmergencyButtonStatus;

    @Field("current_trip_cost")
    private Double currentTripCost;

    @Field("distance")
    private Double distance;

    @Field("duration")
    private String duration;

    @Field("distance_cost")
    private Double distanceCost;

    @Field("waiting_cost")
    private Double waitingCost;

    @Field("number_of_passengers")
    private Integer numberOfPassengers;


    @Field("driver_name")
    private String driverName;

    public MongoDriverLocation(ObjectId _id, Long id, Long driverId, String driverLat, String driverLong, String tripId, String locationTime,
                               Date serverTime, Double speed, String address, Double weight, String vehicleStatus, Boolean driverEmergencyButtonStatus,
                               Boolean passengerEmergencyButtonStatus, Double currentTripCost, Double distance, String duration, Double distanceCost,
                               Double waitingCost, Integer numberOfPassengers) {
        this._id = _id;
        this.id = id;
        this.driverId = driverId;
        this.driverLat = driverLat;
        this.driverLong = driverLong;
        this.tripId = tripId;
        this.locationTime = locationTime;
        this.serverTime = serverTime;
        this.speed = speed;
        this.address = address;
        this.weight = weight;
        this.vehicleStatus = vehicleStatus;
        this.driverEmergencyButtonStatus = driverEmergencyButtonStatus;
        this.passengerEmergencyButtonStatus = passengerEmergencyButtonStatus;
        this.currentTripCost = currentTripCost;
        this.distance = distance;
        this.duration = duration;
        this.distanceCost = distanceCost;
        this.waitingCost = waitingCost;
        this.numberOfPassengers = numberOfPassengers;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(String driverLat) {
        this.driverLat = driverLat;
    }

    public String getDriverLong() {
        return driverLong;
    }

    public void setDriverLong(String driverLong) {
        this.driverLong = driverLong;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Boolean getDriverEmergencyButtonStatus() {
        return driverEmergencyButtonStatus;
    }

    public void setDriverEmergencyButtonStatus(Boolean driverEmergencyButtonStatus) {
        this.driverEmergencyButtonStatus = driverEmergencyButtonStatus;
    }

    public Boolean getPassengerEmergencyButtonStatus() {
        return passengerEmergencyButtonStatus;
    }

    public void setPassengerEmergencyButtonStatus(Boolean passengerEmergencyButtonStatus) {
        this.passengerEmergencyButtonStatus = passengerEmergencyButtonStatus;
    }

    public Double getCurrentTripCost() {
        return currentTripCost;
    }

    public void setCurrentTripCost(Double currentTripCost) {
        this.currentTripCost = currentTripCost;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(Double distanceCost) {
        this.distanceCost = distanceCost;
    }

    public Double getWaitingCost() {
        return waitingCost;
    }

    public void setWaitingCost(Double waitingCost) {
        this.waitingCost = waitingCost;
    }

    public Integer getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(Integer numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}
