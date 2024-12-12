package com.louis.javadesignpatterns.chain_of_responsibility;

/**
 * Created by louisgeek on 2024/12/9.
 */
public abstract class Processor {
    protected Processor nextProcessor;

    public void setNextProcessor(Processor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public abstract void processRequest(Request request);
}
