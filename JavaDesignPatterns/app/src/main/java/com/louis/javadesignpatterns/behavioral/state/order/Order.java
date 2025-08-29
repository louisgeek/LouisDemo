package com.louis.javadesignpatterns.behavioral.state.order;

public class Order {
    private OrderState pendingState;
    private OrderState paidState;
    private OrderState shippedState;
    private OrderState deliveredState;
    private OrderState cancelledState;
    private OrderState currentState;
    private String orderId;
    
    public Order(String orderId) {
        this.orderId = orderId;
        pendingState = new PendingState(this);
        paidState = new PaidState(this);
        shippedState = new ShippedState(this);
        deliveredState = new DeliveredState(this);
        cancelledState = new CancelledState(this);
        currentState = pendingState; // 初始状态为待付款
    }
    
    public void setState(OrderState state) {
        this.currentState = state;
    }
    
    public void pay() { currentState.pay(); }
    public void ship() { currentState.ship(); }
    public void deliver() { currentState.deliver(); }
    public void cancel() { currentState.cancel(); }
    public String getStatus() { return currentState.getStatus(); }
    
    public String getOrderId() { return orderId; }
    
    // 状态获取器
    public OrderState getPendingState() { return pendingState; }
    public OrderState getPaidState() { return paidState; }
    public OrderState getShippedState() { return shippedState; }
    public OrderState getDeliveredState() { return deliveredState; }
    public OrderState getCancelledState() { return cancelledState; }
}