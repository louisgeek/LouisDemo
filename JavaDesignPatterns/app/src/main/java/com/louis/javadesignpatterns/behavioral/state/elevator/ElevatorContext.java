package com.louis.javadesignpatterns.behavioral.state.elevator;

public class ElevatorContext {
    private ElevatorState openState;
    private ElevatorState closeState;
    private ElevatorState runState;
    private ElevatorState stopState;
    private ElevatorState currentState;
    
    public ElevatorContext() {
        openState = new OpenState(this);
        closeState = new CloseState(this);
        runState = new RunState(this);
        stopState = new StopState(this);

        currentState = stopState; // Initial state is stop
    }
    
    public void setState(ElevatorState state) {
        this.currentState = state;
    }
    
    public void open() { currentState.open(); }
    public void close() { currentState.close(); }
    public void run() { currentState.run(); }
    public void stop() { currentState.stop(); }
    
    // Getters
    public ElevatorState getOpenState() { return openState; }
    public ElevatorState getCloseState() { return closeState; }
    public ElevatorState getRunState() { return runState; }
    public ElevatorState getStopState() { return stopState; }
}