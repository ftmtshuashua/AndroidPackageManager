package com.acap.pkg.manager.adapter.child

import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.databinding.VhMenuitemSwitchBinding
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
        val bind = VhMenuitemSwitchBinding.bind(holder.contentView)
        bind.viewTitle.text = title

        bind.viewSwitch.setOnCheckedChangeListener(null)
        bind.viewSwitch.isChecked = current.invoke()
        bind.viewSwitch.setOnCheckedChangeListener { _, isChecked -> action.invoke(isChecked) }
    }
}