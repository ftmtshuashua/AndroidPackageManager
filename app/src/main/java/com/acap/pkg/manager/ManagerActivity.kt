package com.acap.pkg.manager

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.acap.pkg.manager.adapter.child.VH_Manager
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.startActivityByClass
import com.acap.pkg.manager.center.ActivityRecordInstall
import com.acap.pkg.manager.center.ActivityRecordReplaced
import com.acap.pkg.manager.center.ActivityRecordUninstall
import com.acap.pkg.manager.center.DriverManager
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/9 11:36
 * </pre>
 */
class ManagerActivity : BaseActivity() {

    val mAdapter by lazy { MultipleRecyclerViewAdapter<VH_Manager>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        findViewById<View>(R.id.view_Finish).setOnClickListener { finish() }

        val mRecyclerView = findViewById<RecyclerView>(R.id.view_RecyclerView)
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, viewHolder, _, _ ->
            val apkInfo = (viewHolder.saveData as VH_Manager).apkInfo
            val bundle = Bundle()
            bundle.putString("packageName", apkInfo.packageName)
            startActivityByClass(ApkDetailActivity::class.java, bundle)
        }

        DriverManager.getAllActivityRecordObserve(this)
            .onChange {
                when (it) {
                    is ActivityRecordInstall -> {
                        mAdapter.insert(it.index, VH_Manager(getLiveDataOnlyObserve(), it.record))
                    }
                    is ActivityRecordReplaced -> {
                        mAdapter.replace(it.index, VH_Manager(getLiveDataOnlyObserve(), it.record))
                    }
                    is ActivityRecordUninstall -> {
                        mAdapter.remove(it.index)
                    }
                }
            }
            .onInit { it -> mAdapter.set(it.map { VH_Manager(getLiveDataOnlyObserve(), it) }) }
            .start()

    }
}