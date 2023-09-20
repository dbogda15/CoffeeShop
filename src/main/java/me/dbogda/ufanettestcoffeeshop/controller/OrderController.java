package me.dbogda.ufanettestcoffeeshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.ProductType;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.model.Status;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<String> create(@RequestParam ProductType product,
                                 @RequestParam String name){
        Order order = new Order(product, name, LocalDateTime.now(), LocalDateTime.now().plusMinutes(8), LocalDateTime.now(), Status.NEW);
        String message = orderService.create(order);
        Report report = new Report(order, message, LocalDateTime.now());
        reportService.create(report);
        return ResponseEntity.ok(order.toString());
    }

    @GetMapping("/id")
    @Operation(
            summary = "Получить информацию о заказе",
            description = "Введите ID заказа")
    ResponseEntity<String> getInfoAboutOrder(@RequestParam Long id){
        String result = orderService.getOrderInfo(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @Operation(summary = "Получение списка всех заказов")
    ResponseEntity<List<Order>> getAll(){
        List<Order> result = orderService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/new")
    @Operation(summary = "Получение списка новых заказов")
    ResponseEntity<List<Order>> getNewOrders(){
        List<Order> result = orderService.getNewOrders();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Operation(
            summary = "Удаление заказа из БД",
            description = "Введите ID заказа")
    ResponseEntity<String> delete(@RequestParam Long id){
        String result = orderService.deleteById(id);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/toWork")
    @Operation(
            summary = "Взять в работу заказ",
            description = "Введите ID заказа и имя сотрудника")
    ResponseEntity<String> takeAnOrderToWork(@RequestParam Long orderId,
                                             @RequestParam String employeeName){
        String message = orderService.takeAnOrderToWork(orderId, employeeName);
        Report report = new Report(orderService.getById(orderId), message, LocalDateTime.now());
        reportService.create(report);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/ready")
    @Operation(
            summary = "Передать заказ в зону выдачи",
            description = "Введите ID заказа и имя сотрудника")
    ResponseEntity<String> readyOrderForDelivery(@RequestParam Long orderId,
                                                 @RequestParam String employeeName){
        String message = orderService.readyOrderForDelivery(orderId, employeeName);
        Report report = new Report(orderService.getById(orderId), message, LocalDateTime.now());
        reportService.create(report);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/finish")
    @Operation(
            summary = "Передать заказ клиенту",
            description = "Введите ID заказа и имя сотрудника")
    ResponseEntity<String> issueAnOrder(@RequestParam Long orderId,
                                        @RequestParam String employeeName){
        String message = orderService.issueAnOrder(orderId, employeeName);
        Report report = new Report(orderService.getById(orderId), message, LocalDateTime.now());
        reportService.create(report);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/cancel")
    @Operation(
            summary = "Отменить заказ",
            description = "Введите ID заказа и имя сотрудника")
    ResponseEntity<String> cancelTheOrder(@RequestParam Long orderId,
                                          @RequestParam String employeeName){
        String message = orderService.cancelTheOrder(orderId, employeeName);
        Report report = new Report(orderService.getById(orderId), message, LocalDateTime.now());
        reportService.create(report);
        return ResponseEntity.ok(message);
    }
}
