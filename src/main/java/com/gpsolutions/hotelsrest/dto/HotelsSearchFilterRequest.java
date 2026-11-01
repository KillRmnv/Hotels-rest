package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hotel search filter")
public class HotelsSearchFilterRequest {
    @Schema(description = "Partial match on the hotel name (case-insensitive)",
            example = "DoubleTree")
    private String name;
    @Schema(description = "Partial match on the brand (case-insensitive)",
            example = "Hilton")
    private String brand;
    @Schema(description = "Partial match on the city (case-insensitive)",
            example = "Minsk")
    private String city;
    @Schema(description = "Partial match on the country (case-insensitive)",
            example = "Belarus")
    private String country;
    @Schema(description = "Comma-separated list of amenities; a hotel matches if at least one amenity matches",
            example = "Free WiFi, Concierge")
    private String amenities;
}
