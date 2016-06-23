package com.xhly.capture.listener;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 22:21!^-^
 */

import org.xutils.common.Callback;

/**
 * xutils中用于下载图片的回调器类
 */
public class MyCacheCallback<T> implements Callback.CacheCallback<T> {

    @Override
    public boolean onCache(T result) {
        return false;
    }

    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}