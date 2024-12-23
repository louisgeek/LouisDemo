package com.louis.javadesignpatterns.behavioral.pipeline;

/**
 * Created by louisgeek on 2024/12/12.
 */
public interface Pipe<I, O> {
    O process(I input);
}
