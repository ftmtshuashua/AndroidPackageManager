package com.acap.pkg.manager.center.room

import androidx.room.*


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
    suspend fun getAll(): List<StarApp>
//    fun getAll(): Flow<List<StarApp>>

    @Query("SELECT * FROM star_app WHERE package_name == :package_name and activity_name=:activity_name")
    suspend fun findById(package_name: String, activity_name: String): StarApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: StarApp)

    @Delete
    suspend fun delete(entity: StarApp)

    @Query("DELETE FROM star_app")
    suspend fun deleteAll()
}

@Entity(tableName = "star_app", primaryKeys = ["package_name", "activity_name"])  //表示数据库中的表
data class StarApp(
    /*主键 - 包名*/
    val package_name: String,
    /*主键 - 目标页*/
    val activity_name: String,
    /*显示名*/
    var star_name: String,
    /*是否标星*/
    var star_enable: Boolean,
    /*更新时间*/
    var update_time: Long = System.currentTimeMillis(),
) {
    init {
        update_time = System.currentTimeMillis()
    }

    override fun toString(): String {
        return "StarApp(${star_name})"
    }
}
