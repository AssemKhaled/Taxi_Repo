package com.example.examplequerydslspringdatajpamaven.data.mapper;

import com.example.examplequerydslspringdatajpamaven.data.dtos.TaxiProfileDto;
import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor

public class TaxiProfileMapper {

    public TaxiProfileDto entityToDto(TaxiProfile taxiProfile){
        return TaxiProfileDto.builder()
                .id(taxiProfile.getId())
                .name(taxiProfile.getName())
                .km_fare(taxiProfile.getKm_fare())
                .basic_fare(taxiProfile.getBasic_fare())
                .night_fare(taxiProfile.getNight_fare())
                .min_fare(taxiProfile.getMin_fare())
                .companyId(taxiProfile.getCompanyId())
                .delete_date(taxiProfile.getDeleteDate())
                .build();
    }

    public TaxiProfile DtoToEntity(TaxiProfileDto taxiProfileDto){

        return TaxiProfile.builder()
                .id(taxiProfileDto.getId())
                .name(taxiProfileDto.getName())
                .km_fare(taxiProfileDto.getKm_fare())
                .basic_fare(taxiProfileDto.getBasic_fare())
                .night_fare(taxiProfileDto.getNight_fare())
                .min_fare(taxiProfileDto.getMin_fare())
                .companyId(taxiProfileDto.getCompanyId())
                .deleteDate(null)
                .build();
    }
}
