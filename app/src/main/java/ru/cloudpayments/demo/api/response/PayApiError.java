package ru.cloudpayments.demo.api.response;

public class PayApiError extends Throwable {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PayApiError(String message) {
        this.message = message;
    }
}
