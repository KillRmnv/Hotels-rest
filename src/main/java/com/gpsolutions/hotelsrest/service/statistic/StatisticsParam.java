package com.gpsolutions.hotelsrest.service.statistic;


public enum StatisticsParam {
    BRAND("brand"),
    CITY("city"),
    COUNTRY("country"),
    AMENITIES("amenities");

    private final String wireName;

    StatisticsParam(String wireName) {
        this.wireName = wireName;
    }

    public static StatisticsParam fromString(String value) {
        for (var candidate : values()) {
            if (candidate.wireName.equalsIgnoreCase(value)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(
                "Unknown histogram parameter '%s'. Supported: brand, city, country, amenities".formatted(value));
    }
}
