package com.acap.pkg.manager.event

import android.content.pm.PackageInfo
import com.acap.ec.BaseEvent
import com.acap.pkg.manager.base.LOAD_CONFIG_PACKAGES
import com.acap.pkg.manager.center.ActivityRecord
import com.acap.pkg.manager.center.toActivityRecord
import com.acap.pkg.manager.utils.Utils
import com.acap.toolkit.log.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
//        fun readInstalledPackages() = Utils.packageManager.getInstalledPackages(0)
    }


    override fun onCall(params: T?) {

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                var apks = readInstalledPackages()
                val packageName = Utils.getPackageName()
                // 清理
//                apks = if (isLoadSystemPackage) {
//                    apks.filter { packageName != it.packageName }
//                } else {
//                    apks.filter { !Utils.isSystemApp(it) && packageName != it.packageName }
//                }
                //排序
                apks.sortBy { Utils.isSystemApp(it) }

                val transform: (PackageInfo) -> ActivityRecord = { it.toActivityRecord() }
                val apksRecord = apks.map(transform).filter { it.exists() }

//                apks.forEach { it ->
//                    it.activities?.forEach {
//                        LogUtils.i("Activity:${it.name} , isEnabled:${it.isEnabled}")
//                    }
//                }

//                apksRecord.forEach {
//                    val exec = Utils.exec("dumpsys package ${it.packageName}")
//                    LogUtils.i("${it.packageName} -> \n $exec")
//                }



                next(apksRecord)
            }

        }

    }
}
