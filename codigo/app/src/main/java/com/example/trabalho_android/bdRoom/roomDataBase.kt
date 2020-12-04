package com.example.trabalho_android.bdRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trabalho_android.models.HintSkip

@Database(entities = arrayOf(HintSkip::class), version = 1)
abstract class roomDataBase: RoomDatabase() {
    abstract fun hintSkipDao(): HintSkipDao
}