package com.gpsolutions.hotelsrest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
@Schema(description = "Arrival time response")
public class ArrivalTimeResponse {
    @Schema(description = "Earliest check-in time",
            example = "12:34")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkIn;
    @Schema(description = "Check-out deadline (optional)",
            example = "11:09")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkOut;
}
