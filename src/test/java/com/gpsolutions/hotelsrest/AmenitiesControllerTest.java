package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.controller.AmenitiesController;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.service.AmenitiesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AmenitiesController.class)
class AmenitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AmenitiesService amenitiesService;

    @Test
    void addAmenities_shouldReturn201() throws Exception {
        mockMvc.perform(post("/property-view/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\"]"))
                .andExpect(status().isCreated());
    }

    @Test
    void addAmenities_unknownHotel_shouldReturn404ProblemJson() throws Exception {
        doThrow(NotFoundException.hotel("There are no hotel id:99"))
                .when(amenitiesService).addAmenitiesTo(eq(99L), anyList());

        mockMvc.perform(post("/property-view/hotels/99/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\"]"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }

    @Test
    void addAmenities_emptyList_shouldReturn400() throws Exception {
        mockMvc.perform(post("/property-view/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAmenities_wrappedObject_shouldReturn400() throws Exception {
        mockMvc.perform(post("/property-view/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amenities\":[\"Free WiFi\"]}"))
                .andExpect(status().isBadRequest());
    }
}
