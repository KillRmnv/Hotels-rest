package com.gpsolutions.hotelsrest.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;
@Data
@Embeddable
public class ArrivalTime {
    private LocalDate checkIn;
    private LocalDate checkOut;
}
