package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class FixedAmountDiscountStrategy implements DiscountStrategy {
    private double discountAmount;
    
    public FixedAmountDiscountStrategy(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    @Override
    public double calculateDiscount(double originalPrice) {
        return Math.max(0, originalPrice - discountAmount);
    }
    
    @Override
    public String getDiscountType() {
        return "减免 ¥" + discountAmount;
    }
}