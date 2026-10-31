package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.ArrivalTime;
import com.gpsolutions.hotelsrest.model.Brand;
import com.gpsolutions.hotelsrest.model.City;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Country;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.model.Street;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.CityRepository;
import com.gpsolutions.hotelsrest.repository.CountryRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.service.HotelsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelsServiceTest {

    @Mock
    private HotelsRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private StreetRepository streetRepository;

    @InjectMocks
    private HotelsService hotelService;

    @Test
    void getAllHotels_shouldReturnMappedList() {
        var hotel1 = setupHotel("brand", "street", "name", "descr");
        var hotel2 = setupHotel("brand1", "street", "name1", "descr1");
        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2));
        when(hotelMapper.toHotelResponse(any())).thenReturn(
                hotelResponse(1L, "name"),
                hotelResponse(2L, "name1"));

        var result = hotelService.getAllHotels();

        assertEquals(2, result.size());
        assertEquals("name", result.get(0).getName());
        assertEquals("name1", result.get(1).getName());
        verify(hotelRepository).findAll();
        verify(hotelMapper).toHotelResponse(hotel1);
        verify(hotelMapper).toHotelResponse(hotel2);
    }

    @Test
    void getHotelById_shouldReturnExtendedResponse() {
        var hotel = setupHotel("brand", "street", "name", "descr");
        var expected = HotelExtendedResponse.builder()
                .id(1L)
                .name("name")
                .brand("brand")
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toHotelExtendedResponse(hotel)).thenReturn(expected);

        var result = hotelService.getHotel(1L);

        assertEquals(expected, result);

    }

    @Test
    void getHotelById_shouldThrowWhenHotelNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> hotelService.getHotel(99L));

        assertTrue(exception.getMessage().contains("99"));

    }

    @Test
    void createHotel_shouldReturnCreatedHotel() {
        var request = hotelCreateRequest();
        var hotel = setupHotel("brand", "street", "name", "descr");
        var expected = hotelResponse(1L, "name");

        when(brandRepository.findByNameIgnoreCase("brand")).thenReturn(Optional.empty());
        when(brandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(countryRepository.findByNameIgnoreCase("Belarus")).thenReturn(Optional.empty());
        when(countryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(cityRepository.findByNameIgnoreCase("Minsk")).thenReturn(Optional.empty());
        when(cityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(streetRepository.findByNameIgnoreCase("street")).thenReturn(Optional.empty());
        when(streetRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(hotelRepository.save(any())).thenReturn(hotel);
        when(hotelMapper.toHotelResponse(hotel)).thenReturn(expected);

        var result = hotelService.create(request);

        assertEquals(expected, result);
        verify(brandRepository).save(any());
        verify(countryRepository).save(any());
        verify(cityRepository).save(any());
        verify(streetRepository).save(any());
        verify(hotelRepository).save(any());
    }

    @Test
    void createHotel_shouldReuseExistingReferenceData() {
        var request = hotelCreateRequest();
        var brand = Brand.of("brand");
        var country = Country.of("Belarus");
        var city = City.of("Minsk", country);
        var street = Street.of("street");
        street.getCities().add(city);
        var hotel = setupHotel("brand", "street", "name", "descr");

        when(brandRepository.findByNameIgnoreCase("brand")).thenReturn(Optional.of(brand));
        when(countryRepository.findByNameIgnoreCase("Belarus")).thenReturn(Optional.of(country));
        when(cityRepository.findByNameIgnoreCase("Minsk")).thenReturn(Optional.of(city));
        when(streetRepository.findByNameIgnoreCase("street")).thenReturn(Optional.of(street));
        when(hotelRepository.save(any())).thenReturn(hotel);
        when(hotelMapper.toHotelResponse(hotel)).thenReturn(hotelResponse(1L, "name"));

        hotelService.create(request);

        verify(brandRepository, never()).save(any());
        verify(countryRepository, never()).save(any());
        verify(cityRepository, never()).save(any());
        verify(streetRepository, never()).save(any());
        verify(hotelRepository).save(any());
    }

    private HotelResponse hotelResponse(Long id, String name) {
        return HotelResponse.builder()
                .id(id)
                .name(name)
                .description("descr")
                .address("1 street, Minsk, 220022, Belarus")
                .phone("+375 33 379-07-68")
                .build();
    }

    private HotelCreateRequest hotelCreateRequest() {
        return HotelCreateRequest.builder()
                .name("name")
                .description("descr")
                .brand("brand")
                .address(HotelCreateRequest.AddressCreateRequest.builder()
                        .houseNumber(1)
                        .street("street")
                        .city("Minsk")
                        .country("Belarus")
                        .postCode("220022")
                        .build())
                .contacts(HotelCreateRequest.ContactsCreateRequest.builder()
                        .phone("+375 33 379-07-68")
                        .email("jevacation@gmail.com")
                        .build())
                .arrivalTime(HotelCreateRequest.ArrivalTimeCreateRequest.builder()
                        .checkIn(LocalTime.of(12, 22))
                        .checkOut(LocalTime.of(14, 14))
                        .build())
                .build();
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
