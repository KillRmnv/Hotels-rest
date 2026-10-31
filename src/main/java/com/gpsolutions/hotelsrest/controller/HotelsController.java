package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.service.HotelsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@RestController
@RequestMapping("/property-view")
public class HotelsController {
    private final HotelsService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponse>> getAll() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelExtendedResponse> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotel(id));
    }

    // тз указывает эндпоинт /search, но поиск производится среди ресурсов отелей /hotels,
    // поэтому поиск сделал по /search, и по /hotels/search
    @GetMapping("/hotels/search")
    public ResponseEntity<List<HotelResponse>> searchHotel(HotelsSearchFilterRequest request) {
        return ResponseEntity.ok(hotelService.searchHotels(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelResponse>> searchHotelMirror(HotelsSearchFilterRequest request) {
        return ResponseEntity.ok(hotelService.searchHotels(request));
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelResponse> create(@Valid @RequestBody HotelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(request));
    }
}
