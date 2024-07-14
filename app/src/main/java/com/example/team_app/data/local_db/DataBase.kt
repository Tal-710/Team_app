package com.example.team_app.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.team_app.data.model.Player
import com.example.team_app.data.model.Team

@Database(entities = [Team::class, Player::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context) = instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "team_app_database"
            )
                .fallbackToDestructiveMigration().build()
                .also { instance = it }
        }
    }
}

