package com.louis.javadesignpatterns.behavioral.strategy;

public class PaymentDemo {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();
        
        System.out.println("=== 策略模式支付系统演示 ===\n");
        
        // 使用信用卡支付
        context.setPaymentStrategy(new CreditCardStrategy("1234567890123456", "123"));
        context.executePayment(1500.0);
        
        // 使用支付宝支付
        context.setPaymentStrategy(new AlipayStrategy("user@example.com"));
        context.executePayment(2500.0);
        
        // 使用微信支付
        context.setPaymentStrategy(new WechatPayStrategy("wx_openid_123"));
        context.executePayment(800.0);
        
        System.out.println("=== 支付限额测试 ===\n");
        
        // 测试支付限额
        context.setPaymentStrategy(new WechatPayStrategy("wx_openid_456"));
        context.executePayment(25000.0); // 超过微信支付限额
        
        context.setPaymentStrategy(new CreditCardStrategy("9876543210987654", "456"));
        context.executePayment(25000.0); // 信用卡可以支付
        
        // 动态切换策略
        System.out.println("=== 动态策略切换 ===\n");
        double amount = 15000.0;
        
        // 尝试微信支付，失败后切换到信用卡
        context.setPaymentStrategy(new WechatPayStrategy("wx_user"));
        if (!context.executePayment(amount)) {
            System.out.println("切换支付方式...");
            context.setPaymentStrategy(new CreditCardStrategy("1111222233334444", "789"));
            context.executePayment(amount);
        }
    }
}