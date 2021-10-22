package com.acap.pkg.manager.center

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import com.acap.pkg.manager.center.live.SimpleLiveData
import com.acap.pkg.manager.utils.ApkResource
import com.acap.pkg.manager.utils.Utils


/**
 * <pre>
 * Tip:
 *      记录了一个活动的信息
 *
 * Created by A·Cap on 2021/10/21 16:09
 * </pre>
 */
class ActivityRecord {
    companion object {
        /** 用于赋值未获取到 PackageInfo 的对象*/
        private val NULL_PACKAGE_INFO = PackageInfo()
    }

    constructor(packageName: String) {
        this.packageName = packageName
        this.packageInfo = Utils.getPackageInfo(packageName) ?: NULL_PACKAGE_INFO
    }

    constructor(packageInfo: PackageInfo) {
        this.packageName = packageInfo.packageName
        this.packageInfo = packageInfo
    }

    /** 包名 */
    var packageName: String
        private set

    lateinit var packageInfo: PackageInfo
        private set


    /** App 应用名称*/
    val appName: LiveData<String> by lazy { SimpleLiveData { ApkResource.loadAppNameForApp(packageInfo) } }

    /** App logo*/
    val appIcon: LiveData<Drawable> by lazy { SimpleLiveData { ApkResource.loadIconForApp(packageInfo) } }

    /** 检查 App 是否存在 */
    fun exists(): Boolean = NULL_PACKAGE_INFO != packageInfo

    /** 检查当前记录是否为系统App */
    val isSystemApp: Boolean by lazy { Utils.isSystemApp(packageInfo) }

}

/** 快速转换 */
fun PackageInfo.toActivityRecord() = ActivityRecord(this)
