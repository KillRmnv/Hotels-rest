package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.service.AmenitiesService;
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
@RequestMapping("/property-view")
public class AmenitiesController {
    private final AmenitiesService amenitiesService;

    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<Void> addAmenities(
            @PathVariable Long id,
            @RequestBody @NotEmpty List<String> amenities) {
        amenitiesService.addAmenitiesTo(id, amenities);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
