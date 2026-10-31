package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.StatisticsResponse;
import com.gpsolutions.hotelsrest.service.statistic.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@RestController
@RequestMapping("/property-view")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/histogram/{param}")
    public ResponseEntity<StatisticsResponse> histogram(@PathVariable String param) {
        return ResponseEntity.ok(statisticsService.histogram(param));
    }
}
