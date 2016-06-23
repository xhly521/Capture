package com.xhly.capture.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xhly.capture.R;
import com.xhly.capture.util.AppUtils;

import org.xutils.x;

/**
 * Created by 新火燎塬 on 2016/6/22. 以及  on 15:34!^-^
 */
public class ImgDetailFragment extends Fragment {
    private static final String KEY = "image";
    private ImageView imageView;
    private String imagePath;//图片资源url(远程/本地)

    /**
     * 获取ImgDetailFragment 实例
     * @param imgResourceId 图片资源ID
     * @return
     */
    public static ImgDetailFragment newInstance(String imgResourceId) {
        //创建当前类对象
        ImgDetailFragment fragment = new ImgDetailFragment();
        // 保存参数
        Bundle arg = new Bundle();
        arg.putString(KEY, imgResourceId);
        fragment.setArguments(arg);
        //返回
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从fragment的参数中取出保存的参数(图片标识), 并保存为成员变量
        if (getArguments() != null) {
            imagePath = getArguments().getString(KEY);// 取参
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_img_detail, container, false);
        imageView = (ImageView) view.findViewById(R.id.iv_big_img);
        //加载图片显示(可能是远程的, 也可能是远程的)
        x.image().bind(imageView, imagePath, AppUtils.bigImageOptions);
        return view;
    }
}
