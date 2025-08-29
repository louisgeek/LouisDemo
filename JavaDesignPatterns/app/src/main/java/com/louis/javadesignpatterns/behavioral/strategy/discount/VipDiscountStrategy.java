package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class VipDiscountStrategy implements DiscountStrategy {
    private String vipLevel;
    
    public VipDiscountStrategy(String vipLevel) {
        this.vipLevel = vipLevel;
    }
    
    @Override
    public double calculateDiscount(double originalPrice) {
        double discountRate = getVipDiscountRate();
        return originalPrice * (1 - discountRate);
    }
    
    private double getVipDiscountRate() {
        switch (vipLevel.toLowerCase()) {
            case "bronze": return 0.05; // 5% 折扣
            case "silver": return 0.10; // 10% 折扣
            case "gold": return 0.15;   // 15% 折扣
            case "platinum": return 0.20; // 20% 折扣
            default: return 0.0;
        }
    }
    
    @Override
    public String getDiscountType() {
        return vipLevel.toUpperCase() + " VIP折扣 (" + (getVipDiscountRate() * 100) + "%)";
    }
}