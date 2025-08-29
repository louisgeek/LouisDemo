package com.louis.javadesignpatterns.behavioral.comparison;

// 策略模式 - 上下文类
public class DataConverter {
    private DataTransformStrategy strategy;
    
    public void setStrategy(DataTransformStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void convertData(String data) {
        if (strategy == null) {
            System.out.println("未设置转换策略");
            return;
        }
        
        System.out.println("=== 策略模式数据转换 ===");
        System.out.println("转换格式: " + strategy.getFormatType());
        
        // 客户端控制整个流程
        validateData(data);
        String result = strategy.transform(data);
        saveData(result);
        
        System.out.println("=== 转换完成 ===\n");
    }
    
    private void validateData(String data) {
        System.out.println("验证数据: " + data);
    }
    
    private void saveData(String data) {
        System.out.println("保存数据: " + data);
    }
}