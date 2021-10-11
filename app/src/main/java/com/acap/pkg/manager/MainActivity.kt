package com.acap.pkg.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.acap.ec.listener.OnEventNextListener
import com.acap.pkg.manager.adapter.child.VH_ApkInfo
import com.acap.pkg.manager.adapter.child.VH_MenuItem_Text
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.ReadConfig
import com.acap.pkg.manager.dialog.MenuDialog
import com.acap.pkg.manager.event.*
import com.acap.pkg.manager.utils.Utils
import com.acap.toolkit.app.ToastUtils
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/8 11:43
 * </pre>
 */
class MainActivity : BaseActivity() {
    val mAdapter by lazy { MultipleRecyclerViewAdapter<VH_ApkInfo>() }
    val mRecyclerView by lazy { Utils.setOnScrollStateChanged(findViewById<RecyclerView>(R.id.view_RecyclerView)) { _, _ -> mMenu.dismiss() } }

    val mMenu by lazy {
        MenuDialog<ApkInfo>(getContext(), {
            this.mAdapter.add(VH_MenuItem_Text("详情"))
            this.mAdapter.add(VH_MenuItem_Text("收藏"))
            this.mAdapter.add(VH_MenuItem_Text("卸载"))
        }) { pi, vm ->
            when ((vm.saveData as VH_MenuItem_Text).txt) {
                "卸载" -> Utils.uninstall(getContext(), pi)
                "详情" -> startActivity(ApkDetailActivity::class.java)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApkChangeManager(this, mAdapter)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        mAdapter.setOnItemClickListener { adapter, viewHolder, view, position ->
            val mPackageViewModel = viewHolder.saveData as VH_ApkInfo
            val intent = getContext().packageManager.getLaunchIntentForPackage(mPackageViewModel.apkInfo.PackageName)
            intent?.let { startActivity(it) } ?: ToastUtils.toast("该应用没有启动器!")
        }

        mAdapter.setOnItemLongClickListener { adapter, viewHolder, view, position ->
            val mPackageViewModel = viewHolder.saveData as VH_ApkInfo
            mMenu.show(view, mPackageViewModel.apkInfo)
            true
        }

        syn()

        findViewById<View>(R.id.view_Setting).setOnClickListener { startActivity(SettingActivity::class.java) }

        ReadConfig.READ_CHANGE.observe(this) { syn() }
    }


    private fun syn() {
        ThreadIo<Any>()
            .chain(GetPackageAll())
            .apply { list -> list.map { VH_ApkInfo(it) } }
            .chain(ThreadMain())
            .listener(OnEventNextListener { mAdapter.set(it) })
            .listener(OnElapsedTimeListener())
            .start()
    }
}


// Apk 变化监听
class ApkChangeManager(val activity: BaseActivity, val adapter: MultipleRecyclerViewAdapter<VH_ApkInfo>) : BroadcastReceiver() {
    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED")
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")
        intentFilter.addDataScheme("package")
        activity.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.dataString?.substring(8) ?: return
        val action = intent?.action ?: return

        when (action) {
            "android.intent.action.PACKAGE_ADDED" -> {
                activity.runForeground { adapter.insert(0, VH_ApkInfo(Utils.getPackageInfo(packageName).toApkInfo())) }
            }
            "android.intent.action.PACKAGE_REPLACED" -> {
                val find = adapter.data.find { packageName == it.apkInfo.PackageName }
                val indexOf = adapter.data.indexOf(find)
                activity.runForeground { adapter.replace(indexOf, VH_ApkInfo(Utils.getPackageInfo(packageName).toApkInfo())) }
            }
            "android.intent.action.PACKAGE_REMOVED" -> {
                val find = adapter.data.find { packageName == it.apkInfo.PackageName }
                val indexOf = adapter.data.indexOf(find)
                activity.runForeground { adapter.remove(indexOf) }
            }
        }
    }
}
