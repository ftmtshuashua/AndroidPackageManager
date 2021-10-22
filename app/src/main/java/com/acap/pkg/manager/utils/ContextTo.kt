package com.acap.pkg.manager.utils

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.acap.toolkit.app.ContextUtils


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/21 17:59
 * </pre>
 */
object ContextTo : ContextUtils() {
    fun getLifecycleOwner(context: Context): LifecycleOwner? {
        val activity = getActivity(context)
        return if (activity != null && activity is LifecycleOwner) {
            activity
        } else {
            null
        }
    }
}