package me.dbogda.ufanettestcoffeeshop.service.action;

import me.dbogda.ufanettestcoffeeshop.enums.Action;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import org.springframework.stereotype.Service;

@Service
public interface ActionStrategy {
    Action getAction();
    void changeStatus(Order order, String employeeName);
}
