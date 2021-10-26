package com.acap.pkg.manager.utils

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * <pre>
 * Tip:
 *      协程助手
 *
 * Created by A·Cap on 2021/10/26 14:18
 * </pre>
 */
class GlobalScopeHelper private constructor() {
    companion object {
        fun default(action: suspend GlobalScopeHelper.(CoroutineScope) -> Unit): Job = GlobalScopeHelper().run(Dispatchers.Default, action)
        fun io(action: suspend GlobalScopeHelper.(CoroutineScope) -> Unit): Job = GlobalScopeHelper().run(Dispatchers.IO, action)
        fun main(action: suspend GlobalScopeHelper.(CoroutineScope) -> Unit): Job = GlobalScopeHelper().run(Dispatchers.Main, action)
        fun unconfined(action: suspend GlobalScopeHelper.(CoroutineScope) -> Unit): Job = GlobalScopeHelper().run(Dispatchers.Unconfined, action)
    }

    private fun getHelper() = this
    private fun run(context: CoroutineContext, action: suspend GlobalScopeHelper.(CoroutineScope) -> Unit): Job = GlobalScope.launch(context) {
        action(getHelper(), this)
    }

    suspend fun <T> main(action: suspend GlobalScopeHelper.(CoroutineScope) -> T): T = withContext(Dispatchers.Main) { action(getHelper(), this) }

    suspend fun <T> io(action: suspend GlobalScopeHelper.(CoroutineScope) -> T): T = withContext(Dispatchers.IO) { action(getHelper(), this) }

    suspend fun <T> default(action: suspend GlobalScopeHelper.(CoroutineScope) -> T): T = withContext(Dispatchers.Default) { action(getHelper(), this) }

    suspend fun <T> unconfined(action: suspend GlobalScopeHelper.(CoroutineScope) -> T): T = withContext(Dispatchers.Unconfined) { action(getHelper(), this) }


}