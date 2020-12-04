package com.example.trabalho_android.bdRoom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trabalho_android.models.HintSkip

@Dao
interface HintSkipDao {
    @Insert
    fun insert(hintSkips: HintSkip)

    @Query(value = "select * from HintSkip")
    fun listAll(): List<HintSkip>
    
}