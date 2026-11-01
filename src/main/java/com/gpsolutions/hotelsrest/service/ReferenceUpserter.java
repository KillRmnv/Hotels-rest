package com.gpsolutions.hotelsrest.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;


@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class ReferenceUpserter {
    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;
    private final AmenityRepository amenityRepository;
    private final PlatformTransactionManager transactionManager;

    public Long brandId(String name) {
        return upsert(() -> brandRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> brandRepository.save(Brand.of(name)))
                .getId());
    }

    public Long countryId(String name) {
        return upsert(() -> countryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> countryRepository.save(Country.of(name)))
                .getId());
    }

    public Long cityId(String name, Long countryId) {
        return upsert(() -> cityRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> cityRepository.save(City.of(name, countryRepository.getReferenceById(countryId))))
                .getId());
    }

    public Long streetId(String name, Long cityId) {
        return upsert(() -> {
            var street = streetRepository.findByNameIgnoreCase(name).orElseGet(() -> Street.of(name));
            boolean linked = street.getCities().stream().anyMatch(city -> city.getId().equals(cityId));
            if (!linked) {
                street.getCities().add(cityRepository.getReferenceById(cityId));
            }
            return streetRepository.save(street).getId();
        });
    }

    public Long amenityId(String name) {
        return upsert(() -> amenityRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> amenityRepository.save(Amenity.of(name)))
                .getId());
    }

    private <T> T upsert(Supplier<T> work) {
        try {
            return inNewTransaction(work);
        } catch (DataIntegrityViolationException concurrentInsert) {
            return inNewTransaction(work);
        }
    }

    private <T> T inNewTransaction(Supplier<T> work) {
        var template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template.execute(status -> work.get());
    }
}
