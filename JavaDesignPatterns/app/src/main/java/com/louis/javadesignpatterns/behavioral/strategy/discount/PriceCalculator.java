package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class PriceCalculator {
    private DiscountStrategy discountStrategy;
    
    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }
    
    public double calculateFinalPrice(double originalPrice) {
        if (discountStrategy == null) {
            discountStrategy = new NoDiscountStrategy();
        }
        
        double finalPrice = discountStrategy.calculateDiscount(originalPrice);
        double savedAmount = originalPrice - finalPrice;
        
        System.out.println("折扣类型: " + discountStrategy.getDiscountType());
        System.out.println("原价: ¥" + String.format("%.2f", originalPrice));
        System.out.println("优惠: ¥" + String.format("%.2f", savedAmount));
        System.out.println("实付: ¥" + String.format("%.2f", finalPrice));
        System.out.println("-------------------");
        
        return finalPrice;
    }
}