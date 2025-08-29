package com.louis.javadesignpatterns.behavioral.state.elevator;

public class OpenState extends ElevatorState {
    public OpenState(ElevatorContext elevatorContext) {
        super(elevatorContext);
    }
    
    @Override
    public void open() {
        System.out.println("Door is already open");
    }
    
    @Override
    public void close() {
        System.out.println("Door closing");
        elevatorContext.setState(elevatorContext.getCloseState());
    }
    
    @Override
    public void run() {
        System.out.println("Cannot run with door open");
    }
    
    @Override
    public void stop() {
        System.out.println("Already stopped with door open");
    }
}