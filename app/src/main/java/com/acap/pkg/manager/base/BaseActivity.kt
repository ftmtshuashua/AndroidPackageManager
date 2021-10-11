package com.acap.pkg.manager.base

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 11:46
 * </pre>
 */
open class BaseActivity : AppCompatActivity() {
    private val mForegroundTask by lazy { ForegroundTask(lifecycle) }
    fun getContext(): Context = this

    fun runForeground(action: () -> Unit) {
        mForegroundTask.run(action)
    }

    //启动Activity
    fun startActivity(cls: Class<out BaseActivity>) {
        startActivity(Intent(getContext(), cls))
    }

}


private class ForegroundTask(lif: Lifecycle) : LifecycleEventObserver {
    var mForeground = false
    val mForegroundTask = mutableListOf<() -> Unit>()

    init {
        lif.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                mForeground = true
                run()
            }
            Lifecycle.Event.ON_PAUSE -> {
                mForeground = false
            }
        }
    }

    private fun run() {
        var array: List<() -> Unit>
        synchronized(this) {
            array = mForegroundTask.map { it }
            mForegroundTask.clear()
        }
        array.forEach { it.invoke() }
    }

    fun run(action: () -> Unit) {
        if (mForeground) {
            action.invoke()
        } else {
            synchronized(this) {
                mForegroundTask.add(action)
            }
        }
    }


}
