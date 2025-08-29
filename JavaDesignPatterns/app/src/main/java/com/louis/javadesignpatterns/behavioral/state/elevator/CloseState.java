package com.louis.javadesignpatterns.behavioral.state.elevator;

public class CloseState extends ElevatorState {
    public CloseState(ElevatorContext elevatorContext) {
        super(elevatorContext);
    }
    
    @Override
    public void open() {
        System.out.println("Door opening");
        elevatorContext.setState(elevatorContext.getOpenState());
    }
    
    @Override
    public void close() {
        System.out.println("Door is already closed");
    }
    
    @Override
    public void run() {
        System.out.println("Elevator starting to run");
        elevatorContext.setState(elevatorContext.getRunState());
    }
    
    @Override
    public void stop() {
        System.out.println("Elevator stopping");
        elevatorContext.setState(elevatorContext.getStopState());
    }
}