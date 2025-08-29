package com.louis.javadesignpatterns.behavioral.strategy.sorting;

import java.util.Arrays;
import java.util.Random;

public class SortingDemo {
    public static void main(String[] args) {
        SortContext context = new SortContext();
        
        System.out.println("=== 排序算法策略模式演示 ===\n");
        
        // 测试小数组
        int[] smallArray = {64, 34, 25, 12, 22, 11, 90};
        
        // 冒泡排序
        context.setSortStrategy(new BubbleSortStrategy());
        context.performSort(Arrays.copyOf(smallArray, smallArray.length));
        
        // 快速排序
        context.setSortStrategy(new QuickSortStrategy());
        context.performSort(Arrays.copyOf(smallArray, smallArray.length));
        
        // 归并排序
        context.setSortStrategy(new MergeSortStrategy());
        context.performSort(Arrays.copyOf(smallArray, smallArray.length));
        
        // 插入排序
        context.setSortStrategy(new InsertionSortStrategy());
        context.performSort(Arrays.copyOf(smallArray, smallArray.length));
        
        System.out.println("=== 性能对比测试 ===\n");
        
        // 生成不同大小的随机数组进行性能测试
        int[] sizes = {10, 100, 1000};
        
        for (int size : sizes) {
            System.out.println("数组大小: " + size);
            int[] testArray = generateRandomArray(size);
            
            // 测试快速排序
            context.setSortStrategy(new QuickSortStrategy());
            context.performSort(Arrays.copyOf(testArray, testArray.length));
            
            // 测试归并排序
            context.setSortStrategy(new MergeSortStrategy());
            context.performSort(Arrays.copyOf(testArray, testArray.length));
            
            System.out.println();
        }
        
        System.out.println("=== 智能算法选择 ===\n");
        
        // 智能选择排序算法
        int[] testSizes = {5, 25, 500, 2000};
        
        for (int size : testSizes) {
            int[] array = generateRandomArray(size);
            System.out.println("数组大小: " + size);
            context.autoSelectStrategy(size);
            context.performSort(array);
        }
        
        System.out.println("=== 特殊情况测试 ===\n");
        
        // 已排序数组
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("已排序数组测试:");
        context.setSortStrategy(new QuickSortStrategy());
        context.performSort(Arrays.copyOf(sortedArray, sortedArray.length));
        
        // 逆序数组
        int[] reverseArray = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        System.out.println("逆序数组测试:");
        context.setSortStrategy(new MergeSortStrategy());
        context.performSort(Arrays.copyOf(reverseArray, reverseArray.length));
    }
    
    private static int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }
}