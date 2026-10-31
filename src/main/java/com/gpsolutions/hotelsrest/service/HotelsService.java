package com.gpsolutions.hotelsrest.service;

import com.gpsolutions.hotelsrest.repository.HotelsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class HotelsService {
    HotelsRepository hotelDao;
}
