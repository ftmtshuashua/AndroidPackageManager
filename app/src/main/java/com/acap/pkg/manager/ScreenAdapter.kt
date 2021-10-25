package com.acap.pkg.manager

import android.os.Bundle
import com.acap.pkg.manager.base.BaseActivity


/**
 * <pre>
 * Tip:
 *      屏幕适配
 *
 * Created by A·Cap on 2021/10/25 11:39
 * </pre>
 */
class ScreenAdapter : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_dataper)
    }
}