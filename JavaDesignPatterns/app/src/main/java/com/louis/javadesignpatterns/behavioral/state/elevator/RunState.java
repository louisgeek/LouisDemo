package com.louis.javadesignpatterns.behavioral.state.elevator;

public class RunState extends ElevatorState {
    public RunState(ElevatorContext elevatorContext) {
        super(elevatorContext);
    }
    
    @Override
    public void open() {
        System.out.println("Cannot open door while running");
    }
    
    @Override
    public void close() {
        System.out.println("Door is already closed while running");
    }
    
    @Override
    public void run() {
        System.out.println("Elevator is running");
    }
    
    @Override
    public void stop() {
        System.out.println("Elevator stopping");
        elevatorContext.setState(elevatorContext.getStopState());
    }
}