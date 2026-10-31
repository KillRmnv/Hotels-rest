package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public static Amenity of(String name) {
        var amenity = new Amenity();
        amenity.name = name;
        return amenity;
    }
}
