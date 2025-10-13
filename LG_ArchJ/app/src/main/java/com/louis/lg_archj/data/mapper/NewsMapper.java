package com.louis.lg_archj.data.mapper;

import com.louis.lg_archj.data.local.entity.NewsEntity;
import com.louis.lg_archj.data.remote.dto.NewsDto;
import com.louis.lg_archj.domain.model.News;

public class NewsMapper {
    private NewsMapper() {
    }

    public static NewsEntity modelToEntity(News model) {
        return new NewsEntity();
    }

    public static News entityToModel(NewsEntity entity) {
        return new News();
    }

    public static News dtoToModel(NewsDto dto) {
        return new News();
    }

    public static NewsEntity dtoToEntity(NewsDto dto) {
        return new NewsEntity();
    }
}
