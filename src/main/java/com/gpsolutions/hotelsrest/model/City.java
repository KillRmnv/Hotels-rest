package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public static City of(String name, Country country) {
        var city = new City();
        city.name = name;
        city.country = country;
        return city;
    }
}
