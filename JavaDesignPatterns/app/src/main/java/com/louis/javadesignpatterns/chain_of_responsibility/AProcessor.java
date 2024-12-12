package com.louis.javadesignpatterns.chain_of_responsibility;

/**
 * Created by louisgeek on 2024/12/9.
 */
public class AProcessor extends Processor {
    @Override
    public void processRequest(Request request) {
        if (1 <= request.code && request.code < 3) {
            System.out.println("组长审批通过，请假 3 天以内，不含 3 天，实际 " + request.code + " 天");
        } else {
            //给下一个处理者设置处理请求，没有则流程结束
            if (nextProcessor != null) {
                nextProcessor.processRequest(request);
            } else {
                System.out.println("组长后面没有下一个处理者，无法处理 " + request.code + " 天");
            }
        }
    }
}
