package com.louis.javadesignpatterns.behavioral.state.game;

public abstract class CharacterState {
    protected GameContext character;
    
    public CharacterState(GameContext character) {
        this.character = character;
    }
    
    public abstract void attack();
    public abstract void defend();
    public abstract void move();
    public abstract void takeDamage(int damage);
}