package com.louis.javadesignpatterns.behavioral.template;

// 场景3: 数据迁移
public abstract class DataMigration {
    
    // 模板方法 - 定义迁移流程
    public final void migrate() {
        System.out.println("=== 开始数据迁移 ===");
        
        validateSource();
        extractData();
        transformData();
        validateTarget();
        loadData();
        if (needBackup()) {
            createBackup();
        }
        
        System.out.println("=== 迁移完成 ===\n");
    }
    
    // 具体方法 - 通用步骤
    private void validateSource() {
        System.out.println("验证源数据完整性");
    }
    
    private void validateTarget() {
        System.out.println("验证目标系统连接");
    }
    
    // 抽象方法 - 子类实现
    protected abstract void extractData();
    protected abstract void transformData();
    protected abstract void loadData();
    
    // 钩子方法
    protected boolean needBackup() {
        return true;
    }
    
    protected void createBackup() {
        System.out.println("创建数据备份");
    }
}