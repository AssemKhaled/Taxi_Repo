package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class DashboardTripsStatistics {
    private Integer numberOfTodayCompletedTrips;
    private Integer numberOfTodayCurrentTrips;
    private Double numberOfTodayTotalIncome;
}
