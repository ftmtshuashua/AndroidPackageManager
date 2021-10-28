package com.acap.pkg.manager

import android.os.Bundle
import com.acap.pkg.manager.adapter.MultipleViewModel
import com.acap.pkg.manager.adapter.child.VH_MenuItem_Switch
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.ReadConfig
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.base.setStyle_LinearVertical
import com.acap.pkg.manager.databinding.ActivitySettingBinding
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

    lateinit var mViewBind:ActivitySettingBinding
    val mAdapter by lazy { MultipleRecyclerViewAdapter<MultipleViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBind = setViewBinding(ActivitySettingBinding::class.java)

        mViewBind.viewFinish.onClick { finish() }

        mViewBind.viewRecyclerView.setStyle_LinearVertical()
        mViewBind.viewRecyclerView.adapter = mAdapter

        mAdapter.add(VH_MenuItem_Switch("系统应用是否可删除", { ReadConfig.SYSTEM_APP_ALLOW_UNINSTALL }) { ReadConfig.SYSTEM_APP_ALLOW_UNINSTALL = it })


    }
}