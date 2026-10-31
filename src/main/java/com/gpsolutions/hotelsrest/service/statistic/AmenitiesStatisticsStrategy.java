package com.gpsolutions.hotelsrest.service.statistic;

import com.gpsolutions.hotelsrest.dto.StatisticsResponse;
import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Component("amenities")
public class AmenitiesStatisticsStrategy implements StatisticStrategy {

    private final HotelsRepository hotelsRepository;

    @Override
    public StatisticsParam param() {
        return StatisticsParam.AMENITIES;
    }

    @Override
    public StatisticsResponse evaluate() {
        return StatisticStrategy.fromRows(hotelsRepository.countsByAmenity());
    }
}
