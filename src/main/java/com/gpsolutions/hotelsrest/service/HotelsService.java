package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.ArrivalTime;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.specification.HotelSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class HotelsService {
    private final HotelsRepository hotelDao;
    private final HotelMapper hotelMapper;
    private final ReferenceUpserter upserter;
    private final BrandRepository brandRepository;
    private final StreetRepository streetRepository;
    private final TransactionTemplate transactions;

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


    public HotelResponse create(HotelCreateRequest request) {
        var brandId = upserter.brandId(request.getBrand().trim());
        var address = request.getAddress();
        var countryId = upserter.countryId(address.getCountry().trim());
        var cityId = upserter.cityId(address.getCity().trim(), countryId);
        var streetId = upserter.streetId(address.getStreet().trim(), cityId);

        return transactions.execute(status -> {
            var brand = brandRepository.getReferenceById(brandId);
            var street = streetRepository.getReferenceById(streetId);
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
        });
    }
}
