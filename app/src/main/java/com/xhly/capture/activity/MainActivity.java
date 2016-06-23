package com.xhly.capture.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.xhly.capture.R;
import com.xhly.capture.adapter.WebGridAdapter;
import com.xhly.capture.adapter.base.AdapterBase;
import com.xhly.capture.bean.WebLink;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private RecyclerView rv;
    private WebGridAdapter adapter;
    private List<WebLink> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initAdapter();
        initListener();
    }

    public void initData(){
        list = new ArrayList<>();

        list.add(new WebLink("图片天堂", "pic.yesky.com/", R.drawable.i1));
        list.add(new WebLink("硅谷教育", "www.atguigu.com/", R.drawable.i2));
        list.add(new WebLink("新闻图库", "www.cnsphoto.com/", R.drawable.i3));

        list.add(new WebLink("MOKO美空","www.moko.cc/", R.drawable.i4));
        list.add(new WebLink("114啦", "www.4493.com/", R.drawable.i5));
        list.add(new WebLink("动漫之家", "www.27270.com/ent/meinvtupian/", R.drawable.i6));

        list.add(new WebLink("7k7k", "www.jintang.cn/article-13557-1.html" , R.drawable.i7));
        list.add(new WebLink("嘻嘻哈哈", "www.xxhh.com/", R.drawable.i8));
        list.add(new WebLink("有意思吧", "192.168.1.105:8080/av/", R.drawable.i9));
    }

    public void initView(){
        rv = (RecyclerView)findViewById(R.id.rv);

    }

    public void initAdapter(){
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        //rv.setLayoutManager(new GridLayoutManager(this, 3));
        //rv.addItemDecoration(new DividerGridItemDecoration(this));
        adapter = new WebGridAdapter(list);
        rv.setAdapter(adapter);
    }

    public void initListener(){
        adapter.setOnItemClickLitener(new AdapterBase.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,WebPicturesActivity.class);
                intent.putExtra("url",list.get(position).getUrl());
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    private long lastTime;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()-lastTime>2000){
                Toast.makeText(MainActivity.this, "再点击一次退出!", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
                return true;
            }else{
                //清除缓存(内存)
                x.image().clearMemCache();
                //清除缓存(文件)
                x.image().clearCacheFiles();
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}