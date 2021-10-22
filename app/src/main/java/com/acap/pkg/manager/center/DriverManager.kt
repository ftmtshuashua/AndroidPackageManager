package com.acap.pkg.manager.center

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LifecycleOwner
import com.acap.ec.listener.OnEventCompleteListener
import com.acap.ec.listener.OnEventNextListener
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.replace
import com.acap.pkg.manager.center.live.SimpleLiveData
import com.acap.pkg.manager.event.EventGetPackages
import com.acap.pkg.manager.event.utils.OnEventTimeMonitor
import com.acap.toolkit.log.LogUtils
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *      数据驱动,数据配置
 *
 * Created by A·Cap on 2021/10/21 16:01
 * </pre>
 */
object DriverManager {

    // 全部的活动记录
    private val AllActivityRecord by lazy { SimpleLiveData<MutableList<ActivityRecord>>() }
    private val AllActivityRecordChange by lazy { SimpleLiveData<ActivityRecordChange>() }     // 记录ActivityRecord的上一次改变

    // 标星的活动记录
    val StarActivityRecord by lazy { SimpleLiveData<MutableList<ActivityRecord>>() }
    val StarActivityRecordChange by lazy { SimpleLiveData<ActivityRecordChange>() }     // 记录ActivityRecord的上一次改变


    private lateinit var mRecordChange: RecordChange

    /** 安装卸载注册 */
    fun init(context: Context) {
        EventGetPackages<Any>(true)
            .listener(OnEventNextListener {
                val list = it as MutableList<ActivityRecord>
                AllActivityRecord.setValue(list)
                StarActivityRecord.setValue(list)
            })
            .listener(OnEventTimeMonitor())
            .listener(OnEventCompleteListener { registerReceiver(context) })
            .start()
    }

    // 安装卸载广播监听
    private fun registerReceiver(context: Context) {
        mRecordChange = RecordChange(context)

        mRecordChange.onInstall { packageName ->
            val activityRecord = ActivityRecord(packageName)
            if (activityRecord.exists()) {
                val change = ActivityRecordInstall(0, activityRecord)
                AllActivityRecord.value?.apply { change.executor(this) }
                AllActivityRecordChange.setValue(change)

            }
        }
        mRecordChange.onReplaced { packageName ->
            val activityRecord = ActivityRecord(packageName)
            if (activityRecord.exists()) {
                AllActivityRecord.value?.apply {
                    val change = ActivityRecordReplaced(indexOf(find { it.packageName == packageName }), activityRecord)
                    change.executor(this)
                    AllActivityRecordChange.setValue(change)
                }
            }
        }
        mRecordChange.onUninstall { packageName ->
            LogUtils.e("处理前：${AllActivityRecord.value?.size}")
            AllActivityRecord.value?.apply {
                val change = ActivityRecordUninstall(indexOf(find { it.packageName == packageName }), packageName)
                change.executor(this)
                AllActivityRecordChange.setValue(change)
            }
            LogUtils.e("处理后：${AllActivityRecord.value?.size}")
        }
    }

    /** 获得全记录数据 */
    fun getAllActivityRecordObserve(owner: LifecycleOwner): ActivityRecordObserve = ActivityRecordObserve(owner, AllActivityRecord, AllActivityRecordChange)

    /** 获得标星记录数据 */
    fun getStarActivityRecordObserve(owner: LifecycleOwner): ActivityRecordObserve = ActivityRecordObserve(owner, StarActivityRecord, StarActivityRecordChange)
}

// 安装卸载监听
private class RecordChange private constructor() : BroadcastReceiver() {

    companion object {
        @JvmStatic
        val WHAT_INSTALL = 1
        val WHAT_UNINSTALL = 2
        val WHAT_REPLACED = 3
    }

