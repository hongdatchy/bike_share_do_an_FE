package com.google.codelabs.mdc.java.shrine.entities;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterForm {

    private String email;
    private String idNumber;
    private String password;
    private String rePassword;
    private String phone;
    private String firstname;
    private String lastname;
    private Date birthday;
    private String gender;
    private Integer districtId;
    private Integer cityId;
    private Integer wardId;
}
