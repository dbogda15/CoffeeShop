package me.dbogda.ufanettestcoffeeshop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {
    COFFEE("Кофе", 180),
    TEA("Чай", 100),
    COOKIE("Печенье", 80),
    SANDWICH("Сэндвич", 200);

    private final String name;
    private final Integer price;
}
