package com.xhly.capture.listener;

/**
 * Created by 新火燎塬 on 2016/6/22. 以及  on 20:40!^-^
 */
public abstract class OnOptionCallback {
    public void showProgress(){};
    public void dismissProgress(){};
    public void afterOption(String html){};
}
