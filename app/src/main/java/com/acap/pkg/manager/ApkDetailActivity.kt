package com.acap.pkg.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.acap.pkg.manager.adapter.child.VH_ActvitiyLauncher
import com.acap.pkg.manager.base.BaseActivity
import com.acap.pkg.manager.base.isExported
import com.acap.pkg.manager.base.onClick
import com.acap.pkg.manager.base.setStyle_LinearVertical
import com.acap.pkg.manager.center.ActivityRecord
import com.acap.pkg.manager.databinding.ActivityApkDetailBinding
import com.acap.pkg.manager.databinding.ActivityErrorBinding
import com.bumptech.glide.Glide
import com.weather.utils.adapter.MultipleRecyclerViewAdapter


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/9 18:31
 * </pre>
 */
class ApkDetailActivity : BaseActivity() {

    companion object {
        fun start(context: Context, packageName: String) {
            val intent = Intent(context, ApkDetailActivity::class.java)
            intent.putExtra("packageName", packageName)
            context.startActivity(intent)
        }
    }

    private val mActivityRecord by lazy { ActivityRecord(intent.getStringExtra("packageName") ?: "") }
    private lateinit var mViewBinding: ActivityApkDetailBinding
    val mAdapter by lazy { MultipleRecyclerViewAdapter<VH_ActvitiyLauncher>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!mActivityRecord.exists()) {
            val viewBinding = setViewBinding(ActivityErrorBinding::class.java)
            viewBinding.viewFinish.onClick { finish() }
            viewBinding.viewTips.text = "未找到 [${mActivityRecord.packageName}] 的包信息"
            return
        }
        mViewBinding = setViewBinding(ActivityApkDetailBinding::class.java)

        mViewBinding.viewFinish.onClick { finish() }
        mViewBinding.viewRecyclerView.setStyle_LinearVertical()
        mViewBinding.viewRecyclerView.adapter = mAdapter

        mActivityRecord.appName.observe(this) { mViewBinding.viewTitle.text = it }
        mViewBinding.viewVersion.text = "v ${mActivityRecord.versionName}"
        mActivityRecord.appIcon.observe(this) { Glide.with(mViewBinding.viewLogo).load(it).into(mViewBinding.viewLogo) }

        mActivityRecord.getActivities()
            ?.map { it }
            ?.sortedBy { !it.isExported() }
            ?.forEach { mAdapter.add(VH_ActvitiyLauncher(it)) }

    }
}