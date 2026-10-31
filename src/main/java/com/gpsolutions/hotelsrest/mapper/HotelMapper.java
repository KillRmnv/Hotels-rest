package com.gpsolutions.hotelsrest.mapper;

import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {
    HotelResponse toHotelResponse(Hotel hotel);

    HotelExtendedResponse toHotelExtendedResponse(Hotel hotel);
}
