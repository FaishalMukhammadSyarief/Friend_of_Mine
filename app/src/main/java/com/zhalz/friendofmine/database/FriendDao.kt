package com.zhalz.friendofmine.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert
    suspend fun insert(friend: FriendEntity)

    @Update
    suspend fun update(friend: FriendEntity)

    @Delete
    suspend fun delete(friend: FriendEntity)

    @Query("SELECT * FROM FriendEntity")
    fun getAll() : Flow<List<FriendEntity>>
}