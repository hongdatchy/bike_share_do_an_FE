/**
 * Copyright(C) 2022 SanLab Hust
 * class.java, 09/07/2022
 */
package com.google.codelabs.mdc.java.shrine.entities;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author hongdatchy
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractBikeResponse {

    private Integer id;

    private Integer userId;

    private Integer bikeId;

    private String paymentMethod;

    private Date startTime;

    private Date endTime;

    private Double distance;

    private String routes;

}
