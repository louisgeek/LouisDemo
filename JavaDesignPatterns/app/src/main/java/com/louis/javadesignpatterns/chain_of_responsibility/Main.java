package com.louis.javadesignpatterns.chain_of_responsibility;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Created by louisgeek on 2024/12/9.
 */
public class Main {
    public static void main(String[] args) {
        //创建处理者
        Processor aProcessor = new AProcessor();//组长
        Processor bProcessor = new BProcessor();//经理
        Processor cProcessor = new CProcessor();//总监

        //设置责任链
        aProcessor.setNextProcessor(bProcessor);
        bProcessor.setNextProcessor(cProcessor);

        //发送请求
        Request request1 = new Request(1, "请假 1 天");
        Request request2 = new Request(2, "请假 2 天");
        Request request3 = new Request(3, "请假 3 天");
        Request request4 = new Request(4, "请假 4 天");
        Request request5 = new Request(5, "请假 5 天");
        Request request6 = new Request(6, "请假 6 天");
        Request request7 = new Request(7, "请假 7 天");
        aProcessor.processRequest(request1);
        aProcessor.processRequest(request2);
        aProcessor.processRequest(request3);
        aProcessor.processRequest(request4);
        aProcessor.processRequest(request5);
        aProcessor.processRequest(request6);
        aProcessor.processRequest(request7);
    }
}
