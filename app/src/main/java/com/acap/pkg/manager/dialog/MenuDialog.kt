package com.acap.pkg.manager.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.R
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.weather.utils.adapter.MultipleRecyclerViewAdapter
import support.lfp.adapter.BaseViewHolder


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 16:09
 * </pre>
 */
class MenuDialog<T>(context: Context, inits: MenuDialog<T>.() -> Unit, click: (T, BaseViewHolder<MultipleViewModel>) -> Unit) : PopupWindow(context) {

    private var mData: T? = null

    val mAdapter by lazy { MultipleRecyclerViewAdapter<MultipleViewModel>() }
    private val mRecyclerView by lazy { contentView.findViewById<RecyclerView>(R.id.view_RecyclerView) }


    init {
        val mRootView = LayoutInflater.from(context).inflate(R.layout.layout_pop_menu, null)
        contentView = mRootView
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        inits.invoke(this)

        mAdapter.setOnItemClickListener { adapter, viewHolder, view, position ->
            dismiss()
            click.invoke(mData!!, viewHolder)
        }
    }


    fun show(anchor: View, data: T) {
        mData = data
        showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View?) {
        if (isShowing) dismiss()
        super.showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        super.showAsDropDown(anchor, xoff, yoff)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
    }
}