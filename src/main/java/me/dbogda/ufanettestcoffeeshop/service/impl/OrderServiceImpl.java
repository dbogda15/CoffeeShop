package me.dbogda.ufanettestcoffeeshop.service.impl;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Status;
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
    public String create(Order order) {
        orderRepository.save(order);
        return "Заказ № " +  order.getId() + " создан";
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
        if(optional.isEmpty()){
            throw new NotFoundException("Заказ с id = " + id + " не существует!");
        } else {
            return optional.get();
        }
    }

    @Override
    public String takeAnOrderToWork(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.NEW)){
            throw new NonValidStatusException("Нельзя взять в работу данный заказ! Он уже занят");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.CURRENT);
            order.setTimeOfOrderIssue(order.getTimeOfOrder().plusMinutes(5));
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            return "Заказ № " + orderId + " был взят в работу сотрудником " + employeeName;
        }
    }

    @Override
    public String readyOrderForDelivery(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.CURRENT)){
            throw new NonValidStatusException("Нельзя взять в работу данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.READY);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            return "Заказ № " + orderId + " был взят в работу сотрудником " + employeeName + " и готов к выдаче!";
        }
    }

    @Override
    public String issueAnOrder(Long orderId, String employeeName) {
        Order order = getById(orderId);
        if (!order.getStatus().equals(Status.READY)){
            throw new NonValidStatusException("Нельзя выдать данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.FINISHED);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            return "Заказ № " + orderId + " был выдан сотрудником " +employeeName;
        }
    }

    @Override
    public String cancelTheOrder(Long orderId, String employeeName) {
        Order order = getById(orderId);
        order.setEmployee(employeeName);
        order.setTimeOfTheLastMoving(LocalDateTime.now());
        if (order.getStatus().equals(Status.FINISHED)) {
            throw new NonValidStatusException("Нельзя отменить данный заказ! Он уже выдан");
        } else if (order.getStatus().equals(Status.NEW)) {
            order.setStatus(Status.CANCELLED);
            return "Заказ № " + orderId + " был отменен сотрудником " + employeeName+ ", так как не прошла оплата.";
        } else if (order.getStatus().equals(Status.CURRENT)) {
            order.setStatus(Status.CANCELLED);
            return "Заказ № " + orderId + " был отменен сотрудником " + employeeName + ", так как невозможно собрать полный заказ";
        } else if (order.getStatus().equals(Status.READY) && LocalDateTime.now().isAfter(order.getTimeOfOrderIssue().plusMinutes(10))){
            order.setStatus(Status.CANCELLED);
            return "Заказ № " + orderId + " был отменен сотрудником " + employeeName + ", так как заказ не забрали в течение 10 минут после готовности";
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
    public String getOrderInfo(Long id) {
        Order order = getById(id);
        return order.toString();
    }
}
