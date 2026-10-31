package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByNameIgnoreCase(String name);
}
