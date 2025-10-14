package com.louis.lg_archj.data.remote.dto;

import java.util.List;

public class WanAndroidResponse {
    private int errorCode;
    private String errorMsg;
    private ArticleListData data;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ArticleListData getData() {
        return data;
    }

    public static class ArticleListData {
        private List<WanAndroidData> datas;

        public List<WanAndroidData> getDatas() {
            return datas;
        }
    }

    public static class WanAndroidData {
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}