package com.acap.pkg.manager.event.utils

import com.acap.ec.Event
import com.acap.ec.listener.OnEventListener
import com.acap.toolkit.log.LogUtils


/**
 * <pre>
 * Tip:
 *      事件耗时监控
 *
 * Created by A·Cap on 2021/10/21 16:25
 * </pre>
 */
class OnEventTimeMonitor<P, R> : OnEventListener<P, R> {
    private var mStartTime = 0L
    private var mEntTime = 0L
    private lateinit var mEvent: Event<P, R>

    override fun onStart(event: Event<P, R>, params: P) {
        mEvent = event
        mStartTime = System.currentTimeMillis()
    }

    override fun onError(e: Throwable?) {
    }

    override fun onNext(result: R) {
    }

    override fun onComplete() {
        mEntTime = System.currentTimeMillis()
        LogUtils.i("Event time monitor: ${mEvent.javaClass.name} cost ${mEntTime - mStartTime}ms")
    }
}