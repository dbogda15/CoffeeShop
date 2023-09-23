package me.dbogda.ufanettestcoffeeshop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Action {
    TO_WORK ("Взять в работу"),
    READY_FOR_DELIVERY ("Передать в доставку"),
    FINISH("Передать заказ клиенту"),
    CANCEL("Отменить заказ");

    private final String message;
}
