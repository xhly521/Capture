package com.xhly.capture.dao;


import com.xhly.capture.bean.HistoryUrl;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 新火燎塬 on 2016/6/21. 以及  on 19:25!^-^
 */
public class HistoryUrlDao {
    private DbManager.DaoConfig config;

    public HistoryUrlDao() {
        config = new DbManager.DaoConfig()
        .setDbName("image_load.db")
        .setDbVersion(1);
    }
    /**
     * 返回包含所有记录的对象集合
     *
     * @return
     */
    public List<HistoryUrl> getAll(){
        List<HistoryUrl> list = null;
        DbManager db = null;
        try {
            db = x.getDb(config);
            list = db.findAll(HistoryUrl.class);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(list==null){
            list = new ArrayList<>();
        }

        return list;
    }
    /**
     * 保存一条记录
     *
     * @param historyUrl
     */
    public void save(HistoryUrl historyUrl) {
        DbManager db = null;
        try {
            db = x.getDb(config);
            //将数据插入表, 同时将生成的id设置到对象中
            db.saveBindingId(historyUrl);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
