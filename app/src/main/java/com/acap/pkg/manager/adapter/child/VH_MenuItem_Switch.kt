package com.acap.pkg.manager.adapter.child

import android.widget.Switch
import android.widget.TextView
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.base.ReadConfig
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:26
 * </pre>
 */
class VH_MenuItem_Switch(val title: String, val current: () -> Boolean, val action: (Boolean) -> Unit) : MultipleViewModel(R.layout.vh_menuitem_switch) {
    override fun onUpdate(holder: BaseViewHolder<*>) {
        holder.getView<TextView>(R.id.view_Title).text = title
        val v_switch = holder.getView<Switch>(R.id.view_Switch)
        v_switch.setOnCheckedChangeListener(null)
        v_switch.isChecked = current.invoke()
        v_switch.setOnCheckedChangeListener { _, isChecked -> action.invoke(isChecked) }
    }
}