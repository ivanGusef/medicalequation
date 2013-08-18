package com.medicalequation.me.exception;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 11.05.13
 * Time: 18:01
 * May the Force be with you, always
 */
public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
