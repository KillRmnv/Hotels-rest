package com.gpsolutions.hotelsrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@RestController
@RequestMapping("/property-view")
public class AmenitiesController {
}
