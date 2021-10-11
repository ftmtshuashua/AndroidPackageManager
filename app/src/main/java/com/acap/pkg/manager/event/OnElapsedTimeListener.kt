package com.acap.pkg.manager.event

import com.acap.ec.Event
import com.acap.ec.listener.OnEventListener
import com.acap.toolkit.log.LogUtils


/**
 * <pre>
 * Tip:
 *      耗时检测
 *
 * Created by A·Cap on 2021/10/8 15:46
 * </pre>
 */
class OnElapsedTimeListener<P, R>(val tag: String = "Event") : OnEventListener<P, R> {
    private var start = 0L
    private var end = 0L
    override fun onStart(event: Event<P, R>?, params: P) {
        start = System.currentTimeMillis()
    }

    override fun onError(e: Throwable?) {
    }

    override fun onNext(result: R) {
    }

    override fun onComplete() {
        end = System.currentTimeMillis()

        LogUtils.fi("{0} -> 耗时:{1,number,0}ms", tag, end - start)
    }
}