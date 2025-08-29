package com.louis.javadesignpatterns.behavioral.state.game;

public class AttackState extends CharacterState {
    public AttackState(GameContext character) {
        super(character);
    }
    
    @Override
    public void attack() {
        System.out.println("角色发出强力攻击！返回正常状态");
        character.setState(character.getNormalState());
    }
    
    @Override
    public void defend() {
        System.out.println("攻击时无法防御！切换到防御状态");
        character.setState(character.getDefendState());
    }
    
    @Override
    public void move() {
        System.out.println("角色在攻击时移动（较慢）");
    }
    
    @Override
    public void takeDamage(int damage) {
        System.out.println("角色在攻击时受到 " + (damage + 5) + " 点额外伤害");
        character.reduceHealth(damage + 5);
    }
}