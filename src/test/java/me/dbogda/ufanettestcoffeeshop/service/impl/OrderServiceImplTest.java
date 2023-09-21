package me.dbogda.ufanettestcoffeeshop.service.impl;

import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

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

        assertEquals(CORRECT_ORDER, out.create(CORRECT_ORDER));
    }

    @Test
    @DisplayName("Удаление заказа из БД по его ID")
    void deleteById() {
        when(orderRepository.findById(CORRECT_ORDER.getId()))
                .thenReturn(Optional.of(CORRECT_ORDER));

        assertEquals("Заказ удалён из БД", out.deleteById(CORRECT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CORRECT_ORDER.getId());
    }

    @Test
    @DisplayName("Получение инфо заказа из БД по его ID")
    void getById() {
        when(orderRepository.findById(CORRECT_ORDER.getId()))
                .thenReturn(Optional.of(CORRECT_ORDER));

        assertEquals(CORRECT_ORDER, out.getById(CORRECT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CORRECT_ORDER.getId());
    }

    @Test
    @DisplayName("Выбросить исключение, если заказа не существует")
    void throwNotFoundExceptionWhenObjectIsEmpty(){
        when(orderRepository.findById(CORRECT_ORDER.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> out.getById(CORRECT_ORDER.getId()));
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

        assertEquals(CURRENT_ORDER, out.getById(CURRENT_ORDER.getId()));

        verify(orderRepository, times(1)).findById(CURRENT_ORDER.getId());
    }

    @Test
    @DisplayName("Принять заказ в работу")
    void takeAnOrderToWork() {
        String result = out.takeAnOrderToWork(CORRECT_ORDER, "Employee");

        assertEquals("Заказ № " + CORRECT_ORDER.getId() + " был взят в работу сотрудником Employee", result);

        verify(orderRepository, times(1)).save(CORRECT_ORDER);
    }

    @Test
    @DisplayName("Подготовить заказ к выдаче")
    void readyOrderForDelivery() {
        String result = out.readyOrderForDelivery(CURRENT_ORDER, "Employee");

        assertEquals("Заказ № " + CURRENT_ORDER.getId() + " был взят в работу сотрудником Employee и готов к выдаче!", result);

        verify(orderRepository, times(1)).save(CURRENT_ORDER);
    }

    @Test
    @DisplayName("Передать заказ клиенту")
    void issueAnOrder() {
        String result = out.issueAnOrder(READY_ORDER, "Employee");

        assertEquals("Заказ № " + READY_ORDER.getId() + " был выдан сотрудником " + READY_ORDER.getEmployee(), result);
    }

    @Test
    @DisplayName("Отменить новый заказ")
    void cancelTheNewOrder() {
        String newOrder = out.cancelTheOrder(CORRECT_ORDER, "Employee");

        assertEquals("Заказ № " + CORRECT_ORDER.getId() + " был отменен сотрудником " + CORRECT_ORDER.getEmployee() + ", так как не прошла оплата.", newOrder);

    }

    @Test
    @DisplayName("Отменить текущий заказ")
    void cancelTheCurrentOrder() {
        String currentOrder = out.cancelTheOrder(CURRENT_ORDER, "Employee");

        assertEquals("Заказ № " + CURRENT_ORDER.getId() + " был отменен сотрудником " + CURRENT_ORDER.getEmployee() + ", так как невозможно собрать полный заказ", currentOrder);
    }

    @Test
    @DisplayName("Выбросить исключение при попытке отменить готовый заказ")
    void throwNonValidStatusExceptionWhenCancelTheReadyOrder() {
        assertThrows(NonValidStatusException.class, ()-> out.cancelTheOrder(READY_ORDER, "Employee"));
    }
}