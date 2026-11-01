package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.repository.AmenityRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class AmenitiesService {
    private final AmenityRepository amenityRepository;
    private final HotelsRepository hotelRepository;
    private final ReferenceUpserter upserter;
    private final TransactionTemplate transactions;

    public void addAmenitiesTo(Long hotelId, List<String> amenities) {
        transactions.execute(status -> findHotel(hotelId));

        var amenityIds = amenities.stream()
                .filter(raw -> raw != null && !raw.isBlank())
                .map(raw -> upserter.amenityId(raw.trim()))
                .distinct()
                .toList();

        transactions.execute(status -> {
            Hotel hotel = findHotel(hotelId);
            Set<Amenity> resolved = new LinkedHashSet<>();
            for (Long id : amenityIds) {
                resolved.add(amenityRepository.findById(id).orElseThrow());
            }
            hotel.getAmenities().addAll(resolved);
            return null;
        });
    }

    private Hotel findHotel(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> NotFoundException.hotel("There are no hotel id:%d".formatted(hotelId)));
    }
}
