package com.isoft.parkingcalc

import androidx.fragment.app.Fragment
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

fun Fragment.showAlertDialog(message: String) {
    val builder = AlertDialog.Builder(this.activity!!)
    builder.setMessage(message)
    builder.setCancelable(false)
    builder.setNeutralButton("Ok",
        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    val alert = builder.create()
    alert.show()
}