package com.louis.javadesignpatterns.behavioral.state.elevator;

public abstract class ElevatorState {
    protected ElevatorContext elevatorContext;
    
    public ElevatorState(ElevatorContext elevatorContext) {
        this.elevatorContext = elevatorContext;
    }
    
    public abstract void open();
    public abstract void close();
    public abstract void run();
    public abstract void stop();
}