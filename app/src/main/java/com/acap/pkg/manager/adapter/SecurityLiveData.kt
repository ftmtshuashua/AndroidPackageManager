package com.acap.pkg.manager.adapter

import android.os.Looper
import androidx.lifecycle.MutableLiveData


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/9 17:59
 * </pre>
 */
class SecurityLiveData<T> : MutableLiveData<T>() {

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