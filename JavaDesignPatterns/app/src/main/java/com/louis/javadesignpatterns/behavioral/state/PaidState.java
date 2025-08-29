package com.louis.javadesignpatterns.behavioral.state;

public class PaidState implements OrderState {
    @Override
    public void handle(OrderContext context) {
        System.out.println("订单已支付，准备发货");
        context.setState(new ShippedState()); //发货后切换成发货状态
    }
}