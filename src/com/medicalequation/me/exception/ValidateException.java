package com.medicalequation.me.exception;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 11.05.13
 * Time: 18:01
 * May the Force be with you, always
 */
public class ValidateException extends RuntimeException {

    public static final int WRONG_GLISSON = 1;
    public static final int WRONG_AGE = 2;

    private int code;

    public ValidateException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        switch (code) {
            case WRONG_AGE:
                return "Возраст должен быть в пределах от 1 до 120";
            case WRONG_GLISSON:
                return "Сумма Глисона должна быть в пределах от 2 до 10";
            default:
                return super.getMessage();
        }
    }
}
