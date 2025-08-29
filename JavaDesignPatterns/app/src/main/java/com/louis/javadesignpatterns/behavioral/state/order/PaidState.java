package com.louis.javadesignpatterns.behavioral.state.order;

public class PaidState extends OrderState {
    public PaidState(Order order) {
        super(order);
    }
    
    @Override
    public void pay() {
        System.out.println("订单 " + order.getOrderId() + " 已经付款，无需重复支付");
    }
    
    @Override
    public void ship() {
        System.out.println("订单 " + order.getOrderId() + " 开始发货，状态变更为已发货");
        order.setState(order.getShippedState());
    }
    
    @Override
    public void deliver() {
        System.out.println("订单 " + order.getOrderId() + " 尚未发货，无法配送");
    }
    
    @Override
    public void cancel() {
        System.out.println("订单 " + order.getOrderId() + " 已付款，正在处理退款并取消订单");
        order.setState(order.getCancelledState());
    }
    
    @Override
    public String getStatus() {
        return "已付款";
    }
}