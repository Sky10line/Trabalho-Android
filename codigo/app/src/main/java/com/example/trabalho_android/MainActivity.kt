package com.example.trabalho_android

import android.app.Activity
import android.content.Intent
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

            val i = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            startActivityForResult(i, 0)
        }
        else{
            Toast.makeText(this,"Bem-Vindo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"Bem-Vindo", Toast.LENGTH_SHORT).show()
            }
            else{
                finishAffinity()
                //DEVERIA FAZER ALGO AO INVÉS DE DERRUBAR A APLICAÇÃO ???
            }
        }
    }
    //FUNÇÃO QUE VERIFICA SE JÁ EXISTE UM USUÁRIO AUTENTICADO NA APLICAÇÃO
    fun getCurrentUser(): FirebaseUser?{
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }
}