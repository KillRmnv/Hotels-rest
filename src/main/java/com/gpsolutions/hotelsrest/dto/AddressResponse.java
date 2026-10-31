package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response about address")
public class AddressResponse {
    @Schema(description = "Number of house on the street",
    example = "23")
    private Integer houseNumber;
    @Schema(description = "Street name",
            example = "Pobediteley Avenue")
    private String street;
    @Schema(description = "City name",
            example = "Minsk")
    private String city;
    @Schema(description = "Country name",
            example = "Belarus")
    private String country;
    @Schema(description = "Post office code",
            example = "220004")
    private String postCode;
}
