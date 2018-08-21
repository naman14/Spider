package com.naman14.spider.db

import android.content.Context
import androidx.room.*

@Database(entities = arrayOf(RequestEntity::class), version = 0)
abstract class SpiderDatabase: RoomDatabase() {

    abstract fun requestsDao(): RequestsDao

    companion object {
        private var INSTANCE: SpiderDatabase? = null

        fun getInstance(context: Context): SpiderDatabase? {
            if (INSTANCE == null) {
                synchronized(SpiderDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SpiderDatabase::class.java, "spider.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}