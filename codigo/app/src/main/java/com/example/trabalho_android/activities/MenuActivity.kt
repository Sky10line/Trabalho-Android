package com.example.trabalho_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trabalho_android.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val iGame = Intent(this, GameActivity :: class.java)
        val iHigh = Intent(this, HighScoreActivity :: class.java)

        playBtn.setOnClickListener {
            startActivity(iGame)
        }

        highscoreBtn.setOnClickListener {
            startActivity(iHigh)
        }
    }
}