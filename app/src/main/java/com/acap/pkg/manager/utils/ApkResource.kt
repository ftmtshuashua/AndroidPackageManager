package com.acap.pkg.manager.utils

import android.content.pm.PackageInfo


/**
 * <pre>
 * Tip:
 *      Apk 资源加载
 *
 * Created by A·Cap on 2021/10/21 17:48
 * </pre>
 */
object ApkResource {

    /** 记载App的名称 */
    fun loadAppNameForApp(packageInfo: PackageInfo) = packageInfo.applicationInfo.loadLabel(Utils.packageManager) as String

    /** 加载App的Logo */
    fun loadIconForApp(packageInfo: PackageInfo) = packageInfo.applicationInfo.loadIcon(Utils.packageManager)

}