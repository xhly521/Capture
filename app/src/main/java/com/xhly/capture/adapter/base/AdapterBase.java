package com.xhly.capture.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 10:17!^-^
 */
public abstract class AdapterBase<T> extends RecyclerView.Adapter{
        private List<T> list;
        public AdapterBase(List<T> list) {
            this.list = list;
        }

        public interface OnItemClickLitener
        {
            void onItemClick(View view, int position);
            void onItemLongClick(View view, int position);
        }
        private OnItemClickLitener mOnItemClickLitener;
        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
        {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        public List getList() {
            return list;
        }

    public void setList(List<T> list) {
        this.list = list;
    }

    public OnItemClickLitener getmOnItemClickLitener() {
            return mOnItemClickLitener;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void addData(int position,T t){
            list.add(position, t);
            notifyItemInserted(position);
        };
        public void removeData(int position){
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

