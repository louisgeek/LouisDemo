package com.louis.javadesignpatterns.behavioral.chain_of_responsibility;

/**
 * Created by louisgeek on 2024/12/9.
 */
public class CProcessor extends Processor {
    @Override
    public void processRequest(Request request) {
        if (5 <= request.code && request.code < 7) {
            System.out.println("总监审批通过，请假 7 天以内，不含 7 天，实际 " + request.code + " 天");
        } else {
            //给下一个处理者设置处理请求，没有则流程结束
            if (nextProcessor != null) {
                nextProcessor.processRequest(request);
            } else {
                System.out.println("总监后面没有下一个处理者，无法处理 " + request.code + " 天");
            }
        }
    }
}
