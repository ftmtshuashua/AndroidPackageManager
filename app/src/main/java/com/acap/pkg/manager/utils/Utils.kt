package com.acap.pkg.manager.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.base.BaseApplication
import com.acap.pkg.manager.event.ApkInfo
import com.acap.toolkit.app.AppUtils
import com.acap.toolkit.app.IntentUtils
import com.acap.toolkit.log.LogUtils


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 14:33
 * </pre>
 */
object Utils {

    fun getPackageManager(): PackageManager = AppUtils.getApp<BaseApplication>().packageManager

    fun isSystemApp(packageInfo: PackageInfo) = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0

    fun uninstall(context: Context, info: ApkInfo) {
        try {
            context.startActivity(IntentUtils.getUninstallAppIntent(info.PackageName))
        } catch (e: Throwable) {
            LogUtils.e(e)
        }
    }

    fun getInstalledPackages() = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)

    fun getPackageInfo(packageName: String): PackageInfo {
        return getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
    }

    fun setOnScrollStateChanged(recyclerView: RecyclerView, action: (RecyclerView, Int) -> Unit): RecyclerView {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                action.invoke(recyclerView, newState)
            }
        })
        return recyclerView
    }

}