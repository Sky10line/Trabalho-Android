package com.example.trabalho_android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.core.view.iterator
import com.example.trabalho_android.R
import com.example.trabalho_android.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    var database: DatabaseReference? = null
    var questionList: List<Question>? = null
    var i: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        configFirebase()
        makeQuestion(i)

        finishBtn.setOnClickListener {

            for(option in Options){
                if((option as CheckBox).isChecked){
                    print(option.id)
                }
            }

            i++
            makeQuestion(i)
        }
    }

    fun makeQuestion(i: Int){
        questionList?.get(i)?.let {
            question.setText(it.question.toString())
            ans1.setText(it.ans1.toString())
            ans2.setText(it.ans2.toString())
            ans3.setText(it.ans3.toString())
            ans4.setText(it.ans4.toString())
            ans5.setText(it.ans5.toString())
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
//            val correctAns = map.get("correctAns") as Int
print(ans1)
            val quest = Question(id, question, ans1, ans2, ans3, ans4, ans5, 1)

            questionsList.add(quest)
        }

        return questionsList
    }
}