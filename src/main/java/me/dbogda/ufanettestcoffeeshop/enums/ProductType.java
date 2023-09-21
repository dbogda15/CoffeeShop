package me.dbogda.ufanettestcoffeeshop.enums;

public enum ProductType {
    COFFEE("Кофе", 180),
    TEA("Чай", 100),
    COOKIE("Печенье", 80),
    SANDWICH("Сэндвич", 200);

    private final String name;
    private final Integer price;

    ProductType(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
