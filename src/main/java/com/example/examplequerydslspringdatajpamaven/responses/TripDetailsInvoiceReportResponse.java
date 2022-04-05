package com.example.examplequerydslspringdatajpamaven.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class TripDetailsInvoiceReportResponse {
    private String tripId;
    private Long driverId;
    private String driverName;
    private String driverUniqueId;
    private String vehicleName;
    private String pickupDatetime;
    private String dropDateTime;
    private String duration;
    private Double totalCost;
    private Double distance;
    private Integer paymentMethod;
    private Double totalWaitingCost;
    private Double totalDistanceCost;
    private String totalWaitingTime;
    private Long basicCost;
    private Double totalVat;
    private Long actualCost;
}
