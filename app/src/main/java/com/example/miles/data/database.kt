package com.example.miles.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    @ColumnInfo(name = "timestamp")
    val timestamp:Long,
    @ColumnInfo(name = "avgSpeed")
    val speed: Int,
    @ColumnInfo(name = "distance")
    val Distance: Int,
    @ColumnInfo(name = "duration")
    val duration: Int,
    @ColumnInfo(name = "activity")
    val activity: String,
    @ColumnInfo(name = "calories")
    val calories: Int
)
