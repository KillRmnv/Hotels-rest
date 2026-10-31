package com.gpsolutions.hotelsrest.service.statistic;

import com.gpsolutions.hotelsrest.dto.StatisticsResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface StatisticStrategy {

    StatisticsParam param();

    StatisticsResponse evaluate();

    static StatisticsResponse fromRows(List<Object[]> rows) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : rows) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return StatisticsResponse.of(counts);
    }
}
