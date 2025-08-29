package com.louis.javadesignpatterns.behavioral.comparison;

public class ComparisonDemo {
    public static void main(String[] args) {
        String testData = "Hello World";
        
        System.out.println("=== 策略模式 vs 模板方法模式对比 ===\n");
        
        // 模板方法模式演示
        System.out.println("【模板方法模式】");
        System.out.println("特点: 父类定义算法骨架，子类实现具体步骤\n");
        
        DataProcessor xmlProcessor = new XmlProcessor();
        xmlProcessor.processData(testData);
        
        DataProcessor jsonProcessor = new JsonProcessor();
        jsonProcessor.processData(testData);
        
        // 策略模式演示
        System.out.println("【策略模式】");
        System.out.println("特点: 客户端选择算法，运行时可切换\n");
        
        DataConverter converter = new DataConverter();
        
        converter.setStrategy(new XmlTransformStrategy());
        converter.convertData(testData);
        
        converter.setStrategy(new JsonTransformStrategy());
        converter.convertData(testData);
        
        // 运行时切换策略
        System.out.println("【策略模式 - 运行时切换】");
        for (int i = 0; i < 3; i++) {
            if (i % 2 == 0) {
                converter.setStrategy(new XmlTransformStrategy());
            } else {
                converter.setStrategy(new JsonTransformStrategy());
            }
            converter.convertData("数据" + (i + 1));
        }
        
        System.out.println("=== 核心区别总结 ===");
        System.out.println("模板方法模式:");
        System.out.println("- 继承关系，编译时确定");
        System.out.println("- 父类控制算法流程");
        System.out.println("- 子类实现具体步骤");
        System.out.println("- 代码复用性好");
        
        System.out.println("\n策略模式:");
        System.out.println("- 组合关系，运行时切换");
        System.out.println("- 客户端控制算法选择");
        System.out.println("- 策略独立实现");
        System.out.println("- 灵活性更高");
    }
}