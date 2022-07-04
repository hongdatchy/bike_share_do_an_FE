package com.google.codelabs.mdc.java.shrine.entities;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ward {

    private Integer id;

    private String name;

    private Integer districtId;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
