package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalTime;

@Data
@Embeddable
public class ArrivalTime {
    private LocalTime checkIn;
    private LocalTime checkOut;
}
