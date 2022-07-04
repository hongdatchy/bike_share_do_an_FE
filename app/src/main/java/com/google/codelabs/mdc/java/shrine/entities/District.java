package com.google.codelabs.mdc.java.shrine.entities;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class District {

    private Integer id;

    private String name;

    private Integer cityId;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
