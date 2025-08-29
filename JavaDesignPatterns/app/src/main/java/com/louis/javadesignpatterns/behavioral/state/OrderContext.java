package com.louis.javadesignpatterns.behavioral.state;

public class OrderContext {
    //持有一个当前状态类对象
    private OrderState currentState;

    public OrderContext(OrderState state) {
        this.currentState = state;
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void processOrder() {
        if (currentState != null) {
            currentState.handle(this);
        } else {
            System.out.println("订单已完成");
        }
    }
}