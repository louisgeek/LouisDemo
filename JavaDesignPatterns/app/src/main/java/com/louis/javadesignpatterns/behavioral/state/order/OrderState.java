package com.louis.javadesignpatterns.behavioral.state.order;

public abstract class OrderState {
    protected Order order;
    
    public OrderState(Order order) {
        this.order = order;
    }
    
    public abstract void pay();
    public abstract void ship();
    public abstract void deliver();
    public abstract void cancel();
    public abstract String getStatus();
}