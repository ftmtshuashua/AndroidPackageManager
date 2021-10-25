package com.acap.pkg.manager.center.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow


/**
 * <pre>
 * Tip:
 *      记录系统中安装过的APK
 *
 * Created by A·Cap on 2021/10/25 16:50
 * </pre>
 */
@Dao //包含用于访问数据库的方法
interface StarAppDao {
    @Query("select * from star_app ORDER BY update_time ASC")
    fun getAll(): Flow<List<StarApp>>

    @Query("SELECT * FROM star_app WHERE package_name == :packageName")
    fun findByPackageName(packageName: String): StarApp

    @Insert
    suspend fun insertAll(vararg entity: StarApp)

    @Delete
    suspend fun delete(entity: StarApp)

    @Query("DELETE FROM star_app")
    suspend fun deleteAll()
}

@Entity(tableName = "star_app")  //表示数据库中的表
data class StarApp(
    // Record Id
    @PrimaryKey(autoGenerate = true) val rid: Int,
    // 包名
    @ColumnInfo val package_name: String,
    // 自定义的名称
    @ColumnInfo val star_name: Boolean,
    // 更新时间，用于排序
    @ColumnInfo val update_time: Long,

    )