package com.example.donorproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    /**
     * Returns the generated row id, which for [UserEntity] is also its [UserEntity.id].
     *
     * ABORT means a duplicate username fails the insert and leaves the existing row
     * untouched, rather than overwriting it the way REPLACE would.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    /** Matching is case-sensitive, so `Julian` and `julian` are different usernames. */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?
}
