package com.acap.pkg.manager.adapter.child

import android.view.View
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.getObserver
import com.acap.pkg.manager.center.StarRecord
import com.acap.pkg.manager.center.live.LiveDataOnlyObserve
import com.acap.pkg.manager.databinding.VhLayoutStarBinding
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
class VH_Star(val observe: LiveDataOnlyObserve, val apkInfo: StarRecord) : MultipleViewModel(R.layout.vh_layout_star) {

    override fun onUpdate(holder: BaseViewHolder<*>) {
        val bind = VhLayoutStarBinding.bind(holder.contentView)

        bind.viewAppName.text = apkInfo.starName
        observe.observe(apkInfo.appName, bind.viewAppVersion.getObserver { this.text = it })

        ViewUtils.setVisibility(bind.viewMark, if (apkInfo.isSystemApp) View.VISIBLE else View.GONE)

        observe.observe(apkInfo.appIcon, bind.viewAppLogo.getObserver { Glide.with(this).load(it).placeholder(R.mipmap.ic_launcher).into(this) })
    }
}