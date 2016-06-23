package com.xhly.capture.bean;

import java.io.Serializable;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 14:17!^-^
 */
public class ImageBean implements Serializable {

    public String url;
    public boolean checked;//标识对应的界面图片是否需要选中

    public ImageBean(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageBean imageBean = (ImageBean) o;

        return url.equals(imageBean.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}