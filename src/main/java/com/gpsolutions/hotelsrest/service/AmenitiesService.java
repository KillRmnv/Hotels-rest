package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.repository.AmenityRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class AmenitiesService {
    private final AmenityRepository amenityRepository;
    private final HotelsRepository hotelRepository;

    @Transactional
    public void addAmenitiesTo(Long hotelId, List<String> amenities) {
        var hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> NotFoundException.hotel("There are no hotel id:%d".formatted(hotelId)));

        Set<Amenity> resolved = new LinkedHashSet<>();
        for (String raw : amenities) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            resolved.add(findOrCreate(raw.trim()));
        }

        hotel.getAmenities().addAll(resolved);
    }

    private Amenity findOrCreate(String name) {
        return amenityRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> amenityRepository.save(Amenity.of(name)));
    }
}
