package me.dbogda.ufanettestcoffeeshop.service.impl;

import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ReportService reportService;

    @InjectMocks
    OrderServiceImpl out;

    final Order NEW_ORDER = new Order(1L, ProductType.COFFEE, "Customer", Status.NEW, LocalDateTime.now());
    final Report REPORT = new Report(1L,NEW_ORDER, "Message", LocalDateTime.now());
    final Order CORRECT_ORDER = new Order(1L, ProductType.COFFEE, "Customer", Status.NEW, LocalDateTime.now());
    final Order CURRENT_ORDER = new Order(1L, ProductType.COFFEE, "Customer", "Employee", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),LocalDateTime.now(), Status.CURRENT, List.of(REPORT));
    final Order READY_ORDER = new Order(1L, ProductType.COFFEE, "Customer", "Employee", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),LocalDateTime.now(), Status.READY, List.of(REPORT));

    @Test
    @DisplayName("Создание заказа")
    void shouldReturnCorrectMessageAfterCreate() {
        when(orderRepository.save(NEW_ORDER))
                .thenReturn(NEW_ORDER);

        assertEquals(NEW_ORDER, out.create(NEW_ORDER));
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
        when(orderRepository.findById(NEW_ORDER.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> out.getById(NEW_ORDER.getId()));
    }

    @Test
    @DisplayName("Получить список всех заказов")
    void getAll() {
        when(orderRepository.findAll())
                .thenReturn(List.of(NEW_ORDER));

        assertEquals(List.of(NEW_ORDER), out.getAll());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получить список новых заказов")
    void getNewOrders() {
        when(orderRepository.getOrdersByStatus(Status.NEW))
                .thenReturn(List.of(NEW_ORDER));

        assertEquals(List.of(NEW_ORDER), out.getNewOrders());

        verify(orderRepository, times(1)).getOrdersByStatus(Status.NEW);
    }

    @Test
    @DisplayName("Получить информацию о заказе")
    void getOrderInfo() {
        when(orderRepository.findById(CURRENT_ORDER.getId()))
                .thenReturn(Optional.of(CURRENT_ORDER));
        String status = "Текущий статус заказа: " + CURRENT_ORDER.getStatus().toString();
        StringBuilder result = new StringBuilder("Список событий: ");
        List<Report> reports = CURRENT_ORDER.getReports();
        for (Report report : reports){
            result.append("\n").append(report.getMessage()).append(". Время события: ").append(report.getLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
        assertEquals(status + "\n" + result, out.getOrderInfoById(CURRENT_ORDER.getId()).toString());

        verify(orderRepository, times(1)).findById(CURRENT_ORDER.getId());
    }

    @Test
    @DisplayName("Принять заказ в работу")
    void takeAnOrderToWork() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(NEW_ORDER));

        String result = out.takeAnOrderToWork(1L, "Employee");

        assertEquals("Заказ № " + NEW_ORDER.getId() + " был взят в работу сотрудником Employee", result);
    }

    @Test
    @DisplayName("Подготовить заказ к выдаче")
    void readyOrderForDelivery() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(CURRENT_ORDER));

        String result = out.readyOrderForDelivery(1L, "Employee");

        assertEquals("Заказ № 1 был взят в работу сотрудником Employee и готов к выдаче!", result);
    }

    @Test
    @DisplayName("Передать заказ клиенту")
    void issueAnOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(READY_ORDER));

        String result = out.issueAnOrder(1L, "Employee");

        assertEquals("Заказ № " + READY_ORDER.getId() + " был выдан сотрудником " + READY_ORDER.getEmployee(), result);
    }

    @Test
    @DisplayName("Отменить новый заказ")
    void cancelTheNewOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(NEW_ORDER));

        String newOrder = out.cancelTheOrder(1L, "Employee");

        assertEquals("Заказ № " + NEW_ORDER.getId() + " был отменен сотрудником " + NEW_ORDER.getEmployee() + ", так как не прошла оплата.", newOrder);

    }

    @Test
    @DisplayName("Отменить текущий заказ")
    void cancelTheCurrentOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(CURRENT_ORDER));

        String currentOrder = out.cancelTheOrder(1L, "Employee");

        assertEquals("Заказ № " + CURRENT_ORDER.getId() + " был отменен сотрудником " + CURRENT_ORDER.getEmployee() + ", так как невозможно собрать полный заказ", currentOrder);
    }

    @Test
    @DisplayName("Выбросить исключение при попытке отменить готовый заказ")
    void throwNonValidStatusExceptionWhenCancelTheReadyOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(READY_ORDER));

        assertThrows(NonValidStatusException.class, ()-> out.cancelTheOrder(1L, "Employee"));
    }
}