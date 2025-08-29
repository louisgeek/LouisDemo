package com.louis.javadesignpatterns.behavioral.template;

public class FileMigration extends DataMigration {
    @Override
    protected void extractData() {
        System.out.println("从CSV文件读取数据");
    }
    
    @Override
    protected void transformData() {
        System.out.println("清洗和格式化数据");
    }
    
    @Override
    protected void loadData() {
        System.out.println("写入到JSON文件");
    }
    
    @Override
    protected boolean needBackup() {
        return false; // 文件迁移不需要备份
    }
}