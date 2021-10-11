package com.acap.pkg.manager.adapter.child

import android.widget.TextView
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:26
 * </pre>
 */
class VH_MenuItem_Text(val txt: String) : MultipleViewModel(R.layout.vh_menuitem_text) {
    override fun onUpdate(holder: BaseViewHolder<*>) {
        val view = holder.getView<TextView>(R.id.view_Text)
        view.text = txt
    }
}