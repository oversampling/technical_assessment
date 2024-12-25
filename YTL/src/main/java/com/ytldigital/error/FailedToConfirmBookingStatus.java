package com.ytldigital.error;

public class FailedToConfirmBookingStatus extends Exception {
    public FailedToConfirmBookingStatus(Exception cause) {
        super(cause);
    }
}