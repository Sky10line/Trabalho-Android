package com.example.trabalho_android.models

data class Question (
    var id: String? = null,
    var question: String,
    var ans1: String,
    var ans2: String,
    var ans3: String,
    var ans4: String,
    var ans5: String,
    var correctAns: Int,
)