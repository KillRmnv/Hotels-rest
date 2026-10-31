package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@Table(name = "hotels")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Embedded
    private ArrivalTime arrivalTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "contacts_id", referencedColumnName = "id")
    private Contacts contacts;

    public static Hotel of(
            String name, String description, Brand brand, Address address,
            Contacts contacts, ArrivalTime arrivalTime) {
        var hotel = new Hotel();
        hotel.name = name;
        hotel.description = description;
        hotel.brand = brand;
        hotel.address = address;
        hotel.contacts = contacts;
        hotel.arrivalTime = arrivalTime;
        hotel.amenities = Set.of();
        return hotel;
    }
}
