package com.acap.pkg.manager.adapter.child

import android.view.View
import androidx.lifecycle.Observer
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.center.ActivityRecord
import com.acap.pkg.manager.center.live.LiveDataOnlyObserve
import com.acap.pkg.manager.databinding.VhLayoutUninstallBinding
import com.acap.pkg.manager.utils.Utils
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
        val bind = VhLayoutUninstallBinding.bind(holder.contentView)

        bind.viewUninstall.onClick { Utils.uninstall(it.context, apkInfo.packageName) }
        bind.viewUninstall.isEnabled = !apkInfo.isSystemApp

        bind.viewRoot.isFocusable = apkInfo.isSystemApp

        observe.observe(apkInfo.appName, bind.viewAppName.createOrCache { bind.viewAppName.text = it })

        bind.viewAppVersion.text = apkInfo.packageName

        observe.observe(apkInfo.appIcon, bind.viewAppLogo.createOrCache { Glide.with(this).load(it).placeholder(R.mipmap.ic_launcher).into(bind.viewAppLogo) })
    }
}