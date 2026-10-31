package com.gpsolutions.hotelsrest.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private Integer houseNumber;
    private String street;
    private String city;
    private String country;
    private String postCode;
}
