package com.example.trabalho_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.trabalho_android.R
import com.example.trabalho_android.models.Player
import com.example.trabalho_android.services.PlayerServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_game_over.*
import kotlinx.android.synthetic.main.card_player.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat

class GameOverActivity : AppCompatActivity() {

    var playerScore = 0
    var playerName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

//        var jogador = Player(0,"ana",1000)
        val intent = getIntent()
        val score = intent.getIntExtra("score", 0)

        playerScore = score

        playerName = editTextPlayerName.text.toString()

        //Toast.makeText(this,  score.toString(), Toast.LENGTH_LONG).show()
        scoreText.setText("SCORE: " + score.toString())

        sendScoreBtn.setOnClickListener {
            sendPlayerInfo(playerName, playerScore)

            val intent = Intent(this, HighScoreActivity::class.java)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }
    }

    fun  sendPlayerInfo(playerName: String, playerScore: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudcrud.com/api/3dbfd9ee5c4e48429604b46235435bfc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlayerServices::class.java)
        val call = service.create(Player(playerName,playerScore));

    }

}