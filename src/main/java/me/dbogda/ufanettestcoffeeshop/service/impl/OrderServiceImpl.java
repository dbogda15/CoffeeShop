package me.dbogda.ufanettestcoffeeshop.service.impl;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ReportService reportService;

    @Override
    public Order create(Order order) {
        orderRepository.save(order);
        reportService.create(new Report(order, "Заказ № " + order.getId() + " создан", LocalDateTime.now()));
        return order;
    }

    @Override
    public String deleteById(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return "Заказ удалён из БД";
        }).orElseThrow(() -> new NotFoundException("Заказ с id = " + id + " не существует!"));
    }

    @Override
    public Order getById(Long id) {
        Optional<Order> optional = orderRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Заказ с id = " + id + " не существует!");
        } else {
            return optional.get();
        }
    }

    @Override
    public String takeAnOrderToWork(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.NEW)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ! Он уже занят");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.CURRENT);
            order.setTimeOfOrderIssue(order.getTimeOfOrder().plusMinutes(5));
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + orderId + " был взят в работу сотрудником " + employeeName;
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        }
    }

    @Override
    public String readyOrderForDelivery(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.CURRENT)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.READY);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + orderId + " был взят в работу сотрудником " + employeeName + " и готов к выдаче!";
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        }
    }

    @Override
    public String issueAnOrder(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.READY)) {
            throw new NonValidStatusException("Нельзя выдать данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.FINISHED);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + orderId + " был выдан сотрудником " + employeeName;
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        }
    }

    @Override
    public String cancelTheOrder(Long orderId, String employeeName) {
        Order order = getById(orderId);
        order.setEmployee(employeeName);
        order.setTimeOfTheLastMoving(LocalDateTime.now());
        Status status = order.getStatus();
        if (status.equals(Status.FINISHED)) {
            throw new NonValidStatusException("Нельзя отменить данный заказ! Он уже выдан");
        } else if (status.equals(Status.NEW)) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + orderId + " был отменен сотрудником " + employeeName + ", так как не прошла оплата.";
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        } else if (status.equals(Status.CURRENT)) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + orderId + " был отменен сотрудником " + employeeName + ", так как невозможно собрать полный заказ";
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        } else if (status.equals(Status.READY) && LocalDateTime.now().isAfter(order.getTimeOfOrderIssue().plusMinutes(10))) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + orderId + " был отменен сотрудником " + employeeName + ", так как заказ не забрали в течение 10 минут после готовности";
            reportService.create(new Report(order, message, LocalDateTime.now()));
            return message;
        } else throw new NonValidStatusException("Нельзя отменить данный заказ! Он ожидает получения!");
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getNewOrders() {
        return orderRepository.getOrdersByStatus(Status.NEW);
    }

    @Override
    public StringBuilder getOrderInfoById(Long id) {
        Order order = getById(id);
        String status = "Текущий статус заказа № " + id + ": " + order.getStatus().toString();
        StringBuilder message = new StringBuilder("События по заказу: ");
        List<Report> reports = order.getReports();
        for (Report report : reports) {
            message.append("\n").append(report.getMessage()).append(". Время события: ").append(report.getLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
        return new StringBuilder(status).append("\n").append(message);
    }

    @Override
    public StringBuilder getAllOrderInfo() {
        List<Order> orders = getAll();
        StringBuilder result = new StringBuilder("Список отчетов:");
        for (Order order : orders){
            result.append("\n").append(getOrderInfoById(order.getId()));
        }
        return result;
    }
}
