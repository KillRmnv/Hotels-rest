package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "streets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Street {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "streets_cities",
            joinColumns = @JoinColumn(name = "street_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private Set<City> cities;

    public static Street of(String name) {
        var street = new Street();
        street.name = name;
        street.cities = new HashSet<>();
        return street;
    }
}
