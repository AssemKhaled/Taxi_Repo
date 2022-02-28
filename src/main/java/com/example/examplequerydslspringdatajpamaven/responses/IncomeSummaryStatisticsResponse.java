package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class IncomeSummaryStatisticsResponse {
    private Double totalIncome;
    private Integer totalNumberOfTrips;
    private Double totalVat;
    private Double totalCash;
    private Double totalCredit;
    private Double netProfit;
}
