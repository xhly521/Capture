package com.xhly.capture.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhly.capture.R;
import com.xhly.capture.adapter.base.AdapterBase;
import com.xhly.capture.bean.WebLink;

import java.util.List;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 10:17!^-^
 */
public class WebGridAdapter extends AdapterBase {
    public WebGridAdapter(List list) {
        super(list);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_name;
        ImageView image;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            image = (ImageView) view.findViewById(R.id.image);
        }

        public TextView getTv_name() {
            return tv_name;
        }
        public ImageView getImage() {
            return image;
        }
        public View getView() {
            return view;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.web_item_layout,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder)holder;
        WebLink wl = (WebLink) getList().get(position);
        vh.image.setImageResource(wl.getImage());
        vh.tv_name.setText(wl.getName());

        if(getmOnItemClickLitener()!=null){
            vh.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = vh.getLayoutPosition();
                    getmOnItemClickLitener().onItemClick(vh.getView(), pos);
                }
            });
        }
    }
}
