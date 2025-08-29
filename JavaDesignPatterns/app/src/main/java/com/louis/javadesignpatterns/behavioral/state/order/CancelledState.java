package com.louis.javadesignpatterns.behavioral.state.order;

public class CancelledState extends OrderState {
    public CancelledState(Order order) {
        super(order);
    }
    
    @Override
    public void pay() {
        System.out.println("订单 " + order.getOrderId() + " 已取消，无法付款");
    }
    
    @Override
    public void ship() {
        System.out.println("订单 " + order.getOrderId() + " 已取消，无法发货");
    }
    
    @Override
    public void deliver() {
        System.out.println("订单 " + order.getOrderId() + " 已取消，无法配送");
    }
    
    @Override
    public void cancel() {
        System.out.println("订单 " + order.getOrderId() + " 已经取消");
    }
    
    @Override
    public String getStatus() {
        return "已取消";
    }
}