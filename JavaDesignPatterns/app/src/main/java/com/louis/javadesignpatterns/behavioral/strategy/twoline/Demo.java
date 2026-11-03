package com.louis.javadesignpatterns.behavioral.strategy.twoline;

import java.util.ArrayList;
import java.util.List;

public class Demo {
    // 橡皮擦路径
    static LineSegment eraserPath = new LineSegment(new PointD(0, 0), new PointD(0, 0));
    static List<LineSegment> figureLines = new ArrayList<>(); // 所有笔划

    public static void main2(String[] args) {
        // 检测相交
        for (LineSegment line : figureLines) {
            if (LineSegment.isIntersect(eraserPath, line)) {
                // 处理相交线段（如擦除、标记等）
            }
        }
    }

    public static void main(String[] args) {

        //测试相交
        LineSegment line1 = new LineSegment(new PointD(1, 1), new PointD(3, 3));
        LineSegment line2 = new LineSegment(new PointD(1, 3), new PointD(3, 1));
        boolean intersect1 = LineSegment.isIntersect(line1, line2);
        System.out.println("相交测试1: ---------------------------- " + intersect1); // true

        //测试不相交
        line1 = new LineSegment(new PointD(1, 1), new PointD(2, 2));
        line2 = new LineSegment(new PointD(3, 3), new PointD(4, 4));
        boolean intersect2 = LineSegment.isIntersect(line1, line2);
        System.out.println("相交测试2: ---------------------------- " + intersect2); // false

        //测试共线重叠
        line1 = new LineSegment(new PointD(1, 1), new PointD(3, 3));
        line2 = new LineSegment(new PointD(2, 2), new PointD(4, 4));
        boolean intersect3 = LineSegment.isIntersect(line1, line2);
        System.out.println("相交测试3: ---------------------------- " + intersect3); // true

        //测试端点相交
        line1 = new LineSegment(new PointD(1, 1), new PointD(2, 2));
        line2 = new LineSegment(new PointD(2, 2), new PointD(3, 3));
        boolean intersect4 = LineSegment.isIntersect(line1, line2);
        System.out.println("相交测试4: ---------------------------- " + intersect4); // true

    }

}
