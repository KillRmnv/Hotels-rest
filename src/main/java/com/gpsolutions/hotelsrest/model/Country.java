package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="countries")
public class Country {
    private Long id;

    private String name;
}
