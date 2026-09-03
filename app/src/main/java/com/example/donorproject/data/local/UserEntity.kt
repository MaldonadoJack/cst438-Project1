package com.example.donorproject.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Schema definition for the user table.
 *
 * The unique index on [username] enforces uniqueness in SQLite itself, so duplicate
 * usernames are rejected by the database regardless of how a later DAO inserts rows.
 * [passwordHash] only reserves the column; hashing is a later issue.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String
)
