package com.acap.pkg.manager.event

import android.content.pm.PackageInfo
import com.acap.ec.BaseEvent
import com.acap.pkg.manager.base.LOAD_CONFIG_PACKAGES
import com.acap.pkg.manager.center.ActivityRecord
import com.acap.pkg.manager.center.toActivityRecord
import com.acap.pkg.manager.utils.Utils


/**
 * <pre>
 * Tip:
 *      从系统中读取已安装包
 *
 * Created by A·Cap on 2021/10/8 11:59
 * </pre>
 */
class EventGetPackages<T>(
    /** 是否加载系统的包 */
    val isLoadSystemPackage: Boolean = false,
) : BaseEvent<T?, List<ActivityRecord>>() {


    companion object {
        /** 读取系统中已安装的包 */
        fun readInstalledPackages() = Utils.packageManager.getInstalledPackages(LOAD_CONFIG_PACKAGES)
    }


    override fun onCall(params: T?) {
        var installedPackages = readInstalledPackages()
        val packageName = Utils.getPackageName()
        val transform: (PackageInfo) -> ActivityRecord = { it.toActivityRecord() }

        installedPackages = if (isLoadSystemPackage) {
            installedPackages.filter { packageName != it.packageName }
        } else {
            installedPackages.filter { !Utils.isSystemApp(it) && packageName != it.packageName }
        }
        installedPackages.sortBy { Utils.isSystemApp(it) }

        next(installedPackages.map(transform).filter { it.exists() })
    }
}
