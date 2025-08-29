package com.louis.javadesignpatterns.behavioral.template;

// 场景1: 烹饪流程
public abstract class CookingProcess {
    
    // 模板方法 - 定义烹饪流程
    public final void cook() {
        System.out.println("=== 开始烹饪 ===");
        
        prepareIngredients();
        if (needMarinate()) {
            marinate();
        }
        cookMain();
        addSeasoning();
        serve();
        
        System.out.println("=== 烹饪完成 ===\n");
    }
    
    // 抽象方法 - 子类必须实现
    protected abstract void prepareIngredients();
    protected abstract void cookMain();
    
    // 具体方法 - 通用步骤
    private void addSeasoning() {
        System.out.println("添加调料");
    }
    
    private void serve() {
        System.out.println("装盘上菜");
    }
    
    // 钩子方法 - 子类可选择重写
    protected boolean needMarinate() {
        return false;
    }
    
    protected void marinate() {
        System.out.println("腌制食材");
    }
}