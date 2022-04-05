package com.example.examplequerydslspringdatajpamaven.helper;

import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Repository
public class Utilities {
    public String timeZoneConverter(Date date, String timeOffset){
        if(timeOffset.contains("%2B")){
            timeOffset = "+" + timeOffset.substring(3);
        }
        if(date != null){
            ZoneOffset zo = ZoneOffset.of(timeOffset);
            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
            return String.valueOf(odt).substring(0,19).replace("T", " ");
        }
        return null;
    }

}
