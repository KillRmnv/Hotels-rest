package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelsRepository extends JpaRepository<Hotel,Long> {
}
