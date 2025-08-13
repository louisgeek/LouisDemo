package com.louis.mygson;

public class BaseBean<T> {
    int code;
    T data;

    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
