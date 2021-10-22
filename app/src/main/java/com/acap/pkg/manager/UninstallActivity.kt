package com.acap.pkg.manager

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.adapter.child.VH_Uninstall
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.base.setStyle_LinearVertical
import com.acap.pkg.manager.center.ActivityRecordInstall
import com.acap.pkg.manager.center.ActivityRecordReplaced
import com.acap.pkg.manager.center.ActivityRecordUninstall
import com.acap.pkg.manager.center.DriverManager
import com.acap.toolkit.log.LogUtils
import com.weather.utils.adapter.MultipleRecyclerViewAdapter
import java.lang.Exception


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

        DriverManager.getAllActivityRecordObserve(this)
            .onChange {
                LogUtils.i("改变:${it}")
                when (it) {
                    is ActivityRecordInstall -> {

//                        try{
//                            val get = LinearLayoutManager::class.java.getDeclaredField("mPendingScrollPosition").get(mRecyclerView.layoutManager) as Int
//
//                            LogUtils.e("当前所在位置:${get}")
//                        }catch (e:Exception){
//                            LogUtils.e(e)
//                        }

                        mAdapter.insert(it.index, VH_Uninstall(getLiveDataOnlyObserve(), it.record))
                        mRecyclerView.scrollToPosition(0)

                    }
                    is ActivityRecordReplaced -> {
                        mAdapter.replace(it.index, VH_Uninstall(getLiveDataOnlyObserve(), it.record))
                    }
                    is ActivityRecordUninstall -> {
                        mAdapter.remove(it.index)
                    }
                }
            }
            .onInit { it -> mAdapter.set(it.map { VH_Uninstall(getLiveDataOnlyObserve(), it) }) }
            .start()

    }
}