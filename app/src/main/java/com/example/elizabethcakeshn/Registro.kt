package com.example.elizabethcakeshn

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.tv_registrar
import kotlinx.android.synthetic.main.activity_registro.*

class Registro : BaseActivity1() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        tv_login.setOnClickListener {


            startActivity(Intent(this@Registro, LoginActivity::class.java))
            finish()
        }

        button2.setOnClickListener{
            registerUser()
        }
    }
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(nombre_completo.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_nombre_completo),
                    true
                )
                false
            }
            TextUtils.isEmpty(email1.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_email),
                    true
                )
                false
            }
            TextUtils.isEmpty(Password1.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_Password1),
                    true
                )
                false
            }
            Password1.text.toString().trim { it <= ' ' } != Password2.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_Password2), true)
                false
            }
            else -> {

                true
            }
        }
    }

    private fun registerUser() {


        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = email1.text.toString().trim { it <= ' ' }
            val password: String = Password1.text.toString().trim { it <= ' ' }


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = Users(
                                firebaseUser.uid,
                                nombre_completo.text.toString().trim { it <= ' ' },
                                Password1.text.toString().trim { it <= ' ' },
                                email1.text.toString().trim { it <= ' ' }
                            )

                            FireStore().registerUser(this@Registro, user)


                        } else {

                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    fun Registroexitoso(){
        Toast.makeText(
            this@Registro,
            resources.getString(R.string.registery_successfull),
            Toast.LENGTH_SHORT
        ).show()
    }
}