package com.acap.pkg.manager.adapter.child

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.center.ActivityRecord
import com.acap.pkg.manager.center.live.LiveDataOnlyObserve
import com.acap.pkg.manager.utils.Utils
import com.acap.toolkit.view.ViewUtils
import com.bumptech.glide.Glide
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:26
 * </pre>
 */
class VH_Uninstall(val observe: LiveDataOnlyObserve, val apkInfo: ActivityRecord) : MultipleViewModel(R.layout.vh_layout_uninstall) {


    inline fun <E> View.createOrCache(crossinline call: View.(E) -> Unit): Observer<E> {
        var observer = getTag(id)
        if (observer == null) {
            observer = Observer<E> { call(this, it) }
            setTag(id, observer)
        }
        return observer as Observer<E>
    }

    override fun onUpdate(holder: BaseViewHolder<*>) {

        val vLogo = holder.getView<ImageView>(R.id.view_AppLogo)
        val vName = holder.getView<TextView>(R.id.view_AppName)
        val vVersion = holder.getView<TextView>(R.id.view_AppVersion)
//        val vMark = holder.getView<ImageView>(R.id.view_Mark)

        holder.getView<View>(R.id.view_Uninstall).onClick { Utils.uninstall(it.context, apkInfo.packageName) }

        observe.observe(apkInfo.appName, vName.createOrCache { vName.text = it })

        vVersion.text = "v${apkInfo.packageInfo.versionName}"
//
//        ViewUtils.setVisibility(vMark, if (apkInfo.isSystemApp) View.VISIBLE else View.GONE)
//        Glide.with(vMark).load(R.drawable.mark_system).into(vMark)

        observe.observe(apkInfo.appIcon, vLogo.createOrCache { Glide.with(this).load(it).placeholder(R.mipmap.ic_launcher).into(vLogo) })
    }
}