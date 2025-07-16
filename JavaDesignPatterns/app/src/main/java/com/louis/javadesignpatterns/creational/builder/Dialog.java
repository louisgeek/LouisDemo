package com.louis.javadesignpatterns.creational.builder;

import android.content.Context;

//产品类
class Dialog {

    private String title;
    private String message;

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    //私有化构造函数，只能通过 Builder 创建
    private Dialog(Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
    }

    //静态内部建造者类
    public static class Builder {
        private Context context;
        private String title;
        private String message;

        public Builder(Context context) {
            //必要参数通过构造函数传入
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int titleResId) {
            this.title = context.getString(titleResId);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int messageResId) {
            this.message = context.getString(messageResId);
            return this;
        }

        //最终构建
        public Dialog build() {
            return new Dialog(this);
        }
    }

}