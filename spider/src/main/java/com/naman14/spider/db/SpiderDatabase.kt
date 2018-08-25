package com.naman14.spider.db

import android.content.Context
import androidx.room.*

@Database(entities = arrayOf(RequestEntity::class), version = 1)
abstract class SpiderDatabase: RoomDatabase() {

    abstract fun requestsDao(): RequestsDao

    companion object {
        @Volatile private var DISK_INSTANCE: SpiderDatabase? = null
        @Volatile private var IN_MEMORY_INSTANCE: SpiderDatabase? = null


        fun getDiskInstance(context: Context): SpiderDatabase? =
            DISK_INSTANCE ?: synchronized(this) {
                DISK_INSTANCE ?: buildDatabase(context).also { DISK_INSTANCE = it }
            }

        fun getMemoryInstance(context: Context): SpiderDatabase? =
                IN_MEMORY_INSTANCE ?: synchronized(this) {
                    IN_MEMORY_INSTANCE ?: buildInMemoryDatabase(context).also { IN_MEMORY_INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        SpiderDatabase::class.java, "spider.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

        private fun buildInMemoryDatabase(context: Context) =
                Room.inMemoryDatabaseBuilder(context.applicationContext,
                        SpiderDatabase::class.java)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
    }
}