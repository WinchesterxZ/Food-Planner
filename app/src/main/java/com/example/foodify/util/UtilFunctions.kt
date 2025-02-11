package com.example.foodify.util

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.Button
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



fun showLoginDialog(context: Context, login: () -> Unit){
    val loginDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
    loginDialog.titleText = "Login"
    loginDialog.contentText = "Please login to continue"
    loginDialog.confirmText = "Login"
    loginDialog.setConfirmClickListener {
        login()
        it.dismissWithAnimation()
        loginDialog.dismiss()
    }
    loginDialog.setCancelable(true)
    loginDialog.show()
    loginDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.confirm_button)?.apply {
        backgroundTintList = null // Remove default Material background tint
        setPadding(0,5,0,5)
        setTypeface(typeface, Typeface.BOLD) // Make text bold
        setBackgroundResource(R.drawable.custom_button_error) // Apply custo
    }
}

fun showSuccessSnackBar(view:View,message:String){
   Snackify.success(view,message,Snackify.LENGTH_SHORT).show()
}
fun showInfoSnackBar(view:View,message:String){
    Snackify.info(view,message,Snackify.LENGTH_SHORT).show()
}


fun showErrorSnackBar(view:View,message:String){
    Snackify.error(view,message,Snackify.LENGTH_SHORT).show()
}