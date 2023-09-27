package me.dbogda.ufanettestcoffeeshop.service.action;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.enums.Status;
import me.dbogda.ufanettestcoffeeshop.exception.NonValidStatusException;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;


import java.time.LocalDateTime;
@AllArgsConstructor
public class Cancel implements ActionStrategy {
    @Override
    public Action getAction() {
        return Action.CANCEL;
    }

    @Override
    public void changeStatus(Order order, String employeeName) {
        order.setEmployee(employeeName);
        order.setTimeOfTheLastMoving(LocalDateTime.now());
        Status status = order.getStatus();
        switch (status) {
            case NEW -> cancelNew(order, employeeName);
            case CURRENT -> cancelCurrent(order, employeeName);
            case READY -> cancelReady(order, employeeName);
            case FINISHED -> throw new NonValidStatusException("Нельзя отменить данный заказ! Он уже выдан");
        }
    }
    private void cancelNew(Order order, String employeeName) {
        order.setStatus(Status.CANCELLED);
        String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как не прошла оплата.";
        order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
    }

    private void cancelCurrent(Order order, String employeeName) {
        order.setStatus(Status.CANCELLED);
        String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как невозможно собрать полный заказ";
        order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
    }

    private void cancelReady(Order order, String employeeName) {
        if (LocalDateTime.now().isAfter(order.getTimeOfOrderIssue().plusMinutes(10))) {
            order.setStatus(Status.CANCELLED);
            String message = "Заказ № " + order.getId() + " был отменен сотрудником " + employeeName + ", так как заказ не забрали в течение 10 минут после готовности";
            order.getReports().add(Report.builder().order(order).message(message).timeOfReport(LocalDateTime.now()).build());
        } else throw new NonValidStatusException("Нельзя отменить данный заказ! Он ожидает получения!");
    }
}
