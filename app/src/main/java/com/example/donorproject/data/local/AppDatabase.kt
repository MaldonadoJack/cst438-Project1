package com.example.donorproject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Shared Room database for the app. Later issues add their entities to [Database.entities]
 * and declare abstract DAO accessors here; nothing else should build a [RoomDatabase].
 *
 * Main-thread queries are left disabled (Room's default), so callers must access the
 * database from a background thread.
 */
@Database(
    entities = [UserEntity::class, FoodLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun accountDao(): AccountDao

    abstract fun foodLogDao(): FoodLogDao

    companion object {
        private const val DATABASE_NAME = "calorie_tracker.db"

        /**
         * Creates `food_log` and its user/time index without touching `users`.
         * Existing accounts survive this upgrade.
         */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `food_log` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` INTEGER NOT NULL,
                        `foodId` TEXT NOT NULL,
                        `foodName` TEXT NOT NULL,
                        `servingDescription` TEXT NOT NULL,
                        `calories` INTEGER NOT NULL,
                        `proteinGrams` REAL NOT NULL,
                        `carbohydrateGrams` REAL NOT NULL,
                        `fatGrams` REAL NOT NULL,
                        `loggedAtEpochMillis` INTEGER NOT NULL,
                        FOREIGN KEY(`userId`) REFERENCES `users`(`id`)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_food_log_userId_loggedAtEpochMillis`
                    ON `food_log` (`userId`, `loggedAtEpochMillis`)
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
