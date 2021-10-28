package com.acap.pkg.manager

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.adapter.child.VH_Uninstall
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.base.setStyle_LinearVertical
import com.acap.pkg.manager.center.*
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *      应用卸载 Activity
 *
 * Created by A·Cap on 2021/10/22 14:21
 * </pre>
 */
class UninstallActivity : BaseActivity() {

    val mAdapter by lazy { MultipleRecyclerViewAdapter<MultipleViewModel>() }
    val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.view_RecyclerView).setStyle_LinearVertical() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uninstall)

        findViewById<View>(R.id.view_Finish).onClick { finish() }

        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, vh, _, _ ->
            ApkDetailActivity.start(this, (vh.saveData as VH_Uninstall).apkInfo.packageName)
        }

        DriverManager.getAllActivityRecordObserve(this)
            .onChange {
//                LogUtils.i("改变:${it}")
                when (it) {
                    is RecordInstall -> {
                        mAdapter.insert(it.index, VH_Uninstall(getLiveDataOnlyObserve(), it.record))
                    }
                    is RecordReplaced -> {
                        mAdapter.replace(it.index, VH_Uninstall(getLiveDataOnlyObserve(), it.record))
                    }
                    is RecordUninstall -> {
                        mAdapter.remove(it.index)
                    }
                }
            }
            .onInit { it -> mAdapter.set(it.map { VH_Uninstall(getLiveDataOnlyObserve(), it) }) }
            .start()

    }
}