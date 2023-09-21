package me.dbogda.ufanettestcoffeeshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка в параметрах запроса"),
        @ApiResponse(responseCode = "404", description = "Несуществующий URL"),
        @ApiResponse(responseCode = "500", description = "Ошибка со стороны сервера")
})
public class OrderController {
    private final OrderService orderService;
    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Создание заказа")
    Order create(@RequestParam ProductType product,
                 @RequestParam String name) {
        return orderService.create(new Order(product, name, LocalDateTime.now(), LocalDateTime.now().plusMinutes(8), LocalDateTime.now(), Status.NEW));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Получить информацию о текущем статусе заказа и цепочке событий",
            description = "Введите ID заказа")
    StringBuilder getInfoAboutOrderById(@RequestParam Long id) {
        return orderService.getOrderInfoById(id);
    }

    @GetMapping("/all_info")
    @Operation(summary = "Получить информацию о текущем статусе всех заказов и цепочке событий")
    StringBuilder getInfoAboutAllOrder(){
        return orderService.getAllOrderInfo();
    }

    @GetMapping("/all")
    @Operation(summary = "Получение списка всех заказов")
    List<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/new")
    @Operation(summary = "Получение списка новых заказов")
    List<Order> getNewOrders() {
        return orderService.getNewOrders();
    }

    @DeleteMapping
    @Operation(
            summary = "Удаление заказа из БД",
            description = "Введите ID заказа")
    String delete(@RequestParam Long id) {
        return orderService.deleteById(id);
    }

    @PutMapping("/toWork")
    @Operation(
            summary = "Взять в работу заказ",
            description = "Введите ID заказа и имя сотрудника")
    String takeAnOrderToWork(@RequestParam Long orderId,
                             @RequestParam String employeeName) {
        return orderService.takeAnOrderToWork(orderId, employeeName);
    }

    @PutMapping("/ready")
    @Operation(
            summary = "Передать заказ в зону выдачи",
            description = "Введите ID заказа и имя сотрудника")
    String readyOrderForDelivery(@RequestParam Long orderId,
                                 @RequestParam String employeeName) {
        return orderService.readyOrderForDelivery(orderId, employeeName);
    }

    @PutMapping("/finish")
    @Operation(
            summary = "Передать заказ клиенту",
            description = "Введите ID заказа и имя сотрудника")
    String issueAnOrder(@RequestParam Long orderId,
                        @RequestParam String employeeName) {
        return orderService.issueAnOrder(orderId, employeeName);
    }

    @PutMapping("/cancel")
    @Operation(
            summary = "Отменить заказ",
            description = "Введите ID заказа и имя сотрудника")
    String cancelTheOrder(@RequestParam Long orderId,
                          @RequestParam String employeeName) {
        return orderService.cancelTheOrder(orderId, employeeName);
    }
}
