package com.louis.javadesignpatterns.behavioral.strategy.sorting;

import java.util.Arrays;

public class SortContext {
    private SortStrategy strategy;
    
    public void setSortStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void performSort(int[] array) {
        if (strategy == null) {
            System.out.println("未设置排序策略");
            return;
        }
        
        int[] originalArray = Arrays.copyOf(array, array.length);
        
        System.out.println("排序算法: " + strategy.getAlgorithmName());
        System.out.println("原数组: " + Arrays.toString(originalArray));
        
        long startTime = System.nanoTime();
        strategy.sort(array);
        long endTime = System.nanoTime();
        
        System.out.println("排序后: " + Arrays.toString(array));
        System.out.println("耗时: " + (endTime - startTime) / 1000.0 + " 微秒");
        System.out.println("-------------------");
    }
    
    // 智能选择排序算法
    public void autoSelectStrategy(int arraySize) {
        if (arraySize <= 10) {
            setSortStrategy(new InsertionSortStrategy());
            System.out.println("自动选择: 小数组使用插入排序");
        } else if (arraySize <= 50) {
            setSortStrategy(new BubbleSortStrategy());
            System.out.println("自动选择: 中等数组使用冒泡排序");
        } else if (arraySize <= 1000) {
            setSortStrategy(new QuickSortStrategy());
            System.out.println("自动选择: 大数组使用快速排序");
        } else {
            setSortStrategy(new MergeSortStrategy());
            System.out.println("自动选择: 超大数组使用归并排序");
        }
    }
}