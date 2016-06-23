package com.xhly.capture.bean;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 10:29!^-^
 */
public class WebLink {
    private int image;
    private String name;
    private String url;

    public WebLink() {
    }

    public WebLink(String name, String url, int image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebLink{" +
                "image=" + image +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
