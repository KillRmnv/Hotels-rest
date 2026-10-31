package com.gpsolutions.hotelsrest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "Arrival time response")
public class ArrivalTimeResponse {
    @Schema(description = "Post office code",
            example = "220004")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkIn;
    @Schema(description = "Post office code",
            example = "220004")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkOut;
}
