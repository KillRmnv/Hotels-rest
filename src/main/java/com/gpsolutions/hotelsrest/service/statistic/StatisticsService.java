package com.gpsolutions.hotelsrest.service.statistic;

import com.gpsolutions.hotelsrest.dto.StatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Service
public class StatisticsService {

    private final Map<String, StatisticStrategy> strategies;

    @Transactional(readOnly = true)
    public StatisticsResponse histogram(String param) {
        var key = StatisticsParam.fromString(param).name().toLowerCase();
        var strategy = strategies.get(key);
        if (strategy == null) {
            throw new IllegalStateException("No statistics strategy for parameter '%s'".formatted(param));
        }
        return strategy.evaluate();
    }
}
