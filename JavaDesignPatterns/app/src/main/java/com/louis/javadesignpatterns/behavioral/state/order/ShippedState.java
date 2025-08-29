package com.louis.javadesignpatterns.behavioral.state.order;

public class ShippedState extends OrderState {
    public ShippedState(Order order) {
        super(order);
    }
    
    @Override
    public void pay() {
        System.out.println("订单 " + order.getOrderId() + " 已经付款并发货");
    }
    
    @Override
    public void ship() {
        System.out.println("订单 " + order.getOrderId() + " 已经发货，正在运输中");
    }
    
    @Override
    public void deliver() {
        System.out.println("订单 " + order.getOrderId() + " 配送完成，状态变更为已送达");
        order.setState(order.getDeliveredState());
    }
    
    @Override
    public void cancel() {
        System.out.println("订单 " + order.getOrderId() + " 已发货，无法取消，请联系客服");
    }
    
    @Override
    public String getStatus() {
        return "已发货";
    }
}