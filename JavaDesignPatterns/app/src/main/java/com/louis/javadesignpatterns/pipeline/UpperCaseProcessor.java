package com.louis.javadesignpatterns.pipeline;

/**
 * Created by louisgeek on 2024/12/12.
 */
public class UpperCaseProcessor implements Pipe<String, String> {
    @Override
    public String process(String input) {
        return input.toUpperCase();
    }
}
