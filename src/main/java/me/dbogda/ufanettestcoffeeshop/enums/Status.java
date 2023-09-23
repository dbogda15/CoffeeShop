package me.dbogda.ufanettestcoffeeshop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    NEW("Новый заказ"),
    CURRENT("Заказ взят в работу"),
    READY("Заказ готов к выдаче"),
    FINISHED("Заказ выдан! Приятного аппетита!"),
    CANCELLED("Заказ отменён");

    private final String name;
}
