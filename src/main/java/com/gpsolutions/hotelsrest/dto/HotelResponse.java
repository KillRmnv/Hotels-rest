package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Brief information about the hotel")
public class HotelResponse {
    @Schema(description = "Hotel identifier",
            example = "1")
    private Long id;
    @Schema(description = "Name of the hotel",
            example = "DoubleTree by Hilton Minsk")
    private String name;
    @Schema(description = "Description of the hotel",
            example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital")
    private String description;
    @Schema(description = "Address as a single string",
            example = "9 Pobediteley Avenue, Minsk, 220004, Belarus")
    private String address;
    @Schema(description = "Reception phone number",
            example = "+375 17 309-80-00")
    private String phone;
}
