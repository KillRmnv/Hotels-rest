package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArrivalTime {
    private LocalTime checkIn;
    private LocalTime checkOut;
}
