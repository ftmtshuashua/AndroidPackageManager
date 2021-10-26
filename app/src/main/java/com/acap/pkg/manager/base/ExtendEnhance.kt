package com.acap.pkg.manager.base

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*-------------------------------- 扩展配置 ----------------------------------*/

/** View的点击监听 */
fun <T : View> T.onClick(click: (T) -> Unit): T {
    setOnClickListener { click(it as T) }
    return this
}


/** 启动Activity */
fun Activity.startActivityByClass(cls: Class<out Activity>) {
    startActivity(Intent(this, cls))
}

/** 启动Activity */
fun Activity.startActivityByClass(cls: Class<out Activity>, bundle: Bundle) {
    val intent = Intent(this, cls)
    intent.putExtra("bundle", bundle)
    startActivity(intent)
}

/** 数据替换 */
fun <T> MutableList<T>.replace(index: Int, t: T): List<T> {
    this.removeAt(index)
    this.add(index, t)
    return this
}

/** 浅拷贝 */
fun <T> List<T>.clone() = mutableListOf(this)

/** 判断Activity是否支持外部访问 */
fun ActivityInfo.isExported(): Boolean = this.enabled && this.exported

//-------------------------------- 样式

/** Recycler样式配置 */
fun RecyclerView.setStyle_LinearVertical(): RecyclerView {
    this.layoutManager = LinearLayoutManager(context)
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    return this
}

//-------------------------------- 快速创建

fun BroadcastReceiverCreate(action: (context: Context, intent: Intent) -> Unit): BroadcastReceiver {
    val value = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            action(context, intent)
        }
    }
    return value
}



//-------------------------------- 内联

inline fun <E, V : View> V.getObserver(crossinline call: V.(E) -> Unit): Observer<E> {
    var observer = getTag(id)
    if (observer == null) {
        observer = Observer<E> { call(this, it) }
        setTag(id, observer)
    }
    return observer as Observer<E>
}
