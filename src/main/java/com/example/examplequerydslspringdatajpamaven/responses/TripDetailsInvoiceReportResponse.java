package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class TripDetailsInvoiceReportResponse {
    private String tripId;
    private String driverName;
    private String vehicleName;
    private Date pickupDatetime;
    private Date dropDateTime;
    private String duration;
    private Double totalCost;
    private Double distance;
    private Integer paymentMethod;
    private Double totalWaitingCost;
    private Double totalDistanceCost;
    private Long basicCost;
    private Double totalVat;
    private Long actualCost;
}
