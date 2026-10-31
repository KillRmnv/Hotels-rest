package com.gpsolutions.hotelsrest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;


@Data
public class HotelCreateRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String brand;

    @NotNull
    @Valid
    private AddressCreateRequest address;

    @NotNull
    @Valid
    private ContactsCreateRequest contacts;

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
