package com.example.elizabethcakeshn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.elizabethcakes.utils.Constants
import com.example.elizabethcakeshn.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity1(), View.OnClickListener {



    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_details)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }



        if (FireStore().getCurrentUserID() == productOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)

        getProductDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity2::class.java))
                }
            }
        }
    }


    private fun addToCart() {

        val addToCart = Cart(
            FireStore().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStore().addCartItems(this@ProductDetailsActivity, addToCart)
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.drawable_gender_tab_text_color_selector)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getProductDetails() {


        showProgressDialog(resources.getString(R.string.please_wait))


        FireStore().getProductDetails(this@ProductDetailsActivity, mProductId)
    }


    fun productDetailsSuccess(product: Product) {

        mProductDetails = product


        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity


        if(product.stock_quantity.toInt() == 0){


            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE

            tv_product_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{


            if (FireStore().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FireStore().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }


    fun productExistsInCart() {


        hideProgressDialog()


        btn_add_to_cart.visibility = View.GONE

        btn_go_to_cart.visibility = View.VISIBLE
    }


    fun addToCartSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()


        btn_add_to_cart.visibility = View.GONE

        btn_go_to_cart.visibility = View.VISIBLE
    }
}
