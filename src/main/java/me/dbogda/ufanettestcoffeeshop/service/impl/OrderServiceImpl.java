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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

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
        return orderRepository.findById(id).orElseThrow(()-> new NotFoundException("Заказ с id = " + id + " не существует!"));
    }

    @Override
    public void publishEvent(Long orderId, String employeeName, Action action){
        Order order = findOrder(orderId);
        switch (action){
            case TO_WORK -> takeAnOrderToWork(order, employeeName);
            case READY_FOR_DELIVERY -> readyOrderForDelivery(order, employeeName);
            case FINISH -> issueAnOrder(order, employeeName);
            case CANCEL -> cancelTheOrder(order, employeeName);
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
        return "Заказ № " + id + " для " + order.getCustomer() +"\nСостав заказа: " + order.getProduct().getName()
                + "\nСтоимость: " + order.getProduct().getPrice() + "\nПримерное время получения: "
                + order.getTimeOfOrderIssue().format(DateTimeFormatter.ofPattern("HH:mm"))
                + "\nСтатус заказа: " + order.getStatus().getName();
    }
    public void takeAnOrderToWork(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.NEW)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ! Он уже занят");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.CURRENT);
            order.setTimeOfOrderIssue(order.getTimeOfOrder().plusMinutes(5));
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName;
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
            orderRepository.save(order);
        }
    }

    public void readyOrderForDelivery(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.CURRENT)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.READY);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName + " и готов к выдаче!";
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
            orderRepository.save(order);
        }
    }


    public void issueAnOrder(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.READY)) {
            throw new NonValidStatusException("Нельзя выдать данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.FINISHED);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был выдан сотрудником " + employeeName;
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
            orderRepository.save(order);
        }
    }

    public void cancelTheOrder(Order order, String employeeName) {
        order.setEmployee(employeeName);
        order.setTimeOfTheLastMoving(LocalDateTime.now());
        Status status = order.getStatus();
        switch (status){
            case NEW -> cancelNew(order, employeeName);
            case CURRENT -> cancelCurrent(order, employeeName);
            case READY -> cancelReady(order, employeeName);
            case FINISHED -> throw new NonValidStatusException("Нельзя отменить данный заказ! Он уже выдан");
        }
    }

    private void cancelNew(Order order, String employeeName){
        order.setStatus(Status.CANCELLED);
        String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как не прошла оплата.";
        order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        orderRepository.save(order);
    }

    private void cancelCurrent(Order order, String employeeName){
        order.setStatus(Status.CANCELLED);
        String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как невозможно собрать полный заказ";
        order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        orderRepository.save(order);
    }

    private void cancelReady(Order order, String employeeName){
        if (LocalDateTime.now().isAfter(order.getTimeOfOrderIssue().plusMinutes(10))){
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как заказ не забрали в течение 10 минут после готовности";
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
            orderRepository.save(order);
        } else throw new NonValidStatusException("Нельзя отменить данный заказ! Он ожидает получения!");
    }
}
