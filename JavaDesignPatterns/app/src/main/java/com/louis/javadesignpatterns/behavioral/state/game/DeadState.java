package com.louis.javadesignpatterns.behavioral.state.game;

public class DeadState extends CharacterState {
    public DeadState(GameContext character) {
        super(character);
    }
    
    @Override
    public void attack() {
        System.out.println("死亡角色无法攻击");
    }
    
    @Override
    public void defend() {
        System.out.println("死亡角色无法防御");
    }
    
    @Override
    public void move() {
        System.out.println("死亡角色无法移动");
    }
    
    @Override
    public void takeDamage(int damage) {
        System.out.println("死亡角色不再受到伤害");
    }
}