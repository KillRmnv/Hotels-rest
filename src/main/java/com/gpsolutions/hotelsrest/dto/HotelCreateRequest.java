package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;


@Data
@Schema(description = "Hotel create response")
public class HotelCreateRequest {
    @Schema(description = "Name of the hotel",
            example = "transylvania")
    @NotBlank
    private String name;
    @Schema(description = "Description of the hotel",
            example = """
                    Hotel Transylvania is an American media franchise created by Todd Durham and owned by Sony Pictures Animation.
                    It consists of four animated feature films, three short films, an animated television series, and several video games.
                    """)
    private String description;
    @Schema(description = "Brand that owns the hotel",
            example = "Hilton")
    @NotBlank
    private String brand;
    @Schema(description = "Address of the hotel")
    @NotNull
    @Valid
    private AddressCreateRequest address;
    @Schema(description = "Contacts of the hotel")
    @NotNull
    @Valid
    private ContactsCreateRequest contacts;
    @Schema(description = "Detailed info about timestamps of the hotel")
    @Valid
    private ArrivalTimeCreateRequest arrivalTime;

    @Data
    @Schema(description = "Address of the hotel")
    public static class AddressCreateRequest {
        @Schema(description = "House number",
                example = "9")
        @NotNull
        @Positive
        private Integer houseNumber;
        @Schema(description = "Street name",
                example = "Pobediteley Avenue")
        @NotBlank
        private String street;
        @Schema(description = "City name",
                example = "Minsk")
        @NotBlank
        private String city;
        @Schema(description = "Country name",
                example = "Belarus")
        @NotBlank
        private String country;
        @Schema(description = "Post office code",
                example = "220004")
        @NotBlank
        private String postCode;
    }

    @Data
    @Schema(description = "Contacts of the hotel")
    public static class ContactsCreateRequest {
        @Schema(description = "Reception phone number",
                example = "+375 33 777-11-09")
        @NotBlank
        private String phone;
        @Schema(description = "Hotel email",
                example = "jevacation@gmail.com")
        @NotBlank
        @Email
        private String email;
    }

    @Data
    @Schema(description = "Arrival time of the hotel")
    public static class ArrivalTimeCreateRequest {
        @Schema(description = "Earliest check-in time",
                example = "14:14")
        @NotNull
        private LocalTime checkIn;
        @Schema(description = "Check-out deadline (optional)",
                example = "12:21")
        private LocalTime checkOut;
    }
}
