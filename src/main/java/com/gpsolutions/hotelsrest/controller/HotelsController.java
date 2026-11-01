package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.HotelCreateRequest;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.service.HotelsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "Hotels API",
        description = "API for management of hotel entities")
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@RestController
@RequestMapping("/property-view")
@Slf4j
public class HotelsController {
    private final HotelsService hotelService;
    @Operation(
            summary = "Returns all hotels",
            description = "Return short response about all hotels"
    )
    @ApiResponse(responseCode = "200", description = "List of hotels")
    @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponse>> getAll() {
        log.info("GET /property-view/hotels");
        return ResponseEntity.ok(hotelService.getAllHotels());
    }
    @Operation(
            summary = "Returns info about hotel",
            description = "Return full response about the hotel with given id"
    )
    @ApiResponse(responseCode = "200", description = "Response about requested hotel")
    @ApiResponse(responseCode = "404", description = "There are no hotel with given id")

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelExtendedResponse> getHotel(@PathVariable Long id) {
        log.info("GET /property-view/hotels/{}", id);
        return ResponseEntity.ok(hotelService.getHotel(id));
    }

    // тз указывает эндпоинт /search, но поиск производится среди ресурсов отелей /hotels,
    // поэтому поиск сделал по /search, и по /hotels/search
    @Operation(
            summary = "Returns list of hotels",
            description = "Return short hotel info list that meets search criteria"
    )
    @ApiResponse(responseCode = "200", description = "list of hotels")
    @ApiResponse(responseCode = "404", description = "There are no hotel with given id")
    @GetMapping("/hotels/search")
    public ResponseEntity<List<HotelResponse>> searchHotel(HotelsSearchFilterRequest request) {
        log.info("GET /property-view/hotels/search name={} brand={} city={} country={} amenities={}",
                request.getName(), request.getBrand(), request.getCity(), request.getCountry(), request.getAmenities());
        return ResponseEntity.ok(hotelService.searchHotels(request));
    }
    @Operation(
            summary = "Returns list of hotels",
            description = "Return short hotel info list that meets search criteria.Mirror of /hotels/search"
    )
    @ApiResponse(responseCode = "200", description = "List of hotels")
    @ApiResponse(responseCode = "404", description = "There are no hotel with given id")
    @GetMapping("/search")
    public ResponseEntity<List<HotelResponse>> searchHotelMirror(HotelsSearchFilterRequest request) {
        log.info("GET /property-view/search name={} brand={} city={} country={} amenities={}",
                request.getName(), request.getBrand(), request.getCity(), request.getCountry(), request.getAmenities());
        return ResponseEntity.ok(hotelService.searchHotels(request));
    }
    @Operation(
            summary = "Returns created hotel info",
            description = "Return short hotel info of created entity"
    )
    @ApiResponse(responseCode = "201", description = "Created entity")
    @PostMapping("/hotels")
    public ResponseEntity<HotelResponse> create(@Valid @RequestBody HotelCreateRequest request) {
        log.info("POST /property-view/hotels name={} brand={}", request.getName(), request.getBrand());
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(request));
    }
}
