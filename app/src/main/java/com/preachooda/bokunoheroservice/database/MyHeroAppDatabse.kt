package com.preachooda.bokunoheroservice.database

/*import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [], version = 1)
abstract class MyHeroAppDatabase : RoomDatabase() {

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: MyHeroAppDatabase? = null

        fun getInstance(context: Context): MyHeroAppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MyHeroAppDatabase {
            return Room.databaseBuilder(
                context,
                MyHeroAppDatabase::class.java,
                "MyHeroAppDatabase"
            ).build()
        }
    }
}*/
