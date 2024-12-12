package com.louis.javadesignpatterns.behavioral.chain_of_responsibility;

/**
 * Created by louisgeek on 2024/12/9.
 */
public class BProcessor extends Processor {
    @Override
    public void processRequest(Request request) {
        if (3 <= request.code && request.code < 5) {
            System.out.println("经理审批通过，请假 5 天以内，不含 5 天，实际 " + request.code + " 天");
        } else {
            //给下一个处理者设置处理请求，没有则流程结束
            if (nextProcessor != null) {
                nextProcessor.processRequest(request);
            } else {
                System.out.println("经理后面没有下一个处理者，无法处理 " + request.code + " 天");
            }
        }
    }
}
