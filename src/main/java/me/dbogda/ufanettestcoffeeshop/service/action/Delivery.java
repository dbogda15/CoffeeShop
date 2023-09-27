package me.dbogda.ufanettestcoffeeshop.service.action;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;

import java.time.LocalDateTime;

public class Delivery implements ActionStrategy {
    @Override
    public Action getAction() {
        return Action.READY_FOR_DELIVERY;
    }

    @Override
    public void changeStatus(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.CURRENT)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.FINISHED);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был выдан сотрудником " + employeeName;
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        }
    }
}
