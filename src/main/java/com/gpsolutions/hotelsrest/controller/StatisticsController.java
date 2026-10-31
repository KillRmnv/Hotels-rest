package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.StatisticsResponse;
import com.gpsolutions.hotelsrest.service.statistic.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Statistics API",
        description = "API for management of hotel statistics")
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@RestController
@RequestMapping("/property-view")
public class StatisticsController {
    private final StatisticsService statisticsService;
    @Operation(
            summary = "Returns statistics about hotels on given param",
            description = "Returns statistics about hotels on given param"
    )
    @ApiResponse(responseCode = "200", description = "Statistic on requested param")
    @ApiResponse(responseCode = "500", description = "Internal server error, try different request")

    @GetMapping("/histogram/{param}")
    public ResponseEntity<StatisticsResponse> histogram(@PathVariable String param) {
        return ResponseEntity.ok(statisticsService.histogram(param));
    }
}
