package com.acap.pkg.manager.center.live

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * <pre>
 * Tip:
 *      线程安全的
 *
 * Created by A·Cap on 2021/10/9 17:59
 * </pre>
 */
class SimpleLiveData<T> : MutableLiveData<T> {

    /** 加载耗时的数据 */
    constructor(io: () -> T) : super() {
        GlobalScope.launch(Dispatchers.Main) { setValue(withContext(Dispatchers.IO) { io.invoke() }) }
    }

    constructor(value: T) : super(value)

    constructor() : super()

    override fun postValue(value: T) {
        setValueSecurity(value)
    }

    override fun setValue(value: T) {
        setValueSecurity(value)
    }

    private fun setValueSecurity(value: T) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            super.setValue(value)
        } else {
            super.postValue(value)
        }
    }

}