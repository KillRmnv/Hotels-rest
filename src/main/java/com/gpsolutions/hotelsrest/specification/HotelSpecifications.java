package com.gpsolutions.hotelsrest.specification;

import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.model.Hotel;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;


public final class HotelSpecifications {

    private HotelSpecifications() {
    }

    public static Specification<Hotel> withFilter(HotelsSearchFilterRequest f) {
        Specification<Hotel> spec = neutral();

        if (f.getName() != null && !f.getName().isBlank()) {
            spec = spec.and(byName(f.getName()));
        }
        if (f.getBrand() != null && !f.getBrand().isBlank()) {
            spec = spec.and(byBrand(f.getBrand()));
        }
        if (f.getCity() != null && !f.getCity().isBlank()) {
            spec = spec.and(byCity(f.getCity()));
        }
        if (f.getCountry() != null && !f.getCountry().isBlank()) {
            spec = spec.and(byCountry(f.getCountry()));
        }
        if (f.getAmenities() != null && !f.getAmenities().isBlank()) {
            spec = spec.and(byAmenities(f.getAmenities()));
        }
        return spec;
    }

    public static Specification<Hotel> byName(String name) {
        return like("name", name);
    }

    public static Specification<Hotel> byBrand(String brand) {
        return like("brand->name", brand);
    }

    public static Specification<Hotel> byCity(String city) {
        Specification<Hotel> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.like(cb.lower(root.join("address").join("street").join("cities").get("name")), asLike(city));
        };
        return spec;
    }

    public static Specification<Hotel> byCountry(String country) {
        Specification<Hotel> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.like(cb.lower(root.join("address").join("street").join("cities").join("country").get("name")), asLike(country));
        };
        return spec;
    }

    public static Specification<Hotel> byAmenities(String amenities) {
        Specification<Hotel> spec = neutral();
        for (String name : amenities.split(",")) {
            Specification<Hotel> part = (root, query, cb) -> {
                query.distinct(true);
                return cb.like(cb.lower(root.join("amenities").get("name")), asLike(name));
            };
            spec = spec.or(part);
        }
        return spec;
    }

    private static Specification<Hotel> neutral() {
        return (root, query, cb) -> null;
    }

    private static Specification<Hotel> like(String path, String value) {
        return (root, query, cb) -> {
            Path<?> p = root;
            for (String segment : path.split("->")) {
                p = p.get(segment);
            }
            return cb.like(cb.lower(p.as(String.class)), asLike(value));
        };
    }

    private static String asLike(String value) {
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
