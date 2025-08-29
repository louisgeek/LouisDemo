package com.louis.javadesignpatterns.behavioral.state.order;

public class PendingState extends OrderState {
    public PendingState(Order order) {
        super(order);
    }
    
    @Override
    public void pay() {
        System.out.println("订单 " + order.getOrderId() + " 支付成功，状态变更为已付款");
        order.setState(order.getPaidState());
    }
    
    @Override
    public void ship() {
        System.out.println("订单 " + order.getOrderId() + " 未付款，无法发货");
    }
    
    @Override
    public void deliver() {
        System.out.println("订单 " + order.getOrderId() + " 未付款，无法配送");
    }
    
    @Override
    public void cancel() {
        System.out.println("订单 " + order.getOrderId() + " 已取消");
        order.setState(order.getCancelledState());
    }
    
    @Override
    public String getStatus() {
        return "待付款";
    }
}