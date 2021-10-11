package com.acap.pkg.manager.event

import com.acap.ec.BaseEvent
import com.acap.toolkit.thread.ThreadHelper


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 15:55
 * </pre>
 */
class ThreadIo<T> : BaseEvent<T, T>() {
    override fun onCall(params: T) {
        ThreadHelper.io { next(params) }
    }
}

class ThreadMain<T> : BaseEvent<T, T>() {
    override fun onCall(params: T) {
        ThreadHelper.main { next(params) }
    }
}