    constructor(context: Context) : this() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED")
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")
        intentFilter.addDataScheme("package")
        context.registerReceiver(this, intentFilter)
    }

    var onInstall: ((String) -> Unit)? = null
    var onUninstall: ((String) -> Unit)? = null
    var onReplaced: ((String) -> Unit)? = null

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val packageName = msg.obj as String
            when (msg.what) {
                WHAT_INSTALL -> {
                    LogUtils.i("安装 <- $packageName")
                    onInstall?.apply { this(packageName) }
                }
                WHAT_UNINSTALL -> {
                    LogUtils.i("卸载 <- $packageName")
                    onUninstall?.apply { this(packageName) }
                }
                WHAT_REPLACED -> {
                    LogUtils.i("覆盖安装 <- $packageName")
                    onReplaced?.apply { this(packageName) }
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.dataString?.substring(8) ?: return
        when (intent.action) {
            "android.intent.action.PACKAGE_ADDED" -> {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, WHAT_INSTALL, packageName), 100)
            }
            "android.intent.action.PACKAGE_REPLACED" -> {
                mHandler.removeMessages(WHAT_INSTALL)
                mHandler.removeMessages(WHAT_UNINSTALL)
                mHandler.sendMessageDelayed(Message.obtain(mHandler, WHAT_REPLACED, packageName), 100)
            }
            "android.intent.action.PACKAGE_REMOVED" -> {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, WHAT_UNINSTALL, packageName), 100)
            }
        }
    }

    fun onInstall(call: ((String) -> Unit)): RecordChange {
        onInstall = call
        return this
    }

    fun onUninstall(call: ((String) -> Unit)): RecordChange {
        onUninstall = call
        return this
    }

    fun onReplaced(call: ((String) -> Unit)): RecordChange {
        onReplaced = call
        return this
    }

}

// 数据变化监听
class ActivityRecordObserve(
    val owner: LifecycleOwner,
    val record: SimpleLiveData<MutableList<ActivityRecord>>,
    val recordChange: SimpleLiveData<ActivityRecordChange>,
) {

    private var mIsStarted = false
    private var mIsInited = false

    private var onChange: ((ActivityRecordChange) -> Unit)? = null
    private var onInit: ((MutableList<ActivityRecord>) -> Unit)? = null

    /** 数据改变 */
    fun onChange(call: (ActivityRecordChange) -> Unit): ActivityRecordObserve {
        onChange = call
        return this
    }

    /** 数据初始化 */
    fun onInit(call: (MutableList<ActivityRecord>) -> Unit): ActivityRecordObserve {
        onInit = call
        return this
    }

    /** 开始 */
    fun start() {
        if (mIsStarted) throw RuntimeException("The observer is started !")
        mIsStarted = true

        onChange?.let { call ->
            recordChange.observe(owner) {
                if (mIsInited) {
                    call(it)
                }
            }
        }
        onInit?.let { call ->
            record.observe(owner) {
                call(it)
                mIsInited = true
            }
        }

    }

}

// 密封类:记录改变
sealed class ActivityRecordChange {
    abstract fun executor(mutableList: MutableList<ActivityRecord>)
}

// 记录安装事件
data class ActivityRecordInstall(val index: Int, val record: ActivityRecord) : ActivityRecordChange() {
    override fun executor(mutableList: MutableList<ActivityRecord>) {
        mutableList.add(index, record)
    }
}

// 记录更新事件
data class ActivityRecordReplaced(val index: Int, val record: ActivityRecord) : ActivityRecordChange() {
    override fun executor(mutableList: MutableList<ActivityRecord>) {
        mutableList.replace(index, record)
    }
}

// 记录卸载事件
data class ActivityRecordUninstall(val index: Int, var packageName: String) : ActivityRecordChange() {
    override fun executor(mutableList: MutableList<ActivityRecord>) {
        mutableList.removeAt(index)
    }
}

//fun main() {
//    val list = mutableListOf(1, 2, 3, 4, 5)
//    println("原数据：$list")
//    list.removeAll { it == 3 }
//    println("处理之后：$list")
//
//}