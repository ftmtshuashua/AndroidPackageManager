package com.acap.pkg.manager.adapter.child

import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.View
import androidx.lifecycle.Observer
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.isExported
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.center.DriverManager
import com.acap.pkg.manager.databinding.VhLayoutActivityLauncherBinding
import com.acap.toolkit.app.ToastUtils
import com.acap.toolkit.log.LogUtils
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:26
 * </pre>
 */
class VH_ActvitiyLauncher(val activityInfo: ActivityInfo) : MultipleViewModel(R.layout.vh_layout_activity_launcher) {


    inline fun <E> View.createOrCache(crossinline call: View.(E) -> Unit): Observer<E> {
        var observer = getTag(id)
        if (observer == null) {
            observer = Observer<E> { call(this, it) }
            setTag(id, observer)
        }
        return observer as Observer<E>
    }

    override fun onUpdate(holder: BaseViewHolder<*>) {
        val bind = VhLayoutActivityLauncherBinding.bind(holder.contentView)
        bind.viewActivityName.text = activityInfo.name


        val exported = activityInfo.isExported()

        bind.viewOpen.isEnabled = exported
        bind.viewStar.isEnabled = exported
        bind.viewRoot.isFocusable = !exported
        if (exported) {
            bind.viewStar.isSelected = DriverManager.isStarActivity(activityInfo)
        }

        bind.viewStar.onClick {
            bind.viewStar.isSelected = !bind.viewStar.isSelected
            var index = activityInfo.name.lastIndexOf(".")
            if (index < 0) index = 0
            DriverManager.setStarActivity(activityInfo, bind.viewStar.isSelected, activityInfo.name.substring(index))
        }

        bind.viewOpen.onClick {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                bind.viewRoot.context.startActivity(intent)

//                val exec = Utils.exec("am start -n ${activityInfo.applicationInfo.packageName}/${activityInfo.name}")
//                LogUtils.e("执行结果:${exec}")
            } catch (e: Throwable) {
                LogUtils.e(e)
                ToastUtils.toast(e.message)
            }
        }
    }
}

//fun String.substringAtLast(){}