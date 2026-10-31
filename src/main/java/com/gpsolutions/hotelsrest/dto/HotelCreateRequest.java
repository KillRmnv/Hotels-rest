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
    public static class AddressCreateRequest {
        @NotNull
        @Positive
        private Integer houseNumber;
        @NotBlank
        private String street;
        @NotBlank
        private String city;
        @NotBlank
        private String country;
        @NotBlank
        private String postCode;
    }

    @Data
    public static class ContactsCreateRequest {
        @NotBlank
        private String phone;
        @NotBlank
        @Email
        private String email;
    }

    @Data
    public static class ArrivalTimeCreateRequest {
        @NotNull
        private LocalTime checkIn;
        private LocalTime checkOut;
    }
}
