package me.dbogda.ufanettestcoffeeshop.service.impl;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.service.action.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;
    private final List<ActionStrategy> strategyList = List.of(new ToWork(), new Delivery(), new Finish(), new Cancel());

    @InjectMocks
    OrderServiceImpl out;

    final Order CORRECT_ORDER = Order.builder()
            .id(1L)
            .customer("Customer")
            .product(ProductType.COFFEE)
            .status(Status.NEW)
            .timeOfOrder( LocalDateTime.now())
            .reports(new ArrayList<>())
            .build();

    final Order CURRENT_ORDER = Order.builder()
            .id(1L)
            .customer("Customer")
            .employee("Employee")
            .product(ProductType.COFFEE)
            .status(Status.CURRENT)
            .timeOfOrder( LocalDateTime.now())
            .timeOfOrderIssue(LocalDateTime.now().plusMinutes(5))
            .timeOfTheLastMoving(LocalDateTime.now())
            .reports(new ArrayList<>())
            .build();

    final Order READY_ORDER = Order.builder()
            .id(1L)
            .customer("Customer")
            .employee("Employee")
            .product(ProductType.COFFEE)
            .status(Status.READY)
            .timeOfOrder( LocalDateTime.now())
            .timeOfOrderIssue(LocalDateTime.now().plusMinutes(5))
            .timeOfTheLastMoving(LocalDateTime.now())
            .reports(new ArrayList<>())
            .build();

    @Test
    @DisplayName("Создание заказа")
    void shouldReturnCorrectMessageAfterCreate() {
        when(orderRepository.save(CORRECT_ORDER))
                .thenReturn(CORRECT_ORDER);

        assertEquals(CORRECT_ORDER.getId(), out.create(CORRECT_ORDER));
    }

    @Test
    @DisplayName("Удаление заказа из БД по его ID")
    void deleteById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(CORRECT_ORDER));
        out.deleteById(1L);
    }

    @Test
    @DisplayName("Получение инфо заказа из БД по его ID")
    void getById() {
        when(orderRepository.findById(CORRECT_ORDER.getId()))
                .thenReturn(Optional.of(CORRECT_ORDER));

        assertEquals(CORRECT_ORDER, out.findOrder(CORRECT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CORRECT_ORDER.getId());
    }

    @Test
    @DisplayName("Выбросить исключение, если заказа не существует")
    void throwNotFoundExceptionWhenObjectIsEmpty(){
        when(orderRepository.findById(CORRECT_ORDER.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> out.findOrder(CORRECT_ORDER.getId()));
    }

    @Test
    @DisplayName("Получить список всех заказов")
    void getAll() {
        when(orderRepository.findAll())
                .thenReturn(List.of(CORRECT_ORDER));

        assertEquals(List.of(CORRECT_ORDER), out.getAll());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получить список новых заказов")
    void getNewOrders() {
        when(orderRepository.getOrdersByStatus(Status.NEW))
                .thenReturn(List.of(CORRECT_ORDER));

        assertEquals(List.of(CORRECT_ORDER), out.getNewOrders());

        verify(orderRepository, times(1)).getOrdersByStatus(Status.NEW);
    }

    @Test
    @DisplayName("Получить информацию о заказе")
    void getOrderInfo() {
        when(orderRepository.findById(CURRENT_ORDER.getId()))
                .thenReturn(Optional.of(CURRENT_ORDER));

        assertEquals(CURRENT_ORDER, out.findOrder(CURRENT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CURRENT_ORDER.getId());
    }


    @Test
    @DisplayName("Получить информацию о заказе для клиента")
    void shouldReturnCorrectOrderInfoForCustomer(){
        when(orderRepository.findById(CURRENT_ORDER.getId())).thenReturn(Optional.of(CURRENT_ORDER));

        String result = "Заказ № " + CURRENT_ORDER.getId() + " для " + CURRENT_ORDER.getCustomer() +"\nСостав заказа: " + CURRENT_ORDER.getProduct().getName()
                + "\nСтоимость: " + CURRENT_ORDER.getProduct().getPrice() + "\nПримерное время получения: "
                + CURRENT_ORDER.getTimeOfOrderIssue().format(DateTimeFormatter.ofPattern("HH:mm"))
                + "\nСтатус заказа: " + CURRENT_ORDER.getStatus().getName();

        assertEquals(result, out.getOrderInfoForCustomer(CURRENT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CURRENT_ORDER.getId());
    }
}