package com.louis.javadesignpatterns.behavioral.template;

public class TemplateDemo {
    public static void main(String[] args) {
        System.out.println("=== 模板方法模式应用场景演示 ===\n");
        
        // 场景1: 烹饪流程
        System.out.println("【场景1: 烹饪流程】");
        
        CookingProcess friedRice = new FriedRice();
        friedRice.cook();
        
        CookingProcess grilledMeat = new GrilledMeat();
        grilledMeat.cook();
        
        // 场景2: 测试框架
        System.out.println("【场景2: 测试框架】");
        
        TestFramework unitTest = new UnitTest();
        unitTest.runTest();
        
        TestFramework integrationTest = new IntegrationTest();
        integrationTest.runTest();
        
        // 场景3: 数据迁移
        System.out.println("【场景3: 数据迁移】");
        
        DataMigration dbMigration = new DatabaseMigration();
        dbMigration.migrate();
        
        DataMigration fileMigration = new FileMigration();
        fileMigration.migrate();
        
        System.out.println("=== 模板方法模式应用场景总结 ===");
        System.out.println("1. 烹饪流程 - 固定步骤，不同菜品实现不同");
        System.out.println("2. 测试框架 - 统一测试流程，不同测试类型");
        System.out.println("3. 数据迁移 - 标准ETL流程，不同数据源");
        System.out.println("4. 其他场景: 审批流程、报表生成、算法框架等");
    }
}