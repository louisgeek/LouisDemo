package com.louis.myjni;

/**
 * Created by louisgeek on 2024/12/12.
 */
public interface Callback {
    void onSuccess(int data);

    void onFailure(String msg);
}
