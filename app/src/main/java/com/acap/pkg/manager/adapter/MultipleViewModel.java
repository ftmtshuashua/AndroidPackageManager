package com.acap.pkg.manager.adapter;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import support.lfp.adapter.BaseViewHolder;

/**
 * 复合布局的ViewHolder
 *
 * @Author :Hope_LFB
 * @DATE :2019/10/25 11:11
 */
public abstract class MultipleViewModel {

    RecyclerView.Adapter mAdapter;

    public void onAttach(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    public RecyclerView.Adapter getAdapter(){
        return mAdapter;
    }
    /**
     * 给定Model在View中的的布局文件
     */
    int layoutId;

    public MultipleViewModel(int layoutId) {
        this.layoutId = layoutId;
    }

    /**
     * 获得布局文件ID
     */
    public int getLayoutId() {
        return layoutId;
    }

    /**
     * Adapter回调更新,将Model中的数据更新到holder中
     *
     * @param holder
     */
    public abstract void onUpdate(BaseViewHolder holder);

    /**
     * 当Model对应的UI被点击的时候，响应点击事件到Model中
     */
    public void onClick(Context context) {

    }

}
