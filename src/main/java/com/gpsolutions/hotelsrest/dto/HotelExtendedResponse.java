package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Extended information about the hotel")
public class HotelExtendedResponse {
    @Schema(description = "Hotel identifier",
            example = "1")
    private Long id;
    @Schema(description = "Name of the hotel",
            example = "DoubleTree by Hilton Minsk")
    private String name;
    @Schema(description = "Description of the hotel",
            example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital")
    private String description;
    @Schema(description = "Name of the brand that owns the hotel",
            example = "Hilton")
    private String brand;
    @Schema(description = "Address of the hotel")
    private AddressResponse address;
    @Schema(description = "Contacts of the hotel")
    private ContactsResponse contacts;
    @Schema(description = "Arrival time of the hotel")
    private ArrivalTimeResponse arrivalTime;
    @Schema(description = "List of amenities of the hotel",
            example = "[\"Free parking\", \"Free WiFi\", \"Concierge\"]")
    private List<String> amenities;
}
