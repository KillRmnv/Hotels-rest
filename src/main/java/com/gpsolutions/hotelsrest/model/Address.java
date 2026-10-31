package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer houseNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "street_id", nullable = false)
    private Street street;
    private String postCode;

    public static Address of(Integer houseNumber, Street street, String postCode) {
        var address = new Address();
        address.houseNumber = houseNumber;
        address.street = street;
        address.postCode = postCode;
        return address;
    }
}
