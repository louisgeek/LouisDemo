package com.louis.javadesignpatterns.behavioral.state.game;

public class DefendState extends CharacterState {
    public DefendState(GameContext character) {
        super(character);
    }
    
    @Override
    public void attack() {
        System.out.println("角色停止防御并发起攻击！");
        character.setState(character.getAttackState());
    }
    
    @Override
    public void defend() {
        System.out.println("角色继续防御。返回正常状态");
        character.setState(character.getNormalState());
    }
    
    @Override
    public void move() {
        System.out.println("角色在防御时缓慢移动");
    }
    
    @Override
    public void takeDamage(int damage) {
        int reducedDamage = Math.max(1, damage - 10);
        System.out.println("角色格挡！仅受到 " + reducedDamage + " 点伤害");
        character.reduceHealth(reducedDamage);
    }
}