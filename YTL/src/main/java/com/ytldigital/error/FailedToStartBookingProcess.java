package com.ytldigital.error;

public class FailedToStartBookingProcess extends Exception {
    public FailedToStartBookingProcess(Exception cause) {
        super(cause);
    }
}