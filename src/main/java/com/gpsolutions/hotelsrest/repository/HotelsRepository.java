package com.gpsolutions.hotelsrest.repository;

import com.gpsolutions.hotelsrest.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelsRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("select h.brand.name, count(h) from Hotel h group by h.brand.name")
    List<Object[]> countsByBrand();

    @Query("select c.name, count(distinct h) from Hotel h join h.address.street.cities c group by c.name")
    List<Object[]> countsByCity();

    @Query("select co.name, count(distinct h) from Hotel h join h.address.street.cities c join c.country co group by co.name")
    List<Object[]> countsByCountry();

    @Query("select a.name, count(distinct h) from Hotel h join h.amenities a group by a.name")
    List<Object[]> countsByAmenity();
}
