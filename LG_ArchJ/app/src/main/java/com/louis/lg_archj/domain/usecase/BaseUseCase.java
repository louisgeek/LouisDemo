package com.louis.lg_archj.domain.usecase;


import com.louis.lg_archj.ResourceCloseable;

public interface BaseUseCase<P, R>  extends ResourceCloseable {
    //R execute(P params);
    R invoke(P params);
}