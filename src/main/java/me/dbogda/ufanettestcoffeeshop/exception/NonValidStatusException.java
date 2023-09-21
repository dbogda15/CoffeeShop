package me.dbogda.ufanettestcoffeeshop.exception;

public class NonValidStatusException extends RuntimeException {
    public NonValidStatusException(String message) {
        super(message);
    }
}
