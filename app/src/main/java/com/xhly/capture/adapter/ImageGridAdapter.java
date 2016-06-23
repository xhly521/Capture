package com.xhly.capture.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xhly.capture.R;
import com.xhly.capture.adapter.base.AdapterBase;
import com.xhly.capture.bean.ImageBean;
import com.xhly.capture.util.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 10:17!^-^
 */
public class ImageGridAdapter extends AdapterBase {
    public ImageGridAdapter(List list) {
        super(list);
    }
    private boolean isEdit = false;
    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView iv_img;
        ImageView iv_select;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iv_img = (ImageView) view.findViewById(R.id.iv_img);
            iv_select = (ImageView) view.findViewById(R.id.iv_select);
        }

        public ImageView getIv_img() {
            return iv_img;
        }

        public ImageView getIv_select() {
            return iv_select;
        }

        public View getView() {
            return view;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.image_item_layout,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder)holder;
        if(isEdit){
            vh.iv_select.setVisibility(View.VISIBLE);
        }else{
            vh.iv_select.setVisibility(View.GONE);
        }

        ImageBean ib = (ImageBean) getList().get(position);
        x.image().bind(vh.iv_img,ib.url, AppUtils.smallImageOptions);

        if(ib.checked){
            vh.iv_select.setImageResource(R.drawable.blue_selected);
        }else{
            vh.iv_select.setImageResource(R.drawable.blue_unselected);
        }

        if(getmOnItemClickLitener()!=null){
            vh.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = vh.getLayoutPosition();
                    getmOnItemClickLitener().onItemClick(vh.getView(), pos);
                }
            });
            vh.getView().setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int pos = vh.getLayoutPosition();
                    getmOnItemClickLitener().onItemLongClick(vh.getView(), pos);
                    return true;
                }
            });
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        this.isEdit = edit;
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean isAllChecked) {
        List list = getList();
        for (int i = 0; i < list.size(); i++) {
            ImageBean ib = (ImageBean) (list.get(i));
            ib.checked = isAllChecked;
        }
        notifyDataSetChanged();
    }
}
