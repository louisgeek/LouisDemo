package com.louis.javadesignpatterns.behavioral.strategy;

public interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentType();
}