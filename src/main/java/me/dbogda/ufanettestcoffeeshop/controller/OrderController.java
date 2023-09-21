package me.dbogda.ufanettestcoffeeshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @PostMapping
    @Operation(summary = "Создание заказа")
    Order create(@RequestParam ProductType product,
                 @RequestParam String name) {
        return orderService.create(Order.builder()
                .product(product)
                .customer(name)
                .timeOfOrder(LocalDateTime.now())
                .timeOfOrderIssue(LocalDateTime.now().plusMinutes(8))
                .timeOfTheLastMoving(LocalDateTime.now())
                .status(Status.NEW)
                .reports(new ArrayList<>())
                .build());
    }

    @GetMapping("/id")
    @Operation(
            summary = "Получить информацию о текущем статусе заказа и цепочке событий",
            description = "Введите ID заказа")
    Order getInfoAboutOrderById(@RequestParam Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить информацию о текущем статусе всех заказов и цепочке событий")
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

    @PutMapping("/action")
    @Operation(
            summary = "Произвести действие над заказом",
            description = "Введите ID заказа и имя сотрудника")
    String takeAnOrderToWork(@RequestParam Long orderId,
                             @RequestParam String employeeName,
                             @RequestParam Action action) {
        return orderService.makeSomeActionWithOrder(orderId, employeeName, action);
    }
}
