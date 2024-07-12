//package com.example.team_app.data.local_db
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.team_app.data.model.Player
//import com.example.team_app.data.model.Team
//import com.example.team_app.data.local_db.PlayerDao
//import com.example.team_app.data.local_db.TeamDao
//
//@Database(entities = [Team::class, Player::class], version = 1, exportSchema = false)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun teamDao(): TeamDao
//    abstract fun playerDao(): PlayerDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "team_app_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

package com.example.team_app.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.team_app.data.model.Player
import com.example.team_app.data.model.Team

@Database(entities = [Team::class, Player::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context) = instance?: synchronized(this){
            Room.databaseBuilder(context.applicationContext,AppDatabase :: class.java,"team_app_database")
           .build().also { instance = it }


        }

    }
}
