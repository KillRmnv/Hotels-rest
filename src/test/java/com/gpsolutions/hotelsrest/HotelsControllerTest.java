package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.controller.HotelsController;
import com.gpsolutions.hotelsrest.dto.HotelExtendedResponse;
import com.gpsolutions.hotelsrest.dto.HotelResponse;
import com.gpsolutions.hotelsrest.exceptions.NotFoundException;
import com.gpsolutions.hotelsrest.service.HotelsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelsController.class)
class HotelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelsService hotelsService;

    @Test
    void getAll_shouldReturn200WithList() throws Exception {
        when(hotelsService.getAllHotels()).thenReturn(List.of(
                HotelResponse.builder().id(1L).name("DoubleTree").address("9 Pobediteley Avenue, Minsk, 220004, Belarus")
                        .phone("+375 17 309-80-00").build()));

        mockMvc.perform(get("/property-view/hotels"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("DoubleTree"));
    }

    @Test
    void getHotel_shouldReturn200() throws Exception {
        when(hotelsService.getHotel(1L)).thenReturn(
                HotelExtendedResponse.builder().id(1L).name("DoubleTree").brand("Hilton").build());

        mockMvc.perform(get("/property-view/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Hilton"));
    }

    @Test
    void getHotel_unknownId_shouldReturn404ProblemJson() throws Exception {
        when(hotelsService.getHotel(99L)).thenThrow(NotFoundException.hotel("There are no hotel id:99"));

        mockMvc.perform(get("/property-view/hotels/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        String body = """
                {"name":"DoubleTree","brand":"Hilton",
                 "address":{"houseNumber":9,"street":"Pobediteley Avenue","city":"Minsk","country":"Belarus","postCode":"220004"},
                 "contacts":{"phone":"+375 17 309-80-00","email":"a@b.c"},
                 "arrivalTime":{"checkIn":"14:00"}}""";
        when(hotelsService.create(any())).thenReturn(
                HotelResponse.builder().id(1L).name("DoubleTree").build());

        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_missingName_shouldReturn400ProblemJson() throws Exception {
        String body = """
                {"brand":"Hilton",
                 "address":{"houseNumber":9,"street":"S","city":"Minsk","country":"Belarus","postCode":"1"},
                 "contacts":{"phone":"1","email":"a@b.c"}}""";

        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void create_brokenJson_shouldReturn400ProblemJson() throws Exception {
        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }

    @Test
    void search_shouldReturn200() throws Exception {
        when(hotelsService.searchHotels(any())).thenReturn(List.of(
                HotelResponse.builder().id(1L).name("DoubleTree").build()));

        mockMvc.perform(get("/property-view/search").param("city", "minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("DoubleTree"));
    }
}
