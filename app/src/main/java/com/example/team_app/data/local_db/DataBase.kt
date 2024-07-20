package com.example.team_app.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.team_app.data.model.Player
import com.example.team_app.data.model.Team

// Define the AppDatabase class as an abstract class extending RoomDatabase
@Database(entities = [Team::class, Player::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods to get the DAOs
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao

    companion object {
        // Volatile instance to ensure the instance is updated across all threads
        @Volatile
        private var instance: AppDatabase? = null

        // Function to get the database instance
        fun getDatabase(context: Context) = instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "team_app_database"
            )
                // Wipes and rebuilds instead of migrating if no Migration object
                .fallbackToDestructiveMigration().build()
                .also { instance = it }
        }
    }
}
