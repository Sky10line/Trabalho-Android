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

            val intent = Intent(this, HighScoreActivity::class.java)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        sendPlayerInfo(playerName, playerScore)
    }

    fun  sendPlayerInfo(playerName: String, playerScore: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudcrud.com/api/1d79ac9375094e0ab04ab81135b83d66/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Toast.makeText(this, playerName,Toast.LENGTH_LONG).show()

        val service = retrofit.create(PlayerServices::class.java)
        val call = service.create(Player(playerName, playerScore));

        val callback = object : Callback<List<Player>> {

            override fun onResponse(
                call: Call<List<Player>>,
                response: Response<List<Player>>
            ) {
                if (response.isSuccessful) {
                    Snackbar
                        .make(container, "SCORE ENVIADO", Snackbar.LENGTH_LONG)
                        .show()

                     // CONSERTAR DEPOIS O COMO CHAMAR UM SCORES ESPECIFICO
                } else {
                    Snackbar
                        .make(container, "NÃO FOI POSSIVEL ATUALIZAR O SCORE", Snackbar.LENGTH_LONG)
                        .show()
                    Log.e("ERRO", response.errorBody().toString())
                }

            }

            override fun onFailure(call: Call<List<Player>>, t: Throwable) {
                Snackbar
                    .make(container, "NÃO FOI POSSIVEL CONECTAR A INTERNET", Snackbar.LENGTH_LONG)
                    .show()

                Log.e("ERRO", "Falha ao chamar o serviço", t)
            }

        }
        call.enqueue(callback)
    }

}