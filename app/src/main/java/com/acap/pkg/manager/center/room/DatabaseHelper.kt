package com.acap.pkg.manager.center.room

import androidx.room.Room
import com.acap.pkg.manager.utils.Utils


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/25 17:47
 * </pre>
 */
object DatabaseHelper {

    val mDatabaseApp by lazy { Room.databaseBuilder(Utils.context, DatabaseApp::class.java, "app_database").build() }

}