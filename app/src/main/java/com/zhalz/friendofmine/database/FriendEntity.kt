package com.zhalz.friendofmine.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FriendEntity(
    var name: String,
    var school: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
