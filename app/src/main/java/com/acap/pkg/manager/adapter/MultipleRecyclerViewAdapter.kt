package com.weather.utils.adapter

import android.view.View
import com.acap.pkg.manager.adapter.MultipleViewModel
import support.lfp.adapter.BaseLoonRecyclerViewAdapter
import support.lfp.adapter.BaseLoonViewHolder
import support.lfp.adapter.BaseViewHolder
import support.lfp.adapter.interior.AdapterObservable
import support.lfp.adapter.interior.AdapterObservable.OnItemClickListener
import java.util.*

/**
 * 复合布局的数据适配器
 *
 * @Author :Hope_LFB
 * @DATE :2019/10/25 11:10
 */
class MultipleRecyclerViewAdapter<T : MultipleViewModel?> : BaseLoonRecyclerViewAdapter<T, BaseLoonViewHolder<T>?>(0) {
    /*储存Layout与ViewType对应关系*/
    private val layout2Type = HashMap<Int, Int>()

    /*储存ViewType与Layout对应关系*/
    private val type2Layout = HashMap<Int, Int>()

    //查询对象的位置
    fun findIndexByModel(model: T): Int {
        val data = data
        for (i in data.indices) {
            val t = data[i]
            if (t === model) return i
        }
        return -1
    }

    override fun getItemViewType(position: Int): Int {
        val layoutid = getDataItem(position)!!.getLayoutId()
        var type = layout2Type[layoutid]
        if (type == null) {
            type = layout2Type.size
            layout2Type[layoutid] = type
            type2Layout[type] = layoutid
        }
        return type
    }

    override fun getLayoutId(viewType: Int): Int {
        return type2Layout[viewType]!!
    }

    override fun convert(holder: BaseLoonViewHolder<T>, data: T) {
        data!!.onAttach(this)
        data.onUpdate(holder)
    }

    init {
        onItemClickListener = OnItemClickListener { adapter: AdapterObservable<T>?, viewHolder: BaseViewHolder<T>, view1: View?, position: Int ->
            viewHolder.saveData!!.onClick(viewHolder.context)
        }
    }
}