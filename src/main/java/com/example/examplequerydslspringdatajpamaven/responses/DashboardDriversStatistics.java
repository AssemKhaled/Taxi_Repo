package com.example.examplequerydslspringdatajpamaven.responses;

import com.example.examplequerydslspringdatajpamaven.entity.MongoDriverLocation;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class DashboardDriversStatistics {

    private List<MongoDriverLocation> mongoDriverLocationsIdle;
    private List<MongoDriverLocation> mongoDriverLocationsOnTrip;

}
