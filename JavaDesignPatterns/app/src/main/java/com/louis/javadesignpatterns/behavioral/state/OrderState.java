package com.louis.javadesignpatterns.behavioral.state;

public interface OrderState {
    void handle(OrderContext orderContext);
}