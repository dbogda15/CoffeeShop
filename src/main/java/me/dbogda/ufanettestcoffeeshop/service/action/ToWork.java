package me.dbogda.ufanettestcoffeeshop.service.action;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;

import java.time.LocalDateTime;

public class ToWork implements ActionStrategy {
    @Override
    public Action getAction() {
        return Action.TO_WORK;
    }

    @Override
    public void changeStatus(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.NEW)) {
            throw new NonValidStatusException("Нельзя взять в работу данный заказ! Он уже занят");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.CURRENT);
            order.setTimeOfOrderIssue(order.getTimeOfOrder().plusMinutes(5));
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName;
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        }
    }
}
