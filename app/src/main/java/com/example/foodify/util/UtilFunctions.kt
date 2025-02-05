package com.example.foodify.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.R
import com.musfickjamil.snackify.Snackify

fun showProgressDialog(context: Context): SweetAlertDialog {
    val pDialog= SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    pDialog.progressHelper.barColor = ContextCompat.getColor(context, R.color.blue)
    pDialog.titleText = "Loading"
    pDialog.setCancelable(false)
    return pDialog
}

fun showSuccessSnackBar(view:View,message:String){
   Snackify.success(view,message,Snackify.LENGTH_LONG).show()
}

fun showErrorSnackBar(view:View,message:String){
    Snackify.error(view,message,Snackify.LENGTH_LONG).show()
}