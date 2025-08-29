package com.louis.javadesignpatterns.behavioral.state.game;

public class NormalState extends CharacterState {
    public NormalState(GameContext character) {
        super(character);
    }
    
    @Override
    public void attack() {
        System.out.println("角色发起攻击！切换到攻击状态");
        character.setState(character.getAttackState());
    }
    
    @Override
    public void defend() {
        System.out.println("角色进入防御！切换到防御状态");
        character.setState(character.getDefendState());
    }
    
    @Override
    public void move() {
        System.out.println("角色正常移动");
    }
    
    @Override
    public void takeDamage(int damage) {
        System.out.println("角色受到 " + damage + " 点伤害");
        character.reduceHealth(damage);
    }
}