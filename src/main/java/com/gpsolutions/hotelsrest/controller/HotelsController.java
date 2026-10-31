package com.gpsolutions.hotelsrest.controller;

import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.dto.HotelsSearchFilterRequest;
import com.gpsolutions.hotelsrest.service.HotelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("/property-view")
public class HotelsController {
    private HotelsService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponse>> getAll() {
        return new ResponseEntity<>(hotelService.getAllHotels(),HttpStatusCode.valueOf(200));
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelExtendedResponse> getHotel (@PathVariable Long id){
        return new ResponseEntity<>(hotelService.getHotel(id),HttpStatusCode.valueOf(200));
    }

    @GetMapping("/hotels/search")
    public ResponseEntity<HotelExtendedResponse> searchHotel ( HotelsSearchFilterRequest request){
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
    // тз указывает эндпоинт /search, но поиск производится среди ресурсов отелей /hotels, поэтому я не знал, какой именно эндпоинт взять и сделал зеркальные
    @GetMapping("/search")
    public ResponseEntity<HotelExtendedResponse> searchHotelMirror ( HotelsSearchFilterRequest request){
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }


}
