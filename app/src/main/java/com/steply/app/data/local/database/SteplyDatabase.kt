package com.steply.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.steply.app.data.local.dao.MovementHistoryDao
import com.steply.app.data.local.dao.UserProfileDao
import com.steply.app.data.local.entities.MovementHistoryEntity
import com.steply.app.data.local.entities.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, MovementHistoryEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class SteplyDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun movementHistoryDao(): MovementHistoryDao

    companion object {
        @Volatile
        private var instance: SteplyDatabase? = null

        fun getInstance(context: Context): SteplyDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SteplyDatabase::class.java,
                    "steply_mobile_remote.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
