package com.louis.javadesignpatterns.behavioral.template;

// 场景2: 测试框架
public abstract class TestFramework {
    
    // 模板方法 - 定义测试流程
    public final void runTest() {
        System.out.println("=== 开始测试 ===");
        
        setUp();
        executeTest();
        if (needCleanup()) {
            tearDown();
        }
        generateReport();
        
        System.out.println("=== 测试完成 ===\n");
    }
    
    // 具体方法 - 通用步骤
    private void setUp() {
        System.out.println("初始化测试环境");
    }
    
    private void generateReport() {
        System.out.println("生成测试报告");
    }
    
    // 抽象方法 - 子类实现具体测试
    protected abstract void executeTest();
    
    // 钩子方法 - 可选清理
    protected boolean needCleanup() {
        return true;
    }
    
    protected void tearDown() {
        System.out.println("清理测试环境");
    }
}