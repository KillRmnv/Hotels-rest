package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HotelsRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
}
