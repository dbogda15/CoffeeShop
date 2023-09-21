package me.dbogda.ufanettestcoffeeshop.enums;

public enum Action {
    TO_WORK ("Взять в работу"),
    READY_FOR_DELIVERY ("Передать в доставку"),
    FINISH("Передать заказ клиенту"),
    CANCEL("Отменить заказ");

    private final String message;
    Action(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
