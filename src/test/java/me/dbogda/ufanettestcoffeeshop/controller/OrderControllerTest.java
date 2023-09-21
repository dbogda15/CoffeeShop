package me.dbogda.ufanettestcoffeeshop.controller;

import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.service.impl.OrderServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @MockBean
    OrderServiceImpl orderService;
    @MockBean
    ReportServiceImpl reportService;

    @Autowired
    MockMvc mockMvc;

    final Order NEW_ORDER = new Order(1L, ProductType.COFFEE, "Customer", Status.NEW, LocalDateTime.now());
    final Order CREATED_ORDER = new Order(ProductType.COFFEE, "Customer");

    @Test
    @DisplayName("Создание нового заказа через контроллер")
    void shouldReturn200WhenCreateNewOrder() throws Exception {
        when(orderService.create(any(Order.class))).thenReturn(CREATED_ORDER);
        mockMvc.perform(post("/order")
                        .param("product", "COFFEE")
                        .param("name", "Customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("product").value(CREATED_ORDER.getProduct().toString()))
                .andExpect(jsonPath("customer").value(CREATED_ORDER.getCustomer()));

        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    void getAll() throws Exception {
        when(orderService.getAll()).thenReturn(List.of(NEW_ORDER));
        mockMvc.perform(get("/order/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NEW_ORDER.getId()))
                .andExpect(jsonPath("$[0].product").value(NEW_ORDER.getProduct().toString()))
                .andExpect(jsonPath("$[0].customer").value(NEW_ORDER.getCustomer()))
                .andExpect(jsonPath("$[0].status").value(NEW_ORDER.getStatus().toString()));

        verify(orderService, times(1)).getAll();
    }

    @Test
    @DisplayName("Получение списка новых заказов через контроллер")
    void getNewOrders() throws Exception {
        when(orderService.getNewOrders()).thenReturn(List.of(NEW_ORDER));
        mockMvc.perform(get("/order/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NEW_ORDER.getId()))
                .andExpect(jsonPath("$[0].product").value(NEW_ORDER.getProduct().toString()))
                .andExpect(jsonPath("$[0].customer").value(NEW_ORDER.getCustomer()))
                .andExpect(jsonPath("$[0].status").value(NEW_ORDER.getStatus().toString()));

        verify(orderService, times(1)).getNewOrders();
    }

    @Test
    @DisplayName("Удаление заказа из БД через контроллер")
    void shouldReturn200WhenDelete() throws Exception {
        when(orderService.deleteById(1L)).thenReturn("Заказ удалён из БД");
        mockMvc.perform(delete("/order?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Заказ удалён из БД"));
        verify(orderService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Передать заказ в работу через контроллер")
    void takeAnOrderToWork() throws Exception {
        when(orderService.takeAnOrderToWork(1L, "Employee")).thenReturn("Заказ № " + 1L + " был взят в работу сотрудником Employee");
        mockMvc.perform(put("/order/toWork?orderId=1&employeeName=Employee"))
                .andExpect(status().isOk())
                .andExpect(content().string("Заказ № " + 1L + " был взят в работу сотрудником Employee"));

        verify(orderService, times(1)).takeAnOrderToWork(1L, "Employee");
    }

    @Test
    @DisplayName("Передать заказ в зону выдачи через контроллер")
    void readyOrderForDelivery() throws Exception {
        when(orderService.readyOrderForDelivery(1L, "Employee"))
                .thenReturn("Заказ № " + 1L + " был взят в работу сотрудником Employee и готов к выдаче!");
        mockMvc.perform(put("/order/ready?orderId=1&employeeName=Employee"))
                .andExpect(status().isOk())
                .andExpect(content().string("Заказ № " + 1L + " был взят в работу сотрудником Employee и готов к выдаче!"));

        verify(orderService, times(1)).readyOrderForDelivery(1L, "Employee");
    }

    @Test
    @DisplayName("Передать заказ клиенту через контроллер")
    void issueAnOrder() throws Exception {
        when(orderService.issueAnOrder(1L, "Employee"))
                .thenReturn("Заказ № " + 1L + " был выдан сотрудником Employee");
        mockMvc.perform(put("/order/finish?orderId=1&employeeName=Employee"))
                .andExpect(status().isOk())
                .andExpect(content().string("Заказ № " + 1L + " был выдан сотрудником Employee"));

        verify(orderService, times(1)).issueAnOrder(1L, "Employee");
    }
}