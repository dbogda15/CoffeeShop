package me.dbogda.ufanettestcoffeeshop.service.impl;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.service.OrderService;
import me.dbogda.ufanettestcoffeeshop.service.action.*;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final List<ActionStrategy> list = List.of(new ToWork(), new Delivery(), new Finish(), new Cancel());

    @Override
    public Long create(Order order) {
        order.getReports().add(Report.builder().order(order).message("Заказ создан").timeOfReport(LocalDateTime.now()).build());
        orderRepository.save(order);
        return order.getId();
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.delete(findOrder(id));
    }

    @Override
    public Order findOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Заказ с id = " + id + " не существует!"));
    }

    @Override
    public void publishEvent(Long orderId, String employeeName, Action action) {
        Order order = findOrder(orderId);
        for (ActionStrategy strategy : list) {
            if (strategy.getAction() == action) {
                strategy.changeStatus(order, employeeName);
                orderRepository.save(order);
            }
        }
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
    public String getOrderInfoForCustomer(Long id) {
        Order order = findOrder(id);
        return "Заказ № " + id + " для " + order.getCustomer() + "\nСостав заказа: " + order.getProduct().getName()
                + "\nСтоимость: " + order.getProduct().getPrice() + "\nПримерное время получения: "
                + order.getTimeOfOrderIssue().format(DateTimeFormatter.ofPattern("HH:mm"))
                + "\nСтатус заказа: " + order.getStatus().getName();
    }
}
