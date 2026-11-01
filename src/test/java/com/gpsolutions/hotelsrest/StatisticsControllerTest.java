package com.gpsolutions.hotelsrest;

import com.gpsolutions.hotelsrest.controller.StatisticsController;
import com.gpsolutions.hotelsrest.dto.StatisticsResponse;
import com.gpsolutions.hotelsrest.service.statistic.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsService statisticsService;

    @Test
    void histogram_shouldReturnBareMap() throws Exception {
        when(statisticsService.histogram("city")).thenReturn(StatisticsResponse.of(Map.of("Minsk", 2L)));

        mockMvc.perform(get("/property-view/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(2));
    }

    @Test
    void histogram_unknownParam_shouldReturn400ProblemJson() throws Exception {
        when(statisticsService.histogram("foo"))
                .thenThrow(new IllegalArgumentException("Unknown histogram parameter 'foo'"));

        mockMvc.perform(get("/property-view/histogram/foo"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }
}
