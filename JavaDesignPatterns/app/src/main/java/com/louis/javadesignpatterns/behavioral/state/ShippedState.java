package com.louis.javadesignpatterns.behavioral.state;

public class ShippedState implements OrderState {
    @Override
    public void handle(OrderContext context) {
        System.out.println("订单已发货，等待用户确认");
        context.setState(null); //最终状态无后续
    }
}