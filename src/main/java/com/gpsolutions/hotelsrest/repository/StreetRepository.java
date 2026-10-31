package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.Street;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreetRepository extends JpaRepository<Street, Long> {

    Optional<Street> findByNameIgnoreCase(String name);
}
