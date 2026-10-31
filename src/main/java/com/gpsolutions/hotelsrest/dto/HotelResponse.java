package com.gpsolutions.hotelsrest.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HotelResponse {
    Long id;
    String name;
    String description;
    String address;
    String phone;
}
