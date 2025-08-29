package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class PercentageDiscountStrategy implements DiscountStrategy {
    private double percentage;
    
    public PercentageDiscountStrategy(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double calculateDiscount(double originalPrice) {
        return originalPrice * (1 - percentage / 100);
    }
    
    @Override
    public String getDiscountType() {
        return percentage + "% 折扣";
    }
}