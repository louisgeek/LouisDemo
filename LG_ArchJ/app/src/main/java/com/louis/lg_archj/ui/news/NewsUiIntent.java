package com.louis.lg_archj.ui.news;

public abstract class NewsUiIntent {
    public static class LoadData extends NewsUiIntent {
    }

    public static class RefreshData extends NewsUiIntent {
    }

    public static class SearchData extends NewsUiIntent {
        public final String query;

        public SearchData(String query) {
            this.query = query;
        }
    }
}
