package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.model.Brand;
import com.gpsolutions.hotelsrest.model.City;
import com.gpsolutions.hotelsrest.model.Country;
import com.gpsolutions.hotelsrest.model.Street;
import com.gpsolutions.hotelsrest.repository.AmenityRepository;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.CityRepository;
import com.gpsolutions.hotelsrest.repository.CountryRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.service.ReferenceUpserter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceUpserterTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private StreetRepository streetRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private ReferenceUpserter upserter;

    @Test
    void brandId_shouldReuseExistingWithoutInserting() {
        var existing = Brand.of("Hilton");
        existing.setId(7L);
        when(brandRepository.findByNameIgnoreCase("Hilton")).thenReturn(Optional.of(existing));

        assertEquals(7L, upserter.brandId("Hilton"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void brandId_shouldCreateWhenMissing() {
        when(brandRepository.findByNameIgnoreCase("Hilton")).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand saved = invocation.getArgument(0);
            saved.setId(8L);
            return saved;
        });

        assertEquals(8L, upserter.brandId("Hilton"));
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    void brandId_shouldRetryAndReuseWhenConcurrentInsertWonTheRace() {
        var committedByOtherRequest = Brand.of("Hilton");
        committedByOtherRequest.setId(9L);
        when(brandRepository.findByNameIgnoreCase("Hilton"))
                .thenReturn(Optional.empty(), Optional.of(committedByOtherRequest));
        when(brandRepository.save(any(Brand.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key UQ_BRANDS_NAME"));

        assertEquals(9L, upserter.brandId("Hilton"));
        verify(brandRepository, times(2)).findByNameIgnoreCase("Hilton");
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void countryId_shouldReuseExisting() {
        var existing = Country.of("Belarus");
        existing.setId(2L);
        when(countryRepository.findByNameIgnoreCase("Belarus")).thenReturn(Optional.of(existing));

        assertEquals(2L, upserter.countryId("Belarus"));
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void amenityId_shouldRetryAndReuseWhenConcurrentInsertWonTheRace() {
        var committedByOtherRequest = Amenity.of("Free WiFi");
        committedByOtherRequest.setId(5L);
        when(amenityRepository.findByNameIgnoreCase("Free WiFi"))
                .thenReturn(Optional.empty(), Optional.of(committedByOtherRequest));
        when(amenityRepository.save(any(Amenity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key UQ_AMENITIES_NAME"));

        assertEquals(5L, upserter.amenityId("Free WiFi"));
        verify(amenityRepository, times(2)).findByNameIgnoreCase("Free WiFi");
    }

    @Test
    void streetId_shouldLinkExistingStreetToCityOnce() {
        var city = City.of("Minsk", Country.of("Belarus"));
        city.setId(3L);
        var street = Street.of("Pobediteley Avenue");
        street.setId(4L);
        street.getCities().add(city);
        when(streetRepository.findByNameIgnoreCase("Pobediteley Avenue")).thenReturn(Optional.of(street));
        when(streetRepository.save(street)).thenReturn(street);

        assertEquals(4L, upserter.streetId("Pobediteley Avenue", 3L));
        verify(cityRepository, never()).getReferenceById(any());
    }
}
