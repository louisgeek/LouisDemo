package com.louis.lg_archj.data.mapper;

import com.louis.lg_archj.data.local.entity.NewsEntity;
import com.louis.lg_archj.data.remote.dto.NewsDto;
import com.louis.lg_archj.domain.model.News;

//可以考虑 MapStruct 实现
public class NewsMapper {
    private NewsMapper() {
    }

    public static News entityToModel(NewsEntity entity) {
        return new News(entity.getId(), entity.getTitle());
    }

    public static News dtoToModel(NewsDto dto) {
        return new News(dto.getId(), dto.getTitle());
    }

    public static NewsEntity modelToEntity(News model) {
        return new NewsEntity(model.getId(), model.getTitle());
    }

    //少见且不推荐，应避免耦合网络与数据库
//    public static NewsEntity dtoToEntity(NewsDto dto) {
//        return new NewsEntity(dto.getId(), dto.getTitle());
//    }
}
