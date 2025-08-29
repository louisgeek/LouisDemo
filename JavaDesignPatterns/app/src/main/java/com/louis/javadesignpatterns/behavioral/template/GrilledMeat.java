package com.louis.javadesignpatterns.behavioral.template;

public class GrilledMeat extends CookingProcess {
    @Override
    protected void prepareIngredients() {
        System.out.println("准备牛肉、烧烤调料");
    }
    
    @Override
    protected void cookMain() {
        System.out.println("烤架烧烤牛肉");
    }
    
    @Override
    protected boolean needMarinate() {
        return true; // 烤肉需要腌制
    }
    
    @Override
    protected void marinate() {
        System.out.println("用调料腌制牛肉30分钟");
    }
}