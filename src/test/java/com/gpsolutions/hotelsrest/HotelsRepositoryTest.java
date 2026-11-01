package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.model.Address;
import com.gpsolutions.hotelsrest.model.Amenity;
import com.gpsolutions.hotelsrest.model.Brand;
import com.gpsolutions.hotelsrest.model.City;
import com.gpsolutions.hotelsrest.model.Contacts;
import com.gpsolutions.hotelsrest.model.Country;
import com.gpsolutions.hotelsrest.model.Hotel;
import com.gpsolutions.hotelsrest.model.Street;
import com.gpsolutions.hotelsrest.repository.AmenityRepository;
import com.gpsolutions.hotelsrest.repository.BrandRepository;
import com.gpsolutions.hotelsrest.repository.CityRepository;
import com.gpsolutions.hotelsrest.repository.CountryRepository;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import com.gpsolutions.hotelsrest.repository.StreetRepository;
import com.gpsolutions.hotelsrest.specification.HotelSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class HotelsRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private HotelsRepository hotelsRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StreetRepository streetRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    private Hotel persistHotel(String name, String brandName, String streetName,
                               String cityName, String countryName, String... amenityNames) {
        var brand = brandRepository.save(Brand.of(brandName));
        var country = countryRepository.save(Country.of(countryName));
        var city = cityRepository.save(City.of(cityName, country));
        var street = Street.of(streetName);
        street.getCities().add(city);
        street = streetRepository.save(street);
        var address = Address.of(9, street, "220004");
        var contacts = Contacts.of("+375 17 309-80-00", "a@b.c");
        var hotel = Hotel.of(name, "descr", brand, address, contacts, null);
        for (String amenityName : amenityNames) {
            var amenity = amenityRepository.save(Amenity.of(amenityName));
            hotel.getAmenities().add(amenity);
        }
        em.persist(contacts);
        em.persist(address);
        return hotelsRepository.save(hotel);
    }

    private static Map<String, Long> rowsToMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(r -> (String) r[0], r -> (Long) r[1]));
    }

    @Test
    void specifications_shouldFilterByCityBrandCountryNameAndAmenities() {
        persistHotel("DoubleTree", "Hilton", "Pobediteley Avenue", "Minsk", "Belarus", "Free WiFi");
        persistHotel("Moscow Plaza", "Rock", "Tverskaya", "Moscow", "Russia");

        var byCity = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasCity("minsk")));
        assertEquals(1, byCity.size());
        assertEquals("DoubleTree", byCity.get(0).getName());

        var byBrand = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasBrand("hilton")));
        assertEquals(1, byBrand.size());

        var byCountry = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasCountry("belarus")));
        assertEquals(1, byCountry.size());

        var byName = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasName("moscow")));
        assertEquals(1, byName.size());

        var byAmenities = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasAmenities("Free WiFi")));
        assertEquals(1, byAmenities.size());

        var combined = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasCity("minsk"))
                        .and(HotelSpecifications.hasBrand("hilton")));
        assertEquals(1, combined.size());

        var empty = hotelsRepository.findAll(
                Specification.where(HotelSpecifications.hasCity("nowhere")));
        assertTrue(empty.isEmpty());
    }

    @Test
    void groupByQueries_shouldCountHotelsPerParameter() {
        persistHotel("DoubleTree", "Hilton", "Pobediteley Avenue", "Minsk", "Belarus", "Free WiFi", "Concierge");
        persistHotel("Crowne", "Hilton", "Pobediteley Avenue", "Minsk", "Belarus", "Free WiFi");

        assertEquals(Map.of("Hilton", 2L), rowsToMap(hotelsRepository.countsByBrand()));
        assertEquals(Map.of("Minsk", 2L), rowsToMap(hotelsRepository.countsByCity()));
        assertEquals(Map.of("Belarus", 2L), rowsToMap(hotelsRepository.countsByCountry()));
        assertEquals(Map.of("Free WiFi", 2L, "Concierge", 1L),
                rowsToMap(hotelsRepository.countsByAmenity()));
    }
}
