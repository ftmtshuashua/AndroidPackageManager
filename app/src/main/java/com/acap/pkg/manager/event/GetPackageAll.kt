package com.acap.pkg.manager.event

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import com.acap.ec.BaseEvent
import com.acap.pkg.manager.base.BaseApplication
import com.acap.pkg.manager.base.ReadConfig
import com.acap.pkg.manager.utils.Utils
import com.acap.toolkit.app.AppUtils
import com.acap.toolkit.log.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * <pre>
 * Tip:
 *      获得包信息
 *
 * Created by A·Cap on 2021/10/8 11:59
 * </pre>
 */
class GetPackageAll<T>() : BaseEvent<T?, List<ApkInfo>>() {
    companion object {
        val transform: (PackageInfo) -> ApkInfo = { it.toApkInfo() }
    }

    override fun onCall(params: T?) {
        val start = System.currentTimeMillis()

        val installedPackages = Utils.getInstalledPackages()

        val user = mutableListOf<PackageInfo>()
        val system = mutableListOf<PackageInfo>()
        installedPackages.forEach {
            if (!AppUtils.getApp<BaseApplication>().packageName.equals(it.packageName)) {
                if (Utils.isSystemApp(it)) {
                    system.add(it)
                } else {
                    user.add(it)
                }
            }
        }


        val array = mutableListOf<ApkInfo>()
        array.addAll(user.map(transform))

        if (ReadConfig.READ_SYSTEM_APP) {
            array.addAll(system.map(transform))
        }

        LogUtils.fi("耗时 -> {0}ms", System.currentTimeMillis() - start)
        next(array)
    }
}

data class ApkInfo(
    val PackageInfo: PackageInfo,
    val PackageName: String,
    val IsSystemApp: Boolean,
) {
    companion object {
        private val LogoCache = mutableMapOf<String, Drawable?>()

        private suspend fun getDrawable(packageInfo: PackageInfo, packageName: String): Drawable {
            return withContext(Dispatchers.IO) {
                var drawable = LogoCache[packageName]
                if (drawable == null) {
                    drawable = packageInfo.applicationInfo.loadIcon(Utils.getPackageManager())
                    LogoCache[packageName] = drawable
                }
                drawable!!
            }
        }

    }


    val AppName by lazy { PackageInfo.applicationInfo.loadLabel(Utils.getPackageManager()) }

    val VersionName by lazy { PackageInfo.versionName }

    val VersionCode by lazy { PackageInfo.versionCode }

    suspend fun getAppLogo() = getDrawable(PackageInfo, PackageName)

}

fun PackageInfo.toApkInfo(): ApkInfo {
    val instance = ApkInfo(
        this,
        packageName,
        Utils.isSystemApp(this),
    )

    return instance
}
