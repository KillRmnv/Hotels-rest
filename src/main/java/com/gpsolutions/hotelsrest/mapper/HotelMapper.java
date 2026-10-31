package com.gpsolutions.hotelsrest.mapper;

import com.gpsolutions.hotelsrest.dto.AddressResponse;
import com.gpsolutions.hotelsrest.dto.ArrivalTimeResponse;
import com.gpsolutions.hotelsrest.dto.ContactsResponse;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.model.ArrivalTime;
import com.gpsolutions.hotelsrest.model.City;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {

    @Mapping(target = "address", expression = "java(toShortAddress(hotel.getAddress()))")
    @Mapping(target = "phone", source = "contacts.phone")
    HotelResponse toHotelResponse(Hotel hotel);

    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "contacts", source = "contacts")
    @Mapping(target = "arrivalTime", source = "arrivalTime")
    @Mapping(target = "amenities", expression = "java(amenityNames(hotel.getAmenities()))")
    HotelExtendedResponse toHotelExtendedResponse(Hotel hotel);

    @Mapping(target = "street", source = "street.name")
    @Mapping(target = "city", expression = "java(cityName(address))")
    @Mapping(target = "country", expression = "java(countryName(address))")
    AddressResponse toAddressResponse(Address address);

    ContactsResponse toContactsResponse(Contacts contacts);

    ArrivalTimeResponse toArrivalTimeResponse(ArrivalTime arrivalTime);

    private static Optional<City> firstCity(Address address) {
        return Optional.ofNullable(address)
                .map(Address::getStreet)
                .map(s -> s.getCities())
                .flatMap(list -> list.stream().findFirst());
    }

    default String cityName(Address address) {
        return firstCity(address).map(City::getName).orElse(null);
    }

    default String countryName(Address address) {
        return firstCity(address).map(City::getCountry).map(c -> c.getName()).orElse(null);
    }

    default List<String> amenityNames(Set<Amenity> amenities) {
        return Optional.ofNullable(amenities)
                .map(set -> set.stream().map(Amenity::getName).toList())
                .orElse(List.of());
    }

    default String toShortAddress(Address address) {
        if (address == null) {
            return null;
        }
        return "%d %s, %s, %s, %s".formatted(
                address.getHouseNumber(),
                Optional.ofNullable(address.getStreet()).map(s -> s.getName()).orElse(null),
                cityName(address), address.getPostCode(), countryName(address));
    }
}
