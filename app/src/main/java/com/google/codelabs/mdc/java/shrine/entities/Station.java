package com.google.codelabs.mdc.java.shrine.entities;

import lombok.Data;

@Data
public class Station {

    private Integer id;

    private String name;

    private String location;

    private Integer currentNumberCar;

    private Integer slotQuantity;

    private Double latitude;

    private Double longitude;

}
