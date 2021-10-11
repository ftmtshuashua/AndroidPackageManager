package com.acap.pkg.manager.adapter;

/**
 * 带有数据缓存功能的ViewModel
 *
 * @param <T>
 */
public abstract class MultipleViewDataModel<T> extends MultipleViewModel {
    private T mSaveData;

    public MultipleViewDataModel(T t, int layoutId) {
        super(layoutId);
        mSaveData = t;
    }

    public T getSaveData() {
        return mSaveData;
    }

}
