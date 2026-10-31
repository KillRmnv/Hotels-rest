package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;

@Entity
@Table(name="addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long houseNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "street_id", nullable = false)
    private Street street;
    private Long postCode;
}
