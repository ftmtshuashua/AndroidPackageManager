package com.acap.pkg.manager.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.acap.pkg.manager.center.live.LiveDataOnlyObserve


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
    private val mLiveDataOnlyObserve by lazy { LiveDataOnlyObserve(this) }


    fun <T : ViewBinding> setViewBinding(cls: Class<out T>): T {
        val declaredMethod = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
        val invoke = declaredMethod.invoke(cls, layoutInflater) as T
        setContentView(invoke.root)
        return invoke
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        lifecycle.addObserver(object : LifecycleEventObserver {
//            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//                LogUtils.i("${this.javaClass.name} -> ${event}")
//            }
//
//        })
    }

    fun getContext(): Context = this

    fun runForeground(action: () -> Unit) {
        mForegroundTask.run(action)
    }

    fun getLiveDataOnlyObserve() = mLiveDataOnlyObserve
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
