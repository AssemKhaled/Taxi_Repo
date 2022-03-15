package com.example.examplequerydslspringdatajpamaven.responses;

import java.util.Date;

public interface IncomeReportDetailsPerDay {
    Date getPickupDate();
    Double getTotalIncome();
    Integer getTotalNumberOfTrips();
    Double getTotalVat();
    Double getNetProfit();
    Double getTotalCash();
    Double getTotalCredit();
}
