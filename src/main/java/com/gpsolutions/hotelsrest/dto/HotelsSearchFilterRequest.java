package com.gpsolutions.hotelsrest.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class HotelsSearchFilterRequest {
    String name;
    String brand;
    String city;
    String country;
    String amenities;
}
