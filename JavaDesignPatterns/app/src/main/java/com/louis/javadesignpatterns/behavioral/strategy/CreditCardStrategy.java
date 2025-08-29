package com.louis.javadesignpatterns.behavioral.strategy;

public class CreditCardStrategy implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    
    public CreditCardStrategy(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    
    @Override
    public boolean pay(double amount) {
        System.out.println("使用信用卡支付 ¥" + amount);
        System.out.println("卡号: ****" + cardNumber.substring(cardNumber.length() - 4));
        // 模拟支付处理
        if (amount > 0 && amount <= 50000) {
            System.out.println("信用卡支付成功");
            return true;
        }
        System.out.println("信用卡支付失败：金额超限");
        return false;
    }
    
    @Override
    public String getPaymentType() {
        return "信用卡";
    }
}