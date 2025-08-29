package com.louis.javadesignpatterns.behavioral.template;

public class FriedRice extends CookingProcess {
    @Override
    protected void prepareIngredients() {
        System.out.println("准备米饭、鸡蛋、蔬菜");
    }
    
    @Override
    protected void cookMain() {
        System.out.println("热锅炒制炒饭");
    }
}