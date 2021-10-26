package com.acap.pkg.manager

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.acap.pkg.manager.adapter.child.VH_Star
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.base.startActivityByClass
import com.acap.pkg.manager.center.DriverManager
import com.acap.pkg.manager.databinding.ActivityMainBinding
import com.acap.toolkit.app.ToastUtils
import com.acap.toolkit.view.ViewUtils
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
    lateinit var mBind: ActivityMainBinding
    val mAdapter by lazy { MultipleRecyclerViewAdapter<VH_Star>() }

    /*    val mRecyclerView by lazy { Utils.setOnScrollStateChanged(findViewById<RecyclerView>(R.id.view_RecyclerView)) { _, _ -> mMenu.dismiss() } }
    val mMenu by lazy {
        MenuDialog<ActivityRecord>(getContext(), {
            this.mAdapter.add(VH_MenuItem_Text("详情"))
            this.mAdapter.add(VH_MenuItem_Text("收藏"))
            this.mAdapter.add(VH_MenuItem_Text("卸载"))
        }) { pi, vm ->
//            when ((vm.saveData as VH_MenuItem_Text).txt) {
//                "卸载" -> Utils.uninstall(getContext(), pi)
//                "详情" -> startActivity(ApkDetailActivity::class.java)
//            }
        }
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBind = setViewBinding(ActivityMainBinding::class.java)

        mBind.viewRecyclerView.adapter = mAdapter
        mBind.viewRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        mAdapter.setOnItemClickListener { adapter, viewHolder, view, position ->

            try {
                startActivity((viewHolder.saveData as VH_Star).apkInfo.getIntent())
            } catch (e: Throwable) {
                ToastUtils.toast("启动失败")
            }
        }

        /* mAdapter.setOnItemLongClickListener { adapter, viewHolder, view, position ->
             val mPackageViewModel = viewHolder.saveData as VH_Star
             mMenu.show(view, mPackageViewModel.apkInfo)
             true
         }*/

        findViewById<ImageView>(R.id.view_Setting).onClick { startActivityByClass(ManagerActivity::class.java) }
        findViewById<View>(R.id.view_Uninstall).onClick { startActivityByClass(UninstallActivity::class.java) }

        DriverManager.getStarActivityRecordObserve(this)
            .onInit { it -> it.map { VH_Star(getLiveDataOnlyObserve(), it) }.apply { mAdapter.set(this) } }
            .onChange { }
            .onUpdate { ViewUtils.setVisibility(findViewById(R.id.view_Tips), if (it?.isEmpty() != false) View.VISIBLE else View.GONE) }
            .start()

    }
}



