package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.model.*;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.CityRepository;
import com.gpsolutions.hotelsrest.repository.CountryRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.specification.HotelSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class HotelsService {
    private final HotelsRepository hotelDao;
    private final HotelMapper hotelMapper;
    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;

    @Transactional(readOnly = true)
    public List<HotelResponse> searchHotels(HotelsSearchFilterRequest filter) {
        Specification<Hotel> spec = Specification.where(HotelSpecifications.hasName(filter.getName()))
                .and(HotelSpecifications.hasBrand(filter.getBrand()))
                .and(HotelSpecifications.hasCity(filter.getCity()))
                .and(HotelSpecifications.hasCountry(filter.getCountry()))
                .and(HotelSpecifications.hasAmenities(filter.getAmenities()));
        return hotelDao.findAll(spec).stream()
                .map(hotelMapper::toHotelResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getAllHotels() {
        return hotelDao.findAll().stream().map(hotelMapper::toHotelResponse).toList();
    }

    @Transactional(readOnly = true)
    public HotelExtendedResponse getHotel(Long id) {
        var hotel = hotelDao.findById(id).orElseThrow(() -> {
            throw NotFoundException.hotel("There are no hotel id:%d".formatted(id));
        });
        return hotelMapper.toHotelExtendedResponse(hotel);
    }

    @Transactional
    public HotelResponse create(HotelCreateRequest request) {
        var brand = upsertBrand(request.getBrand());

        var address = request.getAddress();
        var street = upsertStreet(address, upsertCity(address.getCity(), upsertCountry(address.getCountry())));
        var newAddress = Address.of(address.getHouseNumber(), street, address.getPostCode());

        var contacts = request.getContacts();
        var newContacts = Contacts.of(contacts.getPhone(), contacts.getEmail());

        var arrival = request.getArrivalTime();
        var newArrivalTime = (arrival == null) ? null
                : ArrivalTime.of(arrival.getCheckIn(), arrival.getCheckOut());

        var hotel = Hotel.of(
                request.getName(), request.getDescription(),
                brand, newAddress, newContacts, newArrivalTime);
        return hotelMapper.toHotelResponse(hotelDao.save(hotel));
    }

    private Brand upsertBrand(String name) {
        return brandRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> brandRepository.save(Brand.of(name.trim())));
    }

    private Country upsertCountry(String name) {
        return countryRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> countryRepository.save(Country.of(name.trim())));
    }

    private City upsertCity(String name, Country country) {
        return cityRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> cityRepository.save(City.of(name.trim(), country)));
    }

    private Street upsertStreet(HotelCreateRequest.AddressCreateRequest addressField, City city) {
        var street = streetRepository.findByNameIgnoreCase(addressField.getStreet().trim())
                .orElseGet(() -> {
                    var created = Street.of(addressField.getStreet().trim());
                    created.getCities().add(city);
                    return streetRepository.save(created);
                });
        if (!street.getCities().contains(city)) {
            street.getCities().add(city);
            streetRepository.save(street);
        }
        return street;
    }
}
