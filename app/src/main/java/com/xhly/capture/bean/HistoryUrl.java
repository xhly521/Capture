package com.xhly.capture.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by 新火燎塬 on 2016/6/21. 以及  on 19:25!^-^
 */
@Table(name = "history_url")
public class HistoryUrl {
    @Column(name = "_id", isId = true)
    private int id;

    @Column(name = "url")
    private String url;
    //保留空参构造器
    public HistoryUrl() {
    }

    public HistoryUrl(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryUrl that = (HistoryUrl) o;

        return url.equals(that.url);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "HistoryUrl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
