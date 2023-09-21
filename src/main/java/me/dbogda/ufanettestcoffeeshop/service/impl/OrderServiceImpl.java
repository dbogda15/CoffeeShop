package me.dbogda.ufanettestcoffeeshop.service.impl;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public Order create(Order order) {
        order.getReports().add(new Report(order, "Заказ создан", LocalDateTime.now()));
        orderRepository.save(order);
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
    public String makeSomeActionWithOrder(Long orderId, String employeeName, Action action){
        Order order = getById(orderId);
        return switch (action){
            case TO_WORK -> takeAnOrderToWork(order, employeeName);
            case READY_FOR_DELIVERY -> readyOrderForDelivery(order, employeeName);
            case FINISH -> issueAnOrder(order, employeeName);
            case CANCEL -> cancelTheOrder(order, employeeName);
        };
    }

    public String takeAnOrderToWork(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.NEW)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ! Он уже занят");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.CURRENT);
            order.setTimeOfOrderIssue(order.getTimeOfOrder().plusMinutes(5));
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName;
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        }
    }

    public String readyOrderForDelivery(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.CURRENT)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.READY);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName + " и готов к выдаче!";
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        }
    }


    public String issueAnOrder(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.READY)) {
            throw new NonValidStatusException("Нельзя выдать данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.FINISHED);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был выдан сотрудником " + employeeName;
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        }
    }

    public String cancelTheOrder(Order order, String employeeName) {
        order.setEmployee(employeeName);
        order.setTimeOfTheLastMoving(LocalDateTime.now());
        Status status = order.getStatus();
        if (status.equals(Status.FINISHED)) {
            throw new NonValidStatusException("Нельзя отменить данный заказ! Он уже выдан");
        } else if (status.equals(Status.NEW)) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как не прошла оплата.";
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        } else if (status.equals(Status.CURRENT)) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как невозможно собрать полный заказ";
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        } else if (status.equals(Status.READY) && LocalDateTime.now().isAfter(order.getTimeOfOrderIssue().plusMinutes(10))) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как заказ не забрали в течение 10 минут после готовности";
            order.getReports().add(new Report(order, message, LocalDateTime.now()));
            orderRepository.save(order);
            return message;
        }
        else throw new NonValidStatusException("Нельзя отменить данный заказ! Он ожидает получения!");
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getNewOrders() {
        return orderRepository.getOrdersByStatus(Status.NEW);
    }
}
