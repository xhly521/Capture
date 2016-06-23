package com.xhly.capture.util;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 14:39!^-^
 */
public class Constants {
    public static final int S_WEB = 0;// 看网络图片
    public static final int S_SDCARD = 1;// 看下载好的本地图片
    public static int state = S_WEB;// 当前状态
    public static final String SAVE_DIR = AppUtils.getSDCardPath() + "/mypics";
}
