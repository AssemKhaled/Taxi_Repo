package com.example.examplequerydslspringdatajpamaven.data.dtos;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String commercialNum;
    private String identityNum;
    private String companyNum;
    private String managerName;
    private String managerPhone;
    private String managerMobile;
    private String commercialReg;
    private String dateOfBirth;
    private Integer dateType;
    private String referenceKey;
    private Integer isDeleted;
    private String companyPhone;
    private String deleteDate;
    private String photo;
    private String rejectReason;
    private Integer isCompany;
    private Long roleId;
    private Integer accountType;
    private String parents;
    private String createDate;
    private String expDate;
    private Date registrationToElmDate;
    private Date deleteFromElmDate;
    private Date updateDateInElm;
    private String extensionNumber;
    private String deviceType;
    private String appToken;
    private Date birthDate;
}
