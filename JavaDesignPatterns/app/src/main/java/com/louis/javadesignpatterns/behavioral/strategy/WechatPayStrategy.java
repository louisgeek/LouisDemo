package com.louis.javadesignpatterns.behavioral.strategy;

public class WechatPayStrategy implements PaymentStrategy {
    private String openId;
    
    public WechatPayStrategy(String openId) {
        this.openId = openId;
    }
    
    @Override
    public boolean pay(double amount) {
        System.out.println("使用微信支付 ¥" + amount);
        System.out.println("用户ID: " + openId);
        // 模拟支付处理
        if (amount > 0 && amount <= 20000) {
            System.out.println("微信支付成功");
            return true;
        }
        System.out.println("微信支付失败：金额超限");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "微信支付";
    }
}