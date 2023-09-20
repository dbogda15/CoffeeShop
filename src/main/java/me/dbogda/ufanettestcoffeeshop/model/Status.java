package me.dbogda.ufanettestcoffeeshop.model;

public enum Status {
    NEW("Новый заказ"),
    CURRENT("Заказ взят в работу"),
    READY("Заказ готов к выдаче"),
    FINISHED("Заказ выдан! Приятного аппетита!"),
    CANCELLED("Заказ отменён");

    private final String name;
    Status(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
