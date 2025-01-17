package com.example.trabalho_android.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.room.Room
import com.example.trabalho_android.R
import com.example.trabalho_android.bdRoom.roomDataBase
import com.example.trabalho_android.models.HintSkip
import com.example.trabalho_android.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    var questionList: List<Question>? = null
    private var i: Int = -1
    var score: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        Thread {
            val hintsSkips = getHintSkipsQuantity()
            var hints = hintsSkips.get(0).hints
            var skips = hintsSkips.get(0).skips

            configFirebase()

            runOnUiThread {
                finishBtn.text = "Iniciar"
                Options.alpha = 0f
                hintBtn.alpha = 0f
                hintBtn.isEnabled = false

                finishBtn.setOnClickListener {
                    if (i == -1) {
                        finishBtn.text = "Próximo"
                        Options.alpha = 1f
                        hintBtn.alpha = 1f
                        hintBtn.isEnabled = true
                        i++
                        makeQuestion(i)
                    } else if (i < questionList?.count() ?: -1) {
                        if (checkQuantityAnswer(1) == true) {
                            for (k in 0 until Options.childCount) {
                                if ((Options.get(k) as CheckBox).isChecked) {
                                    if (checkAnswer(i, k)) {
                                        score += 100
                                    }
                                }
                            }
                            i++
                            if(i < questionList?.count() ?: -1) {
                                makeQuestion(i)
                            }
                            else{
                                val intent = Intent(this, GameOverActivity::class.java)
                                intent.putExtra("score", score)
                                startActivity(intent)
                            }
                        } else {
                            if (checkQuantityAnswer(0) == true) {
                                if (skips > 0) {
                                    skips -= 1
                                    i++
                                    if(i < questionList?.count() ?: -1) {
                                        makeQuestion(i)
                                    }
                                    else{
                                        val intent = Intent(this, GameOverActivity::class.java)
                                        intent.putExtra("score", score)
                                        startActivity(intent)
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Pulos de perguntas esgotados",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                //                        Toast.makeText(this, questionList?.get(i)?.correctAns?.toString(), Toast.LENGTH_LONG).show()
                                Toast.makeText(
                                    this,
                                    "Assinale 1 resposta apenas",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                hintBtn.setOnClickListener {
                    if (hints > 1) {
                        useHint()
                        hints -= 1
                    } else {
                        useHint()
                        hintBtn.isEnabled = false
                    }

                }
            }
        }.start()

        giveUpBtn.setOnClickListener {
            finish()
        }
    }

    private fun getHintSkipsQuantity(): List<HintSkip>{
        val db = Room.databaseBuilder(this, roomDataBase::class.java,  "AppDB").build()
        val hints = db.hintSkipDao().listAll()
        return hints
    }

    private fun useHint(){
        val ranNum = Random.nextInt(0,5)
        if(ranNum+1 != questionList?.get(i)?.correctAns && (Options.get(ranNum) as CheckBox).text != "") {
            (Options.get(ranNum) as CheckBox).text = ""
            (Options.get(ranNum) as CheckBox).isClickable = false
            (Options.get(ranNum) as CheckBox).isEnabled = false
        }
        else {
            useHint()
        }
    }

    private fun checkQuantityAnswer(quantity: Int) : Boolean{
        var answers = 0;
        for(option in Options){
            if((option as CheckBox).isChecked){
                answers += 1
            }
        }

        if(answers == quantity){
            return true
        }
        return false
    }

    private fun checkAnswer(fase: Int, resp: Int) : Boolean{
        if(questionList?.get(fase)?.correctAns == resp+1){
            return true
        }
        return false
    }

    private fun makeQuestion(i: Int){
        for(option in Options){
            (option as CheckBox).isChecked = false
            option.isClickable = true
            option.isEnabled = true
        }

        questionList?.get(i)?.let {
            question.setText(it.question)
            ans1.setText(it.ans1)
            ans2.setText(it.ans2)
            ans3.setText(it.ans3)
            ans4.setText(it.ans4)
            ans5.setText(it.ans5)
//            imgQuestion.setImageBitmap(it.bitmapImg)
        }
    }

    fun getCurrentUser(): FirebaseUser?{
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }

    fun configFirebase(){
        val user = getCurrentUser()

        user?.let {
            database = FirebaseDatabase.getInstance().reference

            val veListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    questionList = convertData(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("MainActivity", "configFirebase", error.toException())

                    AlertDialog.Builder(this@GameActivity)
                        .setTitle("Sem Conexão com a internet")
                        .setNegativeButton("OK", null)
                        .create()
                        .show()
                }
            }

            database?.addValueEventListener(veListener)
        }
    }

    fun convertData(snapshot: DataSnapshot): List<Question>{
        val questionsList = arrayListOf<Question>()

        snapshot.child("questions").children.forEach {
            val map = it.getValue() as HashMap<String, Any>

            val id = map.get("id") as String
            val question = map.get("question") as String
            val ans1 = map.get("ans1") as String
            val ans2 = map.get("ans2") as String
            val ans3 = map.get("ans3") as String
            val ans4 = map.get("ans4") as String
            val ans5 = map.get("ans5") as String
            val correctAns = map.get("correctAns") as Long
//            val bitmapImg = map.get("bitmapImg") as Bitmap

            val quest = Question(id, question, ans1, ans2, ans3, ans4, ans5, correctAns.toInt())
            questionsList.add(quest)
        }

        return questionsList
    }
}