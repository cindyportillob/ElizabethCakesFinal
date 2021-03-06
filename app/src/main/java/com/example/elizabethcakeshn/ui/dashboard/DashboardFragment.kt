package com.example.elizabethcakeshn.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elizabethcakeshn.*
import com.example.elizabethcakeshn.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)

            val adapterProducts = MyProductsListAdapter(requireActivity(), productsList,this)
            rv_my_product_items.adapter = adapterProducts
        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }

    }

    private fun getProductListFromFireStore() {

        FireStore().getProductsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){

            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))

                return true
            }
            R.id.action_perfil_edit -> {
                startActivity(Intent(activity, SettingsActivity::class.java))

                return true
            }
            R.id.action_cart_menu -> {
                startActivity(Intent(activity, CartListActivity2::class.java))

                return true
            }


        }
        

        return super.onOptionsItemSelected(item)
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)


    }

    fun productDeleteSuccess() {



        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(resources.getString(R.string.delete_dialog_title))

        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->




            FireStore().deleteProduct(this@DashboardFragment, productID)


            dialogInterface.dismiss()
        }


        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }







}