package com.example.examplequerydslspringdatajpamaven.responses;


import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class IncomeReportDetailsPerDriverOrVehiclee {
    Long getId;
    String getName;
    Double getTotalIncome;
    Integer getTotalNumberOfTrips;
    Double getTotalVat;
//    Integer getNetProfit;
//    Double getTotalCash;
//    Double getTotalCredit;
}
