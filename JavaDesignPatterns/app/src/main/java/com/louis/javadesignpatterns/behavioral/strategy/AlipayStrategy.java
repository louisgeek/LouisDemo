package com.louis.javadesignpatterns.behavioral.strategy;

public class AlipayStrategy implements PaymentStrategy {
    private String account;
    
    public AlipayStrategy(String account) {
        this.account = account;
    }
    
    @Override
    public boolean pay(double amount) {
        System.out.println("使用支付宝支付 ¥" + amount);
        System.out.println("账户: " + account);
        // 模拟支付处理
        if (amount > 0 && amount <= 100000) {
            System.out.println("支付宝支付成功");
            return true;
        }
        System.out.println("支付宝支付失败：金额超限");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "支付宝";
    }
}