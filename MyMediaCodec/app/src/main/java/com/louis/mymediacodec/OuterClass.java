package com.louis.mymediacodec;

import android.os.Build;

import androidx.annotation.RequiresApi;


/**
 * Created by louisgeek on 2025/2/12.
 */
public class OuterClass {

    class InnerClass {
    }

    static class StaticInnerClass {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        //基本数据类型
        System.out.println(int.class.getName()); //int
        System.out.println(int.class.getTypeName()); //int
        System.out.println(int.class.getCanonicalName()); //int
        System.out.println(int.class.getSimpleName()); //int
        System.out.println("--------------------------------");
        //String
        System.out.println(String.class.getName()); //java.lang.String
        System.out.println(String.class.getTypeName()); //java.lang.String
        System.out.println(String.class.getCanonicalName()); //java.lang.String
        System.out.println(String.class.getSimpleName()); //String
        System.out.println("--------------------------------");
        //数组
        System.out.println(int[].class.getName()); //[I
        System.out.println(int[].class.getTypeName()); //int[]
        System.out.println(int[].class.getCanonicalName()); //int[]
        System.out.println(int[].class.getSimpleName()); //int[]
        //String 数组
        System.out.println(String[].class.getName()); //[Ljava.lang.String;
        System.out.println(String[].class.getTypeName()); //java.lang.String[]
        System.out.println(String[].class.getCanonicalName()); //java.lang.String[]
        System.out.println(String[].class.getSimpleName()); //String[]
        System.out.println("--------------------------------");
        //成员内部类
        System.out.println(InnerClass.class.getName()); //com.louis.mymediacodec.OuterClass$InnerClass
        System.out.println(InnerClass.class.getTypeName()); //com.louis.mymediacodec.OuterClass$InnerClass
        System.out.println(InnerClass.class.getCanonicalName()); //com.louis.mymediacodec.OuterClass.InnerClass
        System.out.println(InnerClass.class.getSimpleName()); //InnerClass
        System.out.println("--------------------------------");
        //匿名内部类
        System.out.println(new Object() {}.getClass().getName()); //com.louis.mymediacodec.OuterClass$1
        System.out.println(new Object() {}.getClass().getTypeName()); //com.louis.mymediacodec.OuterClass$2
        System.out.println(new Object() {}.getClass().getCanonicalName()); //null
        System.out.println(new Object() {}.getClass().getSimpleName()); //""
        System.out.println("--------------------------------");
        //普通类
        System.out.println(OuterClass.class.getName()); //com.louis.mymediacodec.OuterClass
        System.out.println(OuterClass.class.getTypeName()); //com.louis.mymediacodec.OuterClass
        System.out.println(OuterClass.class.getCanonicalName()); //com.louis.mymediacodec.OuterClass
        System.out.println(OuterClass.class.getSimpleName()); //OuterClass
        //
        System.out.println(StaticInnerClass.class.getName()); //com.louis.mymediacodec.OuterClass
        System.out.println(StaticInnerClass.class.getTypeName()); //com.louis.mymediacodec.OuterClass
        System.out.println(StaticInnerClass.class.getCanonicalName()); //com.louis.mymediacodec.OuterClass
        System.out.println(StaticInnerClass.class.getSimpleName()); //OuterClass

    }

}
