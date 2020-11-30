package com.example.trabalho_android.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.trabalho_android.R
import com.example.trabalho_android.models.Question
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getCurrentUser() == null){
            val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

            val i = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            startActivityForResult(i, 0)
        }
        else{
            configFirebase()
            Toast.makeText(this,"Bem-Vindo", Toast.LENGTH_SHORT).show()
        }

        val iGame = Intent(this, GameActivity :: class.java)
        val iHigh = Intent(this, HighScoreActivity :: class.java)

        playBtn.setOnClickListener {
            startActivity(iGame)
        }

        highscoreBtn.setOnClickListener {
            startActivity(iHigh)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (resultCode == Activity.RESULT_OK){
                configFirebase()
                Toast.makeText(this,"Bem-Vindo", Toast.LENGTH_SHORT).show()
            }
            else{
                finishAffinity()
                //DEVERIA FAZER ALGO AO INVÉS DE DERRUBAR A APLICAÇÃO ???
            }
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

        val layout = LinearLayout(this)
        layout.addView(etQuest)
        layout.addView(etAns1)
        layout.addView(etAns2)
        layout.addView(etAns3)
        layout.addView(etAns4)
        layout.addView(etAns5)
        layout.addView(etCorrectAns)
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

                val newEntry = database?.child("questions")?.push()
                quest.id = newEntry?.key
                newEntry?.setValue(quest)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    //FUNÇÃO QUE VERIFICA SE JÁ EXISTE UM USUÁRIO AUTENTICADO NA APLICAÇÃO
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
}