package com.gpsolutions.hotelsrest.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;


public record StatisticsResponse(@JsonValue Map<String, Long> counts) {

    public static StatisticsResponse of(Map<String, Long> counts) {
        return new StatisticsResponse(Map.copyOf(counts));
    }
}
