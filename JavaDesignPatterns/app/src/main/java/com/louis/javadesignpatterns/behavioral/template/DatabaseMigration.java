package com.louis.javadesignpatterns.behavioral.template;

public class DatabaseMigration extends DataMigration {
    @Override
    protected void extractData() {
        System.out.println("从MySQL数据库提取数据");
    }
    
    @Override
    protected void transformData() {
        System.out.println("转换数据格式和结构");
    }
    
    @Override
    protected void loadData() {
        System.out.println("加载数据到PostgreSQL");
    }
}