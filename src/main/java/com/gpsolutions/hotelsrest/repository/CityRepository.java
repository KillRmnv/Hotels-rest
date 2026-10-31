package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByNameIgnoreCase(String name);
}
