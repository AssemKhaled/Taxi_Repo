package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class IncomeReportDetailsPerDriverOrVehicle {

   Long Id;
   String Name;
   String filter;
   Double TotalIncome;
   Integer TotalNumberOfTrips;
   Double TotalVat;
   Double NetProfit;
   Double TotalCash;
   Double TotalCredit;
}
