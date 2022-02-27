package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class DashboardDriversLiveDataResponse {

    private Long driverId;
    private String driverName;
    private String driverStatus;
    private String lastUpdate;
    private Integer numberOfPassengers;
    private String vehicleName;
    private Double TodayIncome;
    private Integer numberOfCompletedTrips;
}
