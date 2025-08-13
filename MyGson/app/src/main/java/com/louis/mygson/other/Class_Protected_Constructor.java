package com.louis.mygson.other;

import java.lang.reflect.Type;

public class Class_Protected_Constructor {
    private Type type;

    protected Class_Protected_Constructor() { //protected 同一包中的类和其他包中的子类可访问
    }


    public Type getType() {
        return type;
    }
}
