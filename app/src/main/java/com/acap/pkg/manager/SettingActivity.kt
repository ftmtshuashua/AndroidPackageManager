package com.acap.pkg.manager

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.adapter.child.VH_MenuItem_Switch
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.ReadConfig
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/9 11:36
 * </pre>
 */
class SettingActivity : BaseActivity() {

    val mAdapter by lazy { MultipleRecyclerViewAdapter<MultipleViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        findViewById<View>(R.id.view_Finish).setOnClickListener { finish() }

        val mRecyclerView = findViewById<RecyclerView>(R.id.view_RecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter

        mAdapter.add(VH_MenuItem_Switch("是否显示系统应用", { ReadConfig.READ_SYSTEM_APP }) { ReadConfig.READ_SYSTEM_APP = it })


    }
}