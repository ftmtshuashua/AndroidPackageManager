package com.acap.pkg.manager.base

import android.content.pm.PackageManager
import android.view.View
import androidx.lifecycle.Observer
import com.acap.pkg.manager.center.live.SimpleLiveData
import com.tencent.mmkv.MMKV


/** 读取包中所有信息 */
val LOAD_CONFIG_PACKAGES = PackageManager.GET_ACTIVITIES

/** 读取包的基本信息 */
val LOAD_CONFIG_PACKAGES_BASE = 0


/**
 * <pre>
 * Tip:
 *      数据读取配置
 *
 * Created by A·Cap on 2021/10/9 16:07
 * </pre>
 */
object ReadConfig {

    /**
     * 数据读取更新
     */
    val READ_CHANGE by lazy { SimpleLiveData<Int>() }

    /**
     * 是否读取系统 App
     */
    var READ_SYSTEM_APP: Boolean
        set(value) {
            MMKV.defaultMMKV().putBoolean("READ_SYSTEM_APP", value).commit()
            notifyUpdate()
        }
        get() = MMKV.defaultMMKV().getBoolean("READ_SYSTEM_APP", true)


    fun notifyUpdate() {
        val value = READ_CHANGE.value
        if (value == null) {
            READ_CHANGE.setValue(1)
        } else {
            READ_CHANGE.setValue(value + 1)
        }
    }

}