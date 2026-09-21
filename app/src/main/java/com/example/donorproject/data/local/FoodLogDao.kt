package com.example.donorproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodLogDao {

    @Insert
    suspend fun insert(entry: FoodLogEntity): Long

    /**
     * Entries for [userId] whose [FoodLogEntity.loggedAtEpochMillis] is in
     * `[startInclusive, endExclusive)`, oldest first. Ties use the generated id.
     */
    @Query(
        """
        SELECT * FROM food_log
        WHERE userId = :userId
          AND loggedAtEpochMillis >= :startInclusive
          AND loggedAtEpochMillis < :endExclusive
        ORDER BY loggedAtEpochMillis ASC, id ASC
        """
    )
    suspend fun findByUserAndTimeRange(
        userId: Int,
        startInclusive: Long,
        endExclusive: Long
    ): List<FoodLogEntity>

    /**
     * Deletes [entryId] only when it belongs to [userId]. Returns the number of
     * rows removed, which is 0 when the entry is missing or owned by someone else.
     */
    @Query("DELETE FROM food_log WHERE id = :entryId AND userId = :userId")
    suspend fun deleteForUser(entryId: Int, userId: Int): Int
}
