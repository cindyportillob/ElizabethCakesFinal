package com.example.elizabethcakeshn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.elizabethcakes.utils.Constants
import com.example.elizabethcakeshn.utils.GlideLoader
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfileActivity : BaseActivity1(), View.OnClickListener {
    private lateinit var detalleUsuario: Users
    private var selectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            detalleUsuario = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }



        if (detalleUsuario.Pcompleto == 0){
            tv_title.text = "Completar Perfil"
            et_first_name.isEnabled = false
            iv_user_photo.setOnClickListener(this@UserProfileActivity)
            btn_submit.setOnClickListener(this@UserProfileActivity)
        }else{

            tv_title.text = resources.getString(R.string.title_edit_profile)
            et_first_name.isEnabled = false
            et_first_name.setText("Cindy Portillo")
            et_email.isEnabled = false
            et_email.setText("cindy.portillo@ujcv.edu.hn")

            if(detalleUsuario.mobile != 0L){
                et_mobile_number.setText(detalleUsuario.mobile.toString())
            }
            if (detalleUsuario.genero == Constants.MALE){
                rb_male.isChecked = true
            }else{
                rb_female.isChecked = true
            }
        }

    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()


        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != detalleUsuario.Nombre) {
            userHashMap[Constants.NAME] = firstName
        }


        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }


        if (mobileNumber.isNotEmpty() && mobileNumber != detalleUsuario.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != detalleUsuario.genero) {
            userHashMap[Constants.GENDER] = gender
        }


        if (detalleUsuario.Pcompleto == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }

        FireStore().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.iv_user_photo ->{
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this@UserProfileActivity)
                    }else{
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit ->{
                    if (validateUserProfileDetails()){
                        showProgressDialog("Por favor Espera")

                            if (selectedImageFileUri != null){
                                FireStore().uploadImageToCloudStore(
                                    this@UserProfileActivity,
                                    selectedImageFileUri, Constants. USER_PROFILE_IMAGE
                                )
                                }else{
                                    updateUserProfileDetails()
                        }
                        }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //showErrorSnackBar("El permiso a los archivos fue permitido",false)
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    try {
                        selectedImageFileUri = data.data!!

                        //iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(selectedImageFileUri!!,iv_user_photo)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else if (resultCode == Activity.RESULT_CANCELED){
                Log.e("Solicitud Cancelada","Seleeccion de Imagen Cancelada")
            }
        }
    }


    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim(){it <= ' '}) -> {
                showErrorSnackBar("el campo de numero telefonico esta vacio",true)
                false
            }else->{
                true
            }

        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dashboard_black_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }


    }

    fun userProfileUpdateSuccess() {



        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            "actualizacion exitosa",
            Toast.LENGTH_SHORT
        ).show()



        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }



    fun imageUploadSuccess(imageURL: String){
        mUserProfileImageURL = imageURL

        updateUserProfileDetails()
    }




}