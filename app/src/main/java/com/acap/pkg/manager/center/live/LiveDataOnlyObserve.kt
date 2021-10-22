package com.acap.pkg.manager.center.live

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * <pre>
 * Tip:
 *      LiveData 唯一 观察者
 *
 * Created by A·Cap on 2021/10/21 17:28
 * </pre>
 */
class LiveDataOnlyObserve(val owner: LifecycleOwner) {

    private val mCache_LO by lazy { mutableMapOf<LiveData<*>?, Observer<*>?>() }
    private val mCache_OL by lazy { mutableMapOf<Observer<*>?, LiveData<*>?>() }


    /** LiveData */
    fun <T> observe(liveData: LiveData<T>, observer: Observer<in T>) {
        clear(liveData, observer)

        liveData.observe(owner, observer)
        mCache_OL[observer] = liveData
        mCache_LO[liveData] = observer
    }

    //清理旧的关系
    private fun clear(liveData: LiveData<*>, observer: Observer<*>) {
        mCache_OL[observer]?.let {
            it.removeObserver(observer as Observer<in Any>)
            mCache_OL[observer] = null
        }
        mCache_LO[liveData]?.let {
            liveData.removeObserver(it as Observer<in Any>)
            mCache_LO[liveData] = null
        }
    }

}