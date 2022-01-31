package com.example.examplequerydslspringdatajpamaven.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "tc_profile_taxi")
public class TaxiProfile{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "km_fare")
    private Double km_fare;

    @Column(name = "basic_fare")
    private Double basic_fare;

    @Column(name = "night_fare")
    private Double night_fare;

    @Column(name = "min_fare")
    private Double min_fare;

    @Column(name = "companyId")
    private Long companyId;

    @Column(name = "delete_date")
    private Date deleteDate;

}
