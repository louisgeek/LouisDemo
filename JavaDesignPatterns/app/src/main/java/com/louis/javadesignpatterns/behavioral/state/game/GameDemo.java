package com.louis.javadesignpatterns.behavioral.state.game;

public class GameDemo {
    public static void main(String[] args) {
        GameContext hero = new GameContext();
        
        System.out.println("=== 游戏角色状态模式演示 ===");
        System.out.println("初始生命值: " + hero.getHealth());
        
        // 正常操作
        hero.move();
        hero.attack();
        hero.move();
        hero.defend();
        
        System.out.println("\n=== 战斗模拟 ===");
        
        // 战斗场景
        hero.takeDamage(20);
        System.out.println("生命值: " + hero.getHealth());
        
        hero.defend();
        hero.takeDamage(30); // 防御时减少伤害
        System.out.println("生命值: " + hero.getHealth());
        
        hero.attack();
        hero.takeDamage(40); // 攻击时额外伤害
        System.out.println("生命值: " + hero.getHealth());
        
        hero.takeDamage(50); // 这将杀死角色
        System.out.println("最终生命值: " + hero.getHealth());
        
        // 死亡后尝试操作
        hero.attack();
        hero.move();
    }
}