package com.example.donorproject.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AccountDao {

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun findById(userId: Int): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query(
        """
        UPDATE users
        SET username = :username,
            passwordHash = :passwordHash
        WHERE id = :userId
        """
    )
    suspend fun updateAccount(
        userId: Int,
        username: String,
        passwordHash: String
    ): Int
}