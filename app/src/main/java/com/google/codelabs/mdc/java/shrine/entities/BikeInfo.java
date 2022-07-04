/**
 * Copyright(C) 2022 SanLab Hust
 * class.java, 01/07/2022
 */
package com.google.codelabs.mdc.java.shrine.entities;

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
public class BikeInfo {

    private Integer id;

    private Integer stationId;

    private String frameNumber;

    private String productYear;

    private Integer deviceId;

    private Boolean statusLock;

    private Double longitude;

    private Double latitude;

    private Integer battery;
}
