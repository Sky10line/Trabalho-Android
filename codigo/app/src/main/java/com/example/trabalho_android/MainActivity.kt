package com.example.trabalho_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getCurrentUser() == null){
            val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

            
        }
        else{
            Toast.makeText(this,"Bem-Vindo", Toast.LENGTH_SHORT).show()
        }
    }
    //FUNÇÃO QUE VERIFICA SE JÁ EXISTE UM USUÁRIO AUTENTICADO NA APLICAÇÃO
    fun getCurrentUser(): FirebaseUser?{
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }
}