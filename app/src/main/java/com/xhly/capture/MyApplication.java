package com.xhly.capture;

import android.app.Application;

import com.xhly.capture.util.AppUtils;

import org.xutils.x;

/**
 * Created by 新火燎塬 on 2016/6/22. 以及  on 19:29!^-^
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        AppUtils.initApp(this);
    }
}
