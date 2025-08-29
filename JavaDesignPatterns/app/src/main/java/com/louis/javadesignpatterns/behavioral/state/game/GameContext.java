package com.louis.javadesignpatterns.behavioral.state.game;

public class GameContext {
    private CharacterState normalState;
    private CharacterState attackState;
    private CharacterState defendState;
    private CharacterState deadState;
    private CharacterState currentState;
    private int health = 100;
    
    public GameContext() {
        normalState = new NormalState(this);
        attackState = new AttackState(this);
        defendState = new DefendState(this);
        deadState = new DeadState(this);
        currentState = normalState; // 初始状态为正常状态
    }
    
    public void setState(CharacterState state) {
        this.currentState = state;
    }
    
    public void attack() { currentState.attack(); }
    public void defend() { currentState.defend(); }
    public void move() { currentState.move(); }
    public void takeDamage(int damage) { currentState.takeDamage(damage); }
    
    public void reduceHealth(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            setState(deadState);
        }
    }
    
    public int getHealth() { return health; }
    
    // 状态获取器
    public CharacterState getNormalState() { return normalState; }
    public CharacterState getAttackState() { return attackState; }
    public CharacterState getDefendState() { return defendState; }
    public CharacterState getDeadState() { return deadState; }
}