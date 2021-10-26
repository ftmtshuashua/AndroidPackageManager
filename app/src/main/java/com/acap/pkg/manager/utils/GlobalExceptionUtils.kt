package com.acap.pkg.manager.utils

import com.acap.toolkit.log.LogUtils


/**
 * <pre>
 * Tip:
 *      全局异常
 *
 * Created by A·Cap on 2021/10/26 15:52
 * </pre>
 */
object GlobalExceptionUtils : Thread.UncaughtExceptionHandler {

    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    fun init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        mDefaultHandler?.let { it.uncaughtException(t, e) }
        LogUtils.e(e)

    }


}