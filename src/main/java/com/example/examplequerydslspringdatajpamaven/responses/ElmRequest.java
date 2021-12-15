package com.example.examplequerydslspringdatajpamaven.responses;


import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder

public class ElmRequest {
    private Map requestData;
    private Map responseData;
}