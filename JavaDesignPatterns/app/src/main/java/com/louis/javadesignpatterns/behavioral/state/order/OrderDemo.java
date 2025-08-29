package com.louis.javadesignpatterns.behavioral.state.order;

public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order("ORD001");
        
        System.out.println("=== 订单状态管理演示 ===");
        System.out.println("订单号: " + order.getOrderId());
        System.out.println("当前状态: " + order.getStatus());
        
        System.out.println("\n=== 正常订单流程 ===");
        
        // 正常流程：待付款 -> 已付款 -> 已发货 -> 已送达
        order.pay();
        System.out.println("当前状态: " + order.getStatus());
        
        order.ship();
        System.out.println("当前状态: " + order.getStatus());
        
        order.deliver();
        System.out.println("当前状态: " + order.getStatus());
        
        System.out.println("\n=== 异常操作演示 ===");
        
        // 创建新订单演示异常操作
        Order order2 = new Order("ORD002");
        System.out.println("\n新订单号: " + order2.getOrderId());
        
        // 尝试未付款直接发货
        order2.ship();
        
        // 付款后取消
        order2.pay();
        order2.cancel();
        System.out.println("最终状态: " + order2.getStatus());
        
        // 尝试对已取消订单操作
        order2.pay();
        order2.ship();
    }
}