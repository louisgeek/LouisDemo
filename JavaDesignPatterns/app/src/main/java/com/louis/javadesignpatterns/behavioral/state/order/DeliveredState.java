package com.louis.javadesignpatterns.behavioral.state.order;

public class DeliveredState extends OrderState {
    public DeliveredState(Order order) {
        super(order);
    }
    
    @Override
    public void pay() {
        System.out.println("订单 " + order.getOrderId() + " 已完成，无需付款");
    }
    
    @Override
    public void ship() {
        System.out.println("订单 " + order.getOrderId() + " 已完成配送");
    }
    
    @Override
    public void deliver() {
        System.out.println("订单 " + order.getOrderId() + " 已经送达完成");
    }
    
    @Override
    public void cancel() {
        System.out.println("订单 " + order.getOrderId() + " 已完成，如需退货请联系客服");
    }
    
    @Override
    public String getStatus() {
        return "已送达";
    }
}