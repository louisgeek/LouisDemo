package com.louis.javadesignpatterns.behavioral.strategy.discount;

public class DiscountDemo {
    public static void main(String[] args) {
        PriceCalculator calculator = new PriceCalculator();
        double originalPrice = 1000.0;
        
        System.out.println("=== 折扣计算策略模式演示 ===\n");
        
        // 无折扣
        calculator.setDiscountStrategy(new NoDiscountStrategy());
        calculator.calculateFinalPrice(originalPrice);
        
        // 百分比折扣
        calculator.setDiscountStrategy(new PercentageDiscountStrategy(20));
        calculator.calculateFinalPrice(originalPrice);
        
        // 固定金额折扣
        calculator.setDiscountStrategy(new FixedAmountDiscountStrategy(150));
        calculator.calculateFinalPrice(originalPrice);
        
        // VIP会员折扣
        calculator.setDiscountStrategy(new VipDiscountStrategy("gold"));
        calculator.calculateFinalPrice(originalPrice);
        
        // 批量购买折扣
        calculator.setDiscountStrategy(new BulkDiscountStrategy(25));
        calculator.calculateFinalPrice(originalPrice);
        
        System.out.println("=== 不同商品价格测试 ===\n");
        
        // 测试不同价格商品
        double[] prices = {50.0, 500.0, 2000.0};
        
        for (double price : prices) {
            System.out.println("商品价格: ¥" + price);
            
            // VIP折扣
            calculator.setDiscountStrategy(new VipDiscountStrategy("platinum"));
            calculator.calculateFinalPrice(price);
            
            // 固定金额折扣（可能超过商品价格）
            calculator.setDiscountStrategy(new FixedAmountDiscountStrategy(100));
            calculator.calculateFinalPrice(price);
            
            System.out.println();
        }
        
        System.out.println("=== 批量折扣阶梯测试 ===\n");
        
        int[] quantities = {5, 15, 30, 60, 120};
        double unitPrice = 100.0;
        
        for (int qty : quantities) {
            calculator.setDiscountStrategy(new BulkDiscountStrategy(qty));
            double totalPrice = unitPrice * qty;
            System.out.println("数量: " + qty + "件，总价: ¥" + totalPrice);
            calculator.calculateFinalPrice(totalPrice);
        }
    }
}