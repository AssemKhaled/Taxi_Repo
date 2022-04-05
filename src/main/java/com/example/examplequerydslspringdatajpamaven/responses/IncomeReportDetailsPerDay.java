package com.example.examplequerydslspringdatajpamaven.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class IncomeReportDetailsPerDay {
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date PickupDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date filter;
    Double TotalIncome;
    Integer TotalNumberOfTrips;
    Double TotalVat;
    Double NetProfit;
    Double TotalCash;
    Double TotalCredit;
}
