package com.example.elizabethcakeshn

import android.view.WindowInsets
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import com.example.elizabethcakes.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.*

class LoginActivity : BaseActivity1(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                FLAG_FULLSCREEN,
                FLAG_FULLSCREEN
            )
        }

        button.setOnClickListener{

            logInRegisteredUser()
        }

        txt_pass_recu.setOnClickListener(this)

        tv_registrar.setOnClickListener {
            val intent3 = Intent(this@LoginActivity, Registro::class.java)
            startActivity(intent3)
        }

    }

    fun userLoggedInSuccess(user: Users){
        hideProgressDialog()


        Log.i("Nombre Completo: ",user.Nombre)
        Log.i("Email: ",user.Email)

        if (user.Pcompleto == 0 ){
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        }else{
            val intent2 = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent2)
        }
        finish()
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.txt_pass_recu ->{
                    val intent = Intent(this@LoginActivity, Recuperacion_Password::class.java)
                    startActivity(intent)

                }
                R.id.button->{

                    logInRegisteredUser()
                }

                R.id.tv_registrar->{
                    val intent = Intent(this@LoginActivity, Registro::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(editTextTextPersonName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(editTextTextPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_Password1), true)
                false
            }
            else -> {

                true
            }
        }
    }


    private fun logInRegisteredUser(){
        if(validateLoginDetails()){
            showProgressDialog("Por favor Espere")


            val email = editTextTextPersonName.text.toString().trim { it <= ' ' }
            val password = editTextTextPassword.text.toString().trim { it <= ' ' }


            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        FireStore().getUserDetails(this@LoginActivity)
                    }else{
                        hideProgressDialog()
                        showErrorSnackBar("Error en las credenciales, intenta de nuevo",true)
                    }
                }
        }
    }

}