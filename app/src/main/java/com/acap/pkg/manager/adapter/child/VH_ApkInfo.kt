package com.acap.pkg.manager.adapter.child

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.event.ApkInfo
import com.acap.toolkit.view.ViewUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:26
 * </pre>
 */
class VH_ApkInfo(val apkInfo: ApkInfo) : MultipleViewModel(R.layout.layout_home_apk) {

    override fun onUpdate(holder: BaseViewHolder<*>) {

        val vAppLogo = holder.getView<ImageView>(R.id.view_AppLogo)
        val vAppName = holder.getView<TextView>(R.id.view_AppName)
        val vAppVersion = holder.getView<TextView>(R.id.view_AppVersion)
        val vMark = holder.getView<ImageView>(R.id.view_Mark)

        vAppName.text = apkInfo.AppName
//        vAppVersion.text = "v${apkInfo.VersionName} (${apkInfo.VersionCode})"
        vAppVersion.text = "v${apkInfo.VersionName}"

        ViewUtils.setVisibility(vMark, if (apkInfo.IsSystemApp) View.VISIBLE else View.GONE)
        Glide.with(vMark).load(R.mipmap.mark_system).into(vMark)

        GlobalScope.launch(Dispatchers.Main) {
            val appLogo = apkInfo.getAppLogo()
            Glide.with(vAppLogo).load(appLogo).into(vAppLogo)
        }
    }
}