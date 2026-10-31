package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.HotelsGetAllRequest;
import com.gpsolutions.hotelsrest.dto.HotelsGetAllResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/hotels")
public class HotelsController {
    @GetMapping
    ResponseEntity<HotelsGetAllResponse> getAll(HotelsGetAllRequest request){
        return null;
    }
}
