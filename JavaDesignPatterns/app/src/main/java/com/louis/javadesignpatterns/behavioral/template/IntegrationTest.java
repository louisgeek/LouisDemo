package com.louis.javadesignpatterns.behavioral.template;

public class IntegrationTest extends TestFramework {
    @Override
    protected void executeTest() {
        System.out.println("执行集成测试");
        System.out.println("测试系统间接口");
    }
    
    @Override
    protected boolean needCleanup() {
        return false; // 集成测试不需要清理
    }
}