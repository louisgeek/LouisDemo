package com.louis.javadesignpatterns.behavioral.strategy.discount;

public interface DiscountStrategy {
    double calculateDiscount(double originalPrice);
    String getDiscountType();
}