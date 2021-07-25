package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class DevicesMapResponse {
    private Long id;
    private String deviceName;
    private Integer vehicleStatus;
    private Double latitude;
    private Double longitude;
    private Double speed ;
    private String icon;

}
