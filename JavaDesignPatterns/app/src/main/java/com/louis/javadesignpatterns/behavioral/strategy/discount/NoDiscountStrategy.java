package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double originalPrice) {
        return originalPrice;
    }
    
    @Override
    public String getDiscountType() {
        return "无折扣";
    }
}