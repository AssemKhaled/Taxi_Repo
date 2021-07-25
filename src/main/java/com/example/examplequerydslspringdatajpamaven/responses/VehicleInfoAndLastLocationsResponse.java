package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;
import java.util.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VehicleInfoAndLastLocationsResponse {
    private Long id;
    private String lastUpdate;
    private String lastUpdateApp;
    private String positionId;
    private Double power;
    private String address;
    private List lastPoints;
    private String driverName;
}
