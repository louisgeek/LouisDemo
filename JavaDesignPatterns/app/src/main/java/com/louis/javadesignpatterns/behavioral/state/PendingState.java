package com.louis.javadesignpatterns.behavioral.state;

public class PendingState implements OrderState {
    @Override
    public void handle(OrderContext context) {
        System.out.println("订单待支付，触发支付提醒");
        context.setState(new PaidState()); //支付后切换成已支付状态
    }
}