package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.specification.HotelSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class HotelsService {
    private final HotelsRepository hotelDao;
    private final HotelMapper hotelMapper;

    public List<HotelResponse> searchHotels(HotelsSearchFilterRequest filter) {
        return hotelDao.findAll(HotelSpecifications.withFilter(filter)).stream()
                .map(hotelMapper::toHotelResponse)
                .toList();
    }

    public List<HotelResponse> getAllHotels() {
        return hotelDao.findAll().stream().map(hotelMapper::toHotelResponse).toList();
    }

    public HotelExtendedResponse getHotel(Long id) {
        var hotel = hotelDao.findById(id).orElseThrow(() -> {
            throw NotFoundException.hotel("There are no hotel id:%d".formatted(id));
        });
        return hotelMapper.toHotelExtendedResponse(hotel);
    }

}
