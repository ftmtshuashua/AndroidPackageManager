package com.acap.pkg.manager.base

import android.app.Application
import com.acap.pkg.manager.BuildConfig
import com.acap.pkg.manager.center.DriverManager
import com.acap.pkg.manager.utils.GlobalExceptionUtils
import com.acap.pkg.manager.utils.ScreenAdapter
import com.acap.toolkit.log.LogUtils
import com.tencent.mmkv.MMKV


/**
 * <pre>
 * Tip:
 *      App start
 *
 * Created by A·Cap on 2021/10/8 15:49
 * </pre>
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalExceptionUtils.init()

        LogUtils.setDebug(BuildConfig.DEBUG)
        MMKV.initialize(this)
        ScreenAdapter.init(this, 420f)

        DriverManager.init(this)
    }
}


