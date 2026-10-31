package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.model.ArrivalTime;
import com.gpsolutions.hotelsrest.model.Brand;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.model.Street;
import com.gpsolutions.hotelsrest.repository.AmenityRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.service.AmenitiesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmenitiesServiceTest {
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private HotelsRepository hotelRepository;
    @InjectMocks
    private AmenitiesService amenitiesService;

    @Test
    void addAllNewAmenities_shouldAttachThemToHotel() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(amenityRepository.save(any(Amenity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        amenitiesService.addAmenitiesTo(1L, List.of("First", "Second"));

        assertEquals(2, hotel.getAmenities().size());
        assertTrue(hotel.getAmenities().stream()
                .allMatch(a -> a.getName().equals("First") || a.getName().equals("Second")));
        verify(amenityRepository, times(2)).save(any(Amenity.class));
        verify(hotelRepository, never()).save(any());
    }

    @Test
    void addExistingAmenity_shouldReuseItInsteadOfCreating() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var existing = Amenity.of("Free WiFi");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findByNameIgnoreCase("Free WiFi")).thenReturn(Optional.of(existing));

        amenitiesService.addAmenitiesTo(1L, List.of("Free WiFi"));

        assertEquals(1, hotel.getAmenities().size());
        assertTrue(hotel.getAmenities().contains(existing));
        verify(amenityRepository, never()).save(any(Amenity.class));
    }

    @Test
    void addDuplicatedAmenitiesInOneRequest_shouldAttachOnce() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var stored = new AtomicReference<Amenity>();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findByNameIgnoreCase(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        when(amenityRepository.save(any(Amenity.class))).thenAnswer(invocation -> {
            stored.set(invocation.getArgument(0));
            return stored.get();
        });

        amenitiesService.addAmenitiesTo(1L, List.of("First", "First"));

        assertEquals(1, hotel.getAmenities().size());
        verify(amenityRepository, times(1)).save(any(Amenity.class));
    }

    @Test
    void addAmenitiesToUnknownHotel_shouldThrowNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> amenitiesService.addAmenitiesTo(99L, List.of("First")));

        verify(amenityRepository, never()).save(any(Amenity.class));
    }

    private Hotel setupHotel(String brandName, String streetName, String name, String description) {
        var brand = Brand.of(brandName);
        var street = Street.of(streetName);
        var address = Address.of(1, street, "220022");
        var contacts = Contacts.of("+375 33 379-07-68", "jevacation@gmail.com");
        var arrivalTime = ArrivalTime.of(LocalTime.of(12, 22), LocalTime.of(14, 14));
        return Hotel.of(name, description, brand, address, contacts, arrivalTime);
    }
}
