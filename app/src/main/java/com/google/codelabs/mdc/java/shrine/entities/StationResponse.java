package com.google.codelabs.mdc.java.shrine.entities;

import lombok.Data;

@Data
public class StationResponse {

    private Integer id;

    private String name;

    private String location;

    private Integer currentNumberCar;

    private Integer slotQuantity;

    private Double latitude;

    private Double longitude;

}
