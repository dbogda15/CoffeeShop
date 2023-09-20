package me.dbogda.ufanettestcoffeeshop.controller;

import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.ProductType;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.model.Status;
import me.dbogda.ufanettestcoffeeshop.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ReportController.class)
@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @MockBean
    ReportServiceImpl reportService;

    @Autowired
    MockMvc mockMvc;

    final Order ORDER = new Order(1L, ProductType.COFFEE, "Customer", Status.CURRENT, LocalDateTime.now());
    final Report REPORT = new Report(1L, ORDER, "Message", LocalDateTime.now());

    @Test
    @DisplayName("")
    void getReportsByOrderId() throws Exception {
        when(reportService.getReportsByOrderId(1L)).thenReturn(List.of(REPORT));
        mockMvc.perform(get("/reports/id?orderId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REPORT.getId()))
                .andExpect(jsonPath("$[0].message").value(REPORT.getMessage()));

        verify(reportService, times(1)).getReportsByOrderId(1L);
    }

    @Test
    @DisplayName("")
    void getAll() throws Exception {
        when(reportService.getAll()).thenReturn(List.of(REPORT));
        mockMvc.perform(get("/reports/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REPORT.getId()))
                .andExpect(jsonPath("$[0].message").value(REPORT.getMessage()));

        verify(reportService, times(1)).getAll();
    }
}