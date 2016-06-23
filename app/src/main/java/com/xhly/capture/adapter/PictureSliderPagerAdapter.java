package com.xhly.capture.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xhly.capture.bean.ImageBean;
import com.xhly.capture.fragment.ImgDetailFragment;

import java.util.List;

/**
 * Created by 新火燎塬 on 2016/6/22. 以及  on 15:31!^-^
 */
public class PictureSliderPagerAdapter extends FragmentStatePagerAdapter {
    private List<ImageBean> list;

    public PictureSliderPagerAdapter(FragmentManager fm, List list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return ImgDetailFragment.newInstance(list.get(position).url);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
