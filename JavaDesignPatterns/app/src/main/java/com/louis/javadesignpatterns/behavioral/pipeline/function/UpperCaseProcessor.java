package com.louis.javadesignpatterns.behavioral.pipeline.function;

import java.util.function.Function;

/**
 * Created by louisgeek on 2024/12/12.
 */
public class UpperCaseProcessor implements Function<String, String> {
    @Override
    public String apply(String input) {
        return input.toUpperCase();
    }
}
