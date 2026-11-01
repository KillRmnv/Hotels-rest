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
import com.gpsolutions.hotelsrest.service.ReferenceUpserter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmenitiesServiceTest {
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private HotelsRepository hotelRepository;
    @Mock
    private ReferenceUpserter upserter;
    @Mock
    private TransactionTemplate transactions;
    @InjectMocks
    private AmenitiesService amenitiesService;

    @BeforeEach
    void runTransactionCallbacksInline() {
        lenient().when(transactions.execute(any())).thenAnswer(invocation ->
                invocation.<TransactionCallback<Object>>getArgument(0).doInTransaction(null));
    }

    @Test
    void addNewAmenities_shouldAttachThemToHotel() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var first = Amenity.of("First");
        var second = Amenity.of("Second");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(upserter.amenityId("First")).thenReturn(1L);
        when(upserter.amenityId("Second")).thenReturn(2L);
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(first));
        when(amenityRepository.findById(2L)).thenReturn(Optional.of(second));

        amenitiesService.addAmenitiesTo(1L, List.of("First", "Second"));

        assertEquals(2, hotel.getAmenities().size());
        assertTrue(hotel.getAmenities().contains(first));
        assertTrue(hotel.getAmenities().contains(second));
        verify(hotelRepository, never()).save(any());
    }

    @Test
    void addExistingAmenity_shouldReuseIdFromUpserter() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var existing = Amenity.of("Free WiFi");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(upserter.amenityId("Free WiFi")).thenReturn(5L);
        when(amenityRepository.findById(5L)).thenReturn(Optional.of(existing));

        amenitiesService.addAmenitiesTo(1L, List.of("Free WiFi"));

        assertEquals(1, hotel.getAmenities().size());
        assertTrue(hotel.getAmenities().contains(existing));
    }

    @Test
    void addDuplicatedAmenitiesInOneRequest_shouldAttachOnce() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var first = Amenity.of("First");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(upserter.amenityId(anyString())).thenReturn(1L);
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(first));

        amenitiesService.addAmenitiesTo(1L, List.of("First", "First"));

        assertEquals(1, hotel.getAmenities().size());
    }

    @Test
    void addAmenitiesToUnknownHotel_shouldThrowNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> amenitiesService.addAmenitiesTo(99L, List.of("First")));

        verify(upserter, never()).amenityId(anyString());
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
