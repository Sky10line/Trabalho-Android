package com.example.trabalho_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.trabalho_android.R
import com.example.trabalho_android.models.Player
import com.example.trabalho_android.models.Question
import com.example.trabalho_android.services.PlayerServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_game_over.*
import kotlinx.android.synthetic.main.activity_game_over.container
import kotlinx.android.synthetic.main.activity_high_score.*
import kotlinx.android.synthetic.main.card_player.*
import kotlinx.android.synthetic.main.card_player.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HighScoreActivity : AppCompatActivity() {
    var PlayerName : String? = "jOGADOR"
    var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)

        val intent = getIntent()
        var playerName = intent.getStringExtra("playerName")

        PlayerName = playerName

        configFirebase()
        refreshPlayerInfo()

        newQuestionBtn.setOnClickListener {
            val etCode = EditText(this)
            etCode.hint = "Digite o Código para adicionar uma pergunta"

            AlertDialog.Builder(this)
                .setTitle("Adicionar novo item")
                .setView(etCode)
                .setPositiveButton("Inserir") {dialog, button ->
                    if(etCode.text.toString() == "1234"){
                        newItem()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .create()
                .show()
        }

        backToMenuBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun newItem(){
        val etQuest = EditText(this)
        etQuest.hint = "Pergunta"

        val etAns1 = EditText(this)
        etAns1.hint = "Resposta 1"

        val etAns2 = EditText(this)
        etAns2.hint = "Resposta 2"

        val etAns3 = EditText(this)
        etAns3.hint = "Resposta 3"

        val etAns4 = EditText(this)
        etAns4.hint = "Resposta 4"

        val etAns5 = EditText(this)
        etAns5.hint = "Resposta 5"

        val etCorrectAns = EditText(this)
        etCorrectAns.inputType.toInt()
        etCorrectAns.hint = "Numero da resposta certa"

        val imgBtn = Button(this)
        imgBtn.text = "Adicionar foto"
        imgBtn.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(i.resolveActivity(packageManager) != null){
                startActivityForResult(i, 1)
            }
        }

        val layout = LinearLayout(this)
        layout.addView(etQuest)
        layout.addView(etAns1)
        layout.addView(etAns2)
        layout.addView(etAns3)
        layout.addView(etAns4)
        layout.addView(etAns5)
        layout.addView(etCorrectAns)
//        layout.addView(imgBtn)
        layout.orientation = LinearLayout.VERTICAL

        AlertDialog.Builder(this)
            .setTitle("Adicionar novo item")
            .setView(layout)
            .setPositiveButton("Inserir") {dialog, button ->
                val quest = Question(
                    question = etQuest.text.toString(),
                    ans1 = etAns1.text.toString(),
                    ans2 = etAns2.text.toString(),
                    ans3 = etAns3.text.toString(),
                    ans4 = etAns4.text.toString(),
                    ans5 = etAns5.text.toString(),
                    correctAns = etCorrectAns.text.toString().toInt())
//                    bitmapImg = bitmapImg as List<>)

                val newEntry = database?.child("questions")?.push()
                quest.id = newEntry?.key
                newEntry?.setValue(quest)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    fun getCurrentUser(): FirebaseUser?{
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }

    fun configFirebase(){
        val user = getCurrentUser()

        user?.let {
            database = FirebaseDatabase.getInstance().reference
        }
    }

    fun refreshUI(listaPlayers: List<Player>?, playerID: String?){
//        container.removeAllViews()

        if (listaPlayers != null) {
            for (player in listaPlayers){
//                if (player.name == playerID){
                    val cardView = layoutInflater
                        .inflate(R.layout.card_player, container, false)

                    cardView.endGameMsgText.text =  "Jogador: " + player.name //PARA TROCAR O TEXTO DO CARD. LEMBRAR DE CRIAR UM TEXTO PARA CASO O JOGADOR PERCA TAMBÉM
                    cardView.scoreText.text = "Score: " + player.highScore

                    container.addView(cardView)
//                }
            }
        }
    }

    fun  refreshPlayerInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudcrud.com/api/1d79ac9375094e0ab04ab81135b83d66/")
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
                        PlayerName

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