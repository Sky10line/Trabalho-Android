package com.example.trabalho_android.activities

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

//        var jogador = Player(0,"ana",1000)

    }

    override fun onResume() {
        super.onResume()

        refreshPlayerInfo()
    }

    fun refreshUI(listaPlayers: List<Player>?, playerID: String){
        container.removeAllViews()

        if (listaPlayers != null) {
            for (player in listaPlayers){
                if (player._id == playerID){
                    val cardView = layoutInflater
                        .inflate(R.layout.card_player, container, false)

                    cardView.endGameMsgText.text = "Parabéns " + player.name + " Você ganhou" //PARA TROCAR O TEXTO DO CARD. LEMBRAR DE CRIAR UM TEXTO PARA CASO O JOGADOR PERCA TAMBÉM
                    cardView.scoreText.text = "Score: " + player.highScore

                    container.addView(cardView)
                }
            }
        }
    }

    fun  refreshPlayerInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudcrud.com/api/3dbfd9ee5c4e48429604b46235435bfc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlayerServices::class.java)
        val call = service.list();

        val callback = object : Callback<List<Player>> {

            override fun onResponse(
                call: Call<List<Player>>,
                response: Response<List<Player>>
            ) {
                if (response.isSuccessful) {
                    refreshUI(
                        response.body(),
                        "5fc973fde445ab03e8f70835"
                    ) // CONSERTAR DEPOIS O COMO CHAMAR UM SCORES ESPECIFICO
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