package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.service.AmenitiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Amenities API",
        description = "API for management of amenity entities")
@RequestMapping("/property-view")
public class AmenitiesController {
    private final AmenitiesService amenitiesService;
    @Operation(
            summary = "Add list of amenities to the hotel with id",
            description = "Adds new amenities to the amenities that stored in the database and updates set of amenities of the hotel with id"
    )
    @ApiResponse(responseCode = "201", description = "Amenities saved and added to the hotel entity")
    @ApiResponse(responseCode = "404", description = "Hotel with the given id not found")

    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<Void> addAmenities(
            @PathVariable Long id,
            @RequestBody @NotEmpty List<String> amenities) {
        amenitiesService.addAmenitiesTo(id, amenities);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
