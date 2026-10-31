package com.gpsolutions.hotelsrest.dto;

import lombok.Data;

@Data
public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
}
