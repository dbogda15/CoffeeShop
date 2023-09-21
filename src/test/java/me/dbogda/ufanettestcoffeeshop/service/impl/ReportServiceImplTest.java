package me.dbogda.ufanettestcoffeeshop.service.impl;

import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    ReportServiceImpl out;

    final Order ORDER = new Order(ProductType.COFFEE, "Customer");
    final Report REPORT = new Report(ORDER, "Message");

    @Test
    @DisplayName("Создание отчета")
    void shouldReturnCorrectReportAfterCreate() {
        when(reportRepository.save(REPORT))
                .thenReturn(REPORT);

        assertEquals(REPORT, out.create(REPORT));

        verify(reportRepository, times(1)).save(REPORT);
    }

    @Test
    @DisplayName("Получить все отчеты")
    void getAll() {
        when(reportRepository.findAll())
                .thenReturn(List.of(REPORT));

        assertEquals(List.of(REPORT), out.getAll());

        verify(reportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получить все отчеты по ID заказа")
    void getReportsByOrderId() {
        when(orderRepository.findById(ORDER.getId()))
                .thenReturn(Optional.of(ORDER));

        List<Report> reports = reportRepository.getAllByOrder(ORDER);

        assertEquals(reports, out.getReportsByOrderId(ORDER.getId()));
    }

    @Test
    @DisplayName("Выбросить исключение, если заказа не существует")
    void shouldThrowNotFoundException() {
        when(orderRepository.findById(ORDER.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> out.getReportsByOrderId(ORDER.getId()));
    }
}