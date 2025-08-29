package com.louis.javadesignpatterns.behavioral.strategy;

public class PaymentContext {
    private PaymentStrategy strategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public boolean executePayment(double amount) {
        if (strategy == null) {
            System.out.println("请选择支付方式");
            return false;
        }
        
        System.out.println("=== 开始支付 ===");
        System.out.println("支付方式: " + strategy.getPaymentType());
        boolean result = strategy.pay(amount);
        System.out.println("=== 支付" + (result ? "完成" : "失败") + " ===\n");
        return result;
    }
}