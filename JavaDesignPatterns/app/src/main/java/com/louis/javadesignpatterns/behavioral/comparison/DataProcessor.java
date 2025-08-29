package com.louis.javadesignpatterns.behavioral.comparison;

// 模板方法模式 - 定义算法骨架
public abstract class DataProcessor {
    
    // 模板方法 - 定义处理流程
    public final void processData(String data) {
        System.out.println("=== 开始数据处理 ===");
        
        validateData(data);
        String processed = transformData(data);
        saveData(processed);
        
        System.out.println("=== 数据处理完成 ===\n");
    }
    
    // 具体方法 - 通用逻辑
    private void validateData(String data) {
        System.out.println("验证数据: " + data);
    }
    
    // 抽象方法 - 子类实现
    protected abstract String transformData(String data);
    
    // 钩子方法 - 可选重写
    protected void saveData(String data) {
        System.out.println("保存数据: " + data);
    }
}