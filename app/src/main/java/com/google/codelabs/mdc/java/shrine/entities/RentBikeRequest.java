package com.google.codelabs.mdc.java.shrine.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RentBikeRequest {
    int bikeId;
    String paymentMethod;
}
