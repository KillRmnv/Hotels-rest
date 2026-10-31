package com.gpsolutions.hotelsrest.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;


@Schema(description = "Number of hotels grouped by the parameter value")
public record StatisticsResponse(@Schema(description = "Name of the value of the parameter",
        example = "Minsk") @JsonValue
                                 Map<String, Long> counts) {

    public static StatisticsResponse of(Map<String, Long> counts) {
        return new StatisticsResponse(Map.copyOf(counts));
    }
}
