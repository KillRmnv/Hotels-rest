package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;

@Entity
@Table(name="brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
}
