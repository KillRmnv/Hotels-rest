package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.mapper.HotelMapper;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HotelsService {
    private HotelsRepository hotelDao;
    private HotelMapper hotelMapper;

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
