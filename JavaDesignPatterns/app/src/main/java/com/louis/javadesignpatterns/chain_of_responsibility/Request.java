package com.louis.javadesignpatterns.chain_of_responsibility;

/**
 * Created by louisgeek on 2024/12/9.
 */
public class Request {
    protected int code;
    protected String name;

    public Request(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
