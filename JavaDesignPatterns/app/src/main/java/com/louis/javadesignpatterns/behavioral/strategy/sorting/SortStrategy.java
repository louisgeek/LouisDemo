package com.louis.javadesignpatterns.behavioral.strategy.sorting;

public interface SortStrategy {
    void sort(int[] array);
    String getAlgorithmName();
}