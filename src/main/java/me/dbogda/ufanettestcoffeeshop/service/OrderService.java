package me.dbogda.ufanettestcoffeeshop.service;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.model.Order;

import java.util.List;

public interface OrderService {
    Order create(Order order);

    String deleteById(Long id);

    Order findOrder(Long id);

    List<Order> getAll();

    String publishEvent(Long orderId, String employeeName, Action action);

    List<Order> getNewOrders();

    String getOrderInfoForCustomer(Long id);

}
