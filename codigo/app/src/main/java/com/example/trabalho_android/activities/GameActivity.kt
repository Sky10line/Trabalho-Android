package com.example.trabalho_android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.iterator
import com.example.trabalho_android.R
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

        configFirebase()

        finishBtn.text = "Iniciar"
        Options.alpha = 0f
        hintBtn.alpha = 0f
        hintBtn.isEnabled = false

        finishBtn.setOnClickListener {
            if(i == -1) {
                finishBtn.text = "Próximo"
                Options.alpha = 1f
                hintBtn.alpha = 1f
                i++
                makeQuestion(i)
            } else if (i < questionList?.count() ?: -1){
                if(check1Answer() == true) {
                    for (k in 0 until Options.childCount) {
                        if ((Options.get(k) as CheckBox).isChecked) {
                            if (checkAnswer(i, k)) {
                                score += 100
                                Toast.makeText(this, score.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    i++
                    makeQuestion(i)
                } else {
                    Toast.makeText(this, questionList?.get(i)?.correctAns?.toString(), Toast.LENGTH_LONG).show()
//                    Toast.makeText(this, "Assinale 1 resposta apenas", Toast.LENGTH_LONG).show()
                }
            }
            else {
                //fazer o a transição para tela de colocar nome e subir o score
                if(score < 0){
                    score = 0
                }
            }
        }

        hintBtn.setOnClickListener {
                val ranNum = Random.nextInt(0,5)

                if(ranNum+1 != questionList?.get(i)?.correctAns){
                    (Options.get(ranNum) as CheckBox).text = ""
                } else {
                    if(ranNum+1 == 5){
                        (Options.get(ranNum-1) as CheckBox).text = ""
                    }
                    else {
                        (Options.get(ranNum+1) as CheckBox).text = ""
                    }
                }
                score -= 20
                hintBtn.isEnabled = false
        }

        giveUpBtn.setOnClickListener {
            finish()
        }
    }

    private fun check1Answer() : Boolean{
        var answers = 0;
        for(option in Options){
            if((option as CheckBox).isChecked){
                answers += 1
            }
        }

        if(answers <= 1){
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
        }

        questionList?.get(i)?.let {
            question.setText(it.question)
            ans1.setText(it.ans1)
            ans2.setText(it.ans2)
            ans3.setText(it.ans3)
            ans4.setText(it.ans4)
            ans5.setText(it.ans5)
        }
        hintBtn.isEnabled = true
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

            val quest = Question(id, question, ans1, ans2, ans3, ans4, ans5, correctAns.toInt())
            questionsList.add(quest)
        }

        return questionsList
    }
}