package com.codergm.orderservice.model;

public enum PaymentStatus {
    ACCEPTED("ACCEPTED");

    public final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
