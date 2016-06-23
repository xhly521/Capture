package com.xhly.capture.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xhly.capture.R;
import com.xhly.capture.adapter.PictureSliderPagerAdapter;
import com.xhly.capture.bean.ImageBean;
import com.xhly.capture.util.AppUtils;
import com.xhly.capture.util.Constants;
import com.xhly.capture.view.FloatingActionButton;
import com.xhly.capture.view.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DragImageActivity extends FragmentActivity {

    private int position;
    private ArrayList<ImageBean> list;
    private ViewPager viewPager;
    private TextView tv_drag_name;
    private TextView tv_drag_pageno;
    private FloatingActionsMenu multiple_actions;
    private FloatingActionButton action_a;
    private FloatingActionButton action_b;
    private PictureSliderPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image);

        initData();
        initView();
        initAdapter();
        initListener();
    }

    private void initListener() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateShow(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                multiple_actions.collapse();
            }
        });

        action_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShare();
            }
        });

        action_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.state == Constants.S_WEB) {
                    AppUtils.downLoadImage(list.get(position).url);
                    multiple_actions.collapse();
                    AppUtils.showToast("下载成功");
                } else {
                    try {
                        setWallpaper(BitmapFactory.decodeFile(list.get(position).url));
                        AppUtils.showToast("壁纸设置成功");
                    } catch (IOException e) {
                        AppUtils.showToast("壁纸设置失败");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateShow(int position) {
        this.position = position;
        tv_drag_name.setText(list.get(position).url);
        tv_drag_pageno.setText(position+1+"/"+list.size());
    }

    private void toShare() {
        File file = new File(list.get(position).url);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/*");//MIME TYPE
            startActivity(intent);
        }
    }

    private void initAdapter() {
        adapter = new PictureSliderPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    private void initView() {
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tv_drag_name = (TextView)findViewById(R.id.tv_drag_name);
        tv_drag_pageno = (TextView)findViewById(R.id.tv_drag_pageno);
        updateShow(position);

        multiple_actions = (FloatingActionsMenu)findViewById(R.id.multiple_actions);

        action_a = (FloatingActionButton)findViewById(R.id.action_a);
        action_b = (FloatingActionButton)findViewById(R.id.action_b);

        if(Constants.state==Constants.S_WEB){
            action_b.setVisibility(View.GONE);
            action_a.setIcon(R.drawable.icon_s_download_press);
        }else{
            action_b.setVisibility(View.VISIBLE);
            action_a.setIcon(R.drawable.garbage_media_cache);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        list = (ArrayList)(intent.getSerializableExtra("imageBeans"));
    }
}
