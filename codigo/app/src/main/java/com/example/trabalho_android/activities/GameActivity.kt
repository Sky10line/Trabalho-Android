package com.example.trabalho_android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.get
import com.example.trabalho_android.R
import com.example.trabalho_android.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    var questionList: List<Question>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        configFirebase()

        questionList?.get(0)?.let {
            question.setText(it.question.toString())
//            Options.get(1).get
        }

        finishBtn.setOnClickListener {
//            if(questionList?.get(0)?.correctAns == )
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
            val correctAns = map.get("correctAns") as Int

            val quest = Question(id = id, question = question, ans1 = ans1, ans2 = ans2, ans3 = ans3, ans4 = ans4, ans5 = ans5, correctAns = correctAns)

            questionsList.add(quest)
        }

        return questionsList
    }
}