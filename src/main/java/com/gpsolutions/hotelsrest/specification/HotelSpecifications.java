package com.gpsolutions.hotelsrest.specification;

import com.gpsolutions.hotelsrest.model.Hotel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelSpecifications {

    public static Specification<Hotel> hasName(String name) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name != null && !name.isBlank())
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), contains(name));
                return null;
            }
        };
    }

    public static Specification<Hotel> hasBrand(String brand) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (brand != null && !brand.isBlank())
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("brand").get("name")), contains(brand));
                return null;
            }
        };
    }

    public static Specification<Hotel> hasCity(String city) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (city != null && !city.isBlank()) {
                    query.distinct(true);
                    From<?, ?> addressJoin = root.join("address");
                    From<?, ?> street = addressJoin.join("street");
                    return criteriaBuilder.like(criteriaBuilder.lower(street.join("cities").get("name")), contains(city));
                }
                return null;
            }
        };
    }

    public static Specification<Hotel> hasCountry(String country) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (country != null && !country.isBlank()) {
                    query.distinct(true);
                    From<?, ?> addressJoin = root.join("address");
                    From<?, ?> street = addressJoin.join("street");
                    From<?, ?> countryAttr = street.join("cities").join("country");
                    return criteriaBuilder.like(criteriaBuilder.lower(countryAttr.get("name")), contains(country));
                }
                return null;
            }
        };
    }

    public static Specification<Hotel> hasAmenities(String amenities) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (amenities == null || amenities.isBlank())
                    return null;
                query.distinct(true);
                List<Predicate> orPredicates = new ArrayList<>();
                for (String name : amenities.split(",")) {
                    if (!name.isBlank())
                        orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("amenities").get("name")), contains(name)));
                }
                return orPredicates.isEmpty() ? null : criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            }
        };
    }

    private static String contains(String value) {
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
