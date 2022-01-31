package com.example.examplequerydslspringdatajpamaven.data.dtos;

import lombok.*;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxiProfileDto {
    private Long id;
    private String name;
    private Double km_fare;
    private Double basic_fare;
    private Double night_fare;
    private Double min_fare;
    private Long companyId;
    private Date delete_date;

}
