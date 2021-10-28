package com.acap.pkg.manager.base

import android.content.pm.PackageManager
import com.tencent.mmkv.MMKV


/** 读取包中所有信息 */
val LOAD_CONFIG_PACKAGES = PackageManager.GET_ACTIVITIES

/** 读取包的基本信息 */
val LOAD_CONFIG_PACKAGES_BASE = 0


/**
 * <pre>
 * Tip:
 *      数据读取配置
 *
 * Created by A·Cap on 2021/10/9 16:07
 * </pre>
 */
object ReadConfig {
    /** 系统应用是否可卸载 */
    var SYSTEM_APP_ALLOW_UNINSTALL: Boolean
        set(value) {
            MMKV.defaultMMKV().putBoolean("SYSTEM_APP_ALLOW_UNINSTALL", value).commit()
        }
        get() = MMKV.defaultMMKV().getBoolean("SYSTEM_APP_ALLOW_UNINSTALL", false)

}