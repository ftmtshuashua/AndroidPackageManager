package com.acap.pkg.manager.utils

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.base.LOAD_CONFIG_PACKAGES
import com.acap.pkg.manager.base.LOAD_CONFIG_PACKAGES_BASE
import com.acap.toolkit.app.AppUtils
import com.acap.toolkit.app.IntentUtils
import com.acap.toolkit.log.LogUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 14:33
 * </pre>
 */
object Utils {
    val context by lazy { AppUtils.getApp<Application>() }

    /** 包管理器 */
    val packageManager by lazy { AppUtils.getApp<Application>().packageManager }


    /** 获得当前App的包名 */
    fun getPackageName() = AppUtils.getAppPackageName()

    /** 判断是否为系统APP */
    fun isSystemApp(packageInfo: PackageInfo) = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0

    /** 判断是否为系统APP */
    fun isSystemApp(packageName: String) = getPackageInfo(packageName, LOAD_CONFIG_PACKAGES_BASE)?.let { isSystemApp(it) } ?: false

    /** 根据包名获得包信息 */
    fun getPackageInfo(packageName: String, flags: Int = LOAD_CONFIG_PACKAGES): PackageInfo? {
        return try {
            packageManager.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    /** 卸载App */
    fun uninstall(context: Context, packageName: String) {
        try {
            context.startActivity(IntentUtils.getUninstallAppIntent(packageName))
        } catch (e: Throwable) {
            LogUtils.e(e)
        }
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

    /**
     * 执行 CMD 命令
     *  获得包信息:
     *      dumpsys package [packageName]
     *  启动Activity:
     *      am start -n [packageName]/[activityName]
     * */
    fun exec(command: String): String {
        val sb = StringBuffer()
        try {

            val process: Process = Runtime.getRuntime().exec(command)
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while ((bufferedReader.readLine().also { line = it }) != null) {
                sb.append(line).append("\n")
            }
        } catch (e: IOException) {
            return e.toString()
        }
        return sb.toString()
    }
}
