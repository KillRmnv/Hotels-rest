package com.gpsolutions.hotelsrest.dto;

import lombok.Data;

@Data
public class HotelsSearchFilterRequest {
    private String name;
    private String brand;
    private String city;
    private String country;
    private String amenities;
}
