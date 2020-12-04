package com.example.trabalho_android.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class HintSkip (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var hints: Int,
    var skips: Int
    )
