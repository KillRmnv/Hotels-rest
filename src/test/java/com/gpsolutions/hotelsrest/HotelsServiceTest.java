package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.ArrivalTime;
import com.gpsolutions.hotelsrest.model.Brand;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.model.Street;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.service.HotelsService;
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
import static org.mockito.Mockito.lenient;
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
    private ReferenceUpserter upserter;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private StreetRepository streetRepository;

    @Mock
    private TransactionTemplate transactions;

    @InjectMocks
    private HotelsService hotelService;

    @BeforeEach
    void runTransactionCallbacksInline() {
        lenient().when(transactions.execute(any())).thenAnswer(invocation ->
                invocation.<TransactionCallback<Object>>getArgument(0).doInTransaction(null));
    }

    @Test
    void getAllHotels_shouldReturnMappedList() {
        var hotel1 = setupHotel("brand", "street", "name", "descr");
        var hotel2 = setupHotel("brand", "street", "name1", "descr1");
        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2));
        when(hotelMapper.toHotelResponse(any())).thenReturn(
                hotelResponse(1L, "DoubleTree"),
                hotelResponse(2L, "Hilton Minsk"));

        var result = hotelService.getAllHotels();

        assertEquals(2, result.size());
        assertEquals("DoubleTree", result.get(0).getName());
        assertEquals("Hilton Minsk", result.get(1).getName());
        verify(hotelRepository).findAll();
        verify(hotelMapper).toHotelResponse(hotel1);
        verify(hotelMapper).toHotelResponse(hotel2);
    }

    @Test
    void getHotelById_shouldReturnExtendedResponse() {
        var hotel = setupHotel("Hilton", "Pobediteley Avenue", "DoubleTree", "descr");
        var expected = HotelExtendedResponse.builder()
                .id(1L)
                .name("DoubleTree")
                .brand("Hilton")
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toHotelExtendedResponse(hotel)).thenReturn(expected);

        assertEquals(expected, hotelService.getHotel(1L));
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getHotelById_shouldThrowWhenHotelNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> hotelService.getHotel(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(hotelRepository).findById(99L);
        verify(hotelMapper, never()).toHotelExtendedResponse(any());
    }

    @Test
    void createHotel_shouldResolveReferenceDataThroughUpserter() {
        var request = hotelCreateRequest();
        var brand = Brand.of("Hilton");
        var street = Street.of("Pobediteley Avenue");
        var hotel = setupHotel("Hilton", "Pobediteley Avenue", "DoubleTree", "descr");
        var expected = hotelResponse(1L, "DoubleTree");

        when(upserter.brandId("Hilton")).thenReturn(1L);
        when(upserter.countryId("Belarus")).thenReturn(2L);
        when(upserter.cityId("Minsk", 2L)).thenReturn(3L);
        when(upserter.streetId("Pobediteley Avenue", 3L)).thenReturn(4L);
        when(brandRepository.getReferenceById(1L)).thenReturn(brand);
        when(streetRepository.getReferenceById(4L)).thenReturn(street);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        when(hotelMapper.toHotelResponse(hotel)).thenReturn(expected);

        assertEquals(expected, hotelService.create(request));
        verify(hotelRepository).save(any(Hotel.class));
    }

    private HotelResponse hotelResponse(Long id, String name) {
        return HotelResponse.builder()
                .id(id)
                .name(name)
                .description("descr")
                .address("1 Pobediteley Avenue, Minsk, 220022, Belarus")
                .phone("+375 33 379-07-68")
                .build();
    }

    private HotelCreateRequest hotelCreateRequest() {
        return HotelCreateRequest.builder()
                .name("DoubleTree")
                .description("descr")
                .brand("Hilton")
                .address(HotelCreateRequest.AddressCreateRequest.builder()
                        .houseNumber(1)
                        .street("Pobediteley Avenue")
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
