/**
 * Copyright(C) 2022 SanLab Hust
 * class.java, 23/06/2022
 */
package com.google.codelabs.mdc.java.shrine.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * chứa các thông tin của user cần gửi cho client sau khi login
 *
 * @author hongdatchy
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponse {
    private String email;

    private String idNumber;

    private String phone;

    private String firstname;

    private String lastname;

    private String creditCard;

    private Date birthday;

    private Integer cityId;

    private Integer districtId;

    private Integer wardId;

    private String image;

    private String gender;

}
