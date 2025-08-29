package com.louis.javadesignpatterns.behavioral.state.elevator;

public class ElevatorDemo {
    public static void main(String[] args) {
        ElevatorContext elevatorContext = new ElevatorContext();
        
        System.out.println("=== Elevator State Pattern Demo ===");
        
        // Normal operation flow
        elevatorContext.open();    // Open door from stop state
        elevatorContext.close();   // Close door from open state
        elevatorContext.run();     // Run from close state
        elevatorContext.stop();    // Stop from run state
        
        System.out.println("\n=== Invalid Operations Demo ===");
        
        // Invalid operations
        elevatorContext.run();     // Run directly from stop state
        elevatorContext.open();    // Try to open door while running (not allowed)
        elevatorContext.stop();    // Stop from run state
        elevatorContext.open();    // Open door from stop state
    }
}