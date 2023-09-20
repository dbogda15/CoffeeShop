package me.dbogda.ufanettestcoffeeshop.service;

import me.dbogda.ufanettestcoffeeshop.model.Order;

import java.util.List;

public interface OrderService {
    String create(Order order);
    String deleteById(Long id);
    Order getById(Long id);
    List<Order> getAll();
    String takeAnOrderToWork(Long orderId, String employeeName);
    String readyOrderForDelivery(Long orderId, String employeeName);
    String issueAnOrder(Long orderId, String employeeName);
    String cancelTheOrder(Long orderId, String employeeName);
    List<Order> getNewOrders();
    String getOrderInfo(Long id);
}
