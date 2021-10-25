package com.acap.pkg.manager.center.room

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/10/25 16:52
 * </pre>
 */
@Database(entities = [StarApp::class], exportSchema = false, version = 1)
abstract class DatabaseApp : RoomDatabase() {
    abstract fun getStarAppDao(): StarAppDao
}