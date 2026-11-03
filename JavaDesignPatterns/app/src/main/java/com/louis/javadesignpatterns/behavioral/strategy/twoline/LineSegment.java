package com.louis.javadesignpatterns.behavioral.strategy.twoline;

public class LineSegment {
    private final PointD pStart;//一条线段的起点
    private final PointD pEnd; //一条线段的终点

    public LineSegment(PointD pStart, PointD pEnd) {
        this.pStart = pStart;
        this.pEnd = pEnd;
    }


    public static boolean isIntersect(LineSegment line1, LineSegment line2) {
        //1 快速排斥实验（预筛选）
        if (!quickRejection(line1, line2)) {
            //矩形不相交，直接返回线段不相交
            System.out.println("矩形不相交，直接返回线段不相交");
            return false;
        }
        //矩形相交（线段可能相交，需要进一步判断）

        //计算跨立实验所需的叉积
        double cross1 = crossProduct(line1.pStart, line1.pEnd, line2.pStart);
        double cross2 = crossProduct(line1.pStart, line1.pEnd, line2.pEnd);
        double cross3 = crossProduct(line2.pStart, line2.pEnd, line1.pStart);
        double cross4 = crossProduct(line2.pStart, line2.pEnd, line1.pEnd);
        //2 跨立实验（使用叉积判断线段是否互相跨立）
        if ((cross1 * cross2 < 0) && (cross3 * cross4 < 0)) {
            //如果线段 1 的两端点分别在线段 2 的两侧，且线段 2 的两端点分别在线段 1 的两侧，则相交
            //返回线段相交
            System.out.println("跨立实验返回线段相交");
            return true;
        }

        //3（可选）处理共线情况（检查一条线段的端点是否在另一条线段上）
        return (cross1 == 0 && isPointOnLineSegment(line2.pStart, line1)) ||
                (cross2 == 0 && isPointOnLineSegment(line2.pEnd, line1)) ||
                (cross3 == 0 && isPointOnLineSegment(line1.pStart, line2)) ||
                (cross4 == 0 && isPointOnLineSegment(line1.pEnd, line2));

    }

    /**
     * 快速排斥实验（可以快速排除明显不相交的情况）
     * 判断两条线段的最小外接矩形是否相交
     *
     * @return true：矩形相交（线段可能相交，需要进一步判断）；false：矩形不相交（线段肯定不相交）
     */
    private static boolean quickRejection(LineSegment line1, LineSegment line2) {
        //线段 1 的矩形边界
        double line1MinX = Math.min(line1.pStart.x, line1.pEnd.x);
        double line1MaxX = Math.max(line1.pStart.x, line1.pEnd.x);
        double line1MinY = Math.min(line1.pStart.y, line1.pEnd.y);
        double line1MaxY = Math.max(line1.pStart.y, line1.pEnd.y);

        //线段 2 的矩形边界
        double line2MinX = Math.min(line2.pStart.x, line2.pEnd.x);
        double line2MaxX = Math.max(line2.pStart.x, line2.pEnd.x);
        double line2MinY = Math.min(line2.pStart.y, line2.pEnd.y);
        double line2MaxY = Math.max(line2.pStart.y, line2.pEnd.y);

        if (line1MaxX < line2MinX) {
            System.out.println("线段 1 完全在线段 2 左侧");
        }
        if (line2MaxX < line1MinX) {
            System.out.println("线段 2 完全在线段 1 左侧");
        }
        if (line1MaxY < line2MinY) {
            System.out.println("线段 1 完全在线段 2 下方");
        }
        if (line2MaxY < line1MinY) {
            System.out.println("线段 2 完全在线段 1 下方");
        }
        return !(line1MaxX < line2MinX || line2MaxX < line1MinX ||
                line1MaxY < line2MinY || line2MaxY < line1MinY);
    }

    /**
     * 使用向量叉积检查线段是否“互相跨立”， (p1->p2) × (p1->p3)  注意“×”是叉乘的意思
     *
     * @return 表示点在直线的哪一侧，正值：p3 在直线 p1→p2 的左侧；负值：p3 在直线 p1→p2 的右侧；零值：p3 在直线 p1→p2 上
     */
    private static double crossProduct(PointD p1, PointD p2, PointD p3) {
        return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
    }

    // 判断点 p 是否在线段 line 上
    private static boolean isPointOnLineSegment(PointD p, LineSegment line) {
        return p.x >= Math.min(line.pStart.x, line.pEnd.x) &&
                p.x <= Math.max(line.pStart.x, line.pEnd.x) &&
                p.y >= Math.min(line.pStart.y, line.pEnd.y) &&
                p.y <= Math.max(line.pStart.y, line.pEnd.y);
    }
}