package com.example.elizabethcakeshn

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.elizabethcakes.utils.Constants
import com.example.elizabethcakeshn.ui.dashboard.DashboardFragment
import com.example.elizabethcakeshn.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FireStore {


    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: Registro, userInfo: Users) {


        mFireStore.collection(Constants.USERS)

            .document(userInfo.id)

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.Registroexitoso()
            }
            .addOnFailureListener { e ->

                Log.e(
                    activity.javaClass.simpleName,
                    "Error no se pudo registrar el user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }


    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)

            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(Users::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.ELIZABETHCAKES_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.Nombre}"
                )
                editor.apply()
                when (activity) {
                    is LoginActivity -> {

                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {

                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error al obtener detalles de usuario",
                    e
                )


            }

    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.toString())

                val product = document.toObject(Product::class.java)!!

                activity.productDetailsSuccess(product)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }


            }.addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error al actualizar el detalle del usuario",
                    e
                )

            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity2 -> {
                        context.itemRemovedSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->

                when (context) {
                    is CartListActivity2 -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity2 -> {
                        context.itemUpdateSuccess()
                    }
                }


            }.addOnFailureListener { e ->

                when (context) {
                    is CartListActivity2 -> {

                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )


            }
    }

    fun getAllProductsList(activity: CartListActivity2) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                activity.successProductsListFromFireStore(productsList)

            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }


    fun uploadImageToCloudStore(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType
                    + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e(
                        "URL de imagen Descargable",
                        uri.toString()
                    )
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())

                        }
                    }
                }
        }
            .addOnFailureListener { exception ->

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
            .document()

            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getProductsList(fragment: Fragment) {

        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List", document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (fragment) {
                    is DashboardFragment -> {

                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }


    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document->
               Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }

    }

    fun getDashboardItemsList(fragment: HomeFragment) {

        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->


                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }
                fragment.successDashboardItemsList(productsList)

            }
            .addOnFailureListener { e ->


                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart:Cart){
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }

    }



    fun getCarlList(activity: Activity){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Cart> = ArrayList()

                for (i in document.documents){
                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when(activity){
                    is CartListActivity2 ->{
                        activity.successCartItemList(list)

                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CartListActivity2 -> {

                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }


    }
    fun deleteProduct(fragment: DashboardFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->



                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }





}