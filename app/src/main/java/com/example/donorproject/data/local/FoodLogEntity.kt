package com.example.donorproject.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One logged serving owned by a [UserEntity]. Nutrition values are a snapshot of
 * the serving at log time so later API changes do not rewrite history.
 */
@Entity(
    tableName = "food_log",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "loggedAtEpochMillis"])]
)
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val foodId: String,
    val foodName: String,
    val servingDescription: String,
    val calories: Int,
    val proteinGrams: Double,
    val carbohydrateGrams: Double,
    val fatGrams: Double,
    val loggedAtEpochMillis: Long
)
