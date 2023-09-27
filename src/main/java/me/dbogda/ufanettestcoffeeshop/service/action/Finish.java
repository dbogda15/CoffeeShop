package me.dbogda.ufanettestcoffeeshop.service.action;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;

import java.time.LocalDateTime;

public class Finish implements ActionStrategy {
    @Override
    public Action getAction() {
        return Action.FINISH;
    }

    @Override
    public void changeStatus(Order order, String employeeName) {
        if (!order.getStatus().equals(Status.READY)) {
            throw new NonValidStatusException("Нельзя выдать данный заказ!");
        } else {
            order.setEmployee(employeeName);
            order.setStatus(Status.READY);
            order.setTimeOfTheLastMoving(LocalDateTime.now());
            String message = "Заказ № " + order.getId() + " был взят в работу сотрудником " + employeeName + " и готов к выдаче!";
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        }
    }
}
