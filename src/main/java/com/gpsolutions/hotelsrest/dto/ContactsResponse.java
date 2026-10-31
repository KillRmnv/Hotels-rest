package com.gpsolutions.hotelsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Hotel contacts")
public class ContactsResponse {
    @Schema(description = "Reception phone number",
            example = "+375 33 66-14-88")
    private String phone;
    @Schema(description = "Hotel email",
            example = "jevacation@gmail.com")
    private String email;
}
