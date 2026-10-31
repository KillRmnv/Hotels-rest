package com.gpsolutions.hotelsrest.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class HotelExtendedResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private AddressResponse address;
    private ContactsResponse contacts;
    private ArrivalTimeResponse arrivalTime;
    private List<String> amenities;
}
