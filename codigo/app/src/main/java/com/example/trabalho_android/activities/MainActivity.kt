package com.example.trabalho_android.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.trabalho_android.R
import com.example.trabalho_android.bdRoom.roomDataBase
import com.example.trabalho_android.models.HintSkip
import com.example.trabalho_android.models.Question
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    var bitmapImg: Bitmap? = null
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

       // addQuestionBtn.setOnClickListener {
       //     newItem()
        // }

        val hintSkips = HintSkip(hints = 2, skips = 2)
        Thread {
            insertHintSkipsQuantitys(hintSkips)
        }.start()
    }

    fun insertHintSkipsQuantitys(hintSkip: HintSkip){
        val db = Room.databaseBuilder(this, roomDataBase::class.java,  "AppDB").build()
        db.hintSkipDao().insert(hintSkip)
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
        }else if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            val img: Bitmap? = data?.getParcelableExtra("data")
            img?.let {
                saveImgBitmap(it)
            }
        }
    }

    fun saveImgBitmap(bitmap: Bitmap){
        bitmapImg = bitmap
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