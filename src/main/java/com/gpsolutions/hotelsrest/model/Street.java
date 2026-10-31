package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;

@Entity
@Table(name="streets")
public class Street {
    private Long id;
    private String name;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "streets_cities",
            joinColumns = @JoinColumn(name = "street_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private City cityIn;
}
