package com.acap.pkg.manager.center

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LifecycleOwner
import com.acap.ec.listener.OnEventCompleteListener
import com.acap.ec.listener.OnEventNextListener
import com.acap.pkg.manager.base.replace
import com.acap.pkg.manager.center.live.SimpleLiveData
import com.acap.pkg.manager.center.room.DatabaseHelper
import com.acap.pkg.manager.center.room.StarApp
import com.acap.pkg.manager.event.EventGetPackages
import com.acap.pkg.manager.event.utils.OnEventTimeMonitor
import com.acap.pkg.manager.utils.GlobalScopeHelper
import com.acap.toolkit.log.LogUtils


/**
 * <pre>
 * Tip:
 *      数据驱动,数据配置
 *
 * Created by A·Cap on 2021/10/21 16:01
 * </pre>
 */
@SuppressLint("StaticFieldLeak")
object DriverManager {

    private lateinit var context: Context

    // 全部的活动记录
    private val AllActivityRecord by lazy { SimpleLiveData<MutableList<ActivityRecord>>() }
    private val AllActivityRecordChange by lazy { SimpleLiveData<RecordChange<ActivityRecord>>() }     // 记录ActivityRecord的上一次改变

    // 标星的活动记录
    private val StarActivityRecord by lazy { SimpleLiveData<MutableList<StarRecord>>() }
    private val StarActivityRecordChange by lazy { SimpleLiveData<RecordChange<StarRecord>>() }     // 记录ActivityRecord的上一次改变

    // 星数据
    private val mStarAppDao by lazy { DatabaseHelper.mDatabaseApp.getStarAppDao() }


    private lateinit var mRecordChange: ApkChange

    /** 安装卸载注册 */
    fun init(context: Context) {
        this.context = context
        EventGetPackages<Any>(true)
            .listener(OnEventNextListener { AllActivityRecord.setValue(it.toMutableList()) })
            .listener(OnEventTimeMonitor())
            .listener(OnEventCompleteListener { initReceiver(context) })
            .start()

        initStar()
    }

    // 安装卸载广播监听
    private fun initReceiver(context: Context) {
        mRecordChange = ApkChange(context)

        mRecordChange.onInstall { packageName ->
            val activityRecord = ActivityRecord(packageName)
            if (activityRecord.exists()) {
                val change = RecordInstall(0, activityRecord)
                AllActivityRecord.value?.apply { this.add(change.index, change.record) }
                AllActivityRecordChange.setValue(change)

            }
        }
        mRecordChange.onReplaced { packageName ->
            val activityRecord = ActivityRecord(packageName)
            if (activityRecord.exists()) {
                AllActivityRecord.value?.apply {
                    val change = RecordReplaced(indexOf(find { it.packageName == packageName }), activityRecord)
                    this.replace(change.index, change.record)
                    AllActivityRecordChange.setValue(change)
                }
            }
        }
        mRecordChange.onUninstall { packageName ->
            AllActivityRecord.value?.apply {
                val change = RecordUninstall<ActivityRecord>(indexOf(find { it.packageName == packageName }), packageName)
                if (change.index != -1) {
                    this.removeAt(change.index)
                    AllActivityRecordChange.setValue(change)
                } else {
                    LogUtils.e("${change.packageName} -> 卸载失败:未找到数据")
                }
            }
        }
    }

    private fun initStar() {
        GlobalScopeHelper.io {
            StarActivityRecord.setValue(mStarAppDao.getAll()
                .filter { it.star_enable }
                .sortedBy { it.update_time }
                .map { StarRecord(it.package_name, it.activity_name, it.star_name) }.toMutableList()
            )
        }
    }

    /** 获得全记录数据 */
    fun getAllActivityRecordObserve(owner: LifecycleOwner): ActivityRecordObserve<ActivityRecord> = ActivityRecordObserve(owner, AllActivityRecord, AllActivityRecordChange)

    /** 获得标星记录数据 */
    fun getStarActivityRecordObserve(owner: LifecycleOwner): ActivityRecordObserve<StarRecord> = ActivityRecordObserve(owner, StarActivityRecord, StarActivityRecordChange)

    /** 设置标星记录 */
    fun setStarActivity(ai: ActivityInfo, isStar: Boolean, star_name: String) {
        GlobalScopeHelper.io {
            val starApp = mStarAppDao.findById(ai.packageName, ai.name) ?: StarApp(ai.packageName, ai.name, star_name, isStar)
            starApp.star_enable = isStar
            mStarAppDao.insert(starApp)

            initStar()
        }
    }

    /** 检擦是否 Star */
    fun isStarActivity(ai: ActivityInfo): Boolean = StarActivityRecord.value?.let { it -> return !it.none { it.packageName == ai.packageName && it.activityName == ai.name } } ?: false


}

// 安装卸载监听
private class ApkChange private constructor() : BroadcastReceiver() {

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

    fun onInstall(call: ((String) -> Unit)): ApkChange {
        onInstall = call
        return this
    }

    fun onUninstall(call: ((String) -> Unit)): ApkChange {
        onUninstall = call
        return this
    }

    fun onReplaced(call: ((String) -> Unit)): ApkChange {
        onReplaced = call
        return this
    }

}

// 数据变化监听
class ActivityRecordObserve<T>(
    val owner: LifecycleOwner,
    val record: SimpleLiveData<MutableList<T>>,
    val recordChange: SimpleLiveData<RecordChange<T>>,
) {

    private var mIsStarted = false
    private var mIsInited = false

    private var onChange: ((RecordChange<T>) -> Unit)? = null
    private var onInit: ((MutableList<T>) -> Unit)? = null

    private var onUpdate: ((MutableList<T>?) -> Unit)? = null

    /** 数据更新 */
    fun onUpdate(call: (MutableList<T>?) -> Unit): ActivityRecordObserve<T> {
        onUpdate = call
        return this
    }

    /** 数据改变 */
    fun onChange(call: (RecordChange<T>) -> Unit): ActivityRecordObserve<T> {
        onChange = call
        return this
    }

    /** 数据初始化 */
    fun onInit(call: (MutableList<T>) -> Unit): ActivityRecordObserve<T> {
        onInit = call
        return this
    }

    /** 开始 */
    fun start() {
        if (mIsStarted) throw RuntimeException("The observer is started !")
        mIsStarted = true

        onUpdate?.apply { this.invoke(record.value) }

        onChange?.let { call ->
            recordChange.observe(owner) {
                if (mIsInited) {
                    call(it)
                    onUpdate?.apply { this.invoke(record.value) }
                }
            }
        }
        onInit?.let { call ->
            record.observe(owner) {
                call(it)
                mIsInited = true
                onUpdate?.apply { this.invoke(it) }
            }
        }

    }

}

// 密封类:记录改变
sealed class RecordChange<T>

// 记录安装事件
data class RecordInstall<T>(val index: Int, val record: T) : RecordChange<T>()

// 记录更新事件
data class RecordReplaced<T>(val index: Int, val record: T) : RecordChange<T>()

// 记录卸载事件
data class RecordUninstall<T>(val index: Int, var packageName: String) : RecordChange<T>()
