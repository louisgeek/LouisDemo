package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class BulkDiscountStrategy implements DiscountStrategy {
    private int quantity;
    
    public BulkDiscountStrategy(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public double calculateDiscount(double originalPrice) {
        double discountRate = getBulkDiscountRate();
        return originalPrice * (1 - discountRate);
    }
    
    private double getBulkDiscountRate() {
        if (quantity >= 100) return 0.20;      // 100件以上 20% 折扣
        else if (quantity >= 50) return 0.15;  // 50-99件 15% 折扣
        else if (quantity >= 20) return 0.10;  // 20-49件 10% 折扣
        else if (quantity >= 10) return 0.05;  // 10-19件 5% 折扣
        else return 0.0;                       // 10件以下无折扣
    }
    
    @Override
    public String getDiscountType() {
        double rate = getBulkDiscountRate();
        return "批量折扣 " + quantity + "件 (" + (rate * 100) + "%)";
    }
}