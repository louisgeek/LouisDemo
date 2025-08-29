package com.louis.javadesignpatterns.behavioral.state;

import android.graphics.PointF;

import com.louis.javadesignpatterns.behavioral.strategy.twoline.LineSegment;

import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        OrderContext context = new OrderContext(new PendingState());

        context.processOrder(); //
        context.processOrder(); //
        context.processOrder(); //

        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("file.encoding"));
        System.out.println(Charset.defaultCharset().name());
        System.out.println("0信1息2信息3");
    }
}